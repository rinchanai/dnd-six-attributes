package dev.rinchan.dndsixattributes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DndSixAttributesAdjustPacket(ResourceLocation attributeId, int delta) implements CustomPacketPayload {
    public static final Type<DndSixAttributesAdjustPacket> TYPE = new Type<>(DndSixAttributes.id("adjust"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DndSixAttributesAdjustPacket> CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        DndSixAttributesAdjustPacket::attributeId,
        ByteBufCodecs.VAR_INT,
        DndSixAttributesAdjustPacket::delta,
        DndSixAttributesAdjustPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
