package com.scapelog.api.model;

public enum Activities {
	BOUNTY_HUNTERS("Bounty Hunters"),
	BH_ROGUES("B.H. Rogues"),
	DOMINION_TOWER("Dominion Tower"),
	CRUCIBLE("The Crucible"),
	CASTLE_WARS("Castle Wars Games"),
	BA_ATTACKERS("B.A. Attackers"),
	BA_DEFENDERS("B.A. Defenders"),
	BA_COLLECTORS("B.A. Collectors"),
	BA_HEALERS("B.A. Healers"),
	DUEL_TOURNAMENT("Duel Tournament"),
	MOBILISING_ARMIES("Mobilising Armies"),
	CONQUEST("Conquest"),
	FIST_OF_GUTHIX("Fist of Guthix"),
	GG_RESOURCE_RACE("GG: Resource Race"),
	GG_ATHLETICS("GG: Athletics"),
	WE2_ARMADYL_CONTRIBUTION("WE2: Armadyl Lifetime Contribution"),
	WE2_BANDOS_CONTRIBUTION("WE2: Bandos Lifetime Contribution"),
	WE2_ARMADYL_PVP_KILLS("WE2: Armadyl PvP Kills"),
	WE2_BANDOS_PVP_KILLS("WE2: Bandos PvP Kills"),
	HEIST_GUARD_LEVEL("Heist Guard Level"),
	HEIST_ROBBER_LEVEL("Heist Robber Level"),
	CFP_5_GAME_AVEGAGE("CFP: 5 Game Average");

	private final String name;

	Activities(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
