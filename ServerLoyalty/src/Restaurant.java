import java.io.Serializable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;


public class Restaurant implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1803157428744659620L;
	static int ids = 0;
	String name;
	private int uid;
	ConcurrentHashMap<String, Integer> users;
	
	public Restaurant(String name)
	{
		users = new ConcurrentHashMap<String, Integer>();
		this.name = name;
		uid = ids++;
	}
	
	public Restaurant() {}
	
	public String save()
	{
		String s = name+"\n"+uid+"\n";
		Enumeration<String> e = users.keys();
		while(e.hasMoreElements())
		{
			String u = e.nextElement();
			s += u+" "+users.get(u)+"\n";
		}
		return s;
	}
	
	public void load(String s)
	{
		StringTokenizer stk = new StringTokenizer(s);
		name = stk.nextToken(); uid = Integer.parseInt(stk.nextToken());
		users = new ConcurrentHashMap<String, Integer>();
                while(stk.hasMoreTokens())
			users.put(stk.nextToken(), Integer.parseInt(stk.nextToken()));
	}
	
	public int getUid()
	{
		return uid;
	}
}
