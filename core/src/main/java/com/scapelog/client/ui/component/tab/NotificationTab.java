package com.scapelog.client.ui.component.tab;

import com.google.common.collect.ImmutableList;
import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import com.scapelog.client.notification.NotificationManager;
import com.scapelog.client.notification.VosNotificationManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class NotificationTab extends IconTab {

	private final ImmutableList<NotificationManager> notificationManagers;

	private final VBox tabContent;

	public NotificationTab() {
		super(FontAwesomeIcon.BELL, "Notifications");

		ImmutableList.Builder<NotificationManager> builder = new ImmutableList.Builder<>();
		builder.add(
				new VosNotificationManager()
		);
		this.notificationManagers = builder.build();
		this.tabContent = getCachedContent();
	}

	@Override
	public Node getTabContent() {
		return tabContent;
	}

	private VBox getCachedContent() {
		VBox tabContent = new VBox(10);
		Components.setPadding(tabContent, 10);

		tabContent.getChildren().add(Components.createHeader("Notifications"));

		for (NotificationManager notificationManager : notificationManagers) {
			Region header = getHeader(notificationManager.getName(), getTabContent(), notificationManager.getPanelContent());
			tabContent.getChildren().add(header);
		}
		return tabContent;
	}

	protected final Region getHeader(String name, Node originalContent, Node newContent) {
		HBox pane = new HBox();

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Button settingsButton = Components.createIconButton(FontAwesomeIcon.COG, "16.0");
		settingsButton.setOnAction(e -> {
			BorderPane content = new BorderPane();
			content.setTop(getSettingsHeader(name, originalContent));
			content.setCenter(newContent);
			setContent(content);
		});

		Label label = new Label(name);
		label.setMaxHeight(Double.MAX_VALUE);
		pane.getChildren().addAll(label, spacer, settingsButton);
		return pane;
	}

	protected final Region getSettingsHeader(String name, Node originalContent) {
		HBox pane = new HBox(3);
		pane.setId("plugin-header");

		Button back = Components.createBorderedButton("Back");
		back.setOnAction(e -> NotificationTab.this.setContent(originalContent));

		Label label = new Label(name);
		label.setMaxHeight(Double.MAX_VALUE);
		pane.getChildren().addAll(label, Components.createSpacer(), back);
		return pane;
	}

}