package com.example.loyalty;

import java.net.Socket;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	SharedPreferences prefs;// = getSharedPreferences("com.example.loyalty", 0);
	SharedPreferences.Editor edit;// = prefs.edit();
	TextView t;// = (TextView) findViewById(R.id.textView1);
	
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
	}
}
