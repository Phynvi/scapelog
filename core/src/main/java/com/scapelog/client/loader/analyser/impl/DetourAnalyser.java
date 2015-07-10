package com.scapelog.client.loader.analyser.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.jagex.jaggl.OpenGLProvider;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.impl.detours.Detour;
import com.scapelog.client.loader.analyser.impl.detours.Interceptor;
import com.scapelog.client.loader.analyser.injection.ReplaceInjection;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

public final class DetourAnalyser extends Analyser {

	private final List<Class<?>> detouredClasses = Lists.newArrayList(
			OpenGLProvider.class
	);

	private final ImmutableMap<String, ImmutableList<DetouredMethod>> detouredClassMap;

	public DetourAnalyser() {
		this.detouredClassMap = processDetouredClasses();
	}

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
			for (MethodNode methodNode : classNode.methods) {
				InstructionSearcher searcher = new InstructionSearcher(methodNode);
				while (searcher.next(MethodInsnNode.class, instr -> true) != null) {
					MethodInsnNode methodInsnNode = (MethodInsnNode) searcher.current();
					ImmutableList<DetouredMethod> detouredMethods = detouredClassMap.get(methodInsnNode.owner);
					if (detouredMethods == null || detouredMethods.isEmpty()) {
						continue;
					}
					for (DetouredMethod detouredMethod : detouredMethods) {
						if (methodInsnNode.name.equals(detouredMethod.detouredMethod)) {
							String desc = methodInsnNode.desc;
							if (detouredMethod.targetType == Detour.TargetType.INSTANCE) {
								desc = "(Ljava/lang/Object;" + desc.substring(1);
							}
							MethodInsnNode newInstruction = new MethodInsnNode(Opcodes.INVOKESTATIC, detouredMethod.ownerClass.replaceAll("\\.", "/"), methodInsnNode.name, desc, false);
							MethodInfo methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
							int index = methodNode.instructions.indexOf(methodInsnNode);
							operation.addInjection(classNode.name, new ReplaceInjection(methodInfo, index, newInstruction));
							methodNode.instructions.set(methodInsnNode, newInstruction);
						}
					}
				}
			}
		}
	}

	private ImmutableMap<String, ImmutableList<DetouredMethod>> processDetouredClasses() {
		ImmutableMap.Builder<String, ImmutableList<DetouredMethod>> mapBuilder = new ImmutableMap.Builder<>();
		for (Class<?> clazz : detouredClasses) {
			ImmutableList.Builder<DetouredMethod> detouredMethodBuilder = new ImmutableList.Builder<>();

			Interceptor interceptor = clazz.getAnnotation(Interceptor.class);
			if (interceptor == null) {
				continue;
			}
			String ownerClass = interceptor.value();
			for (Method method : clazz.getDeclaredMethods()) {
				if (!Modifier.isStatic(method.getModifiers())) {
					continue;
				}
				Detour detour = method.getAnnotation(Detour.class);
				if (detour == null) {
					continue;
				}
				String detouredMethod = detour.target().isEmpty() ? method.getName() : detour.target();
				detouredMethodBuilder.add(new DetouredMethod(clazz.getName(), detour.type(), detouredMethod));
			}
			ImmutableList<DetouredMethod> detouredMethods = detouredMethodBuilder.build();
			if (!detouredMethods.isEmpty()) {
				mapBuilder.put(ownerClass, detouredMethods);
			}
		}
		return mapBuilder.build();
	}

	class DetouredMethod {

		private final String ownerClass;

		private final Detour.TargetType targetType;

		private final String detouredMethod;

		public DetouredMethod(String ownerClass, Detour.TargetType targetType, String detouredMethod) {
			this.ownerClass = ownerClass;
			this.targetType = targetType;
			this.detouredMethod = detouredMethod;
		}

	}

}