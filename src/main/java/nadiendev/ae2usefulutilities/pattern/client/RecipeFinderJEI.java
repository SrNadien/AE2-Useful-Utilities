package nadiendev.ae2usefulutilities.pattern.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@JeiPlugin
public final class RecipeFinderJEI implements RecipeFinder, IModPlugin {

    private static IRecipeManager recipeManager;

    @Nullable
    private RecipeType<?> categoryOf(String categoryId) {
        if (categoryId == null || recipeManager == null) {
            return null;
        }
        ResourceLocation uid = ResourceLocation.tryParse(categoryId);
        if (uid == null) {
            return null;
        }
        return recipeManager.getRecipeType(uid).orElse(null);
    }

    @Override
    public @Nullable Component getWorkstationName(String categoryId) {
        RecipeType<?> category = categoryOf(categoryId);
        if (category == null) {
            return null;
        }
        Optional<ItemStack> catalyst = recipeManager.createRecipeCatalystLookup(category)
                .get()
                .map(typed -> typed.getItemStack().orElse(ItemStack.EMPTY))
                .filter(stack -> !stack.isEmpty())
                .findFirst();
        return catalyst.map(ItemStack::getHoverName).orElse(null);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("ae2usefulutilities", "jei_recipe_finder");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        recipeManager = jeiRuntime.getRecipeManager();
    }
}
