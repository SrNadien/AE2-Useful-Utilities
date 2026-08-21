package nadiendev.ae2usefulutilities.pattern;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

/**
 * One row of the provider picker: what it is called in the Pattern Access Terminal and how much room it has left.
 */
public record ProviderEntry(ProviderKey key, Component name, int freeSlots, int totalSlots) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ProviderEntry> STREAM_CODEC = StreamCodec.of(
            (buf, entry) -> {
                ProviderKey.STREAM_CODEC.encode(buf, entry.key());
                ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, entry.name());
                buf.writeVarInt(entry.freeSlots());
                buf.writeVarInt(entry.totalSlots());
            },
            buf -> new ProviderEntry(
                    ProviderKey.STREAM_CODEC.decode(buf),
                    ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf),
                    buf.readVarInt(),
                    buf.readVarInt()));
}
