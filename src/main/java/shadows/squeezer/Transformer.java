package shadows.squeezer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!transformedName.equals("org.cyclops.integrateddynamics.block.BlockSqueezer")) return basicClass;
		SqueezerCore.LOG.info("Transforming {}...", transformedName);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		MethodNode onLanded = null;
		for (MethodNode m : classNode.methods) {
			if (SqueezerCore.isOnLanded(m)) {
				onLanded = m;
				break;
			}
		}
		if (onLanded != null) {
			InsnList list = new InsnList();
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new VarInsnNode(Opcodes.ALOAD, 1));
			list.add(new VarInsnNode(Opcodes.ALOAD, 2));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "shadows/squeezer/SqueezerPatch", "onLanded", "(Lorg/cyclops/integrateddynamics/block/BlockSqueezer;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)V", false));
			list.add(new InsnNode(Opcodes.RETURN));
			onLanded.instructions.insert(list);
			CustomClassWriter writer = new CustomClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			SqueezerCore.LOG.info("Successfully transformed {}!", transformedName);
			return writer.toByteArray();
		}
		SqueezerCore.LOG.info("Failed transforming {}.", transformedName);
		return basicClass;
	}

}
