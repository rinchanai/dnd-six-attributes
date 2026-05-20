package dev.rinchan.sixattributes.client;

import dev.rinchan.sixattributes.SixAttributeData;
import dev.rinchan.sixattributes.SixAttributesSyncPacket;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public final class SixAttributesClientState {
    private static SixAttributeData data = new SixAttributeData();

    private SixAttributesClientState() {
    }

    public static SixAttributeData data() {
        return data;
    }

    public static void apply(SixAttributesSyncPacket packet) {
        SixAttributeData next = new SixAttributeData();
        next.setAvailablePoints(packet.availablePoints());
        for (Map.Entry<ResourceLocation, Integer> entry : packet.allocated().entrySet()) {
            if (entry.getValue() > 0) {
                for (int i = 0; i < entry.getValue(); i++) {
                    next.setAvailablePoints(next.availablePoints() + 1);
                    next.adjust(entry.getKey(), 1);
                }
            }
        }
        data = next;
    }
}
