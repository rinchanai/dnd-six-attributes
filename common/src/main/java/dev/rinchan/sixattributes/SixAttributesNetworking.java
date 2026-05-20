package dev.rinchan.sixattributes;

import java.util.LinkedHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SixAttributesNetworking {
    private SixAttributesNetworking() {
    }

    public static void sync(ServerPlayer player) {
        SixAttributeData data = SixAttributeData.get(player);
        LinkedHashMap<ResourceLocation, Integer> values = new LinkedHashMap<>();
        for (var attribute : dev.rinchan.sixattributes.api.SixAttributeRegistry.all()) {
            int allocated = data.allocated(attribute.id());
            if (allocated != 0) {
                values.put(attribute.id(), allocated);
            }
        }
        PacketDistributor.sendToPlayer(player, new SixAttributesSyncPacket(data.availablePoints(), values));
    }
}
