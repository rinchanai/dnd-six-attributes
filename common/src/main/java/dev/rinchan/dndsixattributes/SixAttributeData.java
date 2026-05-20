package dev.rinchan.dndsixattributes;

import dev.rinchan.dndsixattributes.api.SixAttribute;
import dev.rinchan.dndsixattributes.api.SixAttributeRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public final class SixAttributeData {
    private static final String ROOT_KEY = "DndSixAttributes";
    private static final String POINTS_KEY = "AvailablePoints";
    private static final String ALLOCATED_KEY = "Allocated";

    private int availablePoints;
    private final Map<ResourceLocation, Integer> allocated = new LinkedHashMap<>();

    public int availablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(int availablePoints) {
        this.availablePoints = Math.max(0, availablePoints);
    }

    public int allocated(ResourceLocation id) {
        return allocated.getOrDefault(id, 0);
    }

    public int value(ResourceLocation id) {
        SixAttribute attribute = SixAttributeRegistry.get(id);
        int base = attribute == null ? 10 : attribute.defaultValue();
        int min = attribute == null ? 0 : attribute.minValue();
        int max = attribute == null ? 20 : attribute.maxValue();
        return Math.max(min, Math.min(max, base + allocated(id)));
    }

    public boolean adjust(ResourceLocation id, int delta) {
        SixAttribute attribute = SixAttributeRegistry.get(id);
        if (attribute == null || delta == 0) {
            return false;
        }
        int currentAllocated = allocated(id);
        int currentValue = attribute.defaultValue() + currentAllocated;
        if (delta > 0) {
            if (availablePoints < delta || currentValue + delta > attribute.maxValue()) {
                return false;
            }
            allocated.put(id, currentAllocated + delta);
            availablePoints -= delta;
            return true;
        }
        int remove = -delta;
        if (currentAllocated < remove || currentValue - remove < attribute.defaultValue()) {
            return false;
        }
        allocated.put(id, currentAllocated - remove);
        availablePoints += remove;
        return true;
    }

    public CompoundTag saveRoot() {
        CompoundTag root = new CompoundTag();
        root.putInt(POINTS_KEY, availablePoints);
        CompoundTag allocatedTag = new CompoundTag();
        for (var entry : allocated.entrySet()) {
            if (entry.getValue() != 0) {
                allocatedTag.putInt(entry.getKey().toString(), entry.getValue());
            }
        }
        root.put(ALLOCATED_KEY, allocatedTag);
        return root;
    }

    public void loadRoot(CompoundTag root) {
        availablePoints = Math.max(0, root.getInt(POINTS_KEY));
        allocated.clear();
        CompoundTag allocatedTag = root.getCompound(ALLOCATED_KEY);
        for (String key : allocatedTag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null && allocatedTag.get(key) instanceof IntTag) {
                allocated.put(id, allocatedTag.getInt(key));
            }
        }
    }

    public static SixAttributeData get(net.minecraft.world.entity.player.Player player) {
        SixAttributeData data = new SixAttributeData();
        CompoundTag persistent = player.getPersistentData();
        if (persistent.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
            data.loadRoot(persistent.getCompound(ROOT_KEY));
        }
        return data;
    }

    public static void set(net.minecraft.world.entity.player.Player player, SixAttributeData data) {
        player.getPersistentData().put(ROOT_KEY, data.saveRoot());
    }
}
