package nadiendev.ae2usefulutilities.mixin.infinitybooster;

import appeng.api.inventories.InternalInventory;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import nadiendev.ae2usefulutilities.compat.infinitybooster.IChunkForceable;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WirelessAccessPointBlockEntity.class, remap = false)
public abstract class MixinWirelessBlockEntity extends AEBaseBlockEntity implements IChunkForceable {

    @Shadow @Final private AppEngInternalInventory inv;

    public MixinWirelessBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    private boolean ae2u$hasChunkLoadCard() {
        ItemStack card = inv.getStackInSlot(0);
        if (card.isEmpty()) return false;
        String id = card.getItem().builtInRegistryHolder().key().location().toString();
        return id.equals("aeinfinitybooster:dimension_card")
                || id.equals("aeinfinitybooster:infinity_card");
    }

    @Override
    public void ae2u$setChunkForced(boolean force) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = getBlockPos();
            serverLevel.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, force);
        }
    }

    @Inject(method = "onReady", at = @At("RETURN"))
    private void ae2u$onReady(CallbackInfo ci) {
        if (AE2UtilitiesConfig.INFINITY_BOOSTER_CHUNK_LOADING.get() && ae2u$hasChunkLoadCard()) {
            ae2u$setChunkForced(true);
        }
    }

    @Inject(method = "saveChanges", at = @At("HEAD"))
    private void ae2u$onSaveChanges(CallbackInfo ci) {
        if (AE2UtilitiesConfig.INFINITY_BOOSTER_CHUNK_LOADING.get()) {
            ae2u$setChunkForced(ae2u$hasChunkLoadCard());
        } else {
            ae2u$setChunkForced(false);
        }
    }
}