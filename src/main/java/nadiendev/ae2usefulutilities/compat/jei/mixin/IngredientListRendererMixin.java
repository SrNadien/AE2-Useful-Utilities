package nadiendev.ae2usefulutilities.compat.jei.mixin;

import mezz.jei.gui.overlay.IngredientListRenderer;
import mezz.jei.gui.overlay.IngredientListSlot;
import nadiendev.ae2usefulutilities.compat.jei.StockOverlayRenderer;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = IngredientListRenderer.class, remap = false)
public abstract class IngredientListRendererMixin {

    @Shadow
    @Final
    private List<IngredientListSlot> slots;

    @Inject(method = "render", at = @At("TAIL"))
    private void ae2uu$renderNetworkStock(GuiGraphics guiGraphics, CallbackInfo ci) {
        StockOverlayRenderer.render(guiGraphics, this.slots);
    }
}
