package dev.rinchan.sixattributes;

import net.minecraft.world.entity.player.Player;

public final class SixAttributesTradeContext {
    private static final ThreadLocal<Player> CURRENT_PLAYER = new ThreadLocal<>();

    private SixAttributesTradeContext() {
    }

    public static Player currentPlayer() {
        return CURRENT_PLAYER.get();
    }

    public static void push(Player player) {
        CURRENT_PLAYER.set(player);
    }

    public static void clear() {
        CURRENT_PLAYER.remove();
    }

    public static int adjustCost(Player player, int original) {
        if (player == null || original <= 1) {
            return original;
        }
        int charisma = SixAttributeData.get(player).value(SixAttributes.CHARISMA);
        double bonus = SixAttributes.percentBonus(charisma);
        int adjusted = (int)Math.ceil(original * (1.0D - bonus));
        return Math.max(1, adjusted);
    }
}
