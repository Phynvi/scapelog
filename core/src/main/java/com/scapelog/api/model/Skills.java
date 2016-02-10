package com.scapelog.api.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class Skills {

	private static final ImmutableList<Skill> skills;
	private static final ImmutableList<Integer> xpTable;

	private static final ImmutableList<Integer> INVENTION_XP_TABLE = ImmutableList.of(
			0, 830, 1861, 2902, 3980, 5126, 6380, 7787, 9400, 11275,
			13605, 16372, 19656, 23546, 28134, 33520, 39809, 47109, 55535,
			65209, 77190, 90811, 106221, 123573, 143025, 164742, 188893, 215651,
			245196, 277713, 316311, 358547, 404634, 454796, 509259, 568254, 632019,
			700797, 774834, 854383, 946227, 1044569, 1149696, 1261903, 1381488, 1508756,
			1644015, 1787581, 1939773, 2100917, 2283490, 2476369, 2679917, 2894505, 3120508,
			3358307, 3608290, 3870846, 4146374, 4435275, 4758122, 5096111, 5449685, 5819299,
			6205407, 6608473, 7028964, 7467354, 7924122, 8399751, 8925664, 9472665, 10041285,
			10632061, 11245538, 11882262, 12542789, 13227679, 13937496, 14672812, 15478994,
			16313404, 17176661, 18069395, 18992239, 19945833, 20930821, 21947856, 22997593,
			24080695, 25259906, 26475754, 27728955, 29020233, 30350318, 31719944, 33129852,
			34580790, 36073511, 37608773, 39270442, 40978509, 42733789, 44537107, 46389292,
			48291180, 50243611, 52247435, 54303504, 56412678, 58575824, 60793812, 63067521,
			65397835, 67785643, 70231841, 72737330, 75303019, 77929820, 80618654, 83370445,
			86186124, 89066630, 92012904, 95025896, 98106559, 101255855, 104474750, 107764216,
			111125230, 114558777, 118065845, 121647430, 125304532, 129038159, 132849323, 136739041,
			140708338, 144758242, 148889790, 153104021, 157401983, 161784728, 166253312, 170808801,
			175452262, 180184770, 185007406, 189921255, 194927409
	);

	public static int getExperienceFromLevel(int level) {
		return getExperienceFromLevel(0, level);
	}

	public static int getExperienceFromLevel(int skillId, int level) {
		Skill skill = Skills.getSkill(skillId);
		if (skill == null)
			return 0;
		int maxLevel = skill.getMaxLevel();
		Preconditions.checkArgument(level >= 1 && level <= maxLevel, "Level must be between 1 and " + maxLevel + "!");
		return isInvention(skillId) ? INVENTION_XP_TABLE.get(level - 1) : xpTable.get(level - 1);
	}

	public static int getLevelFromExperience(int xp) {
		return getLevelFromExperience(0, xp);
	}

	public static int getLevelFromExperience(int skillId, int xp) {
		int len = isInvention(skillId) ? INVENTION_XP_TABLE.size() - 1 : xpTable.size() - 1;
		for (int level = 0; level < len - 1; level++) {
			int nextLevelExperience = isInvention(skillId) ? INVENTION_XP_TABLE.get(level) : xpTable.get(level);
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

	private static boolean isInvention(int skillId) {
		return skillId == SkillSet.INVENTION.getId();
	}

}