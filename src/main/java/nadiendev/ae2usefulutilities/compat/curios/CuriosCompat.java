package nadiendev.ae2usefulutilities.compat.curios;

import appeng.api.networking.IGrid;
import appeng.api.storage.MEStorage;
import appeng.menu.locator.MenuLocators;
import nadiendev.ae2usefulutilities.handler.PickBlockHandler;
import nadiendev.ae2usefulutilities.stock.server.TerminalAccess;
import nadiendev.ae2usefulutilities.stock.server.TerminalLocator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;


public class CuriosCompat {

    public static MEStorage findStorage(ServerPlayer player, ServerLevel level) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> {
                    var curios = handler.getCurios();
                    for (var entry : curios.entrySet()) {
                        var slotHandler = entry.getValue().getStacks();
                        for (int i = 0; i < slotHandler.getSlots(); i++) {
                            var stack = slotHandler.getStackInSlot(i);
                            MEStorage storage = PickBlockHandler.tryGetStoragePublic(stack, level);
                            if (storage != null) return storage;
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    @Nullable
    public static TerminalAccess findAccess(ServerPlayer player, ServerLevel level) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> {
                    var equipped = handler.getEquippedCurios();
                    for (int slot = 0; slot < equipped.size(); slot++) {
                        IGrid grid = TerminalLocator.gridFromTerminal(equipped.getResource(slot).toStack(equipped.getAmountAsInt(slot)), level);
                        if (grid != null) {
                            return new TerminalAccess(grid, MenuLocators.forCurioSlot(slot));
                        }
                    }
                    return null;
                })
                .orElse(null);
    }
}
