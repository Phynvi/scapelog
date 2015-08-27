package com.scapelog.client.ui.component.tab;

import com.scapelog.api.ui.tab.IconTab;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public final class NotificationTab extends IconTab {

	public NotificationTab() {
		super(FontAwesomeIcon.BELL, "Notifications");
	}

	@Override
	public Node getTabContent() {
		return new BorderPane();
	}

}