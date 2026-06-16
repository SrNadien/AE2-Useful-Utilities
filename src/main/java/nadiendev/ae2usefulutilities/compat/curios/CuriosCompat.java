package nadiendev.ae2usefulutilities.compat.curios;

import appeng.api.storage.MEStorage;
import nadiendev.ae2usefulutilities.handler.PickBlockHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
}