package dev.rinchan.dndsixattributes;

import dev.rinchan.dndsixattributes.api.SixAttributeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class DndSixAttributes {
    public static final String MOD_ID = "dnd_six_attributes";

    public static final ResourceLocation STRENGTH = id("strength");
    public static final ResourceLocation DEXTERITY = id("dexterity");
    public static final ResourceLocation CONSTITUTION = id("constitution");
    public static final ResourceLocation INTELLIGENCE = id("intelligence");
    public static final ResourceLocation WISDOM = id("wisdom");
    public static final ResourceLocation CHARISMA = id("charisma");

    private DndSixAttributes() {
    }

    public static void init() {
        SixAttributeRegistry.registerDefaults();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static double percentBonus(int value) {
        return (value - 10) * 0.02D;
    }
}
