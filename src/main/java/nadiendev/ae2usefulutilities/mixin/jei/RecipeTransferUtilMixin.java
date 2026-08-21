package nadiendev.ae2usefulutilities.mixin.jei;

import appeng.menu.me.items.PatternEncodingTermMenu;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.transfer.RecipeTransferUtil;
import nadiendev.ae2usefulutilities.pattern.client.PatternPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeTransferUtil.class)
public class RecipeTransferUtilMixin {

    @Inject(method = "transferRecipe(Lmezz/jei/api/recipe/transfer/IRecipeTransferManager;Lnet/minecraft/world/inventory/AbstractContainerMenu;Lmezz/jei/api/gui/IRecipeLayoutDrawable;Lnet/minecraft/world/entity/player/Player;Z)Z", at = @At("RETURN"), remap = false)
    private static void ae2uu$captureCategory(IRecipeTransferManager recipeTransferManager,
            AbstractContainerMenu container, IRecipeLayoutDrawable<?> recipeLayout, Player player,
            boolean maxTransfer, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || !(container instanceof PatternEncodingTermMenu)) {
            return;
        }
        ResourceLocation categoryId = recipeLayout.getRecipeCategory().getRecipeType().getUid();
        PatternPackets.sendCategory(categoryId);
    }
}
