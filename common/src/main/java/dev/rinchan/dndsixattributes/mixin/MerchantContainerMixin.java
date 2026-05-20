package dev.rinchan.dndsixattributes.mixin;

import dev.rinchan.dndsixattributes.DndSixAttributesTradeContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantContainer.class)
public abstract class MerchantContainerMixin {
    @Shadow @Final private Merchant merchant;

    @Inject(method = "updateSellItem", at = @At("HEAD"))
    private void sixAttributes$pushTradingPlayer(CallbackInfo ci) {
        Player player = merchant.getTradingPlayer();
        if (player != null) {
            DndSixAttributesTradeContext.push(player);
        }
    }

    @Inject(method = "updateSellItem", at = @At("RETURN"))
    private void sixAttributes$clearTradingPlayer(CallbackInfo ci) {
        DndSixAttributesTradeContext.clear();
    }
}
