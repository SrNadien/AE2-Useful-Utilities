package nadiendev.ae2usefulutilities.pattern.network;

import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EncodeWithCategoryIdC2SPacket(ResourceLocation id) implements CustomPacketPayload {

    public static final Type<EncodeWithCategoryIdC2SPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "encode_with_category"));

    public static final StreamCodec<FriendlyByteBuf, EncodeWithCategoryIdC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> buf.writeResourceLocation(pkt.id()),
            buf -> new EncodeWithCategoryIdC2SPacket(buf.readResourceLocation()));

    public static void handle(EncodeWithCategoryIdC2SPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) {
                return;
            }
            if (player.containerMenu instanceof PatternEncodingTermMenu menu
                    && menu instanceof IPatternCategorySync sync) {
                sync.ae2uu$setPendingCategoryId(msg.id());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
