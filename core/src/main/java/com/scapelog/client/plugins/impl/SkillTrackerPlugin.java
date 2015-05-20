package com.scapelog.client.plugins.impl;

import com.google.common.collect.Maps;
import com.scapelog.api.ClientFeature;
import com.scapelog.api.event.EventListener;
import com.scapelog.api.event.impl.SkillEvent;
import com.scapelog.api.model.Skill;
import com.scapelog.api.model.SkillSet;
import com.scapelog.api.model.Skills;
import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.TabPlugin;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.api.util.Utilities;
import com.scapelog.client.ui.component.PopupFrame;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

//todo: level goals
public final class SkillTrackerPlugin extends TabPlugin {
	private static final AwesomeIcon ICON = AwesomeIcon.BAR_CHART_ALT;

	private final SimpleObjectProperty<SkillBox> draggedBox = new SimpleObjectProperty<>();
	private final String dragKey = "skillbox";

	private VBox content = new VBox(5);
	private final Map<Skill, SkillBox> boxedSkills = Maps.newHashMap();
	private final ObservableList<SkillBox> skillBoxes = FXCollections.observableArrayList();
	private SkillBox activeBox;

	private final boolean[] trackedSkills = new boolean[SkillSet.count()];

	public SkillTrackerPlugin() {
		super(SkillTrackerPlugin.ICON, "Skill tracker", Optional.of("skill_tracker"), ClientFeature.SKILLS);
	}

