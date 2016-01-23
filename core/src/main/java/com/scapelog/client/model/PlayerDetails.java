package com.scapelog.client.model;

import com.scapelog.util.proguard.Keep;
import com.scapelog.util.proguard.KeepClassMemberNames;
import com.scapelog.util.proguard.KeepName;

@KeepName
@KeepClassMemberNames
public final class PlayerDetails {

	@Keep
	@KeepName
	private boolean isSuffix;

	@Keep
	@KeepName
	private boolean recruiting;

	@Keep
	@KeepName
	private String name;

	@Keep
	@KeepName
	private String clan;

	@Keep
	@KeepName
	private String title;

	public boolean isSuffix() {
		return isSuffix;
	}

	public boolean isRecruiting() {
		return recruiting;
	}

	public String getName() {
		return name;
	}

	public String getClan() {
		return clan;
	}

	public String getTitle() {
		return title;
	}

}