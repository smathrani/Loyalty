package com.example.loyalty;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Card extends TableLayout {
	
	String name;
	String points;
	boolean isSmall;
	
	private LayoutParams margin = new LayoutParams();
	//private LinearLayout.LayoutParams oneVertical = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
	//private LinearLayout.LayoutParams twoVertical = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 2f);
	//private LinearLayout.LayoutParams threeVertical = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 3f);
	//private LinearLayout.LayoutParams fourVertical = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 4f);
	
	private LinearLayout.LayoutParams oneHorizontal = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
	private LinearLayout.LayoutParams twoHorizontal = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 2f);
	private LinearLayout.LayoutParams threeHorizontal = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 3f);
	//private LinearLayout.LayoutParams fourHorizontal = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 4f);

	
	private int height;
	DisplayMetrics metrics = getResources().getDisplayMetrics();
	
	public Card(Context context)
    {
        super(context);
		height = metrics.heightPixels;
		setPadding(0, 0, 0, height/40);
	}

	public void createBig(String name, String points) {
		
		setOrientation(TableRow.VERTICAL);
		this.name = name;
		this.points = points;
		isSmall = false;
		
		margin.setMargins(0, 0, 0, 2);
		
		TableLayout table = new TableLayout(getContext());
		table.setOrientation(TableLayout.VERTICAL);

		
		LinearLayout top = new LinearLayout(getContext());
		top.setOrientation(TableLayout.HORIZONTAL);
		top.setBackgroundColor(Color.WHITE);
		top.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		top.setPadding(height/100,height/100,height/100, height/100);
		
			ImageView logo = new ImageView(getContext());
			logo.setImageResource(R.drawable.camera);
			int factor = 7;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height/factor, height/factor);
			logo.setLayoutParams(params);
			
			TableLayout thirdTable = new TableLayout(getContext());
			thirdTable.setOrientation(TableLayout.VERTICAL);
			thirdTable.setLayoutParams(oneHorizontal);
			
				TextView nametv = new TextView(getContext());
				nametv.setText(name);
				nametv.setTextSize(30);
				nametv.setGravity(Gravity.CENTER_HORIZONTAL);
				
				TextView pointstv = new TextView(getContext());
				pointstv.setText(points);
				pointstv.setTextSize(30);
				pointstv.setGravity(Gravity.CENTER);
				
				thirdTable.addView(nametv);
				thirdTable.addView(pointstv);
			
			top.addView(logo);
			top.addView(thirdTable);
			top.setLayoutParams(margin);
		
		TableLayout bottom = new TableLayout(getContext());
		bottom.setOrientation(TableLayout.VERTICAL);
		//bottom.setBackgroundColor(Color.GRAY);
		bottom.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		
			LayoutParams topMargin = new LayoutParams();
			topMargin.setMargins(0, 2, 0, 2);
		
		
			TableRow row1 = new TableRow(getContext());
			
				TextView row1tv = new TextView(getContext());
				setupTextView(row1tv, "row 1");
				
				Button row1button = new Button(getContext());
				setupButton(row1button, "button");
				
			row1.addView(row1tv);
			row1.addView(row1button);
			

			TableRow row2 = new TableRow(getContext());
			
				TextView row2tv = new TextView(getContext());
				setupTextView(row2tv, "row 2");
				
				Button row2button = new Button(getContext());
				setupButton(row1button, "button");
				
			row2.addView(row2tv);
			row2.addView(row2button);
			

			TableRow row3 = new TableRow(getContext());
			
				TextView row3tv = new TextView(getContext());
				setupTextView(row3tv, "row 3");
				
				Button row3button = new Button(getContext());
				setupButton(row3button, "button");
				
			row3.addView(row3tv);
			row3.addView(row3button);
			

			TableRow row4 = new TableRow(getContext());
			
				TextView row4tv = new TextView(getContext());
				setupTextView(row4tv, "row 4");
				
				Button row4button = new Button(getContext());
				setupButton(row4button, "button");
				
			row4.addView(row4tv);
			row4.addView(row4button);

			
			bottom.addView(row1);
			bottom.addView(row2);
			bottom.addView(row3);
			bottom.addView(row4);
			
		table.addView(top);
		table.addView(bottom);
		
		addView(table);
		
		top.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				swap();
			}
		});
		
		bottom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
	}
	
	private void setupButton(Button button, String data) {
		button.setText(data);
		button.setBackgroundColor(Color.WHITE);
		button.setLayoutParams(margin);
		button.setTextSize(20);		
	}

	private void setupTextView(TextView row, String data)
	{
		row.setText(data);
		row.setBackgroundColor(Color.WHITE);
		row.setLayoutParams(margin);
		row.setTextSize(20);
	}
	
	
	public void createSmall(String name, String points)
	{
		isSmall = true;
		margin.setMargins(0, 0, 0, 2);
		setOrientation(TableRow.HORIZONTAL);
		this.name = name;
		this.points = points;

		LinearLayout table = new LinearLayout(getContext());
		table.setOrientation(LinearLayout.HORIZONTAL);
		table.setBackgroundColor(Color.WHITE);



			ImageView logo = new ImageView(getContext());
			logo.setImageResource(R.drawable.camera);
			int factor = 12;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height/factor,height/factor);
			params.setMargins(height/200, height/200, height/200, height/200);
			logo.setLayoutParams(params);

			
			TextView nametv = new TextView(getContext());
			nametv.setText(name);
			nametv.setTextSize(30);
			nametv.setLayoutParams(threeHorizontal);
			nametv.setBackgroundColor(Color.WHITE);
			nametv.setGravity(Gravity.CENTER_VERTICAL);
			
			TextView pointstv = new TextView(getContext());
			pointstv.setText(points);
			pointstv.setLayoutParams(oneHorizontal);
			pointstv.setBackgroundColor(Color.WHITE);
			pointstv.setTextSize(30);
			pointstv.setGravity(Gravity.CENTER);
			
			table.addView(logo);
			table.addView(nametv);
			table.addView(pointstv);
		
		addView(table);	
		
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				swap();
			}
		});
		
	}
	
	public void swap()
	{
		if(isSmall)
		{
			removeAllViews();
			createBig(this.name,this.points);
		}
		else
		{
			removeAllViews();
			createSmall(this.name,this.points);
		}
		
	}
	
}
