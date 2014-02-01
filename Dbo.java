import java.util.concurrent.ConcurrentHashMap;


public class Dbo
{
	String uname;
	int passwdHash;
	String email;
	String fname;
	String lname;
	ConcurrentHashMap<Restaurant, Integer> points;
	
	public Dbo(String un, int hash, String em, String f, String l)
	{
		uname = un;
		passwdHash = hash;
		email = em;
		fname = f;
		lname = l;
		points = new ConcurrentHashMap<Restaurant, Integer>();
	}
}
