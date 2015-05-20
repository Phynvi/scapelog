package com.scapelog.api.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberTextField extends TextField {

	private final NumberFormat numberFormat;

	private ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();

	public NumberTextField() {
		this(BigDecimal.ZERO);
	}

	public NumberTextField(BigDecimal value) {
		this(value, NumberFormat.getInstance());
	}

	public NumberTextField(BigDecimal value, NumberFormat numberFormat) {
		super();
		this.numberFormat = numberFormat;
		initHandlers();
		setNumber(value);
	}

	private void initHandlers() {
		setOnAction(e -> parseAndFormatInput());
		focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				parseAndFormatInput();
			}
		});
		numberProperty().addListener((observable, oldValue, newValue) -> setText(numberFormat.format(newValue)));
	}

	private void parseAndFormatInput() {
		try {
			String input = getText();
			if (input == null || input.length() == 0) {
				return;
			}
			Number parsedNumber = numberFormat.parse(input);
			BigDecimal newValue = new BigDecimal(parsedNumber.toString());
			setNumber(newValue);
			selectAll();
		} catch (ParseException e) {
			setText(numberFormat.format(number.get()));
		}
	}

	public BigDecimal getNumber() {
		return number.get();
	}

	public ObjectProperty<BigDecimal> numberProperty() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number.set(number);
	}

}