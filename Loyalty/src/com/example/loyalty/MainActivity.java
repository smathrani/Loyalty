package com.example.loyalty;

import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
	SharedPreferences prefs;
	SharedPreferences.Editor edit;
	TextView t;
	Handler handler;
	final int ERR = -1;
	final int FINDRESTAURANTS = 1;
	final int DEFAULT = 0;
	final int PORT = 2000;
	boolean first;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("***", "at beginning2");
		setContentView(R.layout.main_screen);
		final ImageButton b = (ImageButton)findViewById(R.id.cameraButton);
		Log.d("***", "kjbfv.zdfjv");
		first = true;
		b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, CameraTestActivity.class);
				startActivityForResult(i, 1);
			}
		});
		Log.d("***", "after button onclick");
		prefs = getSharedPreferences("com.example.loyalty", 0);
		final TableLayout names = (TableLayout) findViewById(R.id.table);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		final int height = metrics.heightPixels;
		Log.d("**Safe", "at handler");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) // Handling the different
													// server comms
			{
				Log.d("***handler rcvd", msg + "");
				if (msg.what == ERR) // login successful or account created
				{
					// DIEEEE
				}
				if(msg.what == FINDRESTAURANTS)
				{
					Log.d("***********Received*************",msg.obj+"");
					StringTokenizer tk = new StringTokenizer(msg.obj+"", "\n");
					ArrayList<String> list = new ArrayList<String>();
					names.removeAllViews();
					while(tk.hasMoreTokens())
					{
						String str = tk.nextToken();
						list.add(str);
						StringTokenizer stk = new StringTokenizer(str, ",");
						String name = stk.nextToken();
						stk.nextToken();
						String points = stk.nextToken();
						Card card = new Card(MainActivity.this);
						if(first)
						{
							card.createBig(name, points);
							first = false;
						}
						else card.createSmall(name, points);
						names.addView(card, new TableLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					}
				}
			}
		};
		new ServCon(PORT, handler, FINDRESTAURANTS+"findMyRestaurants").execute();
	}
	
	@SuppressLint("ResourceAsColor")
	public View createRow(String name, String points) {
		
		TableLayout tempTable = new TableLayout(this);
		TableRow row = new TableRow(this);

		TableRow.LayoutParams imagePar = new TableRow.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1f);
		TableRow.LayoutParams namePar = new TableRow.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 5f);
		TableRow.LayoutParams pointPar = new TableRow.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1f);

		ImageView logo = new ImageView(this);
		logo.setLayoutParams(imagePar);
		logo.setImageResource(R.drawable.ic_launcher);

		TextView nameTV = new TextView(this);
		nameTV.setLayoutParams(namePar);
		nameTV.setText(" "+name);
		nameTV.setTextColor(Color.BLACK);
		nameTV.setTextSize(18);

		TextView pointsTV = new TextView(this);
		pointsTV.setLayoutParams(pointPar);
		pointsTV.setText(points);
		pointsTV.setTextColor(Color.BLACK);
		pointsTV.setTextSize(18);


		row.addView(logo);
		row.addView(nameTV);
		row.addView(pointsTV);
		row.setGravity(Gravity.CENTER_VERTICAL);
		
		row.setBackgroundResource(R.color.white);
		
		tempTable.addView(row);
		
		return tempTable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode >= 0)
		{
			String code = data.getStringExtra("barcode");
			Log.d("*******CODE********", code);
			new ServCon(PORT, handler, FINDRESTAURANTS+"addPointsForRestaurant "+code+"\nfindMyRestaurants").execute();
		}
	}
	
	private class ServCon extends AsyncTask<String, Void, String>
	{
		int port;
		String data;
		Handler h;
		
		@Override
		protected String doInBackground(String... params) {
			try
			{
				@SuppressWarnings("resource")
				Socket sock = new Socket("islamabad.clic.cs.columbia.edu", port);
				String inetaddr = sock.getInetAddress().toString();
				Log.d("Connected to", inetaddr);
				Log.d("Sending", data);
				// Do o.println to write to send the server data
				PrintWriter o = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
				// Receive data through r.readLine
				BufferedReader r = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				Log.d("****LGN*****", "ready to log in");
				o.println("login\n"+prefs.getString("username", "")+"\n"+prefs.getString("password", ""));
				Log.d("****LGN*****", "sent login data");
				Log.d("**LGN** dat was", prefs.getString("password", "")+" "+prefs.getString("username", ""));
				int code = DEFAULT;
				int c = data.charAt(0) - '0';
				if(c >= -9 && c <= 9)
				{
					code = c;
					data = data.substring(1);
				}
				String repl = r.readLine();
				Log.d("******Repl******", repl);
				if(!repl.equals("accepted"))
				{
					h.sendMessage(h.obtainMessage(-1, repl));
					return "";
				}
				Log.d("***Data: ", data);
				StringTokenizer tk = new StringTokenizer(data, "\n");
				while(tk.hasMoreTokens())
				{
					String token = tk.nextToken();
					o.println(token);
					String reply = "";
					String l = null;
					while(!(l = r.readLine()).equals("[next cmd]"))
					{
						Log.d("***Line:", l);
						if(l.equals("[connection terminated]"))
						{
							Log.d("***Bad reply", "terminated");
							h.sendMessage(h.obtainMessage(code, reply));
							return "";
						}
						reply += l + "\n";
					}
					Log.d("**prepping return", reply);
					h.sendMessage(h.obtainMessage(code, reply));
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			return data;
		}
		
		public ServCon(int port, Handler h, String data)
		{
			this.port = port;
			this.data = data;
			this.h = h;
		}
	}
}
