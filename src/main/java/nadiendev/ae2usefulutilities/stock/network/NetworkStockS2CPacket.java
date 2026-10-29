package nadiendev.ae2usefulutilities.stock.network;

import appeng.api.stacks.AEKey;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import nadiendev.ae2usefulutilities.stock.client.NetworkStockCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record NetworkStockS2CPacket(boolean connected, boolean reset, List<Entry> entries)
        implements CustomPacketPayload {

    public record Entry(AEKey key, long amount, boolean craftable) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                AEKey.STREAM_CODEC, Entry::key,
                ByteBufCodecs.VAR_LONG, Entry::amount,
                ByteBufCodecs.BOOL, Entry::craftable,
                Entry::new);

        public boolean isRemoval() {
            return amount <= 0 && !craftable;
        }
    }

    public static final Type<NetworkStockS2CPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "network_stock"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkStockS2CPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, NetworkStockS2CPacket::connected,
                    ByteBufCodecs.BOOL, NetworkStockS2CPacket::reset,
                    Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), NetworkStockS2CPacket::entries,
                    NetworkStockS2CPacket::new);

    public static void handle(NetworkStockS2CPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                NetworkStockCache.apply(msg);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
