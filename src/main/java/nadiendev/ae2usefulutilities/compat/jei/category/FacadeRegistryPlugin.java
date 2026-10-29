package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.items.parts.FacadeItem;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.List;
import java.util.Optional;

public class FacadeRegistryPlugin implements IRecipeManagerPlugin {

    private final FacadeItem facadeItem;
    private final Ingredient cableAnchor;

    public FacadeRegistryPlugin() {
        this.facadeItem = AEItems.FACADE.get();
        this.cableAnchor = Ingredient.of(AEParts.CABLE_ANCHOR);
    }

    public static void register(IAdvancedRegistration registration) {
        registration.addRecipeManagerPlugin(new FacadeRegistryPlugin());
    }

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        ItemStack stack = focus.getTypedValue().getIngredient(VanillaTypes.ITEM_STACK).orElse(null);
        if (stack == null) {
            return List.of();
        }
        if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
            if (stack.getItem() instanceof FacadeItem) {
                return List.of(RecipeTypes.CRAFTING);
            }
        } else if (focus.getRole() == RecipeIngredientRole.INPUT) {
            if (!facadeItem.createFacadeForItem(stack, true).isEmpty()) {
                return List.of(RecipeTypes.CRAFTING);
            }
        }
        return List.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        if (!RecipeTypes.CRAFTING.equals(recipeCategory.getRecipeType())) {
            return List.of();
        }
        ItemStack stack = focus.getTypedValue().getIngredient(VanillaTypes.ITEM_STACK).orElse(null);
        if (stack == null) {
            return List.of();
        }
        if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
            if (stack.getItem() instanceof FacadeItem item) {
                ItemStack textureItem = item.getTextureItem(stack);
                if (!textureItem.isEmpty()) {
                    return List.of((T) make(textureItem, stack));
                }
            }
        } else if (focus.getRole() == RecipeIngredientRole.INPUT) {
            ItemStack facade = facadeItem.createFacadeForItem(stack, false);
            if (!facade.isEmpty()) {
                return List.of((T) make(stack, facade));
            }
        }
        return List.of();
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        return List.of();
    }

    private RecipeHolder<ShapedRecipe> make(ItemStack textureItem, ItemStack result) {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
        ingredients.set(1, cableAnchor);
        ingredients.set(3, cableAnchor);
        ingredients.set(5, cableAnchor);
        ingredients.set(7, cableAnchor);
        ingredients.set(4, Ingredient.of(textureItem.getItem()));
        ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, ingredients, Optional.empty());

        ItemStack output = result.copy();
        output.setCount(4);

        ResourceLocation textureId = BuiltInRegistries.ITEM.getKey(textureItem.getItem());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID,
                "facade/" + textureId.getNamespace() + "/" + textureId.getPath());
        return new RecipeHolder<>(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, output));
    }
}
