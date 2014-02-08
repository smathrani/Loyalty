import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
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
	// These 2 hashmaps are the database, they are concurrent so multiple
	// threads (clients) can edit them at the same time.
	ConcurrentHashMap<String, User> users;
	ConcurrentHashMap<Integer, Restaurant> restaurants;
	
	public ServerLoyalty(int portnum)
	{
		servPort = portnum;
		if(portnum <= 1024)
			servPort = 2000;
		users = new ConcurrentHashMap<String, User>();
		restaurants = new ConcurrentHashMap<Integer, Restaurant>();
		try {servSock = new ServerSocket(servPort);}
		catch(IOException e)
		{
			System.out.println("Server socket creation failed!! Aborting");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			loadDB();
		} catch(Exception e) {
			try {
				System.out.println("DB load failed. Reinitializing.");
				PrintWriter p = new PrintWriter(new OutputStreamWriter(new FileOutputStream("error.log", true)));
				p.println("On: "+new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\nDB load failed!! Data possibly lost!! Reinitializing.");
				p.close();
			}
			catch(FileNotFoundException ef) {
				System.out.println("Couldn't open log file!!! Dying!!");
				ef.printStackTrace();
			}
		}
		Enumeration<Integer> e = restaurants.keys();
		int max = 0;
		while(e.hasMoreElements()) max = Math.max(max, e.nextElement());
		Restaurant.ids = max + 1;
                e = restaurants.keys();
                while(e.hasMoreElements())
                {
                    int n = e.nextElement();
                    System.out.println(n+"::"+restaurants.get(n).name);
                }
	}
	
	@SuppressWarnings("unchecked")
	private void loadDB() throws Exception
	{
		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream("users.txt")));
		if(!rd.readLine().equals("EDITTED"))
		{
			File u = new File("users.bin");
			if(u.exists())
				users = (ConcurrentHashMap<String, User>) deserialize(u);
		}
		else
		{
			String obj = "";
			String line = null;
			while((line = rd.readLine()) != null)
			{
				if(line.equals("++++"))
				{
					User u = new User();
					u.load(obj);
					users.put(u.uname, u);
					obj = "";
				}
				else obj += line + "\n";
			}
		}
		rd.close();
		rd = new BufferedReader(new InputStreamReader(new FileInputStream("restaurants.txt")));
		if(!rd.readLine().equals("EDITTED"))
		{
			File r = new File("restaurants.bin");
			if(r.exists())
				restaurants = (ConcurrentHashMap<Integer, Restaurant>) deserialize(r);
		}
		else
		{
			String obj = "";
			String line = null;
			while((line = rd.readLine()) != null)
			{
				if(line.equals("++++"))
				{
					Restaurant r = new Restaurant();
					r.load(obj);
					restaurants.put(r.getUid(), r);
					obj = "";
				}
				else obj += line + "\n";
			}
		}
	}
	
	public void serve()
	{
		// A shutdown hook is a thread executed just before the
		// program terminates. ExitCleanup saves the database in .txt and .bin formats.
		Runtime.getRuntime().addShutdownHook(new ExitCleanup());
		// Loop forever looking for clients.
		try
		{
			for(;;)
			{
				Socket clnt = servSock.accept();
				// Client found, handle them in a separate thread so that
				// the server can continue listening
				new HandleClient(clnt).start();
				System.out.println("Client served. IP: "+clnt.getInetAddress());
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed user connection");
			e.printStackTrace();
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
						System.out.println(uname+" attempting to login");
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
					if(line==null) line = "exit";
					StringTokenizer stk = new StringTokenizer(line);
					ArrayList<String> cmd = new ArrayList<String>();
					while(stk.hasMoreTokens()) cmd.add(stk.nextToken());
                                        System.out.println(cmd.get(0));
					if(cmd.get(0).equals("exit"))
						terminate = true;
					else if(cmd.get(0).equals("findMyRestaurants"))
						restaurants(uname, cmd, out);
					else if(cmd.get(0).equals("getPointsByRestaurantID"))
						restaurantPoints(uname, cmd, out);
					else if(cmd.get(0).equals("addPointsForRestaurant"))
						addPoints(uname, cmd, out);
					else {
                                            System.out.println("bad cmd: "+cmd.get(0));
                                            out.println("invalid command");
                                        }
					if(!terminate) out.println("[next cmd]");
					else {
						out.println("[connection terminated]");
						sock.close();
					}
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
			User usr = users.get(user);
			int restaurantID = Integer.parseInt(cmd.get(1));
			Restaurant restaurant = restaurants.get(restaurantID);
                        System.out.println(restaurant==null);
                        System.out.println(usr.points==null);
//                        System.out.println(restaurant.getUid()==null);
//                        System.out.println("At restaurant "+restaurant.name+" the user has "+(usr.points.get(restaurant.getUid())+1)+" points.");
			if(!usr.points.containsKey(restaurantID))
				usr.points.put(restaurantID, 1);
			else usr.points.put(restaurantID, usr.points.get(restaurantID)+1);
			restaurant.users.put(usr.uname, usr.points.get(restaurantID));
                        System.out.println("The restaurant thinks usr has "+restaurant.users.get(usr.uname)+" points");
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
				Restaurant r = restaurants.get(n);
				out.println(r.name+","+r.getUid()+","+user.points.get(r.getUid()));
			}
		}
		
		// This command requires cmd[1] to be a restaurant ID,
		// and the reply is the number of points the user has at
		// that restaurant
		private void restaurantPoints(String uname, ArrayList<String> cmd, PrintWriter out)
		{
			out.println(users.get(uname).points.get(restaurants.get(cmd.get(1))));
		}		
	}
	
	class ExitCleanup extends Thread
	{
		public void run()
		{
			serialize(users, "users.bin");
			serialize(restaurants, "restaurants.bin");
			try {
				PrintWriter p = new PrintWriter("users.txt");
				p.println("SAVED");
				Enumeration<String> e = users.keys();
				while(e.hasMoreElements())
					p.print(users.get(e.nextElement()).save()+"++++\n");
				p.close();
				p = new PrintWriter("restaurants.txt");
				p.println("SAVED");
				Enumeration<Integer> en = restaurants.keys();
				while(en.hasMoreElements())
					p.print(restaurants.get(en.nextElement()).save()+"++++\n");
				p.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
