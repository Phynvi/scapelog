package com.scapelog.client.ui.component;

import com.google.common.collect.Lists;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.LoadingEvent;
import com.scapelog.client.ui.shapes.Shape3d;
import com.scapelog.client.ui.shapes.impl.Cube;
import com.scapelog.client.ui.shapes.impl.Triangle;

import javax.swing.JPanel;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public final class AppletPanel extends JPanel {

	private final Shape3d shape;
	private final Image logo;
	private final Color backgroundColor = new Color(22, 22, 22);
	private final Color foregroundColor = new Color(29, 126, 166);

	private final List<String> messages = Lists.newArrayList();

	private final Timer timer = new Timer();
	private final TimerTask refreshTask = new TimerTask() {
		@Override
		public void run() {
			repaint();
		}
	};

	public AppletPanel() {
		Shape3d[] shapes = new Shape3d[]{
				new Triangle(100, 100), new Cube(100, 100)
		};
		this.shape = shapes[new Random().nextInt(shapes.length)];
		this.logo = Toolkit.getDefaultToolkit().getImage(AppletPanel.class.getResource("/img/logo.png"));

		setLayout(new BorderLayout());
		startRefreshTask();

		ClientEventDispatcher.registerListener(new ClientEventListener<LoadingEvent>(LoadingEvent.class) {
			@Override
			public void eventExecuted(LoadingEvent event) {
				String message = event.getMessage();
				messages.add(message);
				if (messages.size() >= 100) {
					messages.remove(0);
				}
			}
		});
	}

	public void setApplet(Applet applet) {
		add(applet, BorderLayout.CENTER);
		revalidate();
		refreshTask.cancel();
	}

	public void removeApplet() {
		removeAll();
		revalidate();
	}

	private void startRefreshTask() {
		timer.scheduleAtFixedRate(refreshTask, 0, 1000 / 60);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());

		int centerX = getWidth() / 2 - logo.getWidth(null) / 2;
		g.drawImage(logo, centerX, 50, null);

		g.setColor(Color.white);
		int x = 15;
		int y = getHeight() - g.getFontMetrics().getHeight();
		for (int idx = messages.size() - 1; idx >= 0; idx--) {
			if (y <= g.getFontMetrics().getHeight()) {
				break;
			}
			String message = messages.get(idx);
			g.setColor(Color.black);
			g.drawString(message, x + 1, y + 1);
			g.setColor(foregroundColor);
			g.drawString(message, x, y);
			y -= g.getFontMetrics().getHeight();
		}

		shape.update(g, foregroundColor, backgroundColor, getWidth() - shape.getWidth(), getHeight() - shape.getHeight());
	}

}