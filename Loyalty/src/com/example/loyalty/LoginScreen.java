package com.example.loyalty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends Activity {

	final static String IP = "islamabad.clic.cs.columbia.edu";
	
	static int port = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		
		//allowing access to the shared preferences
		final SharedPreferences prefs = getSharedPreferences("com.example.loyalty", 0);
		final SharedPreferences.Editor edit = prefs.edit();
		
		//creating the EditTexts and Buttons
		final TextView error = (TextView) findViewById(R.id.errorLogin);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		final Button logIn = (Button) findViewById(R.id.login);
		
		error.setText("Hi!");
		//creating the handler
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) //Handling the different server comms
			{
				Log.d("b", msg+"");
				if(msg.what == 0 || msg.what == 2) //login successful or account created
				{	
					error.setText("available");
					if(!prefs.contains("username") || prefs.getString("username", "").length() == 0)
						edit.putString("username", username.getText()+"");
					if(!prefs.contains("password") || prefs.getString("password", "").length() == 0)
						edit.putString("password", password.getText()+"");
					edit.apply();
					Log.d("edit:", edit.toString());
					Intent verified = new Intent(LoginScreen.this, MainActivity.class);
					startActivity(verified);
					finish();
				}
				else if(msg.what == 1) //incorrect usernmae or password
				{
					error.setText("Sorry, that password is incorrect");
				}
				else if(msg.what == 3) //username taken
				{
					error.setText("Sorry, that username is taken");
				}
			}
		};
		
		//auto logging in
		if (prefs.contains("username") && prefs.contains("password"))
		{
			String un = prefs.getString("username", "");
			String pw = prefs.getString("password", "");
			Log.d("AutoLog un", un);
			Log.d("AutoLog pw", pw);
			new ServCon(IP, port, handler, "login\n"+un+"\n"+pw).execute();
		}
		
		
		
		//manual login in
		logIn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				error.setText("logIn");
				
				String un = null;
				String pw = null;
				
				un = username.getText()+"";
				pw = password.getText()+"";
				
				if (un.length()>0 && pw.length()>0)
				{
					new ServCon(IP, port, handler, "login\n"+un+"\n"+pw).execute();
				}
			}
		});
	}
	
	
	public static class ServCon extends AsyncTask<String, Void, String>
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
				Socket sock = new Socket(ip, port);
				String inetaddr = sock.getInetAddress().toString();
				Log.d("Connected to", inetaddr);
				Log.d("Sending", data);
				// Do o.println to write to send the server data
				PrintWriter o = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
				// Receive data through r.readLine
				BufferedReader r = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				o.println(data);
				
				Log.d("Waiting", "For reply");
				String reply = r.readLine();
				Log.d("Reply", reply);
				if(reply.equals("accepted"))
					h.sendMessage(h.obtainMessage(0));
				else if(reply.equals("invalid username") || reply.equals("invalid password"))
					h.sendMessage(h.obtainMessage(1));
				else if (reply.equals("available"))
					h.sendMessage(h.obtainMessage(2));
				else if (reply.equals("unavailable"))
					h.sendMessage(h.obtainMessage(3));
				
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
		
		public ServCon(String iNetAddr, int port, Handler h, String data)
		{
			ip = iNetAddr;
			this.port = port;
			this.data = data;
			this.h = h;
		}
	}		
}
