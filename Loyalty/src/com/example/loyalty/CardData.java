package com.example.loyalty;

class CardData implements Comparable<CardData> {

	private long time;
	private String name;
	private int points;
	private String currentReward;
	private String nextReward;
	private String elapsedTime;

	public CardData(long t, String n, int p, String cR, String nR) {
		time = t;
		name = n;
		points = p;
		cR = currentReward;
		nR = nextReward;
		this.elapsedTime();
	}

	private void elapsedTime() {
		long diff = System.currentTimeMillis() - time;
		int days = (int) (Math.round(diff) / (24 * 60 * 60 * 1000));
		elapsedTime = days + " days since last visit";
	}

	public long getTime() {
		return time;
	}

	public int getPoints() {
		return points;
	}

	public String getName() {
		return name;
	}

	public String getCurrentReward() {
		return currentReward;
	}

	public String getNextReward() {
		return nextReward;
	}

	@Override
	public int compareTo(CardData another) {
		if (this.getTime() < another.getTime())
			return -1;
		if (this.getTime() > another.getTime())
			return 1;
		return 0;
	}
}
