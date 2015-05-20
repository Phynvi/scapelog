package com.scapelog.client.model;

import com.google.common.collect.Lists;

import java.util.List;

public enum UserGroup {
	FOUNDER(4),
	PLUGIN_DEVELOPER(10);

	private final int groupId;

	UserGroup(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}

	public static UserGroup getGroup(int id) {
		for (UserGroup group : values()) {
			if (group.getGroupId() == id) {
				return group;
			}
		}
		return null;
	}

	public static List<UserGroup> parseGroups(String csv) {
		List<UserGroup> groups = Lists.newArrayList();
		String[] parts = csv.split(",");
		for (String part : parts) {
			try {
				int groupId = Integer.parseInt(part);
				UserGroup group = getGroup(groupId);
				if (group != null) {
					groups.add(group);
				}
			} catch (Exception e) {
				/**/
			}
		}
		return groups;
	}

}