package com.scapelog.client.plugins.impl;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.common.collect.Maps;
import com.scapelog.api.model.Activities;
import com.scapelog.api.model.SkillSet;
import com.scapelog.api.plugin.ButtonPlugin;
import com.scapelog.api.plugin.TabMode;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.Utilities;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.reflection.wrappers.Client;
import com.scapelog.client.reflection.wrappers.Player;
import com.scapelog.client.ui.component.AutoCompleteTextField;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HighscoresPlugin extends ButtonPlugin {

	private final Map<Integer, SkillNode> skillNodes = Maps.newHashMap();
	private final Map<Integer, SkillNode> activityNodes = Maps.newHashMap();

	private final SkillNode overallNode = new SkillNode(Pos.CENTER, 90, true, "Overall");
	private final SkillNode combatNode = new SkillNode(Pos.CENTER, 91, true, "Combat");

	private final Label skillNameLabel = new Label("");
	private final Label rankLabel = new Label("");
	private final Label experienceLabel = new Label("");

	private Region content;

	public HighscoresPlugin() {
		super(TabMode.OFF, AwesomeIcon.TROPHY, "Highscores");
	}

	@Override
	public void onStart() {
		content = getPluginContent();
	}

	@Override
	public Region getContent() {
		return content;
	}

	@Override
	public Region getButtonContent() {
		return content;
	}

	public Region getPluginContent() {
		VBox content = new VBox(5);
		content.setId("highscores");
		Components.setPadding(content, 10, 0, 10, 0);
		content.setMinWidth(200);
		content.setMinHeight(300);

		AutoCompleteTextField searchField = new AutoCompleteTextField();
		HBox.setHgrow(searchField, Priority.ALWAYS);
		Button search = Components.createBorderedButton("Search");

		searchField.setOnFocus(items -> {
			List<Player> players = Client.getPlayers();
			items.addAll(players.stream().map(Player::getName).collect(Collectors.toList()));
		});

		EventHandler<ActionEvent> eventHandler = e -> {
			String username = searchField.getText();
			getStats(username, searchField, search);
		};
		search.setOnAction(eventHandler);
		searchField.setOnAction(eventHandler);

		HBox topBox = new HBox(10, searchField, search);
		Components.setPadding(topBox, 0, 10, 0, 10);

		VBox hoverBox = new VBox();
		hoverBox.getChildren().addAll(skillNameLabel, rankLabel, experienceLabel);
		Components.setPadding(hoverBox, 0, 10, 0, 10);

		content.getChildren().addAll(topBox, getMainContent(), hoverBox);
		return content;
	}

	private BorderPane getMainContent() {
		BorderPane grids = new BorderPane();

		int verticalGap = 3;
		int horizontalGap = 3;
		int cellWidth = 60;
		int activityCellWidth = 100;
		int columns = 3;
		int rows = 9;

		VBox skillsBox = new VBox(5);
		Components.setPadding(skillsBox, 0, 5, 0, 5);
		skillsBox.setMaxWidth(cellWidth * columns);

		TilePane skillsPane = new TilePane();
		skillsPane.setId("skills");
		Components.setPadding(skillsPane, 5, 5, 5, 0);
		skillsPane.setHgap(horizontalGap);
		skillsPane.setVgap(verticalGap);
		skillsPane.setPrefColumns(columns);
		skillsPane.setPrefRows(rows);
		skillsPane.setOrientation(Orientation.VERTICAL);

		addColumn(skillsPane, SkillSet.ATTACK, SkillSet.STRENGTH, SkillSet.DEFENCE, SkillSet.RANGED, SkillSet.PRAYER, SkillSet.MAGIC, SkillSet.RUNECRAFTING, SkillSet.CONSTRUCTION, SkillSet.DUNGEONEERING);
		addColumn(skillsPane, SkillSet.CONSTITUTION, SkillSet.AGILITY, SkillSet.HERBLORE, SkillSet.THIEVING, SkillSet.CRAFTING, SkillSet.FLETCHING, SkillSet.SLAYER, SkillSet.HUNTER, SkillSet.DIVINATION);
		addColumn(skillsPane, SkillSet.MINING, SkillSet.SMITHING, SkillSet.FISHING, SkillSet.COOKING, SkillSet.FIREMAKING, SkillSet.WOODCUTTING, SkillSet.FARMING, SkillSet.SUMMONING);

		TilePane additionalStats = new TilePane();
		Components.setPadding(additionalStats, 0, 0, 5, 0);
		additionalStats.setId("additional");
		additionalStats.getChildren().addAll(overallNode, Components.createSpacer(), combatNode);

		skillsBox.getChildren().addAll(skillsPane, additionalStats);

		VBox activitiesBox = new VBox(5);
		activitiesBox.setMaxWidth(activityCellWidth * 2);

		TilePane activitiesPane = new TilePane();
		Components.setPadding(activitiesPane, 5, 5, 5, 10);
		activitiesPane.setHgap(horizontalGap);
		activitiesPane.setVgap(verticalGap);
		activitiesPane.setOrientation(Orientation.VERTICAL);
		activitiesPane.setPrefRows(rows);

		Activities[] activities = Activities.values();
		for (int i = 0; i < activities.length; i++) {
			Activities activity = activities[i];

			SkillNode node = new SkillNode(Pos.CENTER_LEFT, i, false, activity.getName());
			activitiesPane.getChildren().add(node);

			activityNodes.put(i, node);
		}
		activitiesBox.getChildren().addAll(activitiesPane);

		grids.setCenter(skillsBox);
		grids.setRight(activitiesBox);
		return grids;
	}

	private void addColumn(TilePane grid, SkillSet... skills) {
		for (SkillSet skill : skills) {
			SkillNode node = new SkillNode(Pos.CENTER, skill.getId(), true);
			grid.getChildren().add(node);
			skillNodes.put(skill.getId(), node);
		}
	}

	private void getStats(String username, TextField searchField, Button search) {
		Webb webb = Webb.create();
		webb.setBaseUri("http://hiscore.runescape.com");
		webb.setFollowRedirects(true);
		webb.setDefaultHeader(Webb.HDR_USER_AGENT, "scapelog/1.0");

		searchField.setDisable(true);
		search.setDisable(true);
		overallNode.levelLabel.setText("-");
		combatNode.levelLabel.setText("-");
		for (SkillNode skillNode : skillNodes.values()) {
			skillNode.levelLabel.setText("-");
		}
		for (SkillNode skillNode : activityNodes.values()) {
			skillNode.levelLabel.setText("-");
		}

		ScapeLog.getExecutor().submit(() -> {
			String cleanedUsername = username.replaceAll("\\s", "+");
			cleanedUsername = cleanedUsername.toLowerCase();

			Response<String> response = webb.get("/index_lite.ws").param("player", cleanedUsername).asString();
			String result = response.getBody();

			Platform.runLater(() -> {
				if (response.getStatusCode() == 200 && result != null) {
					String[] lines = result.split("\\n");
					for (int i = 0; i < lines.length; i++) {
						String[] parts = lines[i].split(",");
						int rank = Integer.parseInt(parts[0]);
						int level = Integer.parseInt(parts[1]);
						long experience = 0;
						if (parts.length >= 3) {
							experience = Long.parseLong(parts[2]);
						}

						if (i == 0) {
							overallNode.update(rank, level, experience);
						} else if (i <= skillNodes.size() + 1) {
							int skillId = i - 1;
							SkillNode node = skillNodes.get(skillId);
							if (node == null) {
								continue;
							}
							if (skillId == SkillSet.CONSTITUTION.getId() && level < 10) {
								level = 10;
							}
							node.update(rank, level, experience);
						} else {
							int activityId = i - skillNodes.size() - 2;
							if (activityId >= 0 && activityId < activityNodes.size()) {
								SkillNode node = activityNodes.get(activityId);
								if (node == null) {
									continue;
								}
								node.update(rank, level, level);
							}
						}
					}
					combatNode.update(-1, getCombatLevel(), getCombatXP());
				} else {
					String message = "Player search failed";
					if (response.getStatusCode() == 404) {
						message = "Player '" + username + "' was not found";
					}
					rankLabel.setText(message);
				}

				search.setDisable(false);
				searchField.setDisable(false);
				searchField.requestFocus();
				searchField.selectAll();
			});
		});
	}

	private int getCombatLevel() {
		int attack = getLevel(SkillSet.ATTACK);
		int strength = getLevel(SkillSet.STRENGTH);
		int defence = getLevel(SkillSet.DEFENCE);
		int constitution = getLevel(SkillSet.CONSTITUTION);
		int ranged = getLevel(SkillSet.RANGED);
		int prayer = getLevel(SkillSet.PRAYER);
		int magic = getLevel(SkillSet.MAGIC);
		int summoning = getLevel(SkillSet.SUMMONING);
		return (int) (Math.floor((Math.max(Math.max(attack + strength, magic * 2), ranged * 2) * 13 / 10) + defence + constitution + (prayer / 2) + (summoning / 2)) / 4);
	}

	private long getCombatXP() {
		long xp = 0;
		xp += getExperience(SkillSet.ATTACK);
		xp += getExperience(SkillSet.STRENGTH);
		xp += getExperience(SkillSet.DEFENCE);
		xp += getExperience(SkillSet.CONSTITUTION);
		xp += getExperience(SkillSet.RANGED);
		xp += getExperience(SkillSet.PRAYER);
		xp += getExperience(SkillSet.MAGIC);
		xp += getExperience(SkillSet.SUMMONING);
		return xp;
	}

	private int getLevel(SkillSet skill) {
		SkillNode node = skillNodes.get(skill.getId());
		if (node != null) {
			return node.getLevel();
		}
		return 1;
	}

	private long getExperience(SkillSet skill) {
		SkillNode node = skillNodes.get(skill.getId());
		if (node != null) {
			return node.getExperience();
		}
		return 0;
	}

	class SkillNode extends HBox {

		private final Label levelLabel;
		private final HBox imageBox;

		private int rank, level;
		private long experience;

		public SkillNode(Pos alignment, int skillId, boolean isSkill) {
			this(alignment, isSkill, SkillSet.getName(skillId), isSkill ? "/img/skills/" + skillId + ".png" : "/img/activities/" + skillId + ".png");
		}

		public SkillNode(Pos alignment, int skillId, boolean isSkill, String name) {
			this(alignment, isSkill, name, isSkill ? "/img/skills/" + skillId + ".png" : "/img/activities/" + skillId + ".png");
		}

		public SkillNode(Pos alignment, boolean isSkill, String name, String imagePath) {
			super(5);
			setAlignment(alignment);

			double width = isSkill ? 70.0 : 100.0;
			setMinWidth(width);
			setMaxWidth(width);
			setPrefWidth(width);

			levelLabel = Components.createLabel("-");
			levelLabel.setMinWidth(25.0);
			levelLabel.setMaxWidth(120.0);
			levelLabel.setMaxHeight(Double.MAX_VALUE);
			levelLabel.setAlignment(Pos.CENTER);

			ImageView imageView = new ImageView();
			try {
				Image img = new Image(SkillTrackerPlugin.class.getResourceAsStream(imagePath));
				imageView.setImage(img);
			} catch (Exception e) {
				/**/
			}
			imageView.setPreserveRatio(true);
			imageView.setFitWidth(25);
			imageView.setFitHeight(25);

			imageBox = new HBox(imageView);
			imageBox.setAlignment(Pos.CENTER);
			imageBox.setMinWidth(25.0);
			imageBox.setMinHeight(25.0);
			imageBox.setMaxWidth(25.0);
			imageBox.setMaxHeight(25.0);

			setOnMouseEntered(e -> {
				skillNameLabel.setText(name);
				rankLabel.setText("Rank: " + (rank == -1 ? "N/A" : Utilities.format(rank)));
				experienceLabel.setText((isSkill ? "Experience: " : "Score: ") + (experience == -1 ? "N/A" : Utilities.format(experience)));
			});
			setOnMouseExited(e -> {
				skillNameLabel.setText("");
				rankLabel.setText("");
				experienceLabel.setText("");
			});

			setMaxWidth(levelLabel.getMaxWidth() + imageBox.getMaxWidth());

			getChildren().addAll(imageBox, levelLabel);
		}

		public void update(int rank, int level, long experience) {
			this.rank = rank;
			this.level = level;
			this.experience = experience;

			levelLabel.setText(level == -1 ? "N/A" : Utilities.format(level));
		}

		public int getLevel() {
			return level;
		}

		public long getExperience() {
			return experience;
		}

	}

}