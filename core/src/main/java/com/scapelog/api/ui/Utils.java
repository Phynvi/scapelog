package com.scapelog.api.ui;

import com.scapelog.client.ui.ScapeStage;
import com.scapelog.client.ui.component.TitleBar;
import com.scapelog.client.ui.util.CSS;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Iterator;
import java.util.List;

public final class Utils {

	private Utils() {

	}

	public static ScapeStage createStage(Parent root, double width, double height) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		stage.initModality(Modality.WINDOW_MODAL);

		root.getStyleClass().add("content");

		TitleBar titleBar = new TitleBar(stage);

		BorderPane pane = new BorderPane();
		pane.getStyleClass().addAll("popup", "frame");
		pane.setTop(titleBar);
		pane.setCenter(root);

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.setWidth(width);
		stage.setHeight(height);

		stage.setOnCloseRequest(e -> {
			stage.hide();
			e.consume();
		});

		CSS.addDefaultStyles(scene.getStylesheets());
		return new ScapeStage(stage, root, titleBar);
	}

	public static Window getWindow(Object owner) throws IllegalArgumentException {
		if (owner == null) {
			Window window = null;
			// lets just get the focused stage and show the dialog in there
			@SuppressWarnings("deprecation")
			Iterator<Window> windows = Window.impl_getWindows();
			while (windows.hasNext()) {
				window = windows.next();
				if (window.isFocused() && !(window instanceof Popup)) {
					break;
				}
			}
			return window;
		} else if (owner instanceof Window) {
			return (Window) owner;
		} else if (owner instanceof Node) {
			return ((Node) owner).getScene().getWindow();
		} else {
			throw new IllegalArgumentException("Unknown owner: " + owner.getClass()); //$NON-NLS-1$
		}
	}

	public static void centerToScreen(Stage stage) {
		List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		if (screens.size() == 0) {
			System.out.println("No screens found, centering aborted");
			return;
		}
		Screen screen = screens.get(0);
		Rectangle2D bounds = screen.getBounds();

		double width = stage.getWidth();
		double height = stage.getHeight();

		stage.setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
		stage.setY(bounds.getMinY() + (bounds.getHeight() - height) / 2);
	}

}