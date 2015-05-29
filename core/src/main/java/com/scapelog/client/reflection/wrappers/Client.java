package com.scapelog.client.reflection.wrappers;

import com.google.common.collect.Lists;
import com.scapelog.client.reflection.ReflectedFields;
import com.scapelog.client.reflection.ReflectionField;
import com.scapelog.client.reflection.Wrapper;

import java.util.List;

public final class Client extends Wrapper {

	private static final ReflectionField<Object[]> LOCAL_PLAYERS = new ReflectionField<>(ReflectedFields.LOCAL_PLAYERS, new Object[0]);

	protected Client() {

	}

	public static List<Player> getPlayers() {
		Object[] rawPlayers = getValue(LOCAL_PLAYERS);
		List<Player> players = Lists.newArrayList();
		for (Object rawPlayer : rawPlayers) {
			if (rawPlayer == null) {
				continue;
			}
			players.add(Player.create(rawPlayer));
		}
		return players;
	}

}