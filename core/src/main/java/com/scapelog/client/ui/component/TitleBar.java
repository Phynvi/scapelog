package com.scapelog.client.ui.component;

import com.scapelog.api.util.Components;
import com.scapelog.client.ui.ScapeFrame;
import com.scapelog.client.ui.StyleConstants;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public final class TitleBar extends HBox {

	public static final int HEIGHT = 30;

	private double toolbarOffsetX = 0;
	private double toolbarOffsetY = 0;

	private final Label logo;
	private final Region spacer = Components.createSpacer();

	private final HBox content;

	private final PrivilegedExceptionAction getLocationAction = () -> {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		return pointerInfo == null ? null : pointerInfo.getLocation();
	};

	private boolean isMovingWindow;

	public TitleBar(ScapeFrame frame) {
		this.content = new HBox();
		setId("title-bar");
		setMinHeight(StyleConstants.TITLEBAR_DIMENSIONS.height);

		logo = new Label(frame.getTitle());
		logo.setId("logo");

		Label beta = new Label("Beta");
		beta.setId("beta");

		WindowControls windowControls = new WindowControls(frame);
		getChildren().addAll(logo, beta, spacer, content, windowControls);

		setOnMousePressed(e -> {
			if (isRightTarget(e)) {
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
			if (isMovingWindow && isRightTarget(e)) {
				Point windowPoint;
				try {
					windowPoint = (Point) AccessController.doPrivileged(getLocationAction);
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

	private boolean isRightTarget(javafx.scene.input.MouseEvent e) {
		return e.getTarget().equals(logo) || e.getTarget().equals(spacer);
	}

	public HBox getContent() {
		return content;
	}

}