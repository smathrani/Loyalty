import java.io.*;
import java.util.*;


public class Solution2 {
	public static void main(String[] args) throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("test.txt")));
		String l = r.readLine();
		StringTokenizer tk = new StringTokenizer(l);
		int rows = Integer.parseInt(tk.nextToken());
		int cols = Integer.parseInt(tk.nextToken());
		char[][] mat = new char[rows][cols];
		for(int i = 0; i < rows; ++i)
		{
			l = r.readLine();
			for(int j = 0; j < cols; ++j)
				mat[i][j] = l.charAt(j);
		}
		while((l = r.readLine()) != null)
		{
			tk = new StringTokenizer(l);
			int row = Integer.parseInt(tk.nextToken());
			int col = Integer.parseInt(tk.nextToken());
			char c = tk.nextToken().charAt(0);
			floodFill(mat, row, col, c);
		}
		print(mat);
	}
	
	static void print(char[][] mat)
	{
		for(int i = 0; i < mat.length; ++i)
		{
			for(int j = 0; j < mat[i].length; ++j)
			{
				System.out.print(mat[i][j]);
			}
			System.out.println();
		}
	}
	
	static void floodFill(char[][] mat, int row, int col, char c)
	{
		char old = mat[row][col];
		mat[row][col] = c;
		if(row > 0 && mat[row-1][col] == old)
			floodFill(mat, row-1, col, c);
		if(col > 0 && mat[row][col-1] == old)
			floodFill(mat, row, col-1, c);
		if(row < mat.length-1 && mat[row+1][col] == old)
			floodFill(mat, row+1, col, c);
		if(col < mat[0].length-1 && mat[row][col+1] == old)
			floodFill(mat, row, col+1, c);
	}
}
