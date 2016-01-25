package com.scapelog.api.model;

public enum SkillSet {
	ATTACK(0),
	DEFENCE(1),
	STRENGTH(2),
	CONSTITUTION(3),
	RANGED(4),
	PRAYER(5),
	MAGIC(6),
	COOKING(7),
	WOODCUTTING(8),
	FLETCHING(9),
	FISHING(10),
	FIREMAKING(11),
	CRAFTING(12),
	SMITHING(13),
	MINING(14),
	HERBLORE(15),
	AGILITY(16),
	THIEVING(17),
	SLAYER(18),
	FARMING(19),
	RUNECRAFTING(20),
	HUNTER(21),
	CONSTRUCTION(22),
	SUMMONING(23),
	DUNGEONEERING(24),
	DIVINATION(25),
	INVENTION(26);

	private final int id;

	SkillSet(int id) {
		this.id = id;
	}

	public static SkillSet forId(int id) {
		if (id < 0 || id >= count()) {
			throw new ArrayIndexOutOfBoundsException("id has to be between 0 and " + SkillSet.values().length);
		}
		return SkillSet.values()[id];
	}

	public static String getName(int id) {
		SkillSet skill = forId(id);
		return skill.getName();
	}

	public String getName() {
		String name = toString();
		if (name.length() == 0) {
			return name;
		}
		return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	public int getId() {
		return id;
	}

	public static int count() {
		return SkillSet.values().length;
	}

}