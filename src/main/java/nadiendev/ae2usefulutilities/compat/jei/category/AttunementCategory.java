package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.api.features.P2PTunnelAttunementInternal;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ItemModText;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AttunementCategory extends AbstractRecipeCategory<AttunementDisplay> {

    public static final RecipeType<AttunementDisplay> TYPE = RecipeType.create(PatternNetwork.MODID, "attunement",
            AttunementDisplay.class);

    private static final int WIDTH = 130;
    private static final int HEIGHT = 36;

    public AttunementCategory(IJeiHelpers helpers) {
        super(TYPE, ItemModText.P2P_TUNNEL_ATTUNEMENT.text(),
                helpers.getGuiHelper().createDrawableItemLike(AEParts.ME_P2P_TUNNEL), WIDTH, HEIGHT);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        List<AttunementDisplay> displays = new ArrayList<>();

        for (var entry : P2PTunnelAttunementInternal.getApiTunnels()) {
            displays.add(new AttunementDisplay(
                    Ingredient.of(BuiltInRegistries.ITEM.stream()
                            .filter(item -> entry.stackPredicate().test(item.getDefaultInstance()))
                            .toArray(Item[]::new)),
                    entry.tunnelType(),
                    ItemModText.P2P_API_ATTUNEMENT.text(),
                    entry.description()));
        }

        for (var entry : P2PTunnelAttunementInternal.getTagTunnels().entrySet()) {
            displays.add(new AttunementDisplay(
                    Ingredient.of(entry.getKey()),
                    entry.getValue(),
                    ItemModText.P2P_TAG_ATTUNEMENT.text(),
                    Component.empty()));
        }

        displays.removeIf(display -> display.inputs().isEmpty());
        registration.addRecipes(TYPE, displays);
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AttunementDisplay display, IFocusGroup focuses) {
        int x = WIDTH / 2 - 41;
        int y = HEIGHT / 2 - 13;

        builder.addInputSlot(x + 4, y + 5)
                .setStandardSlotBackground()
                .addIngredients(display.inputs())
                .addRichTooltipCallback((slotView, tooltip) -> {
                    tooltip.add(display.title());
                    if (!display.description().getString().isEmpty()) {
                        tooltip.add(display.description());
                    }
                });
        builder.addOutputSlot(x + 61, y + 5)
                .setStandardSlotBackground()
                .addItemLike(display.tunnel());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, AttunementDisplay display, IFocusGroup focuses) {
        builder.addRecipeArrow(WIDTH / 2 - 12, HEIGHT / 2 - 9);
    }
}
