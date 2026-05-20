package dev.rinchan.sixattributes.api;

import net.minecraft.resources.ResourceLocation;

public record SixAttribute(ResourceLocation id, int defaultValue, int minValue, int maxValue, int sortOrder) {
    public String translationKey() {
        return "attribute." + id.getNamespace() + "." + id.getPath();
    }

    public String descriptionKey() {
        return translationKey() + ".description";
    }

    public String effectKey() {
        return translationKey() + ".effect";
    }
}
