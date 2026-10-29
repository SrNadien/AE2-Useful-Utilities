package nadiendev.ae2usefulutilities.compat.jei;

import appeng.client.gui.AEBaseScreen;
import appeng.core.AEConfig;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.integration.abstraction.ItemListMod;
import appeng.integration.abstraction.ItemListModAdapter;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.me.items.WirelessCraftingTermMenu;
import appeng.client.gui.StackWithBounds;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import nadiendev.ae2usefulutilities.compat.jei.category.AttunementCategory;
import nadiendev.ae2usefulutilities.compat.jei.category.CertusGrowthCategory;
import nadiendev.ae2usefulutilities.compat.jei.category.ChargerCategory;
import nadiendev.ae2usefulutilities.compat.jei.category.CondenserCategory;
import nadiendev.ae2usefulutilities.compat.jei.category.EntropyManipulatorCategory;
import nadiendev.ae2usefulutilities.compat.jei.category.FacadeRegistryPlugin;
import nadiendev.ae2usefulutilities.compat.jei.category.InscriberRecipeCategory;
import nadiendev.ae2usefulutilities.compat.jei.category.TransformCategory;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Optional;

@JeiPlugin
public class Ae2JeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "jei");

    private static boolean standalone() {
        return !ModList.get().isLoaded("ae2jeiintegration");
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        if (standalone()) {
            PluginExtras.registerSubtypes(registration);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!standalone()) {
            return;
        }
        IJeiHelpers helpers = registration.getJeiHelpers();
        registration.addRecipeCategories(
                new InscriberRecipeCategory(helpers),
                new ChargerCategory(helpers),
                new CondenserCategory(helpers),
                new TransformCategory(helpers),
                new EntropyManipulatorCategory(helpers),
                new AttunementCategory(helpers),
                new CertusGrowthCategory(helpers));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!standalone()) {
            return;
        }
        InscriberRecipeCategory.registerRecipes(registration);
        ChargerCategory.registerRecipes(registration);
        CondenserCategory.registerRecipes(registration);
        TransformCategory.registerRecipes(registration);
        EntropyManipulatorCategory.registerRecipes(registration);
        AttunementCategory.registerRecipes(registration);
        CertusGrowthCategory.registerRecipes(registration);
        PluginExtras.registerDescriptions(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (!standalone()) {
            return;
        }
        InscriberRecipeCategory.registerCatalysts(registration);
        ChargerCategory.registerCatalysts(registration);
        CondenserCategory.registerCatalysts(registration);
        TransformCategory.registerCatalysts(registration);
        EntropyManipulatorCategory.registerCatalysts(registration);
        AttunementCategory.registerCatalysts(registration);
        CertusGrowthCategory.registerCatalysts(registration);
        registration.addRecipeCatalyst(AEParts.CRAFTING_TERMINAL.stack(), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(AEItems.WIRELESS_CRAFTING_TERMINAL.stack(), RecipeTypes.CRAFTING);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        if (standalone() && AEConfig.instance().isEnableFacadeRecipesInRecipeViewer()) {
            FacadeRegistryPlugin.register(registration);
        }
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        if (!standalone()) {
            return;
        }
        IRecipeTransferHandlerHelper helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new CraftingTerminalTransferHandler<>(CraftingTermMenu.class, helper),
                RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(
                new CraftingTerminalTransferHandler<>(WirelessCraftingTermMenu.class, helper), RecipeTypes.CRAFTING);
        registration.addUniversalRecipeTransferHandler(
                new PatternEncodeTransferHandler<>(PatternEncodingTermMenu.class, helper));
        if (ModList.get().isLoaded("ae2wtlib")) {
            WirelessTransferHandlers.register(registration);
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (!standalone()) {
            return;
        }
        registration.addGenericGuiContainerHandler(AEBaseScreen.class, new IGuiContainerHandler<AEBaseScreen<?>>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(AEBaseScreen<?> screen) {
                return screen.getExclusionZones();
            }

            @Override
            public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
                    IClickableIngredientFactory factory, AEBaseScreen<?> screen, double mouseX, double mouseY) {
                StackWithBounds hovered = screen.getStackUnderMouse(mouseX, mouseY);
                IJeiRuntime runtime = JeiRuntimeHolder.get();
                if (hovered == null || runtime == null) {
                    return Optional.empty();
                }
                ITypedIngredient<?> ingredient = JeiKeys.toIngredient(runtime.getIngredientManager(), hovered.stack());
                return ingredient == null ? Optional.empty() : build(factory, ingredient, hovered.bounds());
            }
        });
        registration.addGhostIngredientHandler((Class) AEBaseScreen.class, new AeGhostIngredientHandler());
        InscriberRecipeCategory.registerGuiHandlers(registration);
    }

    private static <T> Optional<? extends IClickableIngredient<?>> build(IClickableIngredientFactory factory,
            ITypedIngredient<T> ingredient, Rect2i area) {
        return factory.createBuilder(ingredient).buildWithArea(area);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        JeiRuntimeHolder.set(runtime);
        if (standalone()) {
            PluginExtras.hideUnwanted(runtime);
            ItemListMod.setAdapter(new JeiSearchAdapter(runtime));
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        JeiRuntimeHolder.set(null);
        if (standalone()) {
            ItemListMod.setAdapter(ItemListModAdapter.none());
        }
    }
}
