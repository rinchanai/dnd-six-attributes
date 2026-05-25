package dev.rinchan.dndsixattributes.client;

import dev.rinchan.dndsixattributes.DndSixAttributes;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class DndSixAttributesClient {
    private static final Set<Integer> GLOWING_ENTITIES = new HashSet<>();
    private static final ResourceLocation ATTRIBUTE_BUTTON = DndSixAttributes.id("textures/gui/attribute_button.png");

    private DndSixAttributesClient() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(DndSixAttributesClient::onScreenInit);
        NeoForge.EVENT_BUS.addListener(DndSixAttributesClient::onClientTick);
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            AttributeIconButton button = new AttributeIconButton(screen, -13, 56, 14, 18, 19, ATTRIBUTE_BUTTON, 14, 37, ignored -> Minecraft.getInstance().setScreen(new DndSixAttributesScreen()), net.minecraft.network.chat.Component.translatable("screen.dnd_six_attributes.open"));
            button.setTooltip(Tooltip.create(net.minecraft.network.chat.Component.translatable("screen.dnd_six_attributes.open")));
            event.addListener(button);
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
