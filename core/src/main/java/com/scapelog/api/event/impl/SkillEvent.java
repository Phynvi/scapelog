package com.scapelog.api.event.impl;

import com.scapelog.client.event.SourceVerifiedEvent;
import com.scapelog.client.event.parser.SkillEventParser;
import com.scapelog.api.model.Skill;

/**
 * An event class for incoming skill changes
 */
public final class SkillEvent extends SourceVerifiedEvent {

	private final Skill skill;
	private final int levelChange;
	private final int xpChange;

	public SkillEvent(Skill skill, int levelChange, int xpChange) {
		super(SkillEventParser.class);
		this.skill = skill;
		this.levelChange = levelChange;
		this.xpChange = xpChange;
	}

	/**
	 * The {@link Skill} that has changed
	 * @return The changed skill
	 */
	public Skill getSkill() {
		return skill;
	}

	/**
	 * Changes in the skill's current level such as potion boosts, not used for level-ups.
	 * @return The amount of levels the skill changed
	 */
	public int getLevelChange() {
		return levelChange;
	}

	/**
	 * The amount of gained in the skill since the last update
	 * @return The amount of xp gained since the last skill update
	 */
	public int getXpChange() {
		return xpChange;
	}

}