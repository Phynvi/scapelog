package com.scapelog.client.loader.analyser.impl;

import com.google.common.collect.Lists;
import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class StringFieldAnalyser extends Analyser {

	private static final List<TranslatedString> STRING_STORE = Lists.newArrayList();

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
			MethodNode clinit = ClassNodeUtils.getMethod(classNode, "<clinit>");
			if (clinit == null) {
				continue;
			}

			InstructionSearcher searcher = new InstructionSearcher(clinit);

			List<AbstractInsnNode[]> patterns = searcher.findPatterns("LDC LDC LDC LDC LDC INVOKESPECIAL PUTSTATIC");
			for (AbstractInsnNode[] pattern : patterns) {
				LdcInsnNode ldc1 = (LdcInsnNode) pattern[0];
				LdcInsnNode ldc2 = (LdcInsnNode) pattern[1];
				LdcInsnNode ldc3 = (LdcInsnNode) pattern[2];
				LdcInsnNode ldc4 = (LdcInsnNode) pattern[3];
				LdcInsnNode ldc5 = (LdcInsnNode) pattern[4];
				FieldInsnNode field = (FieldInsnNode) pattern[6];

				TranslatedString str = new TranslatedString(field, (String) ldc1.cst, (String) ldc2.cst, (String) ldc3.cst, (String) ldc4.cst, (String) ldc5.cst);
				STRING_STORE.add(str);
			}
		}
	}

	public static Optional<TranslatedString> getString(String english) {
		return STRING_STORE.stream().filter(str -> str.english.equals(english)).findFirst();
	}

	public static class TranslatedString {

		private final FieldInsnNode fieldInsnNode;
		private final String english;
		private final String german;
		private final String french;
		private final String portugese;
		private final String spanish;

		public TranslatedString(FieldInsnNode fieldInsnNode, String english, String german, String french, String portugese, String spanish) {
			this.fieldInsnNode = fieldInsnNode;
			this.english = english;
			this.german = german;
			this.french = french;
			this.portugese = portugese;
			this.spanish = spanish;
		}

		public FieldInsnNode getFieldInsnNode() {
			return fieldInsnNode;
		}

		public String getEnglish() {
			return english;
		}

		public String getGerman() {
			return german;
		}

		public String getFrench() {
			return french;
		}

		public String getPortugese() {
			return portugese;
		}

		public String getSpanish() {
			return spanish;
		}

		@Override
		public String toString() {
			return fieldInsnNode + ", " + english + ", " + german + ", " + french + ", " + portugese + ", " + spanish;
		}
	}

}