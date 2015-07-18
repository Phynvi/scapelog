package com.scapelog.api;

import com.scapelog.api.event.Event;
import com.scapelog.client.event.parser.EventParser;
import com.scapelog.client.event.parser.GameMessageParser;
import com.scapelog.client.event.parser.IdleResetParser;
import com.scapelog.client.event.parser.SkillEventParser;
import com.scapelog.client.event.parser.VariableEventParser;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigInteger;
import java.security.SecureRandom;

public enum ClientFeature {
	SKILLS(new SkillEventParser(), "Skills", "Skill updates"),
	GAME_MESSAGES(new GameMessageParser(), "Game messages", "Received game messages (does not include chat messages)"),
	IDLE_RESET(new IdleResetParser(), "Idle events", "Mouse and keyboard events that reset the idle timer"),
	VARIABLES(new VariableEventParser(), "Settings", "Many game settings, examples being accept aid, auto retaliate, chatbox settings and object states such as farming patches"),
	OPENGL(null, "OpenGL drawing", "Ability to draw on the screen while using the OpenGL display mode (currently for Linux only)");

	private final String name, description, identifier;
	private final EventParser eventParser;

	private SimpleObjectProperty<ClientFeatureStatus> status = new SimpleObjectProperty<>(ClientFeatureStatus.DISABLED);

	ClientFeature(EventParser eventParser, String name, String description) {
		this.eventParser = eventParser;
		this.identifier = generateRandom();
		this.name = name;
		this.description = description;
	}

	public static Event parseEvent(String identifier, String[] parts) {
		ClientFeature feature = getFeature(identifier);
		if (feature == null) {
			return null;
		}
		EventParser parser = feature.eventParser;
		if (parser != null) {
			return parser.parse(parts);
		}
		return null;
	}

	public static ClientFeature getFeature(String identifier) {
		for (ClientFeature clientFeature : values()) {
			if (clientFeature.identifier.equals(identifier)) {
				return clientFeature;
			}
		}
		return null;
	}

	private static String generateRandom() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getIdentifier() {
		return identifier;
	}

	public EventParser getEventParser() {
		return eventParser;
	}

	public ClientFeatureStatus getStatus() {
		return status.get();
	}

	public SimpleObjectProperty<ClientFeatureStatus> statusProperty() {
		return status;
	}

	public void setStatus(ClientFeatureStatus status) {
		this.status.set(status);
	}

}