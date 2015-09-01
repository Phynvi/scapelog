package com.scapelog.client.ui.component.tab;

import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public final class NewsTab extends IconTab {

	public NewsTab() {
		super(FontAwesomeIcon.ENVELOPE, "News and updates");
	}

	@Override
	public Node getTabContent() {
		BorderPane content = new BorderPane();
		Components.setPadding(content, 10);

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
		insertNews(textArea, new String[] {
				"Updates #5 - 19 July 2015",
				" - You can now copy program output (\"diagnostics\") to your clipboard by doing the key combo CTRL+SHIFT+INSERT, ",
				"   that you may then deliver to the developers in case you have any problems",
				" - Popup windows should no longer appear behind the main window",
				" - Added window size presets in the settings tab in case you want to quickly resize the window for specific activities",
				" - Added capability to draw on the OpenGL surface (DirectX will be added at some point)",
		});
		insertNews(textArea, new String[] {
				"Updates #6 - 1 September 2015",
				" - Hid game messages and OpenGL features as they are manually disabled for now",
				" - Disabled the slayer plugin",
				" - Added player's clan and avatar view in the highscores plugin",
				" - Added current Voice of Seren in the dashboard tab",
				" - Added notifications for Voice of Seren change (disabled by default)",
				" - Updated some third party dependencies"
		});
	}

	private void insertNews(TextArea textArea, String[] lines) {
		String text = String.join("\n", lines);
		text += "\n\n";
		textArea.insertText(0, text);
	}

}