package com.example.loyalty;

import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends Activity {
	SharedPreferences prefs;// = getSharedPreferences("com.example.loyalty", 0);
	SharedPreferences.Editor edit;// = prefs.edit();
	TextView t;// = (TextView) findViewById(R.id.textView1);
	Handler handler;
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
//		Bundle b = intent.getExtras();
//		prefs = getSharedPreferences("com.example.loyalty", 0);
//		Log.d("Hi", b.getString("barcode", "qr"));
//		edit = prefs.edit();
//		edit.putString("barcode", b.getString("barcode", "didnt find"));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		final Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, CameraTestActivity.class);
				startActivityForResult(i, 1);
//				finish();
			}
		});
		prefs = getSharedPreferences("com.example.loyalty", 0);
		t = (TextView) findViewById(R.id.textView1);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) // Handling the different
													// server comms
			{
				Log.d("b", msg + "");
				if (msg.what == 0) // login successful or account created
				{
				}
			}
		};

		TableLayout logos = (TableLayout) findViewById(R.id.logo_table);
		TableLayout names = (TableLayout) findViewById(R.id.name_table);
		TableLayout points = (TableLayout) findViewById(R.id.points_table);

		ArrayList<String> list = new ArrayList<String>();
		try {
			FileOutputStream fos1 = openFileOutput("storage_file", Context.MODE_PRIVATE);
			Scanner s = new Scanner(openFileInput("storage_file"));
			String write = "Testing write to file\n";
			for(int i = 0; i < 200; i++)
				fos1.write(write.getBytes());
			while(s.hasNextLine())
			{
				String str = s.nextLine();
				list.add(str);
				TextView tv = new TextView(this);
				tv.setText(str);
				tv.setTextColor(Color.BLACK);
				TableRow t = new TableRow(this);
				t.addView(tv);
				t.setOrientation(TableRow.HORIZONTAL);
				names.addView(t, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}
			
			fos1.close();
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TableRow tr1 = new TableRow(this);
		// tr1.setOrientation(TableRow.HORIZONTAL);
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
		String code = data.getStringExtra("barcode");
		String un = prefs.getString("username","");
		String pw = prefs.getString("password", "");
		new ServCon(2000, handler, "findMyRestaurants");
	}
	
	private class ServCon extends AsyncTask<String, Void, String>
	{
		String ip;
		int port;
		String data;
		Handler h;
		
		@Override
		protected String doInBackground(String... params) {
			try
			{
				@SuppressWarnings("resource")
				Socket sock = new Socket("islamabad.clic.cs.columbia.edu", 2000);
				String inetaddr = sock.getInetAddress().toString();
				Log.d("Connected to", inetaddr);
				Log.d("Sending", data);
				// Do o.println to write to send the server data
				PrintWriter o = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
				// Receive data through r.readLine
				BufferedReader r = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				o.println("login\n"+prefs.getString("username", "")+"\n"+prefs.getString("password", ""));
				if(!r.readLine().equals("available"))
				{
					h.sendMessage(h.obtainMessage(-1, "login failed"));
					return "";
				}
				o.println(data);
				String reply = "";
				String l = null;
				while(!(l = r.readLine()).equals("[next cmd]"))
				{
					if(l.equals("[connection terminated]"))
					{
						h.sendMessage(h.obtainMessage(0, reply));
						return "";
					}
					reply += l + "\n";
				}
				h.sendMessage(h.obtainMessage(0, reply));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
