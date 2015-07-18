package com.scapelog.client.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public final class DiagnosticsLogger extends PrintStream {

	private static final List<String> OUTPUT = Collections.synchronizedList(Lists.newArrayList());

	public DiagnosticsLogger(OutputStream out) {
		super(out);
	}

	@Override
	public void println(String x) {
		addOutput(x);
		super.println(x);
	}

	@Override
	public void println(Object x) {
		if (x != null) {
			addOutput(x.toString());
		}
		super.println(x);
	}

	private void addOutput(String x) {
		synchronized (OUTPUT) {
			OUTPUT.add(x);
		}
	}

	public static String getHashedOutput() {
		String joinedString = Joiner.on("\n").join(OUTPUT);
		return Base64.getEncoder().encodeToString(joinedString.getBytes());
	}

}