package dev.rinchan.dndsixattributes.client;

import dev.rinchan.dndsixattributes.DndSixAttributes;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class DndSixAttributesClient {
    private static final Set<Integer> GLOWING_ENTITIES = new HashSet<>();

    private DndSixAttributesClient() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(DndSixAttributesClient::onScreenInit);
        NeoForge.EVENT_BUS.addListener(DndSixAttributesClient::onClientTick);
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            int x = screen.width / 2 + 75;
            int y = screen.height / 2 - 86;
            event.addListener(Button.builder(Component.literal("◈"), button -> Minecraft.getInstance().setScreen(new DndSixAttributesScreen()))
                .bounds(x, y, 20, 20)
                .tooltip(net.minecraft.client.gui.components.Tooltip.create(Component.translatable("screen.dnd_six_attributes.open")))
                .build());
        }
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            clearGlowing(minecraft);
            return;
        }
        int wisdom = DndSixAttributesClientState.data().value(DndSixAttributes.WISDOM);
        double range = Math.max(0, (wisdom - 10) * 2.0D);
        Set<Integer> next = new HashSet<>();
        if (range > 0) {
            double rangeSq = range * range;
            for (Entity entity : minecraft.level.entitiesForRendering()) {
                if (entity != minecraft.player && entity.isAlive() && entity.distanceToSqr(minecraft.player) <= rangeSq) {
                    entity.setGlowingTag(true);
                    next.add(entity.getId());
                }
            }
        }
        for (Integer id : GLOWING_ENTITIES) {
            if (!next.contains(id)) {
                Entity old = minecraft.level.getEntity(id);
                if (old != null) {
                    old.setGlowingTag(false);
                }
            }
        }
        GLOWING_ENTITIES.clear();
        GLOWING_ENTITIES.addAll(next);
    }

    private static void clearGlowing(Minecraft minecraft) {
        if (minecraft.level != null) {
            for (Integer id : GLOWING_ENTITIES) {
                Entity old = minecraft.level.getEntity(id);
                if (old != null) {
                    old.setGlowingTag(false);
                }
            }
        }
        GLOWING_ENTITIES.clear();
    }
}
