package nadiendev.ae2usefulutilities.pattern.network;

import nadiendev.ae2usefulutilities.pattern.server.PatternUploadService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestProvidersC2SPacket() implements CustomPacketPayload {

    public static final Type<RequestProvidersC2SPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(PatternNetwork.MODID, "request_providers"));

    public static final StreamCodec<FriendlyByteBuf, RequestProvidersC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
            },
            buf -> new RequestProvidersC2SPacket());

    public static void handle(RequestProvidersC2SPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                PatternUploadService.sendProviderList(player);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
