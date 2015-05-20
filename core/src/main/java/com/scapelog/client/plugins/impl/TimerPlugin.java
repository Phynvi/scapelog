package com.scapelog.client.plugins.impl;

import com.scapelog.api.event.EventListener;
import com.scapelog.api.event.impl.IdleResetEvent;
import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.plugin.TabMode;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.api.util.TimeUtils;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Optional;

// todo: do something about the settings
// todo: persisted timers
public final class TimerPlugin extends Plugin {
	private static final AwesomeIcon icon = AwesomeIcon.CLOCK_ALT;

	private final Label timerLabel = new Label("00:00");
	private final Label sessionLabel = new Label("00:00:00");

	private long logoutTime = System.currentTimeMillis();
	private long sessionStart = System.currentTimeMillis();

	private final ObservableList<Timer> timerList = FXCollections.observableArrayList();
	private int timerCounter = 0;

	public TimerPlugin() {
		super(TabMode.ON, icon, "Timers", Optional.of("timers"));
	}

	@Override
	public void onStart() {
		resetTimer();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), event -> {
			long diff = logoutTime - System.currentTimeMillis();
			if (diff < 0) {
				diff = 0;
			}
			String elapsed = TimeUtils.formatMinutes(diff);
			timerLabel.setText(elapsed);

			diff = System.currentTimeMillis() - sessionStart;
			elapsed = TimeUtils.formatHours(diff);
			sessionLabel.setText(elapsed);

			timerList.forEach(TimerPlugin.Timer::update);
		}), new KeyFrame(Duration.seconds(1)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		registerListener(new EventListener<IdleResetEvent>(IdleResetEvent.class) {
			@Override
			public void eventExecuted(IdleResetEvent event) {
				resetTimer();
			}
		});
	}

	@Override
	public Region getContent() {
		VBox content = new VBox(5);
		content.setPadding(new Insets(10, 10, 10, 10));

		VBox timerBox = new VBox(10, getTimerNode());

		content.getChildren().addAll(
				Components.createBox("Idle logout", timerLabel),
				Components.createBox("Session age", sessionLabel),
				Components.createSpacer(),
				Components.createHeader("Timers", "Add timers for various activities"),

				timerBox
		);

		timerList.addListener((ListChangeListener<Timer>) change -> {
			change.next();
			if (change.wasAdded() || change.wasRemoved()) {
				timerBox.getChildren().remove(timerBox.getChildren().size() - 1);

				if (change.wasAdded()) {
					timerBox.getChildren().addAll(change.getAddedSubList());
				}
				if (change.wasRemoved()) {
					timerBox.getChildren().removeAll(change.getRemoved());
				}

				timerBox.getChildren().add(getTimerNode());
			}
		});


		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

	private Parent getTimerNode() {
		HBox box = new HBox(5);

		TextField nameField = new TextField();
		nameField.setPromptText("Name");

		TextField durationField = new TextField();
		durationField.setPromptText("hh:mm:ss");

		Button start = Components.createBorderedButton("Start");
		start.setOnAction(e -> {
			String rawDuration = durationField.getText();

			if (rawDuration == null || rawDuration.isEmpty()) {
				durationField.requestFocus();
				durationField.selectAll();
				return;
			}

			timerList.add(new Timer(nameField.getText(), getDuration(rawDuration)));
		});

		box.getChildren().addAll(nameField, Components.createSpacer(), durationField, start);
		return box;
	}

	private long getDuration(String rawDuration) {
		long duration = 0;
		String[] parts = rawDuration.split(":");
		if (parts.length == 0) {
			return duration;
		}

		int step = 0;
		for (int i = parts.length - 1; i >= 0; i--) {
			String part = parts[i];
			int value;
			try {
				value = Integer.parseInt(part);
			} catch (NumberFormatException e) {
				value = 0;
			}
			if (step == 0) {
				duration += value * 1000;
			} else if (step == 1) {
				duration += value * (60 * 1000);
			} else if (step == 2) {
				duration += value * (60 * 60 * 1000);
			}
			step++;
		}
		return duration;
	}

	@Override
	public OpenTechnique getOpenTechnique() {
		return OpenTechnique.EXPANDED_BUTTON;
	}

	@Override
	public Region getButtonContent() {
		buttonIconPropertyProperty().set(icon);

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(0));

		Label timerLabel = new Label();
		timerLabel.setId("idle-label");
		timerLabel.textProperty().bind(this.timerLabel.textProperty());
		pane.setCenter(timerLabel);

		Label sessionLabel = new Label();
		sessionLabel.setId("session-label");
		sessionLabel.textProperty().bind(this.sessionLabel.textProperty());
		sessionLabel.setMaxWidth(Double.MAX_VALUE);
		sessionLabel.setAlignment(Pos.CENTER);
		pane.setBottom(sessionLabel);

		Runnable bindDefault = () -> {
			timerLabel.textProperty().unbind();
			timerLabel.textProperty().bind(this.timerLabel.textProperty());

			sessionLabel.textProperty().unbind();
			sessionLabel.textProperty().bind(this.sessionLabel.textProperty());
		};

		SimpleObjectProperty<Timer> currentTimer = new SimpleObjectProperty<>();

		pane.setOnMousePressed(e -> {
			Timer timer = currentTimer.get();
			int index = -1;
			if (timer != null) {
				index = timerList.indexOf(timer);
			}

			if (timerList.isEmpty()) {
				return;
			}
			if (e.isPrimaryButtonDown()) {
				index++;
			}
			/*if (e.isSecondaryButtonDown()) {
				index--;
			}*/
			if (index >= timerList.size()) {
				index = 0;
				bindDefault.run();
				currentTimer.set(null);
				return;
			}
			if (index < 0) {
				index = timerList.size() - 1;
			}
			Timer nextTimer = timerList.get(index);

			timerLabel.textProperty().unbind();
			timerLabel.setText(nextTimer.label);
			sessionLabel.textProperty().unbind();
			sessionLabel.textProperty().bind(nextTimer.timerLabel.textProperty());

			currentTimer.set(nextTimer);
		});
		timerList.addListener((ListChangeListener<Timer>) change -> {
			change.next();
			if (timerList.isEmpty()) {
				bindDefault.run();
			}
		});
		return pane;
	}

	@Override
	public Region getSettingsContent() {
		VBox content = new VBox(10);
		content.setPadding(new Insets(10, 10, 10, 10));

		content.getChildren().addAll(SettingsUtils.createShowButtonSetting(getSectionName(), this));
		return content;
	}

	private void resetTimer() {
		logoutTime = System.currentTimeMillis() + 1000 * 60 * 5 + 800;
	}

	class Timer extends HBox {

		private long startTime;
		private boolean paused;
		private long pauseStart;

		private final long duration;
		private final String label;
		private final Label timerLabel = new Label("00:00:00");

		public Timer(String label, long duration) {
			super(5);
			this.duration = duration;
			this.startTime = System.currentTimeMillis();

			Button reset = Components.createIconButton(AwesomeIcon.REFRESH, "13");
			Button remove = Components.createIconButton(AwesomeIcon.REMOVE, "13");
			Button pause = Components.createIconButton(AwesomeIcon.PAUSE, "13");

			reset.setOnAction(e -> startTime = System.currentTimeMillis());
			remove.setOnAction(e -> timerList.remove(this));
			pause.setOnAction(e -> {
				paused = !paused;
				pause.setGraphic(AwesomeDude.createIconLabel(paused ? AwesomeIcon.PLAY : AwesomeIcon.PAUSE, "13"));

				if (paused) {
					pauseStart = System.currentTimeMillis();
				} else {
					startTime += System.currentTimeMillis() - pauseStart;
				}
			});

			if (label == null || label.isEmpty()) {
				timerCounter++;
				label = "Timer " + timerCounter;
			}
			this.label = label;
			getChildren().addAll(new Label(label), Components.createSpacer(), timerLabel, pause, reset, remove);
		}

		public void update() {
			if (paused) {
				return;
			}
			long diff = (startTime + duration) - System.currentTimeMillis();
			if (diff < 0) {
				diff = 0;
			}
			String elapsed = TimeUtils.formatHours(diff);
			timerLabel.setText(elapsed);
		}

	}

}