	@Override
	public void onStart() {
		registerListener(new EventListener<SkillEvent>(SkillEvent.class) {
			@Override
			public void eventExecuted(SkillEvent event) {
				Platform.runLater(() -> {
					if (event.getXpChange() == 0) {
						return;
					}

					Skill skill = event.getSkill();
					SkillBox box = boxedSkills.get(skill);
					// create a box after the first xp gain
					if (skill.getEventCount() == 1 || box == null) {
						skill.reset();
						box = new SkillBox(skill);
						setDraggable(box);
					}

					if (trackedSkills[skill.getId()]) {
						if (!boxedSkills.containsKey(skill)) {
							boxedSkills.put(skill, box);
						}
						if (!skillBoxes.contains(box)) {
							skillBoxes.add(box);
						}
						box.update();
					}
				});
			}
		});

		boolean showButton = getBooleanOrAdd("show-button", true);
		if (!showButton) {
			hideButton();
		}

		for (int skillId = 0; skillId < SkillSet.count(); skillId++) {
			final String skillName = SkillSet.getName(skillId).toLowerCase();
			String configKey = "track-" + skillName;
			boolean isTracked = getBooleanOrAdd(configKey, true);
			trackedSkills[skillId] = isTracked;
		}

//		AtomicInteger xp2 = new AtomicInteger(1000);
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), event -> {
/*			for (int i = 0; i < SkillSet.count(); i++) {
				int xp = xp2.getAndAdd(100);
				int level = Skills.getLevelFromExperience(xp);
				ClientEventReceiver.receive(ClientFeature.SKILLS.getIdentifier() + "_" + i + "_" + level + "_" + xp);
			}*/

			// updateRates xp/h label
			skillBoxes.stream().filter(skillBox -> System.currentTimeMillis() - skillBox.lastUpdate >= 5000).forEach(SkillBox::updateRates);
		}), new KeyFrame(Duration.seconds(2)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	@Override
	public Region getContent() {
		skillBoxes.addListener((ListChangeListener<SkillBox>) c -> {
			c.next();
			if (c.wasAdded()) {
				content.getChildren().addAll(c.getAddedSubList());
			}
			if (c.wasRemoved()) {
				content.getChildren().removeAll(c.getRemoved());
			}
		});
		content.setMaxWidth(Integer.MAX_VALUE);
		content.setPadding(new Insets(5, 5, 5, 5));
		HBox.setHgrow(content, Priority.ALWAYS);
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setPannable(true);
		return scrollPane;
	}

	@Override
	public OpenTechnique getOpenTechnique() {
		return OpenTechnique.EXPANDED_BUTTON;
	}

	@Override
	public Region getButtonContent() {
		buttonIconPropertyProperty().set(ICON);
		BorderPane pane = new BorderPane();
		pane.setId("skill-tracker-button");
		Label label = new Label();
		label.setId("xp-label");

		final AtomicReference<SkillBox> activeBox = new AtomicReference<>();
		skillBoxes.addListener((ListChangeListener<SkillBox>) c -> {
			c.next();
			if (c.wasRemoved() && c.getRemoved() != null && c.getRemoved().size() > 0) {
				SkillBox active = activeBox.get();
				if (active == null) {
					return;
				}
				c.getRemoved().stream().filter(active::equals).forEach(box -> {
					activeBox.set(null);
					setActiveSkillBox(label, activeBox, null);
				});
			}
		});

		PopupFrame popup = new PopupFrame("Title", 500, 400, true);

		HBox content = new HBox(new Label("current skill content"));
		content.setStyle("-fx-padding: 10");
		content.setMinWidth(300);
		popup.setContent(content);

		pane.setOnMousePressed((e) -> {
/*			// open popover
			if (e.isPrimaryButtonDown()) {
				if (popup.isVisible()) {
					popup.hide();
				} else {
					popup.show(pane, 0);
				}
			}
			// cycle through boxes
			if (e.isSecondaryButtonDown()) {*/
			int index = skillBoxes.indexOf(activeBox.get());
			if (skillBoxes.isEmpty()) {
				return;
			}
			if (e.isPrimaryButtonDown()) {
				index++;
			}
			if (e.isSecondaryButtonDown()) {
				index--;
			}
			if (index >= skillBoxes.size()) {
				index = 0;
			}
			if (index < 0) {
				index = skillBoxes.size();
			}
			SkillBox nextBox = skillBoxes.get(index);
			setActiveSkillBox(label, activeBox, nextBox);
//			}
		});
		pane.setCenter(label);
		return pane;
	}

	@Override
	public Region getSettingsContent() {
		VBox content = new VBox(10);
		content.setPadding(new Insets(10, 10, 10, 10));

		/* skill toggle */
		VBox header = Components.createHeader("Tracked skills", "Click to toggle tracked skills");

		FlowPane skillsPane = new FlowPane(Orientation.HORIZONTAL);
		skillsPane.setHgap(5);
		skillsPane.setVgap(5);
		Effect effect = new ColorAdjust(0.0, 0.0, -0.8, 0.0);
		ImageView[] skillImages = new ImageView[SkillSet.values().length];
		for (int i = 0; i < skillImages.length; i++) {
			final int skillId = i;
			final String skillName = SkillSet.getName(skillId).toLowerCase();
			String configKey = "track-" + skillName;
			boolean isTracked = trackedSkills[skillId];

			ImageView image = new ImageView(new Image(SkillTrackerPlugin.class.getResourceAsStream("/img/skills/big/" + i + ".png")));
			image.fitWidthProperty().set(50);
			image.fitHeightProperty().set(50);
			if (!isTracked) {
				image.setEffect(effect);
			}

			image.setOnMousePressed((e) -> {
				boolean tracked;
				if (image.getEffect() != null && image.getEffect().equals(effect)) {
					image.setEffect(null);
					tracked = true;
				} else {
					image.setEffect(effect);
					tracked = false;
				}
				setBoolean(configKey, tracked);
				trackedSkills[skillId] = tracked;
			});

			skillsPane.getChildren().add(image);
			skillImages[i] = image;
		}

		content.getChildren().addAll(
				SettingsUtils.createShowButtonSetting(getSectionName(), this),
				header,
				skillsPane);
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

	private void setActiveSkillBox(Label label, AtomicReference<SkillBox> skillBoxReference, SkillBox box) {
		skillBoxReference.set(box);
		activeBox = box;
		if (box == null) {
			label.setText("");
			label.setGraphic(null);
			return;
		}
		ImageView icon = new ImageView(box.icon.getImage());
		icon.fitWidthProperty().set(15.0);
		icon.fitHeightProperty().set(15.0);

		label.setGraphic(icon);
		label.setText(Utilities.withSuffix(box.calculateHourlyXp(box.skill)) + "/h");

		box.hourlyXpLabel.textProperty().addListener((observable, oldValue, newValue) -> {
			if (box.equals(activeBox)) {
				label.setText(Utilities.withSuffix(box.calculateHourlyXp(box.skill)) + "/h");
			}
		});
	}

	private ImageView getSkillIcon(int id) {
		InputStream inputStream = SkillTrackerPlugin.class.getResourceAsStream("/img/skills/" + id + ".png");
		ImageView view = new ImageView();
		if (inputStream != null) {
			Image image = new Image(inputStream);
			view.setImage(image);
		}
		view.setId("skill-icon");
		view.getStyleClass().add("image");
		return view;
	}

	private void setDraggable(SkillBox box) {
		box.setOnDragOver(e -> {
			final Dragboard dragboard = e.getDragboard();
			if (dragboard.hasString() && dragKey.equals(dragboard.getString()) && draggedBox.get() != null) {
				e.acceptTransferModes(TransferMode.MOVE);
				e.consume();
			}
		});
		box.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			boolean success = false;
			if (db.hasString()) {
				Pane parent = (Pane) box.getParent();
				Node source = (Node) e.getGestureSource();
				int sourceIndex = parent.getChildren().indexOf(source);
				int targetIndex = parent.getChildren().indexOf(box);
				List<Node> nodes = new ArrayList<>(parent.getChildren());
				if (sourceIndex < targetIndex) {
					Collections.rotate(nodes.subList(sourceIndex, targetIndex + 1), -1);
				} else {
					Collections.rotate(nodes.subList(targetIndex, sourceIndex + 1), 1);
				}
				parent.getChildren().clear();
				parent.getChildren().addAll(nodes);
				success = true;
			}
			e.setDropCompleted(success);
			e.consume();
		});
		box.setOnDragDetected(e -> {
			Dragboard dragboard = box.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent clipboardContent = new ClipboardContent();
			clipboardContent.putString(dragKey);
			dragboard.setContent(clipboardContent);
			draggedBox.set(box);
			e.consume();
		});
	}

	class SkillBox extends BorderPane {

		private final Skill skill;
		private final Label hourlyXpLabel = Components.createLabel("", "hourly-xp");
		private final ImageView icon;

		private final Label levelLabel, levelProgressLabel;
		private final Label xpLabel, gainedXpLabel;
		private final ProgressBar progressBar;

		private long lastUpdate = System.currentTimeMillis();

		public SkillBox(Skill skill) {
			this.skill = skill;
			this.icon = getSkillIcon(skill.getId());

			this.levelLabel = Components.createLabel(skill.levelProperty().get() + "", "current-level");
			this.levelProgressLabel = Components.createLabel("", "level-progress");
			this.xpLabel = Components.createLabel(skill.xpProperty().get() + " xp", "current-xp");
			this.gainedXpLabel = Components.createLabel("", "gained-xp");
			this.progressBar = new ProgressBar(0.0);

			setup();
		}

		private void setup() {
			setId("skill-box");
			setMinHeight(40);

			setCache(true);
			setCacheShape(true);
			setCacheHint(CacheHint.SPEED);

			if (skill.getStartTime() < 1) {
				skill.resetStartTime();
			}

			HBox box = new HBox(5);
			box.setId("info");
			box.setFillHeight(true);
			HBox.setHgrow(box, Priority.ALWAYS);

			Label popupLabel = Components.createLabel("", "xp-popup");
			hourlyXpLabel.setMaxWidth(Double.MAX_VALUE);
			hourlyXpLabel.setMaxHeight(Double.MAX_VALUE);
			gainedXpLabel.setMaxWidth(Double.MAX_VALUE);
			gainedXpLabel.setMaxHeight(Double.MAX_VALUE);
			xpLabel.setMaxHeight(Double.MAX_VALUE);

			progressBar.setId("level-progress-bar");
			progressBar.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(progressBar, Priority.ALWAYS);

/*			ChangeListener<Number> xpListener = (observable, oldVal, newVal) -> {
				int xp = (int) newVal;
				int oldXp = (int) oldVal;

				int level = Skills.getLevelFromExperience(xp);
				int levelXp = Skills.getExperienceFromLevel(level);
				int nextLevel = level >= 125 ? 126 : level + 1;
				int nextLevelXp = Skills.getExperienceFromLevel(nextLevel);
				int remaining = nextLevelXp - xp;
				int gained = xp - oldXp;
				double percent = ((double) (xp - levelXp) / (nextLevelXp - levelXp));

				skill.increaseXP(gained);
				updateRates();

				levelLabel.setText(String.valueOf(level));
				xpLabel.setText(format(xp) + " xp");
				levelProgressLabel.setText(format(remaining) + " xp to " + nextLevel + " (" + (int) (percent * 100) + "%)");
				gainedXpLabel.setText(format(skill.getTotalGainedXp()) + " xp gained");
				progressBar.setProgress(percent);
			};
			skill.xpProperty().addListener(xpListener);*/

			Button refreshButton = Components.createIconButton(AwesomeIcon.REFRESH, "10.0", (e) -> {
				skill.resetStartTime();
				skill.resetGainedXp();
			});
			Button closeButton = Components.createIconButton(AwesomeIcon.TIMES, "10.0", (e) -> {
				skill.reset();
				skillBoxes.remove(this);
				boxedSkills.remove(skill);
			});
			refreshButton.setTooltip(new Tooltip("Refresh xp/h"));
			closeButton.setTooltip(new Tooltip("Close"));

			VBox controls = new VBox(1,
					refreshButton,
					closeButton
			);
			controls.setId("size-controls");

			box.getChildren().addAll(
					icon,
					new VBox(new HBox(3,
							levelLabel,
							xpLabel),
							levelProgressLabel
					),
					Components.createSpacer(),
					new VBox(
							hourlyXpLabel,
							new HBox(5,
									popupLabel,
									Components.createSpacer(),
									gainedXpLabel
							)
					),
					controls
			);

			setCenter(box);
			setBottom(progressBar);
		}

		public void update() {
			int xp = skill.getXP();

			int level = Skills.getLevelFromExperience(xp);
			int levelXp = Skills.getExperienceFromLevel(level);
			int nextLevel = level >= 125 ? 126 : level + 1;
			int nextLevelXp = Skills.getExperienceFromLevel(nextLevel);
			int remaining = nextLevelXp - xp;
			double percent = ((double) (xp - levelXp) / (nextLevelXp - levelXp));

			updateRates();

			levelLabel.setText(String.valueOf(level));
			xpLabel.setText(Utilities.format(xp) + " xp");
			levelProgressLabel.setText(Utilities.format(remaining) + " xp to " + nextLevel + " (" + (int) (percent * 100) + "%)");
			gainedXpLabel.setText(Utilities.format(skill.getTotalGainedXp()) + " xp gained");
			progressBar.setProgress(percent);
		}

		public void updateRates() {
			Tooltip tooltip = hourlyXpLabel.getTooltip() == null ? new Tooltip() : hourlyXpLabel.getTooltip();
			tooltip.setText("Based on " + Utilities.format(skill.getGainedXp()) + " xp gained in " + Utilities.formatTime(skill.getStartTime()));
			if (hourlyXpLabel.getTooltip() == null) {
				tooltip.setAutoHide(false);
				hourlyXpLabel.setTooltip(tooltip);
			}
			long hourlyXp = calculateHourlyXp(skill);
			hourlyXpLabel.setText(Utilities.format(hourlyXp) + " xp/h");

			lastUpdate = System.currentTimeMillis();
		}

		public long calculateHourlyXp(Skill skill) {
			return (int) (skill.getGainedXp() * 3600000.0D / (System.currentTimeMillis() - skill.getStartTime()));
		}

	}

}