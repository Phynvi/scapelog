package com.scapelog.client.event.parser;

import com.scapelog.api.event.impl.SkillEvent;
import com.scapelog.api.model.Skill;
import com.scapelog.api.model.Skills;

public final class SkillEventParser extends EventParser<SkillEvent> {

	public SkillEventParser() {
		super(SkillEvent.class);
	}

	@Override
	public SkillEvent parse(String[] messageParts) {
		int skillId = Integer.parseInt(messageParts[1]);
		int level = Integer.parseInt(messageParts[2]);
		int xp = Integer.parseInt(messageParts[3]);

		/* only initialize the skill when xp is received for the first time */
		Skill skill = Skills.getSkill(skillId);
		if (!skill.isInitialized()) {
			skill.setLevel(level);
			skill.setXp(xp);
			return null;
		}

		int levelChange = level - skill.getLevel();
		int xpChange = xp - skill.getXP();

		/* lobby / login, no change so don't treat it as a change */
		if (levelChange == 0 && xpChange == 0) {
			return null;
		}

		skill.setLevel(level);
		skill.setXp(xp);
		skill.increaseEventCount();
		skill.increaseXP(xpChange);

		return new SkillEvent(skill, levelChange, xpChange);
	}

}