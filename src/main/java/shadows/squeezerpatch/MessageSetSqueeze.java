package shadows.squeezerpatch;

import java.util.function.Supplier;

import org.cyclops.cyclopscore.helper.BlockEntityHelpers;
import org.cyclops.integrateddynamics.block.BlockSqueezer;
import org.cyclops.integrateddynamics.blockentity.BlockEntitySqueezer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent.Context;
import shadows.placebo.network.MessageHelper;
import shadows.placebo.network.MessageProvider;

public class MessageSetSqueeze implements MessageProvider<MessageSetSqueeze> {

	BlockPos pos;
	int newHeight;

	public MessageSetSqueeze() {

	}

	public MessageSetSqueeze(BlockPos pos, int newHeight) {
		this.pos = pos;
		this.newHeight = newHeight;
	}

	@Override
	public void write(MessageSetSqueeze msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.pos);
		buf.writeInt(msg.newHeight);
	}

	@Override
	public MessageSetSqueeze read(FriendlyByteBuf buf) {
		return new MessageSetSqueeze(buf.readBlockPos(), buf.readInt());
	}

	@Override
	public void handle(MessageSetSqueeze msg, Supplier<Context> ctx) {
		MessageHelper.handlePacket(() -> () -> {
			Level level = ctx.get().getSender().level;
			BlockState state = level.getBlockState(msg.pos);
			if (state.getBlock() instanceof BlockSqueezer) {
				level.setBlock(msg.pos, state.setValue(BlockSqueezer.HEIGHT, msg.newHeight), 3);
				BlockEntityHelpers.get(level, msg.pos, BlockEntitySqueezer.class).ifPresent(tile -> tile.setItemHeight(Math.max(msg.newHeight, tile.getItemHeight())));
			}
		}, ctx);
	}

}
