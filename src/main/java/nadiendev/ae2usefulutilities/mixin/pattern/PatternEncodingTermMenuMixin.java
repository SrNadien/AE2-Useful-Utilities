package nadiendev.ae2usefulutilities.mixin.pattern;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.core.definitions.AEItems;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public abstract class PatternEncodingTermMenuMixin {

    @Shadow
    @Final
    private RestrictedInputSlot blankPatternSlot;

    @Shadow
    @Final
    private RestrictedInputSlot encodedPatternSlot;

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/IPatternTerminalMenuHost;Z)V", at = @At("TAIL"))
    private void ae2uu$registerActions(MenuType<?> menuType, int id, Inventory inventory,
            IPatternTerminalMenuHost host, boolean bindInventory, CallbackInfo ci) {
        ((AEBaseMenuAccessor) this).ae2uu$registerClientAction(PatternNetwork.RETURN_ACTION,
                this::ae2uu$returnPattern);
    }

    @Inject(method = "encode", at = @At("HEAD"), remap = false)
    private void ae2uu$restockBlankPatterns(CallbackInfo ci) {
        PatternEncodingTermMenu self = (PatternEncodingTermMenu) (Object) this;
        if (self.isClientSide() || !AE2UtilitiesConfig.PATTERN_RESTOCK_BLANKS.get()) {
            return;
        }
        if (!this.blankPatternSlot.getItem().isEmpty()) {
            return;
        }
        MEStorage storage = ((MEStorageMenuAccessor) self).ae2uu$getStorage();
        if (storage == null) {
            return;
        }
        long extracted = storage.extract(AEItemKey.of(AEItems.BLANK_PATTERN.asItem()), 64, Actionable.MODULATE,
                IActionSource.ofPlayer(self.getPlayer()));
        if (extracted > 0) {
            this.blankPatternSlot.set(AEItems.BLANK_PATTERN.stack((int) extracted));
        }
    }

    @Unique
    private void ae2uu$returnPattern() {
        PatternEncodingTermMenu self = (PatternEncodingTermMenu) (Object) this;
        if (self.isClientSide() || !AE2UtilitiesConfig.PATTERN_RETURN_BUTTON.get()) {
            return;
        }
        ItemStack encoded = this.encodedPatternSlot.getItem();
        if (encoded.isEmpty()) {
            return;
        }

        int count = encoded.getCount();
        this.encodedPatternSlot.set(ItemStack.EMPTY);
        ItemStack blanks = AEItems.BLANK_PATTERN.stack(count);

        ItemStack inSlot = this.blankPatternSlot.getItem();
        if (inSlot.isEmpty()) {
            this.blankPatternSlot.set(blanks);
            return;
        }
        if (inSlot.is(AEItems.BLANK_PATTERN.asItem())) {
            int space = inSlot.getMaxStackSize() - inSlot.getCount();
            int toAdd = Math.min(space, blanks.getCount());
            if (toAdd > 0) {
                ItemStack merged = inSlot.copy();
                merged.grow(toAdd);
                this.blankPatternSlot.set(merged);
                blanks.shrink(toAdd);
            }
        }
        if (blanks.isEmpty()) {
            return;
        }

        MEStorage storage = ((MEStorageMenuAccessor) self).ae2uu$getStorage();
        if (storage != null) {
            long inserted = storage.insert(AEItemKey.of(AEItems.BLANK_PATTERN.asItem()), blanks.getCount(),
                    Actionable.MODULATE, IActionSource.ofPlayer(self.getPlayer()));
            blanks.shrink((int) inserted);
        }
        if (!blanks.isEmpty()) {
            self.getPlayer().getInventory().placeItemBackInInventory(blanks);
        }
    }
}
