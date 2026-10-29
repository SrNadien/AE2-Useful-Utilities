package nadiendev.ae2usefulutilities.stock.network;

import appeng.api.stacks.AEKey;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import nadiendev.ae2usefulutilities.stock.server.NetworkCraftService;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenCraftC2SPacket(AEKey key) implements CustomPacketPayload {

    public static final Type<OpenCraftC2SPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(PatternNetwork.MODID, "open_craft"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCraftC2SPacket> STREAM_CODEC =
            AEKey.STREAM_CODEC.map(OpenCraftC2SPacket::new, OpenCraftC2SPacket::key);

    public static void handle(OpenCraftC2SPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                NetworkCraftService.open(player, msg.key());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
