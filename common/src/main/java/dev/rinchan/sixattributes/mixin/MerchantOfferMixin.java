package dev.rinchan.sixattributes.mixin;

import dev.rinchan.sixattributes.SixAttributesTradeContext;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {
    @Inject(method = "getModifiedCostCount", at = @At("RETURN"), cancellable = true)
    private void sixAttributes$adjustCharismaPrice(ItemCost cost, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SixAttributesTradeContext.adjustCost(SixAttributesTradeContext.currentPlayer(), cir.getReturnValueI()));
    }
}
