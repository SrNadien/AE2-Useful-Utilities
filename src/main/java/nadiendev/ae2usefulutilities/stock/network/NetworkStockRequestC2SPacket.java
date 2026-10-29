package nadiendev.ae2usefulutilities.stock.network;

import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import nadiendev.ae2usefulutilities.stock.server.NetworkStockService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NetworkStockRequestC2SPacket(boolean full) implements CustomPacketPayload {

    public static final Type<NetworkStockRequestC2SPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "network_stock_request"));

    public static final StreamCodec<FriendlyByteBuf, NetworkStockRequestC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> buf.writeBoolean(pkt.full()),
            buf -> new NetworkStockRequestC2SPacket(buf.readBoolean()));

    public static void handle(NetworkStockRequestC2SPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                NetworkStockService.sync(player, msg.full());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
