package shadows.squeezer;

import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.block.BlockSqueezer;
import org.cyclops.integrateddynamics.tileentity.TileSqueezer;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSetSqueeze implements IMessage {

	BlockPos pos;
	int newHeight;

	public MessageSetSqueeze() {

	}

	public MessageSetSqueeze(BlockPos pos, int newHeight) {
		this.pos = pos;
		this.newHeight = newHeight;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(newHeight);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		newHeight = buf.readInt();
	}

	public static class Handler implements IMessageHandler<MessageSetSqueeze, IMessage> {

		@Override
		public IMessage onMessage(MessageSetSqueeze message, MessageContext ctx) {
			FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
				IBlockState state = ctx.getServerHandler().player.world.getBlockState(message.pos);
				if (state.getBlock() == BlockSqueezer.getInstance()) {
					ctx.getServerHandler().player.world.setBlockState(message.pos, state.withProperty(BlockSqueezer.HEIGHT, message.newHeight));
					TileSqueezer tile = TileHelpers.getSafeTile(ctx.getServerHandler().player.world, message.pos, TileSqueezer.class);
					tile.setItemHeight(Math.max(message.newHeight, tile.getItemHeight()));
				}
			});
			return null;
		}

	}

}
