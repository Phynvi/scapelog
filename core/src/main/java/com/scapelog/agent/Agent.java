package com.scapelog.agent;

import com.scapelog.client.ScapeLog;
import com.scapelog.client.util.Debug;

import java.lang.instrument.Instrumentation;

public final class Agent {

	static {
		Debug.println("Starting Agent...");
	}

	public static void premain(String args, Instrumentation instrumentation) {
		ScapeLog.enableAgent();
		instrumentation.addTransformer(new RSClassTransformer());
	}

}