package dev.rinchan.dndsixattributes.client;

import dev.rinchan.dndsixattributes.DndSixAttributes;
import dev.rinchan.dndsixattributes.DndSixAttributesAdjustPacket;
import dev.rinchan.dndsixattributes.api.SixAttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class DndSixAttributesScreen extends Screen {
    private static final int ROW_HEIGHT = 27;
    private static final int PANEL_WIDTH = 420;
    private static final int BUTTON_AREA_WIDTH = 52;

    public DndSixAttributesScreen() {
        super(Component.translatable("screen.dnd_six_attributes.title"));
    }

    @Override
    protected void init() {
        int x = (width - PANEL_WIDTH) / 2;
        int y = 48;
        int row = 0;
        for (var attribute : SixAttributeRegistry.all()) {
            int rowY = y + row * ROW_HEIGHT;
            ResourceLocation id = attribute.id();
            addRenderableWidget(Button.builder(Component.literal("-"), button -> PacketDistributor.sendToServer(new DndSixAttributesAdjustPacket(id, -1)))
                .bounds(x + PANEL_WIDTH - 48, rowY + 3, 20, 20)
                .build());
            addRenderableWidget(Button.builder(Component.literal("+"), button -> PacketDistributor.sendToServer(new DndSixAttributesAdjustPacket(id, 1)))
                .bounds(x + PANEL_WIDTH - 24, rowY + 3, 20, 20)
                .build());
            row++;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);

        int x = (width - PANEL_WIDTH) / 2;
        int y = 48;
        int contentWidth = PANEL_WIDTH - BUTTON_AREA_WIDTH;
        int row = 0;
        for (var ignored : SixAttributeRegistry.all()) {
            int rowY = y + row * ROW_HEIGHT;
            graphics.fill(x, rowY, x + contentWidth, rowY + 24, 0x66000000);
            row++;
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(font, title, width / 2, 16, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("screen.dnd_six_attributes.points", DndSixAttributesClientState.data().availablePoints()), width / 2, 30, 0xFFE082);

        row = 0;
        for (var attribute : SixAttributeRegistry.all()) {
            int rowY = y + row * ROW_HEIGHT;
            int value = DndSixAttributesClientState.data().value(attribute.id());
            int allocated = DndSixAttributesClientState.data().allocated(attribute.id());
            double bonus = DndSixAttributes.percentBonus(value) * 100.0D;
            ResourceLocation icon = iconTexture(attribute.id());
            if (icon != null) {
                graphics.blit(icon, x + 8, rowY + 4, 0, 0, 16, 16, 16, 16);
            } else {
                graphics.fill(x + 8, rowY + 4, x + 24, rowY + 20, 0xFFAAAAAA);
            }
            drawCentered(graphics, Component.translatable(attribute.translationKey()), x + 100, rowY + 8, 0xFFFFFF);
            drawCentered(graphics, Component.translatable("screen.dnd_six_attributes.value", value), x + 205, rowY + 8, 0xD6E6FF);
            drawCentered(graphics, Component.translatable("screen.dnd_six_attributes.allocated", allocated), x + 280, rowY + 8, 0xD6E6FF);
            drawCentered(graphics, Component.literal(String.format("%+.0f%%", bonus)).withStyle(bonus >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED), x + 340, rowY + 8, 0xFFFFFF);
            if (!Boolean.getBoolean("dndSixAttributes.screenshot") && mouseX >= x && mouseX <= x + contentWidth && mouseY >= rowY && mouseY <= rowY + 24) {
                graphics.renderTooltip(font, Component.translatable(attribute.effectKey()), mouseX, mouseY);
            }
            row++;
        }
    }

    private void drawCentered(GuiGraphics graphics, Component text, int centerX, int y, int color) {
        graphics.drawString(font, text, centerX - font.width(text) / 2, y, color, false);
    }

    private static ResourceLocation iconTexture(ResourceLocation id) {
        if (id.equals(DndSixAttributes.STRENGTH)) return DndSixAttributes.id("textures/gui/stat/strength.png");
        if (id.equals(DndSixAttributes.DEXTERITY)) return DndSixAttributes.id("textures/gui/stat/dexterity.png");
        if (id.equals(DndSixAttributes.CONSTITUTION)) return DndSixAttributes.id("textures/gui/stat/constitution.png");
        if (id.equals(DndSixAttributes.INTELLIGENCE)) return DndSixAttributes.id("textures/gui/stat/intelligence.png");
        if (id.equals(DndSixAttributes.WISDOM)) return DndSixAttributes.id("textures/gui/stat/wisdom.png");
        if (id.equals(DndSixAttributes.CHARISMA)) return DndSixAttributes.id("textures/gui/stat/charisma.png");
        return null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
