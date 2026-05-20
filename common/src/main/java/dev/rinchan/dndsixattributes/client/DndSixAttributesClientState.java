package dev.rinchan.dndsixattributes.client;

import dev.rinchan.dndsixattributes.SixAttributeData;
import dev.rinchan.dndsixattributes.DndSixAttributesSyncPacket;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public final class DndSixAttributesClientState {
    private static SixAttributeData data = new SixAttributeData();

    private DndSixAttributesClientState() {
    }

    public static SixAttributeData data() {
        return data;
    }

    public static void apply(DndSixAttributesSyncPacket packet) {
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
