package com.scapelog.tools;

import com.google.common.collect.ImmutableList;
import com.scapelog.tools.diagnostics.DiagnosticsReader;

public final class ToolRunner {

	private final ImmutableList<Tool> tools;

	public static void main(String[] args) {
		ToolRunner runner = new ToolRunner(
				new DiagnosticsReader()
		);
		runner.runTool(args.length != 1 ? null : args[0]);
	}

	private ToolRunner(Tool... tools) {
		ImmutableList.Builder<Tool> listBuilder = new ImmutableList.Builder<>();
		for (Tool t : tools) {
			listBuilder.add(t);
		}
		this.tools = listBuilder.build();
	}

	private void runTool(String name) {
		if (name == null) {
			printUsage();
			return;
		}

		for (Tool tool : tools) {
			if (tool.getTrigger().equals(name)) {
				tool.run();
			}
		}
	}

	private void printUsage() {
		System.out.println("USAGE:");
		System.out.println("\tjava -jar tools.jar [name]");
		System.out.println();
		System.out.println("Valid names:");
		for (Tool tool : tools) {
			System.out.println(tool.getTrigger() + " - " + tool.getName() + " - " + tool.getDescription());
		}
	}

}