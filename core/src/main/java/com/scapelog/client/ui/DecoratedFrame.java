package com.scapelog.client.ui;

import com.scapelog.client.ui.component.TitleBar;
import com.scapelog.client.ui.listeners.FrameResizeHandler;
import com.scapelog.client.ui.util.CSS;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class DecoratedFrame {

	private final ScapeFrame frame;
	private Scene scene;
	private BorderPane root;
	private TitleBar titleBar;

	public DecoratedFrame(String title, int width, int height, boolean resizable) {
		this(title, width, height, height, resizable);
	}

	public DecoratedFrame(String title, int width, int height, int sceneHeight, boolean resizable) {
		this.frame = new ScapeFrame(title);
		setup(width, height, sceneHeight, resizable);
	}

	private void setup(int width, int height, int sceneHeight, boolean resizable) {
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setMinimumSize(new Dimension(300, 300));
		frame.setUndecorated(true);
		frame.setBackground(StyleConstants.BACKGROUND_COLOR);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setSize(width, height);
		frame.setResizable(resizable);

		root = new BorderPane();
		root.setId("root");
		StackPane layerPane = new StackPane();
		layerPane.getChildren().addAll(root);

		addTitleBar();

		scene = new Scene(layerPane, width, sceneHeight, false, SceneAntialiasing.BALANCED);
		if(frame.isResizable()) {
			FrameResizeHandler resizeHandler = new FrameResizeHandler(scene, frame);
			scene.addEventFilter(MouseEvent.MOUSE_MOVED, resizeHandler);
			scene.addEventFilter(MouseEvent.MOUSE_PRESSED, resizeHandler);
			scene.addEventFilter(MouseEvent.MOUSE_RELEASED, resizeHandler);
			scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, resizeHandler);
			scene.addEventFilter(MouseEvent.MOUSE_ENTERED, resizeHandler);
			scene.addEventFilter(MouseEvent.MOUSE_EXITED, resizeHandler);
		}
		setContent(scene, width, sceneHeight);
		CSS.addDefaultStyles(scene.getStylesheets());
	}

	public final void addTitleBar() {
		if (titleBar == null) {
			titleBar = new TitleBar(frame);
		}
		root.setTop(titleBar);
		GridPane.setHgrow(titleBar, Priority.ALWAYS);
	}

	public final void removeTitleBar() {
		root.setTop(null);
	}

	protected void setContent(Scene scene, int width, int sceneHeight) {
		JFXPanel jfxPanel = new JFXPanel();
		jfxPanel.setScene(scene);
		jfxPanel.setLayout(null);
		jfxPanel.setBounds(0, 0, width, sceneHeight);

		frame.getContentPane().add(jfxPanel);
	}

	public final ScapeFrame getFrame() {
		return frame;
	}

	public final Scene getScene() {
		return scene;
	}

	public final BorderPane getRoot() {
		return root;
	}

	public final TitleBar getTitleBar() {
		return titleBar;
	}

	public final boolean isVisible() {
		return frame.isVisible();
	}

	public final void show() {
		frame.setVisible(true);
	}

	public final void hide() {
		frame.setVisible(false);
	}

	public final void setDefaultCloseOperation(int operation) {
		this.frame.setDefaultCloseOperation(operation);
	}

}