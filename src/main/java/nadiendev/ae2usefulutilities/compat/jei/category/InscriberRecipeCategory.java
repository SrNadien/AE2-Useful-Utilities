package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.client.gui.implementations.InscriberScreen;
import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.InscriberRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
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

public class InscriberRecipeCategory implements IRecipeCategory<RecipeHolder<InscriberRecipe>> {

    public static final RecipeType<RecipeHolder<InscriberRecipe>> TYPE = RecipeType
            .createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "inscriber"));

    private static final int WIDTH = 106;
    private static final int HEIGHT = 54;

    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final IDrawable icon;

    public InscriberRecipeCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        ResourceLocation texture = AppEng.makeId("textures/guis/inscriber.png");
        this.background = guiHelper.createDrawable(texture, 36, 20, WIDTH, HEIGHT);
        IDrawableStatic bar = guiHelper.drawableBuilder(texture, 176, 0, 6, 18).build();
        this.progress = guiHelper.createAnimatedDrawable(bar, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.icon = guiHelper.createDrawableItemStack(AEBlocks.INSCRIBER.stack());
    }

    @Override
    public RecipeType<RecipeHolder<InscriberRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.ae2.inscriber");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<InscriberRecipe> holder) {
        return holder.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<InscriberRecipe> holder, IFocusGroup focuses) {
        InscriberRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .setSlotName("top")
                .addIngredients(recipe.getTopOptional());
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 19)
                .setSlotName("middle")
                .addIngredients(recipe.getMiddleInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 35)
                .setSlotName("bottom")
                .addIngredients(recipe.getBottomOptional());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 20)
                .setSlotName("output")
                .addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(RecipeHolder<InscriberRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        progress.draw(guiGraphics, 99, 19);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        List<RecipeHolder<InscriberRecipe>> recipes = level == null
                ? List.of()
                : level.getRecipeManager().getAllRecipesFor(AERecipeTypes.INSCRIBER);
        registration.addRecipes(TYPE, recipes);
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AEBlocks.INSCRIBER.stack(), TYPE);
    }

    public static void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(InscriberScreen.class, 82, 39, 26, 16, TYPE);
    }
}
