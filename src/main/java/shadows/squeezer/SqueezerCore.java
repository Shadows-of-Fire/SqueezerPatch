package shadows.squeezer;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.12.2")
public class SqueezerCore implements IFMLLoadingPlugin {

	static String onLanded;

	public static final Logger LOG = LogManager.getLogger("SqueezerCore");

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "shadows.squeezer.Transformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		boolean dev = !(Boolean) data.get("runtimeDeobfuscationEnabled");
		onLanded = dev ? "onLanded" : "func_176216_a";
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static boolean isOnLanded(MethodNode m) {
		return onLanded.equals(m.name);
	}

}
