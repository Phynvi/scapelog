package com.scapelog.api;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "settings")
public class Setting {

	public Setting() {

	}

	@DatabaseField(generatedId = true)
	public int id;

	@DatabaseField(canBeNull = false, columnDefinition = "VARCHAR UNIQUE ON CONFLICT REPLACE")
	public String key;

	@DatabaseField(canBeNull = false)
	public String section;

	@DatabaseField
	public String value;

}