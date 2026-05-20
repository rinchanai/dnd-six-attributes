package dev.rinchan.dndsixattributes;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DndSixAttributesSyncPacket(int availablePoints, Map<ResourceLocation, Integer> allocated) implements CustomPacketPayload {
    public static final Type<DndSixAttributesSyncPacket> TYPE = new Type<>(DndSixAttributes.id("sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DndSixAttributesSyncPacket> CODEC = new StreamCodec<>() {
        @Override
        public DndSixAttributesSyncPacket decode(RegistryFriendlyByteBuf buf) {
            int points = buf.readVarInt();
            int size = buf.readVarInt();
            Map<ResourceLocation, Integer> allocated = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                allocated.put(ResourceLocation.STREAM_CODEC.decode(buf), buf.readVarInt());
            }
            return new DndSixAttributesSyncPacket(points, allocated);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, DndSixAttributesSyncPacket packet) {
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
