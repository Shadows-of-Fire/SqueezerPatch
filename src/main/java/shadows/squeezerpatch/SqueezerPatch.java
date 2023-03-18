package shadows.squeezerpatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import shadows.placebo.network.MessageHelper;

@Mod(SqueezerPatch.MODID)
public class SqueezerPatch {

	public static final String MODID = "squeezerpatch";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	//Formatter::off
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, MODID))
            .clientAcceptedVersions(s->true)
            .serverAcceptedVersions(s->true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();
    //Formatter::on

	public SqueezerPatch() {
		MessageHelper.registerMessage(CHANNEL, 0, new MessageSetSqueeze());
	}

}