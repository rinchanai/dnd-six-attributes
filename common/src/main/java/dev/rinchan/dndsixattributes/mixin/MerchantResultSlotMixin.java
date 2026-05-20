package dev.rinchan.dndsixattributes.mixin;

import dev.rinchan.dndsixattributes.DndSixAttributesTradeContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public abstract class MerchantResultSlotMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void sixAttributes$pushTakingPlayer(Player player, ItemStack stack, CallbackInfo ci) {
        DndSixAttributesTradeContext.push(player);
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void sixAttributes$clearTakingPlayer(Player player, ItemStack stack, CallbackInfo ci) {
        DndSixAttributesTradeContext.clear();
    }
}
