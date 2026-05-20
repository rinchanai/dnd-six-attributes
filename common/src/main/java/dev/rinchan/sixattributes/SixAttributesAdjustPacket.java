package dev.rinchan.sixattributes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SixAttributesAdjustPacket(ResourceLocation attributeId, int delta) implements CustomPacketPayload {
    public static final Type<SixAttributesAdjustPacket> TYPE = new Type<>(SixAttributes.id("adjust"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SixAttributesAdjustPacket> CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        SixAttributesAdjustPacket::attributeId,
        ByteBufCodecs.VAR_INT,
        SixAttributesAdjustPacket::delta,
        SixAttributesAdjustPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
