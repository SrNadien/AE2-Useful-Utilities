package nadiendev.ae2usefulutilities.handler;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.items.tools.powered.WirelessTerminalItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import nadiendev.ae2usefulutilities.compat.curios.CuriosCompat;

public class PickBlockHandler {

    public static final Identifier PACKET_ID =
            Identifier.fromNamespaceAndPath("ae2utilities", "pick_block");

    public record PickBlockPayload(BlockPos pos) implements CustomPacketPayload {

        public static final Type<PickBlockPayload> TYPE = new Type<>(PACKET_ID);

        public static final StreamCodec<ByteBuf, PickBlockPayload> STREAM_CODEC =
                ByteBufCodecs.fromCodec(BlockPos.CODEC).map(
                        PickBlockPayload::new,
                        PickBlockPayload::pos
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                PickBlockPayload.TYPE,
                PickBlockPayload.STREAM_CODEC,
                this::handlePickBlock
        );
    }

    private void handlePickBlock(PickBlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            BlockState state = player.level().getBlockState(payload.pos());
            if (state.isAir()) return;

            ItemStack blockItem = new ItemStack(state.getBlock());
            if (blockItem.isEmpty()) return;

            AEItemKey key = AEItemKey.of(blockItem);
            if (key == null) return;

            // Si el jugador ya tiene el bloque en hotbar o mano secundaria, no extraer nada
            if (playerHasItemInHotbarOrOffhand(player, key)) return;

            MEStorage storage = findStorage(player);
            if (storage == null) return;

            KeyCounter available = new KeyCounter();
            storage.getAvailableStacks(available);
            long has = available.get(key);
            if (has <= 0) return;

            long extracted = storage.extract(key, Math.min(64, has), Actionable.MODULATE, IActionSource.empty());
            if (extracted <= 0) return;

            ItemStack result = key.toStack((int) extracted);
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
        });
    }

    
    private boolean playerHasItemInHotbarOrOffhand(ServerPlayer player, AEItemKey key) {
        // Inventario principal completo: hotbar (0-8) + resto del inventario (9-35)
        var mainItems = player.getInventory().getNonEquipmentItems();
        for (int i = 0; i < mainItems.size(); i++) {
            ItemStack stack = mainItems.get(i);
            if (!stack.isEmpty() && AEItemKey.of(stack) != null && AEItemKey.of(stack).equals(key)) {
                return true;
            }
        }
        // Mano secundaria (offhand)
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.isEmpty() && AEItemKey.of(offhand) != null && AEItemKey.of(offhand).equals(key)) {
            return true;
        }
        return false;
    }

    private MEStorage findStorage(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return null;

        // Buscar en inventario normal
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            MEStorage storage = tryGetStorage(stack, serverLevel);
            if (storage != null) return storage;
        }

        // Buscar en Curios si está instalado
        if (ModList.get().isLoaded("curios")) {
            MEStorage storage = CuriosCompat.findStorage(player, serverLevel);
            if (storage != null) return storage;
        }

        return null;
    }

    private static MEStorage tryGetStorage(ItemStack stack, ServerLevel level) {
        if (stack.isEmpty()) return null;
        if (!(stack.getItem() instanceof WirelessTerminalItem termItem)) return null;

        var grid = termItem.getLinkedGrid(stack, level, null);
        if (grid == null) return null;

        if (termItem.getAECurrentPower(stack) <= 0) return null;

        return grid.getStorageService().getInventory();
    }

    public static MEStorage tryGetStoragePublic(ItemStack stack, ServerLevel level) {
        return tryGetStorage(stack, level);
    }
}