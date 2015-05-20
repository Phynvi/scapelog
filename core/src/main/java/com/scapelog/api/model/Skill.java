package com.scapelog.api.model;

import javafx.beans.property.SimpleIntegerProperty;

public final class Skill {
	public static final Skill UNSET = new Skill(-1);

	private final int id;
	private SimpleIntegerProperty level = new SimpleIntegerProperty(-1);
	private SimpleIntegerProperty xp = new SimpleIntegerProperty(-1);
	private int totalGainedXp = 0, gainedXp = 0;
	private long startTime;
	private int eventCount = 0;

	public Skill(int id) {
		this.id = id;
	}

	public void reset() {
		totalGainedXp = 0;
		gainedXp = 0;
		eventCount = 1;
		resetStartTime();
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return levelProperty().get();
	}

	public int getXP() {
		return xpProperty().get();
	}

	public void setLevel(int level) {
		this.level.set(level);
	}

	public void setXp(int xp) {
		this.xp.set(xp);
	}

	public SimpleIntegerProperty levelProperty() {
		return level;
	}

	public SimpleIntegerProperty xpProperty() {
		return xp;
	}

	public int getTotalGainedXp() {
		return totalGainedXp;
	}

	public int getGainedXp() {
		return gainedXp;
	}

	public long getStartTime() {
		return startTime;
	}

	public void resetStartTime() {
		this.startTime = System.currentTimeMillis();
	}

	public void resetGainedXp() {
		this.gainedXp = 0;
	}

	public void increaseXP(int xp) {
		totalGainedXp += xp;
		gainedXp += xp;
	}

	public int getEventCount() {
		return eventCount;
	}

	public void increaseEventCount() {
		eventCount++;
	}

	public boolean isInitialized() {
		return getLevel() != -1 && getXP() != -1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Skill skill = (Skill) o;

		return id == skill.id && level.equals(skill.level) && xp.equals(skill.xp);
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + level.hashCode();
		result = 31 * result + xp.hashCode();
		return result;
	}


}