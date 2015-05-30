package com.scapelog.client.ui.component.tab;

import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public final class NewsTab extends IconTab {

	public NewsTab() {
		super(AwesomeIcon.ENVELOPE, "News and updates");
	}

	@Override
	public Node getTabContent() {
		BorderPane content = new BorderPane();
		content.setPadding(new Insets(10, 10, 10, 10));

		TextArea newsArea = new TextArea();
		addNews(newsArea);

		content.setTop(Components.createHeader("News and updates", "Latest ScapeLog news and updates"));
		content.setCenter(newsArea);
		return content;

	}

	// todo: eventually load these from the web
	private void addNews(TextArea textArea) {
		insertNews(textArea, new String[] {
				"Updates #1 - 30 May 2015",
				" - Loading screen made to look more appealing",
				" - Replaced the system specific font ",
				" - Highscores plugin can now autocomplete local players' usernames",
				" - RS config loader will now attempt random worlds if the requests timeout",
				" - Added an initial slayer plugin",
				" - Replaced INI config with SQLite to allow better local data persistence (your old preferences have reset, sorry about that!)",
				" - Added this news tab",
				"and lots more!"
		});
	}

	private void insertNews(TextArea textArea, String[] lines) {
		String text = String.join("\n", lines);
		text += "\n\n";
		textArea.insertText(0, text);
	}

}