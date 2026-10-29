package nadiendev.ae2usefulutilities.stock.server;

import appeng.api.stacks.AEKey;
import appeng.menu.me.crafting.CraftAmountMenu;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkCraftService {

    private NetworkCraftService() {
    }

    public static void open(ServerPlayer player, AEKey key) {
        if (!AE2UtilitiesConfig.NETWORK_STOCK_ENABLED.get()) {
            return;
        }
        TerminalAccess access = TerminalLocator.find(player);
        if (access == null || access.locator() == null) {
            say(player, "ae2usefulutilities.craft.nogrid");
            return;
        }
        if (!access.grid().getCraftingService().isCraftable(key)) {
            say(player, "ae2usefulutilities.craft.notcraftable");
            return;
        }
        CraftAmountMenu.open(player, access.locator(), key, 1);
    }

    private static void say(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.RED), true);
    }
}
