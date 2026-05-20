package dev.rinchan.dndsixattributes.api;

import dev.rinchan.dndsixattributes.DndSixAttributes;
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
        register(new SixAttribute(DndSixAttributes.STRENGTH, 10, 0, 20, 0));
        register(new SixAttribute(DndSixAttributes.DEXTERITY, 10, 0, 20, 10));
        register(new SixAttribute(DndSixAttributes.CONSTITUTION, 10, 0, 20, 20));
        register(new SixAttribute(DndSixAttributes.INTELLIGENCE, 10, 0, 20, 30));
        register(new SixAttribute(DndSixAttributes.WISDOM, 10, 0, 20, 40));
        register(new SixAttribute(DndSixAttributes.CHARISMA, 10, 0, 20, 50));
    }

    public static synchronized SixAttribute register(SixAttribute attribute) {
        if (ATTRIBUTES.containsKey(attribute.id())) {
            throw new IllegalArgumentException("Duplicate DnD Six Attributes id: " + attribute.id());
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
