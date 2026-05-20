package dev.rinchan.sixattributes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class SixAttributeEffects {
    private static final ResourceLocation STRENGTH_ID = SixAttributes.id("strength_attack_damage");
    private static final ResourceLocation DEXTERITY_ID = SixAttributes.id("dexterity_attack_speed");
    private static final ResourceLocation CONSTITUTION_ID = SixAttributes.id("constitution_max_health");

    private SixAttributeEffects() {
    }

    public static void apply(ServerPlayer player) {
        SixAttributeData data = SixAttributeData.get(player);
        updateModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), STRENGTH_ID, SixAttributes.percentBonus(data.value(SixAttributes.STRENGTH)), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        updateModifier(player.getAttribute(Attributes.ATTACK_SPEED), DEXTERITY_ID, SixAttributes.percentBonus(data.value(SixAttributes.DEXTERITY)), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        updateModifier(player.getAttribute(Attributes.MAX_HEALTH), CONSTITUTION_ID, SixAttributes.percentBonus(data.value(SixAttributes.CONSTITUTION)), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void updateModifier(AttributeInstance instance, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        if (instance == null) {
            return;
        }
        instance.removeModifier(id);
        if (Math.abs(amount) > 0.0001D) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, amount, operation));
        }
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getSource().is(DamageTypes.MAGIC) || event.getSource().is(DamageTypes.INDIRECT_MAGIC)) {
            int intelligence = SixAttributeData.get(player).value(SixAttributes.INTELLIGENCE);
            event.setAmount((float)(event.getAmount() * (1.0D + SixAttributes.percentBonus(intelligence))));
        }
    }
}
