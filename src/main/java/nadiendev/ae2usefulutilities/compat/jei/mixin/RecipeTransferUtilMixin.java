package nadiendev.ae2usefulutilities.compat.jei.mixin;

import appeng.menu.me.items.PatternEncodingTermMenu;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.common.transfer.RecipeTransferService;
import nadiendev.ae2usefulutilities.pattern.client.PatternPackets;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeTransferService.class, remap = false)
public class RecipeTransferUtilMixin {

    @Inject(method = "transferRecipe(Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Lmezz/jei/api/gui/IRecipeLayoutDrawable;Lnet/minecraft/world/entity/player/Player;Z)Z", at = @At("RETURN"))
    private <C extends AbstractContainerMenu> void ae2uu$captureCategory(AbstractContainerScreen<C> screen,
            IRecipeLayoutDrawable<?> recipeLayout, Player player, boolean maxTransfer,
            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || !(screen.getMenu() instanceof PatternEncodingTermMenu)) {
            return;
        }
        Identifier categoryId = recipeLayout.getRecipeCategory().getRecipeType().getUid();
        PatternPackets.sendCategory(categoryId);
    }
}
