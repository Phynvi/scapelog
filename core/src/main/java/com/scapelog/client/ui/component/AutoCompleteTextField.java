package com.scapelog.client.ui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class AutoCompleteTextField extends TextField {

	private FieldFocusAction focusAction;

	private final ObservableList<String> items = FXCollections.observableArrayList();

	public AutoCompleteTextField() {
		setOnKeyReleased(event -> {
			KeyCode keyCode = event.getCode();
			if ((!keyCode.isDigitKey() && !keyCode.isLetterKey()) || event.getCode() == KeyCode.BACK_SPACE || event.isAltDown() || event.isControlDown()) {
				return;
			}
			String enteredText = getText();
			if (enteredText.isEmpty()) {
				return;
			}
			for (String item : items) {
				if (item.startsWith(enteredText)) {
					setText(item);
					selectRange(item.length(), enteredText.length());
					break;
				}
			}

		});
		focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				if (focusAction != null) {
					ObservableList<String> newItems = FXCollections.observableArrayList();
					focusAction.onFocus(newItems);
					if (newItems.isEmpty()) {
						return;
					}
					items.clear();
					items.addAll(newItems.sorted(String::compareTo));
				}
			}
		});

	}

	public void setOnFocus(FieldFocusAction focusAction) {
		this.focusAction = focusAction;
	}

	@FunctionalInterface
	public interface FieldFocusAction {

		void onFocus(ObservableList<String> items);

	}

}