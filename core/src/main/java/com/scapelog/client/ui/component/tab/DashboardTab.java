package com.scapelog.client.ui.component.tab;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.scapelog.api.ClientFeature;
import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.TimeUtils;
import com.scapelog.api.util.Utilities;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.model.VoiceOfSeren;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class DashboardTab extends IconTab {

	private final long startTime = System.currentTimeMillis();

	private final SimpleIntegerProperty onlinePlayersProperty = new SimpleIntegerProperty(0);

	public DashboardTab() {
		super(FontAwesomeIcon.HOME, "Dashboard");

		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), new EventHandler<ActionEvent>() {
			int tick = 0;
			@Override
			public void handle(ActionEvent event) {
				update(tick);

				tick++;
			}
		}), new KeyFrame(Duration.seconds(1)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private final Label runTimeLabel = new Label("Time running: 00:00:00");

	@Override
	public Node getTabContent() {
		VBox content = new VBox(10);
		Components.setPadding(content, 10);

		/* logo */
		ImageView img = new ImageView(new Image(DashboardTab.class.getResourceAsStream("/img/logo.png")));
		img.setId("logo");
		img.setPreserveRatio(true);
		BorderPane logoPane = new BorderPane(img);
		logoPane.setPrefWidth(content.getWidth());

		/* player counts */
		final Label playerCountLabel = new Label("Players online: N/A");
		onlinePlayersProperty.addListener((observable, oldValue, newValue) -> Platform.runLater(() -> playerCountLabel.setText("Players online: " + Utilities.format(newValue.longValue()))));

		String username = "";
		if (ScapeLog.getUser() != null && ScapeLog.getUser().getUsername() != null) {
			username = "Logged in as " + Utilities.capitalize(ScapeLog.getUser().getUsername());
		}

		ImageView voice1 = new ImageView();
		ImageView voice2 = new ImageView();
		updateVoice(voice1, voice2, VoiceOfSeren.getCurrentVoice().get());

		VoiceOfSeren.getCurrentVoice().addListener((observable, oldValue, newVoice) -> {
			updateVoice(voice1, voice2, newVoice);
		});

		VoiceOfSeren.getCurrentVoice().set(new VoiceOfSeren.Clan[]{VoiceOfSeren.Clan.AMLODD, VoiceOfSeren.Clan.TRAHAEARN});

		HBox statsBox = new HBox(10,
				new VBox(5,
						playerCountLabel,
						//new Label("ScapeLog online: TODO") //TODO: Implement
						new HBox(10, new Label("Voice of Seren:"), voice1, voice2)
				),
				Components.createSpacer(),
				new VBox(10,
						runTimeLabel,
						new Label(username)
				)
		);
		content.getChildren().addAll(logoPane, statsBox, Components.createSpacer(), Components.createSpacer());

		if (ScapeLog.isAgentEnabled()) {
			VBox featureHeader = Components.createHeader("Feature status", "Current status of the features that ScapeLog hooks into");
			Components.setPadding(featureHeader, 10, 0, 0, 0);

			VBox featureBox = new VBox(10);
			featureBox.setId("feature-statuses");
			for (ClientFeature feature : ClientFeature.values()) {
				// todo: remove when fixed
				if (feature == ClientFeature.GAME_MESSAGES || feature == ClientFeature.OPENGL) {
					continue;
				}
				HBox box = new HBox(10);

				Label explanationLabel = Components.createIconLabel(FontAwesomeIcon.QUESTION, "12");
				explanationLabel.setTooltip(new Tooltip(feature.getDescription()));

				Label statusLabel = new Label(feature.getStatus().getStatus());
				statusLabel.getStyleClass().addAll("status-label", feature.getStatus().getStyleClass());
				feature.statusProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
					statusLabel.setText(newValue.getStatus());

					statusLabel.getStyleClass().remove(oldValue.getStyleClass());
					statusLabel.getStyleClass().add(newValue.getStyleClass());
				}));

				box.getChildren().addAll(
						new Label(feature.getName()),
						Components.createSpacer(),
						statusLabel,
						explanationLabel
				);
				featureBox.getChildren().add(box);
			}
			content.getChildren().addAll(featureHeader, featureBox);
		}

		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		return scrollPane;
	}

	private void update(int tick) {
		// updateRates online player count every 2 minutes
		if (tick == 0 || tick % 120 == 0) {
			updateOnlineUsers();
		}
		updatePlaytime();
	}

	private void updatePlaytime() {
		long elapsed = System.currentTimeMillis() - startTime;
		String time = TimeUtils.formatHours(elapsed);
		runTimeLabel.setText("Time running: " + time);
	}

	private void updateOnlineUsers() {
		ScapeLog.getExecutor().submit(() -> {
			Webb webb = Webb.create();
			webb.setBaseUri("http://runescape.com");

			Response<String> response = webb.get("/player_count.js?varname=iPlayerCount&callback=jQuery000000000000000_0000000000").ensureSuccess().asString();
			String body = response.getBody();
			body = body.replaceAll(".*?\\(", "");
			body = body.replaceAll("\\);", "");
			try {
				int online = Integer.parseInt(body);
				onlinePlayersProperty.set(online);
			} catch (NumberFormatException e) {
				/**/
			}
		});
	}

	private void updateVoice(ImageView voice1, ImageView voice2, VoiceOfSeren.Clan[] clans) {
		if (clans.length != 2 || clans[0] == null || clans[1] == null) {
			return;
		}
		voice1.setImage(clans[0].getImage());
		voice2.setImage(clans[1].getImage());

		voice1.setSmooth(true);
		voice2.setSmooth(true);
	}

}