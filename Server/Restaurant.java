import java.util.concurrent.ConcurrentHashMap;


public class Restaurant
{
	static int ids = 0;
	String name;
	final int uid = ids++;
	ConcurrentHashMap<String, Integer> users;
	
	public Restaurant(String name)
	{
		users = new ConcurrentHashMap<String, Integer>();
		this.name = name;
	}
}
