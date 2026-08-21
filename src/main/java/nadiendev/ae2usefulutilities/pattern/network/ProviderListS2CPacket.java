package nadiendev.ae2usefulutilities.pattern.network;

import nadiendev.ae2usefulutilities.pattern.ProviderEntry;
import nadiendev.ae2usefulutilities.pattern.client.ProviderPickerHooks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ProviderListS2CPacket(List<ProviderEntry> entries) implements CustomPacketPayload {

    public static final Type<ProviderListS2CPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "provider_list"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProviderListS2CPacket> STREAM_CODEC =
            ProviderEntry.STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(ProviderListS2CPacket::new, ProviderListS2CPacket::entries);

    public static void handle(ProviderListS2CPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ProviderPickerHooks.open(msg.entries());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
