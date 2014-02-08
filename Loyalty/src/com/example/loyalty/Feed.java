package com.example.loyalty;

import java.util.ArrayList;

import com.example.loyalty.R.color;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class Feed extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		TableLayout table = (TableLayout) findViewById(R.id.table);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = metrics.heightPixels;
		
		for(int i = 0; i<20; i++){
		String name = "aaaaaaaaaaaaaaaaaaaa";
		String points = "9";
		View innerTable = createRow(name, points);
		innerTable.setPadding(0, 0, 0, height/80);
		table.addView(innerTable, new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}


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
}
