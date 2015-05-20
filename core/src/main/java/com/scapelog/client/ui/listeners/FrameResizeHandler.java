package com.scapelog.client.ui.listeners;

import com.scapelog.client.ui.FrameConstants;
import com.scapelog.client.ui.ScapeFrame;
import com.scapelog.client.ui.UserInterface;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

public final class FrameResizeHandler implements ResizeHandler, EventHandler<MouseEvent> {

	private enum CursorState {
		EXITED, ENTERED, NIL
	}

	/**
	 * Used to determine the corner the resize is occuring from.
	 */
	private Cursor dragCursor;

	/**
	 * X location the mouse went down on for a drag operation.
	 */
	private int dragOffsetX;

	/**
	 * Y location the mouse went down on for a drag operation.
	 */
	private int dragOffsetY;

	/**
	 * Width of the window when the drag started.
	 */
	private int dragWidth;

	/**
	 * Height of the window when the drag started.
	 */
	private int dragHeight;

	private Cursor lastCursor = Cursor.DEFAULT;

	private CursorState cursorState = CursorState.NIL;

	private final Scene scene;

	private final ScapeFrame frame;

	public FrameResizeHandler(Scene scene, ScapeFrame frame) {
		this.scene = scene;
		this.frame = frame;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			moved(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			pressed(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			released();
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			dragged(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
			entered(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
			exited();
		}
	}

	private void moved(MouseEvent event) {
		Cursor cursor = getCursor(calculateCorner(scene, (int) event.getX(), (int) event.getY()));
		if (cursor != Cursor.DEFAULT) {
			if (frame != null && (!frame.isResizable() || (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0)) {
				return;
			}
			scene.setCursor(cursor);
		} else {
			scene.setCursor(lastCursor);
		}
	}

	private void pressed(MouseEvent event) {
		Point dragWindowOffset = new Point((int) event.getX(), (int) event.getY());
		if (frame != null) {
			frame.toFront();
		}
		int frameState = (frame != null) ? frame.getExtendedState() : 0;
		if (frame != null && frame.isResizable() && (frameState & Frame.MAXIMIZED_BOTH) == 0) {
			this.dragOffsetX = dragWindowOffset.x;
			this.dragOffsetY = dragWindowOffset.y;
			this.dragWidth = frame.getWidth();
			this.dragHeight = frame.getHeight();
			this.dragCursor = getCursor(calculateCorner(scene, dragWindowOffset.x, dragWindowOffset.y));
		}
	}

	private void released() {
		if (dragCursor != Cursor.DEFAULT && frame != null && !frame.isValid()) {
			frame.validate();
			frame.getRootPane().repaint();
		}
		dragCursor = Cursor.DEFAULT;
	}

	private void dragged(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());

		if (dragCursor != Cursor.DEFAULT) {
			Rectangle bounds = frame.getBounds();
			Rectangle startBounds = new Rectangle(bounds);
			Dimension min = frame.getMinimumSize();

			if (dragCursor == Cursor.E_RESIZE) {
				adjust(bounds, min, 0, 0, point.x + (dragWidth - dragOffsetX) - bounds.width, 0);
			} else if (dragCursor == Cursor.S_RESIZE) {
				adjust(bounds, min, 0, 0, 0, point.y + (dragHeight - dragOffsetY) - bounds.height);
			} else if (dragCursor == Cursor.N_RESIZE) {
				adjust(bounds, min, 0, point.y - dragOffsetY, 0, -(point.y - dragOffsetY));
			} else if (dragCursor == Cursor.W_RESIZE) {
				adjust(bounds, min, point.x - dragOffsetX, 0, -(point.x - dragOffsetX), 0);
			} else if (dragCursor == Cursor.NE_RESIZE) {
				adjust(bounds, min, 0, point.y - dragOffsetY, point.x + (dragWidth - dragOffsetX) - bounds.width, -(point.y - dragOffsetY));
			} else if (dragCursor == Cursor.SE_RESIZE) {
				adjust(bounds, min, 0, 0, point.x + (dragWidth - dragOffsetX) - bounds.width, point.y + (dragHeight - dragOffsetY) - bounds.height);
			} else if (dragCursor == Cursor.NW_RESIZE) {
				adjust(bounds, min, point.x - dragOffsetX, point.y - dragOffsetY, -(point.x - dragOffsetX), -(point.y - dragOffsetY));
			} else if (dragCursor == Cursor.SW_RESIZE) {
				adjust(bounds, min, point.x - dragOffsetX, 0, -(point.x - dragOffsetX), point.y + (dragHeight - dragOffsetY) - bounds.height);
			}
			if (!bounds.equals(startBounds)) {
				frame.setBounds(bounds);
				if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
					frame.validate();
					frame.getRootPane().repaint();
				}
				event.consume();
			}
		}
	}

	private void entered(MouseEvent event) {
		if (cursorState == CursorState.EXITED || cursorState == CursorState.NIL) {
			lastCursor = scene.getCursor();
		}
		cursorState = CursorState.ENTERED;
		moved(event);
	}

	private void exited() {
		scene.setCursor(lastCursor);
		cursorState = CursorState.EXITED;
	}

	private void adjust(Rectangle bounds, Dimension min, int deltaX, int deltaY, int deltaWidth, int deltaHeight) {
		bounds.x += deltaX;
		bounds.y += deltaY;
		bounds.width += deltaWidth;
		bounds.height += deltaHeight;
		if (min != null) {
			if (bounds.width < min.width) {
				int correction = min.width - bounds.width;
				if (deltaX != 0) {
					bounds.x -= correction;
				}
				bounds.width = min.width;
			}
			if (bounds.height < min.height) {
				int correction = min.height - bounds.height;
				if (deltaY != 0) {
					bounds.y -= correction;
				}
				bounds.height = min.height;
			}
		}
	}

	private int calculateCorner(Scene scene, int x, int y) {
		int xPosition = calculatePosition(x, (int) scene.getWidth());
		int yPosition = calculatePosition(y, (int) scene.getHeight());
		if (xPosition == -1 || yPosition == -1) {
			return -1;
		}
		return yPosition * 5 + xPosition;
	}

	private Cursor getCursor(int corner) {
		if (corner == -1) {
			return Cursor.DEFAULT;
		}
		return cursorMapping[corner];
	}

	private int calculatePosition(int spot, int length) {
		if (spot < UserInterface.getBorderRadius()) {
			return 0;
		}
		if (spot < FrameConstants.CORNER_DRAG_WIDTH) {
			return 1;
		}
		if (spot >= length - UserInterface.getBorderRadius()) {
			return 4;
		}
		if (spot >= length - FrameConstants.CORNER_DRAG_WIDTH) {
			return 3;
		}
		return 2;
	}

	private static final Cursor[] cursorMapping = {
			Cursor.NW_RESIZE, Cursor.NW_RESIZE,
			Cursor.N_RESIZE, Cursor.NE_RESIZE,
			Cursor.NE_RESIZE, Cursor.NW_RESIZE,
			Cursor.DEFAULT, Cursor.DEFAULT,
			Cursor.DEFAULT, Cursor.NE_RESIZE,
			Cursor.W_RESIZE, Cursor.DEFAULT,
			Cursor.DEFAULT, Cursor.DEFAULT,
			Cursor.E_RESIZE, Cursor.SW_RESIZE,
			Cursor.DEFAULT, Cursor.DEFAULT,
			Cursor.DEFAULT, Cursor.SE_RESIZE,
			Cursor.SW_RESIZE, Cursor.SW_RESIZE,
			Cursor.S_RESIZE, Cursor.SE_RESIZE,
			Cursor.SE_RESIZE
	};

}