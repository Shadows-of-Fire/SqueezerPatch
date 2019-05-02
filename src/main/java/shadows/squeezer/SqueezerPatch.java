package shadows.squeezer;

import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.block.BlockSqueezer;
import org.cyclops.integrateddynamics.tileentity.TileSqueezer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "sqpatch", name = "Squeezer Patch", version = "1.0.0", dependencies = "required:integrateddynamics")
public class SqueezerPatch {

	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("sqpatch");

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		NETWORK.registerMessage(MessageSetSqueeze.Handler.class, MessageSetSqueeze.class, 0, Side.SERVER);
	}

	public static void onLanded(BlockSqueezer sq, World world, Entity entity) {
		double motionY = entity.motionY;
		entity.motionY = 0;
		if (!world.isRemote && motionY <= -0.37D && entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
			// Same way of deriving blockPos as is done in Entity#moveEntity
			int i = MathHelper.floor(entity.posX);
			int j = MathHelper.floor(entity.posY - 0.2D);
			int k = MathHelper.floor(entity.posZ);
			BlockPos blockPos = new BlockPos(i, j, k);
			IBlockState blockState = world.getBlockState(blockPos);

			// The faster the entity is falling, the more steps to advance by
			int steps = 1 + MathHelper.floor((-motionY - 0.37D) * 5);

			if ((entity.posY - blockPos.getY()) - sq.getRelativeTopPositionTop(world, blockPos, blockState) <= 0.1F) {
				if (blockState.getBlock() == sq) { // Just to be sure...
					int newHeight = Math.min(7, blockState.getValue(BlockSqueezer.HEIGHT) + steps);
					world.setBlockState(blockPos, blockState.withProperty(BlockSqueezer.HEIGHT, newHeight));
					TileSqueezer tile = TileHelpers.getSafeTile(world, blockPos, TileSqueezer.class);
					tile.setItemHeight(Math.max(newHeight, tile.getItemHeight()));
				}
			}
		} else if (world.isRemote && motionY <= -0.37D && entity instanceof EntityPlayer) {
			// Same way of deriving blockPos as is done in Entity#moveEntity
			int i = MathHelper.floor(entity.posX);
			int j = MathHelper.floor(entity.posY - 0.2D);
			int k = MathHelper.floor(entity.posZ);
			BlockPos blockPos = new BlockPos(i, j, k);
			IBlockState blockState = world.getBlockState(blockPos);

			// The faster the entity is falling, the more steps to advance by
			int steps = 1 + MathHelper.floor((-motionY - 0.37D) * 5);
			if ((entity.posY - blockPos.getY()) - sq.getRelativeTopPositionTop(world, blockPos, blockState) <= 0.1F) {
				if (blockState.getBlock() == sq) { // Just to be sure...
					int newHeight = Math.min(7, blockState.getValue(BlockSqueezer.HEIGHT) + steps);
					NETWORK.sendToServer(new MessageSetSqueeze(blockPos, newHeight));
				}
			}
		}
	}
}
