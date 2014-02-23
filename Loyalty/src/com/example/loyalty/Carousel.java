package com.example.loyalty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Carousel extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d("***","Started");
		setContentView(R.layout.carousel);
		Log.d("***","2");
		LinearLayout car = (LinearLayout) findViewById(R.id.linearImage);
		for(int i = 0; i < 100; ++i)
		{
			ImageView t = new ImageView(this);
			t.setBackgroundResource(R.drawable.ic_launcher);
//			if(i==1)
//				t.setScaleY((float) 2);
			car.addView(t);
		}
	}
}
