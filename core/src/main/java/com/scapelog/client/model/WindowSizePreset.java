package com.scapelog.client.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "window_presets")
public final class WindowSizePreset {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(canBeNull = true)
	private final String name;

	@DatabaseField
	private final int width;

	@DatabaseField
	private final int height;

	public WindowSizePreset() {
		this(null, -1, -1);
	}

	public WindowSizePreset(int width, int height) {
		this(null, width, height);
	}

	public WindowSizePreset(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean hasName() {
		return name != null && !name.isEmpty();
	}

	public boolean validSize() {
		return width != -1 && height != -1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (hasName()) {
			builder.append(name).append(" ");
		}
		if (width > 0 && height > 0) {
			if (hasName()) {
				builder.append("(");
			}
			builder.append(width).append("x").append(height);
			if (hasName()) {
				builder.append(")");
			}
		}
		return builder.toString();
	}

}