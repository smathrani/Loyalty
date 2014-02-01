import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;


public class ServerLoyalty
{
	// By default set to 2000
	int servPort;
	// This is the socket on which the server listens
	ServerSocket servSock;
	// db is the database, it is concurrent so multiple
	// threads (clients) can edit it at the same time.
	ConcurrentHashMap<String, Dbo> db;
	ConcurrentHashMap<Integer, Restaurant> restaurantsByID;
	
	public ServerLoyalty(int portnum)
	{
		servPort = portnum;
		if(portnum <= 1024)
			servPort = 2000;
		db = new ConcurrentHashMap<String, Dbo>();
		try {servSock = new ServerSocket(servPort);}
		catch(IOException e)
		{
			System.out.println("Server socket creation failed!! Aborting");
			e.printStackTrace();
		}
	}
	
	public void serve()
	{
		// Loop forever looking for clients.
		for(;;)
		{
			try
			{
				Socket clnt = servSock.accept();
				// Client found, handle them in a separate thread so that
				// the server can continue listening
				new HandleClient(clnt).start();
				System.out.println("Client served. IP: "+clnt.getInetAddress());
			}
			catch (Exception e)
			{
				System.out.println("Failed user connection");
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		ServerLoyalty s = new ServerLoyalty(0);
		s.serve();
	}
	
	// Method that checks whether the given socket can transfer any data
	private static boolean isClosed(Socket s)
	{
		try {
			s.setSoTimeout(10);
			BufferedReader i = new BufferedReader(new InputStreamReader(s.getInputStream()));
			if(i.readLine()==null)
				return true;
		}
		catch(SocketTimeoutException e)
		{
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	private class HandleClient extends Thread
	{
		Socket sock;
		BufferedReader inp;
		PrintWriter out;
		
		public HandleClient(Socket client)
		{
			sock = client;
			try {
				inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(), true);
			}
			catch (IOException e)
			{
				System.out.println("Client I/O streams failed to create!");
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			try {
				String user = null;
				boolean loggedIn = false;
				while(!loggedIn)
				{
					// Read from user - authenticate
					// Find out if they're logging in or out;
					String auth = inp.readLine();
					boolean ufail = false;
					boolean pfail = false;
					if(auth.equals("login"))
					{
						String uname = inp.readLine();
						String passwd = inp.readLine();
						if(!db.containsKey(uname))
							ufail = true;
						// Compare password hashes
						if(!ufail && db.get(uname).passwdHash != passwd.hashCode())
							pfail = true;
						if(ufail)
							out.println("invalid username");
						else if(pfail)
							out.println("invalid password");
						else
						{
							out.println("accepted");
							loggedIn = true;
							user = uname;
						}
					}
					else if(auth.equals("signup"))
					{
						String uname = inp.readLine();
						String passwd = inp.readLine();
						String email = inp.readLine();
						String fname = inp.readLine();
						String lname = inp.readLine();
						if(db.containsKey(uname))
							out.println("unavailable");
						else
						{
							Dbo rec = new Dbo(uname, passwd.hashCode(), email, fname, lname);
							db.put(uname, rec);
							out.println("available");
							user = uname;
							loggedIn = true;
						}
					}
					else if(auth.equals("check username availability"))
					{
						String uname = inp.readLine();
						if(db.containsKey(uname))
							out.println("unavailable");
						else out.println("available");
					}
				}
				startSession(user, sock, inp, out);
			}
			catch (IOException e)
			{
				System.out.println("Client had an IO exception! Socket possibly dead.");
				e.printStackTrace();
			}
		}
		
		public void startSession(String uname, Socket sock, BufferedReader inp, PrintWriter out)
		{
			boolean terminate = false;
			try
			{
				while(!terminate)
				{
					String line = inp.readLine();
					StringTokenizer stk = new StringTokenizer(line);
					ArrayList<String> cmd = new ArrayList<String>();
					while(stk.hasMoreTokens()) cmd.add(stk.nextToken());
					if(cmd.get(0).equals("exit"))
						terminate = true;
					else if(cmd.get(0).equals("findMyRestaurants"))
						restaurants(uname, cmd, out);
					else if(cmd.get(0).equals("getPointsByRestaurantID"))
						restaurantPoints(uname, cmd, out);
					else if(cmd.get(0).equals("AddPointsForRestaurant"))
						addPoints(uname, cmd, out);
					else out.println("invalid command");
					out.println("[next cmd]");
				}
			}
			catch (IOException e)
			{
				System.out.println("Communication failed. Client socket possible dead!");
				e.printStackTrace();
			}
		}
		
		private void addPoints(String user, ArrayList<String> cmd, PrintWriter out)
		{
			Dbo rec = db.get(user);
			int restaurantID = Integer.parseInt(cmd.get(1));
			Restaurant restaurant = restaurantsByID.get(restaurantID);
			if(!rec.points.containsKey(restaurant))
				rec.points.put(restaurant, 1);
			else rec.points.put(restaurant, rec.points.get(restaurant)+1);
			restaurant.users.put(user, rec.points.get(restaurant));
		}
	}
}