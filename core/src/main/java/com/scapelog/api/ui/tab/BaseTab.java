package com.scapelog.api.ui.tab;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;

public abstract class BaseTab {

	protected final Tab tab = new Tab();
	private final SimpleBooleanProperty visibilityProperty = new SimpleBooleanProperty(true);

	public BaseTab(String tooltip) {
		super();
		tab.setTooltip(new Tooltip(tooltip));
	}

	public abstract Node getTabContent();

	public void show(boolean show) {
		visibilityProperty.set(show);
	}

	public SimpleBooleanProperty visibilityPropertyProperty() {
		return visibilityProperty;
	}

	public Node getContent() {
		return tab.getContent();
	}

	public void setContent(Node content) {
		tab.setContent(content);
	}

	public Tab getTab() {
		if (tab.getContent() == null) {
			tab.setContent(getTabContent());
		}
		tab.getStyleClass().add("rotateTab");
		return tab;
	}

}