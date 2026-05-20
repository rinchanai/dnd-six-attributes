package dev.rinchan.sixattributes;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SixAttributesSyncPacket(int availablePoints, Map<ResourceLocation, Integer> allocated) implements CustomPacketPayload {
    public static final Type<SixAttributesSyncPacket> TYPE = new Type<>(SixAttributes.id("sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SixAttributesSyncPacket> CODEC = new StreamCodec<>() {
        @Override
        public SixAttributesSyncPacket decode(RegistryFriendlyByteBuf buf) {
            int points = buf.readVarInt();
            int size = buf.readVarInt();
            Map<ResourceLocation, Integer> allocated = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                allocated.put(ResourceLocation.STREAM_CODEC.decode(buf), buf.readVarInt());
            }
            return new SixAttributesSyncPacket(points, allocated);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SixAttributesSyncPacket packet) {
            buf.writeVarInt(packet.availablePoints());
            buf.writeVarInt(packet.allocated().size());
            for (var entry : packet.allocated().entrySet()) {
                ResourceLocation.STREAM_CODEC.encode(buf, entry.getKey());
                buf.writeVarInt(entry.getValue());
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
