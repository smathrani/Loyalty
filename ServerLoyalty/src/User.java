import java.io.Serializable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;


public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2008303579427032411L;
	String uname;
	int passwdHash;
	String email;
	String fname;
	String lname;
	// The keys here are the restaurant ids, and the objects the points
	// the user has at that restaurant.
	ConcurrentHashMap<Integer, Integer> points;
	
	public User(String un, int hash, String em, String f, String l)
	{
		uname = un;
		passwdHash = hash;
		email = em;
		fname = f;
		lname = l;
		if(email.length() == 0) email = "N/A;";
		if(fname.length() == 0) fname = "N/A;";
		if(lname.length() == 0) lname = "N/A;";
		points = new ConcurrentHashMap<Integer, Integer>();
	}
	
	public User() {}
	
	public String save()
	{
		String s = uname+"\n"+passwdHash+"\n"+email+"\n"+fname+"\n"+lname+"\n";
		Enumeration<Integer> e = points.keys();
		while(e.hasMoreElements())
		{
			int n = e.nextElement();
			s += n+" "+points.get(n)+"\n";
		}
		return s;
	}
	
	public void load(String s)
	{
		StringTokenizer stk = new StringTokenizer(s);
		uname = stk.nextToken(); passwdHash = Integer.parseInt(stk.nextToken());
		email = stk.nextToken(); fname = stk.nextToken(); lname = stk.nextToken();
		points = new ConcurrentHashMap<Integer, Integer>();
                while(stk.hasMoreTokens())
			points.put(Integer.parseInt(stk.nextToken()), Integer.parseInt(stk.nextToken()));
	}
}
