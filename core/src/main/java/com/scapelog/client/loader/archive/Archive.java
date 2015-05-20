package com.scapelog.client.loader.archive;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * @author Sean
 */
public abstract class Archive<T> {

	/**
	 * The {@link Map} of the data within an {@link Archive}.
	 */
	public final Map<String, T> archivedData;

	/**
	 * Creates a new {@link Archive} with {@link Map} of archived data.
	 * @param archivedData The {@link Map} of archived data.
	 */
	public Archive(Map<String, T> archivedData) {
		this.archivedData = archivedData;
	}

	/**
	 * Creates a new {@link Archive}.
	 */
	public Archive() {
		this(Maps.newHashMap());
	}

	/**
	 * Gets a piece of data from the {@link Archive}.
	 * @param name The name of the piece of data in the archive.
	 * @return The piece of data.
	 */
	public T get(String name) {
		T data = archivedData.get(name);
		if (data != null) {
			return data;
		} else {
			throw new IllegalArgumentException(name + " doesn't exist in this archive.");
		}
	}

	/**
	 * Returns the {@code archivedData} as a {@link Collection}.
	 * @return The {@link Collection}.
	 */
	public Collection<T> toCollection() {
		return archivedData.values();
	}

	/**
	 * Adds data into the {@link Map} of {@code archivedData}.
	 * @param name The name given to the data.
	 * @param data The data to add.
	 */
	public void updateArchivedData(String name, T data) {
		archivedData.put(name, data);
	}

	/**
	 * Gets the {@link Map} of {@code archivedData}.
	 * @return the archivedData.
	 */
	public Map<String, T> getArchivedData() {
		return archivedData;
	}

}