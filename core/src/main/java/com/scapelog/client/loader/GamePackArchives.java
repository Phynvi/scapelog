package com.scapelog.client.loader;

import com.scapelog.client.loader.archive.JarArchive;

public final class GamePackArchives {

	private final JarArchive gamepackArchive, clientArchive;

	public GamePackArchives(JarArchive gamepackArchive, JarArchive clientArchive) {
		this.gamepackArchive = gamepackArchive;
		this.clientArchive = clientArchive;
	}

	public JarArchive getGamepackArchive() {
		return gamepackArchive;
	}

	public JarArchive getClientArchive() {
		return clientArchive;
	}

}