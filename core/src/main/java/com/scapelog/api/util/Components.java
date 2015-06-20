package com.scapelog.api.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.scapelog.client.ui.util.Fonts;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class for creating common UI components in a more inline way
 */
public final class Components {

	private Components() {

	}

	/**
	 * Creates a horizontal spacer to fill up the empty space between two components.
	 *
	 * @return The spacer region
	 */
	public static Region createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	/**
	 * Creates a button with the specified icon of given the size
	 * @param icon Icon to be used for the button
	 * @param size Size of the icon in JavaFX compatible format for -fx-font-size
	 * @return A button with an icon of given size
	 */
	public static Button createIconButton(AwesomeIcon icon, String size) {
		return createIconButton(icon, size, null);
	}

	public static Button createIconButton(AwesomeIcon icon, String size, EventHandler<ActionEvent> event) {
		Button button = new Button();
		button.setGraphic(AwesomeDude.createIconLabel(icon, size));
		if (event != null) {
			button.setOnAction(event);
		}
		return button;
	}

	public static Button createBorderedButton(String text) {
		Button button = new Button(text);
		button.getStyleClass().clear();
		button.getStyleClass().add("bordered");
		return button;
	}

	public static Label createLabel(String text, String id) {
		Label label = new Label(text);
		label.setId(id);
		return label;
	}

	public static Label createLabel(String text) {
		Label label = new Label(text);
		label.setMaxHeight(Double.MAX_VALUE);
		return label;
	}

	public static VBox createHeader(String title, String description) {
		VBox box = new VBox();
		box.getStyleClass().add("header");

		Label titleLabel = new Label(title);
		titleLabel.getStyleClass().add("title");
		box.getChildren().add(titleLabel);

		if (description != null && !description.isEmpty()) {
			Label descriptionLabel = new Label(description);
			descriptionLabel.setWrapText(true);
			descriptionLabel.getStyleClass().add("description");
			box.getChildren().add(descriptionLabel);
		}
		return box;
	}

	public static HBox createBox(String title, Node... nodes) {
		return createBox(5, title, nodes);
	}

	public static HBox createBox(int spacing, String title, Node... nodes) {
		HBox box = new HBox(spacing, Components.createLabel(title), Components.createSpacer());
		box.getChildren().addAll(nodes);
		return box;
	}

	public static void setWebFont(Parent node, String name) {
		node.getStylesheets().add(Fonts.getWebFontStyleSheet(name));
		node.setStyle("-fx-font-family: " + name + ";");
	}

	public static final PrivilegedExceptionAction<Point> GET_LOCATION_ACTION = () -> {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		return pointerInfo == null ? null : pointerInfo.getLocation();
	};

}