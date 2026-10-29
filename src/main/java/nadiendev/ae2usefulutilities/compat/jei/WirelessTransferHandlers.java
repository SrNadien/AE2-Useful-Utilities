package nadiendev.ae2usefulutilities.compat.jei;

import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;

public final class WirelessTransferHandlers {

    private WirelessTransferHandlers() {
    }

    public static void register(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new CraftingTerminalTransferHandler<>(WCTMenu.class, registration.getTransferHelper()),
                RecipeTypes.CRAFTING);
        registration.addUniversalRecipeTransferHandler(
                new PatternEncodeTransferHandler<>(WETMenu.class, registration.getTransferHelper()));
    }
}
