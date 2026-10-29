package nadiendev.ae2usefulutilities.mixin.pattern;

import appeng.crafting.pattern.EncodedPatternItem;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.client.RecipeFinder;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(EncodedPatternItem.class)
public abstract class EncodedPatternItemMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void ae2uu$appendWorkstation(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
            Consumer<Component> lines, TooltipFlag flag, CallbackInfo ci) {
        if (!AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        RecipeFinder finder = RecipeFinder.get();
        if (finder == null) {
            return;
        }
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (!tag.contains(PatternNetwork.CATEGORY_TAG)) {
            return;
        }
        Component workstation = finder.getWorkstationName(tag.getStringOr(PatternNetwork.CATEGORY_TAG, ""));
        if (workstation != null) {
            lines.accept(Component.translatable("ae2usefulutilities.pattern.tooltip.workstation", workstation)
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
