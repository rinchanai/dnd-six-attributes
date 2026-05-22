package dev.rinchan.dndsixattributes.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

final class AttributeIconButton extends Button {
    private final ResourceLocation texture;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    AttributeIconButton(int x, int y, int width, int height, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.yDiffTex = yDiffTex;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int v = isHoveredOrFocused() ? yDiffTex : 0;
        graphics.blit(texture, getX(), getY(), 0, v, width, height, textureWidth, textureHeight);
    }
}
