import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

public class Solution {
	public static void main(String[] args) throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("test.txt")));
		HashMap<Date, Integer> agg = new HashMap<Date, Integer>();
		int n = Integer.parseInt(r.readLine());
		for(int i = 0; i < n; ++i)
		{
			String l = r.readLine();
			StringTokenizer stk = new StringTokenizer(l);
			boolean rf = false;
			if(stk.nextToken().equals("Refund"))
				rf = true;
			Date d = new Date(stk.nextToken());
			int val = Integer.parseInt(stk.nextToken());
			if(rf) val *= -1;
			if(agg.containsKey(d))
				agg.put(d, agg.get(d)+val);
			else agg.put(d, val);
		}
		Date[] d = new Date[agg.size()];
		int i = 0;
		for(Date date : agg.keySet())
			d[i++] = date;
		Arrays.sort(d);
		for(i = 0; i < d.length; ++i)
			System.out.println(d[i]+" "+agg.get(d[i]));
	}
	
	static class Date implements Comparable<Date>
	{
		String line;
		int d, m, y;
		
		public Date(String line)
		{
			StringTokenizer stk = new StringTokenizer(line, "-");
			d = Integer.parseInt(stk.nextToken());
			m = Integer.parseInt(stk.nextToken());
			y = Integer.parseInt(stk.nextToken());
			this.line = line;
		}
		
		@Override
		public String toString() {
			return line;
		}

		@Override
		public int compareTo(Date o) {
			int my, os;
			my = os = -1;
			if(o.d != d)
			{
				my = d; os = o.d;
			}
			if(o.m != m)
			{
				my = m; os = o.m;
			}
			if(o.y != y)
			{
				my = y; os = o.y;
			}
			if(my == os)
				return 0;
			if(my < os)
				return -1;
			return 1;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((line == null) ? 0 : line.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			System.out.println(getClass());
			Date other = (Date) obj;
			if (line == null) {
				if (other.line != null)
					return false;
			} else if (!line.equals(other.line))
				return false;
			return true;
		}
	}
}