package shadows.squeezerpatch.mixin;

import java.util.function.BiFunction;

import org.cyclops.cyclopscore.block.BlockWithEntity;
import org.cyclops.cyclopscore.blockentity.CyclopsBlockEntity;
import org.cyclops.integrateddynamics.block.BlockSqueezer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shadows.squeezerpatch.MessageSetSqueeze;
import shadows.squeezerpatch.SqueezerPatch;

@Mixin(BlockSqueezer.class)
public class BlockSqueezerMixin extends BlockWithEntity {

	public BlockSqueezerMixin(Properties properties, BiFunction<BlockPos, BlockState, CyclopsBlockEntity> blockEntitySupplier) {
		super(properties, blockEntitySupplier);
	}

	@Redirect(method = "updateEntityAfterFallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), require = 1)
	private boolean squeezerpatch_redirectCSCheck(Level level, BlockGetter world, Entity entity) {
		boolean client = level.isClientSide;
		return client ? !(entity instanceof Player) : entity instanceof Player;
	}

	@Inject(method = "updateEntityAfterFallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1, cancellable = true)
	private void squeezerpatch_sendServerMsg(BlockGetter world, Entity entity, CallbackInfo ci, double motionY, int i, int j, int k, BlockPos pos, BlockState state, int steps, int newHeight) {
		if (entity.level.isClientSide && entity instanceof Player player) {
			SqueezerPatch.CHANNEL.sendToServer(new MessageSetSqueeze(pos, newHeight));
			ci.cancel();
		}
	}

}
