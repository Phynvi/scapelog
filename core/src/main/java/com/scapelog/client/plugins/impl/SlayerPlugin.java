package com.scapelog.client.plugins.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scapelog.api.event.EventListener;
import com.scapelog.api.event.impl.VariableEvent;
import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.plugin.TabMode;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.client.event.EventDispatcher;
import com.scapelog.client.reflection.wrappers.Settings;
import com.scapelog.client.ui.util.WebUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Optional;

public final class SlayerPlugin extends Plugin {
	private static final FontAwesomeIcon ICON = FontAwesomeIcon.CROSSHAIRS;

	private SlayerTask slayerTask = new SlayerTask(0, 0);

	private final ImmutableMap<Integer, SlayerCategory> slayerCategories;

	public SlayerPlugin() {
		super(TabMode.ON, SlayerPlugin.ICON, "Slayer", Optional.of("slayer-plugin"));
		this.slayerCategories = loadSlayerCategories();
	}

	@Override
	public void onStart() {
		EventDispatcher.registerListener(new EventListener<VariableEvent>(VariableEvent.class) {
			@Override
			public void eventExecuted(VariableEvent event) {
				int oldValue = event.getOldValue();
				int newValue = event.getNewValue();

				if (event.getId() == Settings.SLAYER_ASSIGNMENT_MOB_CATEGORY) {
					if (newValue != oldValue && slayerTask != null) {
						slayerTask.setCategoryId(newValue);
					}
				}

				if (event.getId() == Settings.SLAYER_ASSIGNMENT_KILLS_LEFT) {
					if (newValue == 0) {
						slayerTask.reset();

						// todo: completed task logic here
						return;
					}

					if (oldValue == 0 && newValue > 0) {
						int categoryId = Settings.get(Settings.SLAYER_ASSIGNMENT_MOB_CATEGORY);
						slayerTask.setInitialAmount(newValue);
						slayerTask.setKillsLeft(newValue);
						slayerTask.setCategoryId(categoryId);

						// todo: new task logic here
						return;
					}
					slayerTask.setKillsLeft(newValue);
				}
			}
		});

		boolean showButton = getBooleanOrAdd("show-button", true);
		if (!showButton) {
			hideButton();
		}

	}

	@Override
	public Region getContent() {
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(new Label("Task logging coming soon!"));
		return borderPane;
	}

	@Override
	public Region getButtonContent() {
		buttonIconPropertyProperty().set(SlayerPlugin.ICON);

		BorderPane pane = new BorderPane();
		Components.setPadding(pane, 0);

		Label killsLabel = new Label("N/A");
		killsLabel.setId("kills-label");
		pane.setCenter(killsLabel);

		Label taskLabel = new Label("No task");
		taskLabel.setId("task-label");
		taskLabel.setMaxWidth(Double.MAX_VALUE);
		taskLabel.setAlignment(Pos.CENTER);
		pane.setBottom(taskLabel);

		slayerTask.killsLeftProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> {
				if (newValue.intValue() == 0) {
					killsLabel.setText("N/A");
					taskLabel.setText("No task");
					return;
				}

				SlayerCategory category = getSlayerCategory(slayerTask.getCategoryId());

				killsLabel.setText(newValue + "/" + slayerTask.getInitialAmount());
				taskLabel.setText(category == null ? "N/A" : category.getName());
			});
		});

		return pane;
	}

	@Override
	public OpenTechnique getOpenTechnique() {
		return OpenTechnique.EXPANDED_BUTTON;
	}

	@Override
	public Region getSettingsContent() {
		VBox content = new VBox(10);
		Components.setPadding(content, 10);

		content.getChildren().addAll(SettingsUtils.createShowButtonSetting(getSectionName(), this));
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

	public SlayerCategory getSlayerCategory(int id) {
		return slayerCategories.get(id);
	}

	private ImmutableMap<Integer, SlayerCategory> loadSlayerCategories() {
		ImmutableMap.Builder<Integer, SlayerCategory> builder = new ImmutableMap.Builder<>();

		try {
			// todo: replace with rs-api when it's public
			String pageContent = WebUtils.readPage("http://services.runescape.com/m=itemdb_rs/bestiary/bestiary", "/slayerCatNames.json");
			Map<String, Integer> categories = new Gson().fromJson(pageContent, new TypeToken<Map<String, Integer>>() {
			}.getType());
			for (Map.Entry<String, Integer> categoryEntry : categories.entrySet()) {
				int id = categoryEntry.getValue();
				String name = categoryEntry.getKey();
				builder.put(id, new SlayerCategory(id, name));
			}
		} catch (Exception e) {
			/**/
		}
		return builder.build();
	}

	class SlayerCategory {

		private final int id;

		private final String name;

		public SlayerCategory(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

	}

	class SlayerTask {

		private int initialAmount;

		private final SimpleIntegerProperty killsLeft = new SimpleIntegerProperty(0);
		private final SimpleIntegerProperty category = new SimpleIntegerProperty(0);

		public SlayerTask(int killsLeft, int category) {
			this.killsLeft.set(killsLeft);
			this.category.set(category);
		}

		public int getCategoryId() {
			return category.get();
		}

		public int getKillsLeft() {
			return killsLeft.get();
		}

		public SimpleIntegerProperty killsLeftProperty() {
			return killsLeft;
		}

		public SimpleIntegerProperty categoryProperty() {
			return category;
		}

		public void setInitialAmount(int initialAmount) {
			this.initialAmount = initialAmount;
		}

		public int getInitialAmount() {
			return initialAmount;
		}

		public void setKillsLeft(int killsLeft) {
			this.killsLeft.set(killsLeft);
		}

		public void setCategoryId(int category) {
			this.category.set(category);
		}

		public void reset() {
			killsLeft.set(0);
			category.set(0);
			initialAmount = 0;
		}

	}

}