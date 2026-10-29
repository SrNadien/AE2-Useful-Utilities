package nadiendev.ae2usefulutilities.compat.jei;

import appeng.integration.modules.itemlists.CraftingHelper;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.me.items.CraftingTermMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CraftingTerminalTransferHandler<T extends CraftingTermMenu>
        implements IRecipeTransferHandler<T, RecipeHolder<CraftingRecipe>> {

    private final Class<T> menuClass;
    private final IRecipeTransferHandlerHelper helper;

    public CraftingTerminalTransferHandler(Class<T> menuClass, IRecipeTransferHandlerHelper helper) {
        this.menuClass = menuClass;
        this.helper = helper;
    }

    @Override
    public Class<? extends T> getContainerClass() {
        return menuClass;
    }

    @Override
    public Optional<MenuType<T>> getMenuType() {
        return Optional.empty();
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(T menu, RecipeHolder<CraftingRecipe> holder,
            IRecipeSlotsView slots, Player player, boolean maxTransfer, boolean doTransfer) {
        Recipe<?> recipe = holder.value();
        if (!recipe.canCraftInDimensions(3, 3)) {
            return helper.createUserErrorWithTooltip(Component.translatable("ae2usefulutilities.jei.recipe_too_large"));
        }

        boolean craftMissing = Screen.hasControlDown();
        Map<Integer, Ingredient> byGuiSlot = helper.getGuiSlotIndexToIngredientMap(holder);
        var missing = menu.findMissingIngredients(byGuiSlot);
        List<IRecipeSlotView> inputs = slots.getSlotViews(RecipeIngredientRole.INPUT);

        if (missing.missingSlots().size() == byGuiSlot.size()) {
            List<IRecipeSlotView> missingViews = missing.missingSlots().stream()
                    .map(index -> index < inputs.size() ? inputs.get(index) : null)
                    .filter(Objects::nonNull)
                    .toList();
            return helper.createUserErrorForMissingSlots(Component.translatable("ae2usefulutilities.jei.no_items"),
                    missingViews);
        }

        if (!doTransfer) {
            if (missing.totalSize() != 0) {
                int color = missing.anyMissing()
                        ? TransferHelper.ORANGE_PLUS_BUTTON_COLOR
                        : TransferHelper.BLUE_PLUS_BUTTON_COLOR;
                return new MissingError(missing, craftMissing, color);
            }
            return null;
        }

        CraftingHelper.performTransfer(menu, holder.id(), recipe, craftMissing);
        return null;
    }

    private record MissingError(CraftingTermMenu.MissingIngredientSlots indices, boolean craftMissing, int color)
            implements IRecipeTransferError {

        @Override
        public Type getType() {
            return Type.COSMETIC;
        }

        @Override
        public int getButtonHighlightColor() {
            return color;
        }

        @Override
        public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots,
                int recipeX, int recipeY) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(recipeX, recipeY, 0);
            List<IRecipeSlotView> views = slots.getSlotViews(RecipeIngredientRole.INPUT);
            for (int i = 0; i < views.size(); i++) {
                boolean missing = indices.missingSlots().contains(i);
                boolean craftable = indices.craftableSlots().contains(i);
                if (missing || craftable) {
                    views.get(i).drawHighlight(guiGraphics,
                            missing ? TransferHelper.RED_SLOT_HIGHLIGHT_COLOR : TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR);
                }
            }
            guiGraphics.pose().popPose();
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font,
                    TransferHelper.createCraftingTooltip(indices, craftMissing, true), mouseX, mouseY);
        }
    }
}
