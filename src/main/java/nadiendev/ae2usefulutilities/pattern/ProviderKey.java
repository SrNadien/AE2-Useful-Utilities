package nadiendev.ae2usefulutilities.pattern;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies one pattern provider on the network. Cable-mounted providers share a block position, so the side is
 * part of the identity; block providers use {@link #NO_SIDE}.
 */
public record ProviderKey(ResourceLocation dimension, BlockPos pos, int side) {

    public static final int NO_SIDE = -1;

    public static final StreamCodec<FriendlyByteBuf, ProviderKey> STREAM_CODEC = StreamCodec.of(
            (buf, key) -> {
                buf.writeResourceLocation(key.dimension());
                buf.writeBlockPos(key.pos());
                buf.writeByte(key.side());
            },
            buf -> new ProviderKey(buf.readResourceLocation(), buf.readBlockPos(), buf.readByte()));

    public static ProviderKey of(ResourceLocation dimension, BlockPos pos, @Nullable Direction side) {
        return new ProviderKey(dimension, pos, side == null ? NO_SIDE : side.get3DDataValue());
    }
}
