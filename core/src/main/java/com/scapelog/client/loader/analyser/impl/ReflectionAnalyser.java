package com.scapelog.client.loader.analyser.impl;

import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.reflection.ReflectedField;

public abstract class ReflectionAnalyser extends Analyser {

	public abstract ReflectedField[] getRequiredFields();

}