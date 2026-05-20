package dev.rinchan.sixattributes;

import dev.rinchan.sixattributes.api.SixAttributeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class SixAttributes {
    public static final String MOD_ID = "six_attributes";

    public static final ResourceLocation STRENGTH = id("strength");
    public static final ResourceLocation DEXTERITY = id("dexterity");
    public static final ResourceLocation CONSTITUTION = id("constitution");
    public static final ResourceLocation INTELLIGENCE = id("intelligence");
    public static final ResourceLocation WISDOM = id("wisdom");
    public static final ResourceLocation CHARISMA = id("charisma");

    private SixAttributes() {
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
