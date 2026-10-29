package nadiendev.ae2usefulutilities.compat.jei;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.items.PatternEncodingTermMenu;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternEncodeTransferHandler<T extends PatternEncodingTermMenu> implements IUniversalRecipeTransferHandler<T> {

    private static final int GRID_SLOTS = 9;

    private final Class<T> menuClass;
    private final IRecipeTransferHandlerHelper helper;

    public PatternEncodeTransferHandler(Class<T> menuClass, IRecipeTransferHandlerHelper helper) {
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

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(T menu, Object recipeBase,
            IRecipeSlotsView slots, Player player, boolean maxTransfer, boolean doTransfer) {
        RecipeHolder<?> holder = recipeBase instanceof RecipeHolder<?> found ? found : null;
        Recipe<?> recipe = holder == null ? null : holder.value();
        boolean crafting = recipe != null && EncodingHelper.isSupportedCraftingRecipe(recipe);

        if (crafting && !recipe.canCraftInDimensions(3, 3)) {
            return helper.createUserErrorWithTooltip(Component.translatable("ae2usefulutilities.jei.recipe_too_large"));
        }

        if (!doTransfer) {
            return new CraftableSlotsError(findCraftableSlots(menu, slots));
        }

        if (crafting) {
            EncodingHelper.encodeCraftingRecipe(menu, holder, craftingGrid(slots), PatternEncodeTransferHandler::isVisible);
        } else {
            EncodingHelper.encodeProcessingRecipe(menu, JeiKeys.inputs(slots), JeiKeys.outputs(slots));
        }
        return null;
    }

    private static boolean isVisible(ItemStack stack) {
        IJeiRuntime runtime = JeiRuntimeHolder.get();
        return runtime == null || runtime.getJeiHelpers().getIngredientVisibility().isIngredientVisible(VanillaTypes.ITEM_STACK, stack);
    }

    private static List<List<GenericStack>> craftingGrid(IRecipeSlotsView slots) {
        List<IRecipeSlotView> inputs = slots.getSlotViews(RecipeIngredientRole.INPUT);
        List<List<GenericStack>> grid = new ArrayList<>(GRID_SLOTS);
        for (int i = 0; i < GRID_SLOTS; i++) {
            grid.add(i < inputs.size() ? JeiKeys.ofSlot(inputs.get(i)) : Collections.emptyList());
        }
        return grid;
    }

    private static List<IRecipeSlotView> findCraftableSlots(PatternEncodingTermMenu menu, IRecipeSlotsView slots) {
        var repo = menu.getClientRepo();
        if (repo == null) {
            return List.of();
        }
        Set<AEKey> craftable = repo.getAllEntries().stream()
                .filter(GridInventoryEntry::isCraftable)
                .map(GridInventoryEntry::getWhat)
                .collect(Collectors.toSet());
        return slots.getSlotViews(RecipeIngredientRole.INPUT).stream()
                .filter(slot -> JeiKeys.ofSlot(slot).stream().anyMatch(stack -> craftable.contains(stack.what())))
                .toList();
    }

    private record CraftableSlotsError(List<IRecipeSlotView> craftableSlots) implements IRecipeTransferError {

        @Override
        public Type getType() {
            return Type.COSMETIC;
        }

        @Override
        public int getButtonHighlightColor() {
            return 0;
        }

        @Override
        public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots,
                int recipeX, int recipeY) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(recipeX, recipeY, 0);
            for (IRecipeSlotView slot : craftableSlots) {
                slot.drawHighlight(guiGraphics, TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR);
            }
            guiGraphics.pose().popPose();
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font,
                    TransferHelper.createEncodingTooltip(!craftableSlots.isEmpty(), false), mouseX, mouseY);
        }
    }
}
