package dev.rinchan.sixattributes.api;

import dev.rinchan.sixattributes.SixAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public final class SixAttributeRegistry {
    private static final Map<ResourceLocation, SixAttribute> ATTRIBUTES = new LinkedHashMap<>();
    private static boolean defaultsRegistered;

    private SixAttributeRegistry() {
    }

    public static synchronized void registerDefaults() {
        if (defaultsRegistered) {
            return;
        }
        defaultsRegistered = true;
        register(new SixAttribute(SixAttributes.STRENGTH, 10, 0, 20, 0));
        register(new SixAttribute(SixAttributes.DEXTERITY, 10, 0, 20, 10));
        register(new SixAttribute(SixAttributes.CONSTITUTION, 10, 0, 20, 20));
        register(new SixAttribute(SixAttributes.INTELLIGENCE, 10, 0, 20, 30));
        register(new SixAttribute(SixAttributes.WISDOM, 10, 0, 20, 40));
        register(new SixAttribute(SixAttributes.CHARISMA, 10, 0, 20, 50));
    }

    public static synchronized SixAttribute register(SixAttribute attribute) {
        if (ATTRIBUTES.containsKey(attribute.id())) {
            throw new IllegalArgumentException("Duplicate Six Attributes id: " + attribute.id());
        }
        ATTRIBUTES.put(attribute.id(), attribute);
        return attribute;
    }

    public static synchronized SixAttribute get(ResourceLocation id) {
        return ATTRIBUTES.get(id);
    }

    public static synchronized Collection<SixAttribute> all() {
        ArrayList<SixAttribute> result = new ArrayList<>(ATTRIBUTES.values());
        result.sort(Comparator.comparingInt(SixAttribute::sortOrder).thenComparing(attribute -> attribute.id().toString()));
        return Collections.unmodifiableList(result);
    }
}
