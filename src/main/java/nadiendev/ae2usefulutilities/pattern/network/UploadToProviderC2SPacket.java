package nadiendev.ae2usefulutilities.pattern.network;

import nadiendev.ae2usefulutilities.pattern.ProviderKey;
import nadiendev.ae2usefulutilities.pattern.server.PatternUploadService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UploadToProviderC2SPacket(ProviderKey key) implements CustomPacketPayload {

    public static final Type<UploadToProviderC2SPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(PatternNetwork.MODID, "upload_to_provider"));

    public static final StreamCodec<FriendlyByteBuf, UploadToProviderC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> ProviderKey.STREAM_CODEC.encode(buf, pkt.key()),
            buf -> new UploadToProviderC2SPacket(ProviderKey.STREAM_CODEC.decode(buf)));

    public static void handle(UploadToProviderC2SPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                PatternUploadService.uploadTo(player, msg.key());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
