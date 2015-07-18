package com.scapelog.client.jagex.jaggl;

import com.scapelog.api.ui.Overlay;
import com.scapelog.client.ui.UserInterface;
import com.scapelog.util.proguard.Keep;

import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Optional;

public final class MouseDetour {

	private static boolean isMoving = false;
	private static Overlay movingOverlay = null;
	private static int overlayOffsetX = 0;
	private static int overlayOffsetY = 0;

	@Keep
	public static MouseEvent mousePressed(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			Optional<Overlay> overlay = getMovableOverlay(event.getPoint());
			overlay.ifPresent(o -> {
				isMoving = true;
				movingOverlay = o;
				overlayOffsetX = o.getX() - event.getX();
				overlayOffsetY = o.getY() - event.getY();
				event.consume();
			});
		}
		return event;
	}

	@Keep
	public static MouseEvent mouseDragged(MouseEvent event) {
		if (isMoving && movingOverlay != null) {
			movingOverlay.setX(overlayOffsetX + event.getX());
			movingOverlay.setY(overlayOffsetY + event.getY());
			event.consume();
		}
		return event;
	}

	@Keep
	public static MouseEvent mouseMoved(MouseEvent event) {
		if (isMoving) {
			event.consume();
		}
		return event;
	}

	@Keep
	public static MouseEvent mouseReleased(MouseEvent event) {
		isMoving = false;
		movingOverlay = null;
		return event;
	}

	private static Optional<Overlay> getMovableOverlay(Point point) {
		for (Overlay overlay : UserInterface.getOverlays()) {
			if (!overlay.isMovable()) {
				continue;
			}
			if (point.x >= overlay.getX() && point.x <= overlay.getX() + overlay.getWidth() && point.y >= overlay.getY() && point.y <= overlay.getY() + overlay.getHeight()) {
				return Optional.of(overlay);
			}
		}
		return Optional.empty();
	}

}