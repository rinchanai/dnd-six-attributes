package dev.rinchan.dndsixattributes.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

final class AttributeIconButton extends Button {
    private final ResourceLocation texture;

    AttributeIconButton(int x, int y, ResourceLocation texture, OnPress onPress, Component message) {
        super(x, y, 20, 18, message, onPress, DEFAULT_NARRATION);
        this.texture = texture;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int v = isHoveredOrFocused() ? 19 : 0;
        graphics.blit(texture, getX(), getY(), 0, v, width, height, 20, 38);
    }
}
