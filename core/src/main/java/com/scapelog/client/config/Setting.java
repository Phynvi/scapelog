package com.scapelog.client.config;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "settings")
public class Setting {

	Setting() {

	}

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(canBeNull = false, columnDefinition = "VARCHAR UNIQUE ON CONFLICT REPLACE")
	String key;

	@DatabaseField(canBeNull = false)
	String section;

	@DatabaseField
	String value;

}