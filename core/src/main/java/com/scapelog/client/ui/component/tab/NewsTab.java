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
		insertNews(textArea, new String[] {
				"Updates #2 - 12 June 2015",
				" - Plugin buttons on title bar are now pannable if they take up too much space relative to the window width",
				" - Added a testing mode ('-testing') to run the testing version of ScapeLog",
				" - Loader should now properly find the portable Java's executable on OSX"
		});
		insertNews(textArea, new String[] {
				"Updates #3 - 20 June 2015",
				" - Loader will now download the portable Java package again in case it's needed but not present",
				" - Portable Java version updated from update 20 to update 45",
				" - Repositioned the window control buttons",
				" - Slayer tasks should now update properly when switching characters",
				" - Skill tracker should no longer show that you've gained 0 xp in a skill",
				" - Numeric fields that are controlled with + and - buttons are now more pleasant to cycle through",
				" - Popup menu can now be detached and dragged by the tabs area",
				" - Attemps to improve 2D performance"
		});
		insertNews(textArea, new String[] {
				"Updates #4 - 28 June 2015",
				" - Added a way to copy problem diagnostics to clipboard (CTRL+SHIFT+INSERT to trigger)",
				" - Detached popup windows now show their \"detachness\" more clearly"
		});
	}

	private void insertNews(TextArea textArea, String[] lines) {
		String text = String.join("\n", lines);
		text += "\n\n";
		textArea.insertText(0, text);
	}

}