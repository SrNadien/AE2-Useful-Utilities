package nadiendev.ae2usefulutilities.mixin.infinitybooster;

import nadiendev.ae2usefulutilities.compat.infinitybooster.IChunkForceable;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockEntity.class, remap = true)
public abstract class MixinWirelessBlockEntityRemove {

    @Inject(method = "setRemoved", at = @At("HEAD"), remap = false)
    private void ae2u$onSetRemoved(CallbackInfo ci) {
        if ((Object) this instanceof IChunkForceable forceable) {
            forceable.ae2u$setChunkForced(false);
        }
    }
}