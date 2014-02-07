import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;


public class ServerLoyalty
{
	// By default set to 2000
	int servPort;
	// This is the socket on which the server listens
	ServerSocket servSock;
	// db is the database, it is concurrent so multiple
	// threads (clients) can edit it at the same time.
	ConcurrentHashMap<String, User> users;
	ConcurrentHashMap<Integer, Restaurant> restaurantsByID;
	
	@SuppressWarnings("unchecked")
	public ServerLoyalty(int portnum)
	{
		servPort = portnum;
		if(portnum <= 1024)
			servPort = 2000;
		users = new ConcurrentHashMap<String, User>();
		restaurantsByID = new ConcurrentHashMap<Integer, Restaurant>();
		try {servSock = new ServerSocket(servPort);}
		catch(IOException e)
		{
			System.out.println("Server socket creation failed!! Aborting");
			e.printStackTrace();
		}
		File u = new File("users.bin");
		File r = new File("restaurants.bin");
		if(u.exists())
			users = (ConcurrentHashMap<String, User>) deserialize(u);
		if(r.exists())
			restaurantsByID = (ConcurrentHashMap<Integer, Restaurant>) deserialize(r);
	}
	
	public void serve()
	{
		// A shutdown hook is a thread executed just before the
		// program terminates
		Runtime.getRuntime().addShutdownHook(new ExitCleanup());
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
	
	// Saves any given object in binary version. Convention is
	// to use the .bin extension for such files.
	private static void serialize(Object o, String fileName) {

		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
			out.writeObject(o);
			out.flush();
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object deserialize(String f) {return deserialize(new File(f));}
	
	// This is the opposite of the previous method - it loads saved objects
	private static Object deserialize(File f)
	{
		Object obj = null;
		
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try
		{
			fis = new FileInputStream(f);
			in = new ObjectInputStream(fis);
			obj = in.readObject();
			in.close();
			fis.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		return obj;
	}
	
	public static void main(String[] args)
	{
		ServerLoyalty s = new ServerLoyalty(0);
		s.serve();
	}
	
	// Method that checks whether the given socket can transfer any data
	public static boolean isClosed(Socket s)
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
						if(!users.containsKey(uname))
							ufail = true;
						// Compare password hashes
						if(!ufail && users.get(uname).passwdHash != passwd.hashCode())
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
						if(users.containsKey(uname))
							out.println("unavailable");
						else
						{
							User rec = new User(uname, passwd.hashCode(), email, fname, lname);
							users.put(uname, rec);
							out.println("available");
							user = uname;
							loggedIn = true;
						}
					}
					else if(auth.equals("check username availability"))
					{
						String uname = inp.readLine();
						if(users.containsKey(uname))
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
					if(!terminate) out.println("[next cmd]");
					else out.println("[connection terminated]");
				}
			}
			catch (IOException e)
			{
				System.out.println("Communication failed. Client socket possibly dead!");
				e.printStackTrace();
			}
		}
		
		// cmd.get(1) should return the id of the restaurant
		// where the user's points need to be increased
		private void addPoints(String user, ArrayList<String> cmd, PrintWriter out)
		{
			User rec = users.get(user);
			int restaurantID = Integer.parseInt(cmd.get(1));
			Restaurant restaurant = restaurantsByID.get(restaurantID);
			if(!rec.points.containsKey(restaurant))
				rec.points.put(restaurantID, 1);
			else rec.points.put(restaurantID, rec.points.get(restaurant)+1);
			restaurant.users.put(user, rec.points.get(restaurant));
		}
		
		// Enumerates all restaurants with their ID's and the user's
		// points at each one.
		private void restaurants(String uname, ArrayList<String> cmd, PrintWriter out)
		{
			User user = users.get(uname);
			Enumeration<Integer> e = user.points.keys();
			while(e.hasMoreElements())
			{
				int n = e.nextElement();
				Restaurant r = restaurantsByID.get(n);
				out.println(r.name+","+r.uid+","+user.points.get(r));
			}
		}
		
		// This command requires cmd[1] to be a restaurant ID,
		// and the reply is the number of points the user has at
		// that restaurant
		private void restaurantPoints(String uname, ArrayList<String> cmd, PrintWriter out)
		{
			out.println(users.get(uname).points.get(restaurantsByID.get(cmd.get(1))));
		}		
	}
	
	class ExitCleanup extends Thread
	{
		public void run()
		{
			serialize(users, "users.bin");
			serialize(restaurantsByID, "restaurants.bin");
			try {
				PrintWriter p = new PrintWriter("users.txt");
				Enumeration<String> e = users.keys();
				while(e.hasMoreElements())
				{
					String uname = e.nextElement();
					p.println(uname+"++"+users.get(uname));
				}
				p.close();
				p = new PrintWriter("restaurants.txt");
				Enumeration<Integer> en = restaurantsByID.keys();
				while(en.hasMoreElements())
				{
					int n = en.nextElement();
					p.println(n+"++"+restaurantsByID.get(n));
				}
				p.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}