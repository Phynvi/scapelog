package com.scapelog.client.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.scapelog.api.ui.NumberTextField;
import com.scapelog.api.ui.Utils;
import com.scapelog.api.util.Components;
import com.scapelog.client.config.Config;
import com.scapelog.client.ui.ScapeFrame;
import com.scapelog.client.ui.ScapeStage;
import com.scapelog.client.ui.component.TitleBar;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public final class WindowSizes {
	private static final long PRESET_LIMIT = 5;

	public static final String MANAGE_PRESETS = "Manage presets...";
	public static final WindowSizePreset EMPTY_PRESET = new WindowSizePreset("", -1, -1);
	private static final Stage popupWindow;

	private static ObservableList<WindowSizePreset> fullPresetList = FXCollections.observableArrayList(EMPTY_PRESET, new WindowSizePreset(MANAGE_PRESETS, 0, 0));
	private static ObservableList<WindowSizePreset> customPresetList = FXCollections.observableArrayList();

	private static ScapeFrame scapeFrame;
	private static Button saveCurrentButton;
	private static Label messageLabel;

	static {
		try {
			Dao<WindowSizePreset, String> presetDao = DaoManager.createDao(Config.getConnectionSource(), WindowSizePreset.class);
			presetDao.setObjectCache(true);

			customPresetList.addListener((ListChangeListener<WindowSizePreset>) c -> {
				while(c.next()) {
					try {
						presetDao.callBatchTasks(() -> {
							if (c.wasAdded()) {
								for (WindowSizePreset preset : c.getAddedSubList()) {
									int index = fullPresetList.size() - 1;
									if (index < 0) {
										index = 0;
									}
									fullPresetList.add(index, preset);
									presetDao.createOrUpdate(preset);
								}
							}
							if (c.wasRemoved()) {
								for (WindowSizePreset preset : c.getRemoved()) {
									fullPresetList.remove(preset);
									presetDao.delete(preset);
								}
							}
							return null;
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// fetch the first 5 presets
			// todo: lift limits for paying users
			List<WindowSizePreset> presets = presetDao.queryBuilder().limit(PRESET_LIMIT).query();
			for (WindowSizePreset preset : presets) {
				if (!preset.hasName() && preset.getWidth() <= 0 && preset.getHeight() <= 0) {
					presetDao.delete(preset);
					continue;
				}
				customPresetList.add(preset);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		popupWindow = createWindow();
	}

	public static ObservableList<WindowSizePreset> getPresets() {
		return fullPresetList;
	}

	public static void managePresets() {
		Platform.runLater(() -> {
			popupWindow.show();
			Utils.centerToScreen(popupWindow);
		});
	}

	private static Stage createWindow() {
		ListView<WindowSizePreset> presetListView = new ListView<>(customPresetList);

		presetListView.setCellFactory(lv -> {
			ListCell<WindowSizePreset> cell = new ListCell<>();

			ContextMenu contextMenu = new ContextMenu();
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction(e -> {
				WindowSizePreset item = cell.getItem();
				lv.getItems().remove(item);
				customPresetList.remove(item);
			});
			contextMenu.getItems().addAll(deleteItem);

			cell.emptyProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					cell.setText(null);
					cell.setContextMenu(null);
				} else {
					cell.setText(cell.getItem().toString());
					cell.setContextMenu(contextMenu);
				}
			});
			return cell;
		});

		BorderPane pane = new BorderPane();
		Components.setPadding(pane, 0, 1, 0, 1);

		TextField nameField = new TextField();
		NumberTextField widthField = new NumberTextField();
		NumberTextField heightField = new NumberTextField();
		nameField.setPrefWidth(90);
		widthField.setPrefWidth(65);
		heightField.setPrefWidth(65);

		nameField.clear();
		nameField.setPromptText("Name");
		widthField.clear();
		widthField.setPromptText("Width");
		heightField.clear();
		heightField.setPromptText("Height");

		saveCurrentButton = Components.createBorderedButton("Save current size");
		saveCurrentButton.setDisable(true);
		saveCurrentButton.setOnAction(e -> {
			if (scapeFrame == null) {
				return;
			}
			String name = nameField.getText();
			int width = scapeFrame.getWidth();
			int height = scapeFrame.getHeight();
			save(name, width, height, nameField, widthField, heightField);
		});

		Button saveButton = Components.createBorderedButton("Save");
		saveButton.setOnAction(e -> {
			String name = nameField.getText();
			int width = widthField.getNumber().intValue();
			int height = heightField.getNumber().intValue();
			save(name, width, height, nameField, widthField, heightField);
		});

		HBox buttonPane = new HBox(5, nameField, widthField, heightField, Components.createSpacer(), saveCurrentButton, saveButton);
		Components.setPadding(buttonPane, 5);

		pane.setCenter(presetListView);
		pane.setBottom(buttonPane);

		ScapeStage scapeStage = Utils.createStage(pane, 450, 250);
		Stage stage = scapeStage.getStage();
		stage.setTitle("Presets");

		HBox titleBarContent = scapeStage.getTitleBar().getContent();

		messageLabel = new Label();
		messageLabel.setPrefHeight(TitleBar.HEIGHT - 2);
		titleBarContent.getChildren().add(messageLabel);

		return stage;
	}

	private static void save(String name, int width, int height, TextField... fields) {
		if (width <= 0 || height <= 0) {
			messageLabel.setText("Width and height need to be higher than 0");
			resetFields(fields);
			return;
		}

		if (customPresetList.size() >= PRESET_LIMIT) {
			messageLabel.setText("You have reached your limit of " + PRESET_LIMIT + " presets");
			resetFields(fields);
			return;
		}

		customPresetList.add(new WindowSizePreset(name, width, height));
		resetFields(fields);
		messageLabel.setText("");
	}

	private static void resetFields(TextField... fields) {
		for (TextField field : fields) {
			if (field == null) {
				continue;
			}
			field.clear();
		}
	}


	public static void setFrame(ScapeFrame scapeFrame) {
		if (WindowSizes.scapeFrame != null) {
			return;
		}
		WindowSizes.scapeFrame = scapeFrame;
		saveCurrentButton.setDisable(false);
	}
}