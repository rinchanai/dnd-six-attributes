package dev.rinchan.dndsixattributes.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

final class AttributeIconButton extends Button {
    private final InventoryScreen parent;
    private final ResourceLocation texture;
    private final int xOffset;
    private final int yOffset;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    AttributeIconButton(InventoryScreen parent, int xOffset, int yOffset, int width, int height, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(parent.getGuiLeft() + xOffset, parent.getGuiTop() + yOffset, width, height, message, onPress, DEFAULT_NARRATION);
        this.parent = parent;
        this.texture = texture;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.yDiffTex = yDiffTex;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        setX(parent.getGuiLeft() + xOffset);
        setY(parent.getGuiTop() + yOffset);
        int v = isHoveredOrFocused() ? yDiffTex : 0;
        graphics.blit(texture, getX(), getY(), 0, v, width, height, textureWidth, textureHeight);
    }
}
