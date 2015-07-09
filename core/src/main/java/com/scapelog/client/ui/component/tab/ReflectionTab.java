package com.scapelog.client.ui.component.tab;

import com.scapelog.api.ClassStore;
import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import com.scapelog.client.reflection.wrappers.Client;
import com.scapelog.client.reflection.wrappers.Player;
import com.scapelog.client.util.Debug;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public final class ReflectionTab extends IconTab {

	public ReflectionTab() {
		super(AwesomeIcon.CUBE, "Reflection testing");
	}

	@Override
	public Region getTabContent() {
		VBox content = new VBox(5);
		Components.setPadding(content, 10);

		Label loadedClasses = new Label("Loaded classes: 0");
		ClassStore.addListener(change -> Platform.runLater(() -> loadedClasses.setText("Loaded classes: " + ClassStore.getClassCount())));

		Button playersButton = Components.createBorderedButton("Players");
		playersButton.setOnAction(e -> {
			List<Player> players = Client.getPlayers();
			players.forEach(Debug::println);
		});

		Button fieldsButton = Components.createBorderedButton("Fields");
		fieldsButton.setOnAction(e -> {
			Optional<Class<?>> client = ClassStore.getClass("client");
			if (!client.isPresent()) {
				Debug.println("class 'client' not loaded");
				return;
			}

			client.ifPresent(clazz -> {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					try {
						Debug.println("%s, %s, %s", field.getName(), field.getType(), field.get(client));
					} catch (IllegalAccessException e1) {
						/**/
					}
				}
			});
		});

		content.getChildren().addAll(loadedClasses, playersButton, fieldsButton);
		return content;
	}

}