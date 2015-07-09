package com.scapelog.client.ui.component.tab;

import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.client.config.ClientConfigKeys;
import com.scapelog.client.config.Config;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.impl.ClientResizeEvent;
import com.scapelog.client.model.Language;
import com.scapelog.client.model.WindowSizes;
import com.scapelog.client.ui.UserInterface;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

public final class SettingsTab extends IconTab {

	public SettingsTab() {
		super(AwesomeIcon.COGS, "Settings");
	}

	@Override
	public Node getTabContent() {
		VBox content = new VBox(10);
		Components.setPadding(content, 10);

		ObservableList<Region> nodes = FXCollections.observableArrayList(
				Components.createHeader("ScapeLog settings", "Settings for the ScapeLog client"),
				SettingsUtils.createSpinnerSetting("Border radius", 1, 5, UserInterface.getBorderRadius(), (observable, oldValue, newValue) -> {
					int oldRadius = oldValue.intValue();
					int borderRadius = newValue.intValue();
					if (borderRadius != oldRadius) {
						UserInterface.borderRadiusProperty().set(borderRadius);
					}
					Config.setInt(ClientConfigKeys.SECTION_NAME, ClientConfigKeys.BORDER_RADIUS, borderRadius);
				}),
				SettingsUtils.createComboBoxSetting("Window size preset",
						WindowSizes.getPresets(),
						Optional.of((double) 175),
						WindowSizes.EMPTY_PRESET,
						(box, item) -> {
							if (item == null || !item.validSize()) {
								return;
							}
							if (item.getName() != null && item.getName().equals(WindowSizes.MANAGE_PRESETS)) {
								WindowSizes.managePresets();
							} else {
								ClientEventDispatcher.fireEvent(new ClientResizeEvent(item.getWidth(), item.getHeight()));
							}
							box.getSelectionModel().selectFirst();
						}
				),

				Components.createSpacer(),
				Components.createHeader("RuneScape settings", "Settings for the RuneScape client"),

				SettingsUtils.createCheckBoxSetting("Disable modifications", selected -> Config.setBoolean("client", "disable_modifications", selected), Config.getBooleanOrAdd("client", "disable_modifications", false)),
				SettingsUtils.createComboBoxSetting("Default language",
						Language.asList(),
						Optional.of((double) 175),
						Language.getSavedLanguage(),
						(box, item) -> Language.saveLanguage(item)
				)/*,
				SettingsUtils.createComboBoxSetting("Default world",
						WorldList.asList(),
						Optional.of((double) 175),
						WorldList.getSavedWorld(),
						new ComboBoxSelectEvent<WorldList>() {
							@Override
							public void selected(WorldList world) {
								WorldList.saveWorld(world);
							}
						}
				)*/
		);

		content.getChildren().addAll(nodes);

		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

}