package com.example.loyalty;
import java.util.*;

class CardCreator {
	public static ArrayList<CardData> getCardData(ArrayList<String> unformated) {
		ArrayList<CardData> data = new ArrayList<CardData>();
		for (String s : unformated) {
			StringTokenizer stk = new StringTokenizer(s, ",");
			long time = Long.parseLong(stk.nextToken());
			String name = stk.nextToken();
			int points = Integer.parseInt(stk.nextToken());
			String currentReward = stk.nextToken();
			String nextReward = stk.nextToken();
			data.add(new CardData(time, name, points, currentReward, nextReward));
		}
		return data;
	}
}