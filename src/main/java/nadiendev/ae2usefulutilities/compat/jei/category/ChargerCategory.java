package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.CrankBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.ChargerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class ChargerCategory implements IRecipeCategory<RecipeHolder<ChargerRecipe>> {

    public static final RecipeType<RecipeHolder<ChargerRecipe>> TYPE = RecipeType
            .createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "charger"));

    private static final int TEXT_COLOR = 0xFF404040;

    private final IDrawable icon;
    private final IDrawable arrow;

    public ChargerCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(AEBlocks.CHARGER.stack());
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<RecipeHolder<ChargerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.ae2.charger");
    }

    @Override
    public int getWidth() {
        return 130;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<ChargerRecipe> holder) {
        return holder.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChargerRecipe> holder, IFocusGroup focuses) {
        ChargerRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 31, 8)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 8)
                .setStandardSlotBackground()
                .addItemStack(recipe.getResultItem());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 3, 30)
                .addItemStack(AEBlocks.CRANK.stack());
    }

    @Override
    public void draw(RecipeHolder<ChargerRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 52, 8);
        int turns = (ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + CrankBlockEntity.POWER_PER_CRANK_TURN - 1)
                / CrankBlockEntity.POWER_PER_CRANK_TURN;
        Component label = Component.translatable("ae2.rei_jei_integration.charger_required_power", turns,
                ChargerBlockEntity.POWER_MAXIMUM_AMOUNT);
        guiGraphics.drawString(Minecraft.getInstance().font, label, 20, 35, TEXT_COLOR, false);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        List<RecipeHolder<ChargerRecipe>> recipes = level == null
                ? List.of()
                : level.getRecipeManager().getAllRecipesFor(AERecipeTypes.CHARGER);
        registration.addRecipes(TYPE, recipes);
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AEBlocks.CHARGER.stack(), TYPE);
        registration.addRecipeCatalyst(AEBlocks.CRANK.stack(), TYPE);
    }
}
