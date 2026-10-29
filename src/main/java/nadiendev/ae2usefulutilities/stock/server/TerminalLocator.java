package nadiendev.ae2usefulutilities.stock.server;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.common.MEStorageMenu;
import nadiendev.ae2usefulutilities.compat.curios.CuriosCompat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TerminalLocator {

    private TerminalLocator() {
    }

    @Nullable
    public static TerminalAccess find(ServerPlayer player) {
        if (player.containerMenu instanceof MEStorageMenu menu) {
            IGridNode node = menu.getGridNode();
            if (node != null && node.getGrid() != null) {
                return new TerminalAccess(node.getGrid(), menu.getLocator());
            }
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return null;
        }
        List<ItemStack> items = player.getInventory().getNonEquipmentItems();
        for (int slot = 0; slot < items.size(); slot++) {
            IGrid grid = gridFromTerminal(items.get(slot), level);
            if (grid != null) {
                return new TerminalAccess(grid, MenuLocators.forInventorySlot(slot));
            }
        }
        IGrid offhand = gridFromTerminal(player.getOffhandItem(), level);
        if (offhand != null) {
            return new TerminalAccess(offhand, MenuLocators.forInventorySlot(Inventory.SLOT_OFFHAND));
        }
        return ModList.get().isLoaded("curios") ? CuriosCompat.findAccess(player, level) : null;
    }

    @Nullable
    public static IGrid gridFromTerminal(ItemStack stack, ServerLevel level) {
        if (stack.isEmpty() || !(stack.getItem() instanceof WirelessTerminalItem terminal)) {
            return null;
        }
        if (terminal.getAECurrentPower(stack) <= 0) {
            return null;
        }
        return terminal.getLinkedGrid(stack, level, null);
    }
}
