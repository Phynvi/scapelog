package com.scapelog.client.model;

import java.util.List;

public final class User {

	private final String username;
	private final String token;
	private final List<UserGroup> groups;

	public User(String username, String token, List<UserGroup> groups) {
		this.username = username;
		this.token = token;
		this.groups = groups;
	}

	public User(String username, String token, String groupCSV) {
		this(username, token, UserGroup.parseGroups(groupCSV));
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

	public List<UserGroup> getGroups() {
		return groups;
	}

}