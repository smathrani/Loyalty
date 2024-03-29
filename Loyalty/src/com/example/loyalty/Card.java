package com.example.loyalty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Card extends TableLayout implements Comparable<Card> {
	
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
	
	long time;
	String currentReward;
	String nextReward;
	
	public Card(Context context)
    {
        super(context);
		height = metrics.heightPixels;
		setPadding(0, 0, 0, height/40);
	}
	
	public Card(Context context, String s)
    {
        this(context);
        StringTokenizer stk = new StringTokenizer(s, ",");
		time = Long.parseLong(stk.nextToken());
		name = stk.nextToken();
		points = stk.nextToken();
		currentReward = stk.nextToken();
		nextReward = stk.nextToken();
	}
	
	public static ArrayList<Card> getCards(Context c, String unformatted) {
		ArrayList<Card> cards = new ArrayList<Card>();
		StringTokenizer tk = new StringTokenizer(unformatted, "\n");
		while(tk.hasMoreTokens())
			cards.add(new Card(c, tk.nextToken()));
		Collections.sort(cards);
		return cards;
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
		
		LinearLayout bottom = new LinearLayout(getContext());
		bottom.setOrientation(TableLayout.VERTICAL);
		//bottom.setBackgroundColor(Color.GRAY);
		bottom.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		
			LayoutParams topMargin = new LayoutParams();
			topMargin.setMargins(0, 2, 0, 2);
			
			final TextView row1 = new TextView(getContext());
			setupRow(row1, "row 1");
			
			TextView row2 = new TextView(getContext());
			setupRow(row2, "row 2");
			
			TextView row3 = new TextView(getContext());
			setupRow(row3, "row 3");

			TextView row4 = new TextView(getContext());
			setupRow(row4, "row 4");

			
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
	
	private void setupRow(TextView row, String data)
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

	@Override
	public int compareTo(Card another) {
		return ((Long) time).compareTo(another.time);
	}
	
}
