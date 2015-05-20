package com.scapelog.client.ui;

import javafx.beans.property.SimpleBooleanProperty;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class ScapeFrame extends JFrame {

	private final SimpleBooleanProperty iconifiedProperty = new SimpleBooleanProperty(false);

	public static final SimpleBooleanProperty MAXIMIZED_PROPERTY = new SimpleBooleanProperty(false);

	protected GraphicsConfiguration currentRootPaneGC;

	public ScapeFrame(String title) {
		super(title);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				this.processNewPosition();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				this.processNewPosition();
			}

			protected void processNewPosition() {
				SwingUtilities.invokeLater(() -> {
					if (!isShowing() || !isDisplayable()) {
						currentRootPaneGC = null;
						return;
					}

					GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
					if (graphicsDevices.length == 1) {
						return;
					}
					Point middleLocation = new Point(getLocationOnScreen().x + getWidth() / 2, getLocationOnScreen().y + getHeight() / 2);
					for (GraphicsDevice graphicsDevice : graphicsDevices) {
						GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
						Rectangle bounds = graphicsConfiguration.getBounds();
						if (bounds.contains(middleLocation)) {
							if (graphicsConfiguration != currentRootPaneGC) {
								currentRootPaneGC = graphicsConfiguration;
								setMaximized();
							}
							break;
						}
					}
				});
			}
		});

		addWindowStateListener(new WindowAdapter() {
			@Override
			public void windowIconified(WindowEvent e) {
				iconifiedProperty.set(true);
			}

			@Override
			public void windowStateChanged(WindowEvent e) {
				super.windowStateChanged(e);
				boolean iconified = e.getNewState() == Frame.ICONIFIED;
				iconifiedProperty.set(iconified);
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				iconifiedProperty.set(false);
			}
		});
		addWindowStateListener(e -> {
			boolean iconified = (e.getNewState() & WindowEvent.WINDOW_ICONIFIED) != 0;
			iconifiedProperty.set(iconified);
		});
	}

	public void toggleMaximize() {
		int state = getExtendedState();
		if ((state & Frame.MAXIMIZED_BOTH) != 0) {
			setMaximized();
			setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
		} else {
			setMaximized();
			setExtendedState(state | Frame.MAXIMIZED_BOTH);
		}
		MAXIMIZED_PROPERTY.set(isMaximized());
	}

	public void setMaximized() {
		Component topLevelAncestor = getRootPane().getTopLevelAncestor();
		GraphicsConfiguration graphicsConfiguration = (currentRootPaneGC != null) ? currentRootPaneGC : topLevelAncestor.getGraphicsConfiguration();
		Rectangle screenBounds = graphicsConfiguration.getBounds();
		screenBounds.x = 0;
		screenBounds.y = 0;
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
		Rectangle maxBounds = new Rectangle(
				(screenBounds.x + screenInsets.left),
				(screenBounds.y + screenInsets.top),
				screenBounds.width - ((screenInsets.left + screenInsets.right)),
				screenBounds.height - ((screenInsets.top + screenInsets.bottom))
		);
		if (topLevelAncestor instanceof JFrame) {
			((JFrame) topLevelAncestor).setMaximizedBounds(maxBounds);
		}
		MAXIMIZED_PROPERTY.set(isMaximized());
	}

	public boolean isMaximized() {
		int state = getExtendedState();
		return (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
	}

	public SimpleBooleanProperty iconifiedPropertyProperty() {
		return iconifiedProperty;
	}

}