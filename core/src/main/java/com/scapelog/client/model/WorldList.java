package com.scapelog.client.model;

import com.scapelog.client.config.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum WorldList {
	WORLD_1(1, "Trade - Members", Language.ENGLISH, true),
	WORLD_2(2, "Trade - Members", Language.ENGLISH, true),
	WORLD_3(3, "Trade - Free", Language.ENGLISH, false),
	WORLD_4(4, "Canada", Language.ENGLISH, true),
	WORLD_5(5, "Norway", Language.ENGLISH, true),
	WORLD_6(6, "Barbarian Assault", Language.ENGLISH, true),
	WORLD_7(7, "Dungeoneering", Language.ENGLISH, false),
	WORLD_8(8, "Clan Recruitment", Language.ENGLISH, false),
	WORLD_9(9, "Falador Party Room", Language.ENGLISH, true),
	WORLD_10(10, "Netherlands", Language.ENGLISH, true),
	WORLD_11(11, "UK", Language.ENGLISH, false),
	WORLD_12(12, "New Zealand", Language.ENGLISH, true),
	// world 13?
	WORLD_13(13, "New Zealand", Language.ENGLISH, false),
	WORLD_14(14, "United States (West Coast)", Language.ENGLISH, true),
	WORLD_15(15, "Barrows", Language.ENGLISH, true),
	WORLD_16(16, "Bonfires", Language.ENGLISH, true),
	WORLD_17(17, "Europe", Language.ENGLISH, false),
	WORLD_18(18, "Legacy Only - Player killing", Language.ENGLISH, true),
	WORLD_19(19, "Netherlands", Language.ENGLISH, false),
	WORLD_20(20, "Minigames - Free", Language.ENGLISH, false),
	WORLD_21(21, "Europe", Language.ENGLISH, true),
	WORLD_22(22, "Community Fishing Training", Language.ENGLISH, true),
	WORLD_23(23, "UK", Language.ENGLISH, true),
	WORLD_24(24, "Castle Wars", Language.ENGLISH, true),
	WORLD_25(25, "Mobilising Armies (20)", Language.ENGLISH, true),
	WORLD_26(26, "Europe", Language.ENGLISH, true),
	WORLD_27(27, "Social Slayer", Language.ENGLISH, true),
	WORLD_28(28, "Poland", Language.ENGLISH, true),
	WORLD_29(29, "Bonfires", Language.ENGLISH, false),
	WORLD_30(30, "Skill Total (2000)", Language.ENGLISH, true),
	WORLD_31(31, "Open Gilded Altars", Language.ENGLISH, true),
	WORLD_32(32, "Dungeoneering: Levels 1-80", Language.ENGLISH, true),
	WORLD_33(33, "Legacy Only - Player Killing", Language.ENGLISH, false),
	WORLD_34(34, "United States (East Coast 2)", Language.ENGLISH, false),
	WORLD_35(35, "Fight pits", Language.ENGLISH, true),
	WORLD_36(36, "RC Running & ZMI Altar", Language.ENGLISH, true),
	WORLD_37(37, "United States (West Coast)", Language.ENGLISH, true),
	WORLD_38(38, "Runespan", Language.ENGLISH, false),
	WORLD_39(39, "Runespan", Language.ENGLISH, true),
	WORLD_40(40, "Duel Arena - Friendly", Language.ENGLISH, true),
	WORLD_41(41, "Role-Playing Server", Language.ENGLISH, false),
	WORLD_42(42, "Role-Playing Server", Language.ENGLISH, true),
	WORLD_43(43, "Canada", Language.ENGLISH, false),
	WORLD_44(44, "Soul Wars", Language.ENGLISH, true),
	WORLD_45(45, "Stealing Creation", Language.ENGLISH, true),
	WORLD_46(46, "Community Agility Training", Language.ENGLISH, true),
	WORLD_47(47, "Português", Language.ENGLISH, true), //todo: check language
	WORLD_48(48, "Skill Total (2400)", Language.ENGLISH, true),
	WORLD_49(49, "Australia", Language.ENGLISH, true);
	//WORLD_5(5, "", Language.ENGLISH, ),
	// todo: do rest of the worlds

	private static ObservableList<WorldList> list;

	private final int id;
	private final String activity;
	private final Language language;
	private final boolean members;

	WorldList(int id, String activity, Language language, boolean members) {
		this.id = id;
		this.activity = activity;
		this.language = language;
		this.members = members;
	}

	public int getId() {
		return id;
	}

	public String getActivity() {
		return activity;
	}

	public Language getLanguage() {
		return language;
	}

	public boolean isMembers() {
		return members;
	}

	public static WorldList getWorld(int worldId) {
		for (WorldList world : WorldList.values()) {
			if (world.getId() == worldId) {
				return world;
			}
		}
		return WORLD_1;
	}

	public static ObservableList<WorldList> asList() {
		if (list == null) {
			list = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(WorldList.values()));
		}
		return list;
	}

	public static WorldList getSavedWorld() {
		int defaultWorld = WorldList.WORLD_1.getId();
		return WorldList.getWorld(Config.getIntOrAdd("client", "world", defaultWorld));
	}

	public static void saveWorld(WorldList world) {
		Config.setInt("client", "world", world.getId());
	}

	@Override
	public String toString() {
		String str = "World " + id;
		if (activity != null) {
			str = str + " - " + activity;
		}
		return str;
	}

}