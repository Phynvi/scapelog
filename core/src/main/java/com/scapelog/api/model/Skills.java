package com.scapelog.api.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class Skills {

	private static final ImmutableList<Skill> skills;
	private static final ImmutableList<Integer> xpTable;

	public static int getExperienceFromLevel(int level) {
		Preconditions.checkArgument(level >= 1 && level <= 126, "Level must be between 1 and 126!");
		return xpTable.get(level - 1);
	}

	public static int getLevelFromExperience(int xp) {
		for (int level = 0; level < xpTable.size() - 1; level++) {
			int nextLevelExperience = xpTable.get(level);
			if (xp < nextLevelExperience) {
				return level;
			}
		}
		return 126;
	}

	public static Skill getSkill(int skillId) {
		Preconditions.checkArgument(skillId >= 0 && skillId <= SkillSet.count(), "Skill id must be between 0 and " + SkillSet.count() + "!");
		Skill skill = skills.get(skillId);
		if (skill.getId() != skillId) {
			for (Skill s : skills) {
				if (s.getId() == skillId) {
					return s;
				}
			}
		}
		return skill;
	}

	static {
		xpTable = initializeXPTable();
		skills = initializeSkills();
	}

	private static ImmutableList<Integer> initializeXPTable() {
		ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<>();

		int points = 0;
		int output = 0;

		for (int level = 1; level <= 126; level++) {
			builder.add(output);

			points += Math.floor(level + 300.0 * Math.pow(2.0, level / 7.0));
			output = points / 4;
		}
		return builder.build();
	}

	private static ImmutableList<Skill> initializeSkills() {
		ImmutableList.Builder<Skill> builder = new ImmutableList.Builder<>();
		for (int skillId = 0; skillId < SkillSet.count(); skillId++) {
			Skill skill = new Skill(skillId);
			builder.add(skill);
		}
		return builder.build();
	}

}