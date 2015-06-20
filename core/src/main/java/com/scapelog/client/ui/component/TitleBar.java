package com.scapelog.client.ui.component;

import com.scapelog.api.util.Components;
import com.scapelog.client.ui.ScapeFrame;
import com.scapelog.client.ui.StyleConstants;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.awt.Point;
import java.security.AccessController;
import java.security.PrivilegedActionException;

public final class TitleBar extends HBox {

	public static final int HEIGHT = 30;

	private double toolbarOffsetX = 0;
	private double toolbarOffsetY = 0;

	private final Label logo, beta;
	private final WindowControls windowControls;
	private final Region spacer = Components.createSpacer();

	private final HBox content;
	private final HBox staticContent;

	private boolean isMovingWindow;

	public TitleBar(ScapeFrame frame) {
		this.content = new HBox();
		this.staticContent = new HBox();
		setId("title-bar");
		setMinHeight(StyleConstants.TITLEBAR_DIMENSIONS.height);

		logo = new Label(frame.getTitle());
		logo.setId("logo");
		logo.setMinWidth(67);
		logo.setPrefWidth(67);

		beta = new Label("Beta");
		beta.setId("beta");
		beta.setMinWidth(40);
		beta.setPrefWidth(40);

		windowControls = new WindowControls(frame);

		ScrollPane contentScroll = new ScrollPane(content);
		contentScroll.setPadding(new Insets(0, 0, 0, 0));
		contentScroll.setPannable(true);
		contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		getChildren().addAll(logo, beta, spacer, contentScroll, staticContent, windowControls);

		logo.impl_processCSS(true);
		beta.impl_processCSS(true);
		spacer.impl_processCSS(true);
		contentScroll.impl_processCSS(true);

		frame.addResizeListener((observable, oldValue, newValue) -> {
			int frameWidth = newValue.width;
			double logoWidth = logo.prefWidth(-1);
			double betaWidth = beta.prefWidth(-1);
			double controlsWidth = windowControls.getWidth();
			double staticWidth = staticContent.getWidth();
			double emptySpace = frameWidth - logoWidth - betaWidth - controlsWidth - staticWidth;

			contentScroll.setMaxWidth(emptySpace);
		});

		setOnMousePressed(e -> {
			if (isDraggableTarget(e)) {
				if (e.isPrimaryButtonDown()) {
					isMovingWindow = true;
				}
				toolbarOffsetX = e.getX();
				toolbarOffsetY = e.getY();

				if (e.getClickCount() % 2 == 0 && frame.isResizable()) {
					frame.toggleMaximize();
				}
			}
		});
		setOnMouseReleased(e -> isMovingWindow = false);
		setOnMouseDragged(e -> {
			if (isMovingWindow && isDraggableTarget(e)) {
				Point windowPoint;
				try {
					windowPoint = AccessController.doPrivileged(Components.GET_LOCATION_ACTION);
					if (windowPoint != null) {
						windowPoint.x = (int) (windowPoint.x - toolbarOffsetX);
						windowPoint.y = (int) (windowPoint.y - toolbarOffsetY);
						frame.setLocation(windowPoint);
					}
				} catch (PrivilegedActionException ignored) {
					/**/
				}
			}
		});
	}

	private boolean isDraggableTarget(javafx.scene.input.MouseEvent e) {
		return e.getTarget().equals(logo) || e.getTarget().equals(beta) || e.getTarget().equals(spacer);
	}

	public HBox getContent() {
		return content;
	}

	public HBox getStaticContent() {
		return staticContent;
	}

}