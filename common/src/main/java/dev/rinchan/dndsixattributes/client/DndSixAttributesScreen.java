package dev.rinchan.dndsixattributes.client;

import dev.rinchan.dndsixattributes.DndSixAttributes;
import dev.rinchan.dndsixattributes.DndSixAttributesAdjustPacket;
import dev.rinchan.dndsixattributes.api.SixAttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class DndSixAttributesScreen extends Screen {
    private static final int ROW_HEIGHT = 27;

    public DndSixAttributesScreen() {
        super(Component.translatable("screen.dnd_six_attributes.title"));
    }

    @Override
    protected void init() {
        int panelWidth = 300;
        int x = (width - panelWidth) / 2;
        int y = 48;
        int row = 0;
        for (var attribute : SixAttributeRegistry.all()) {
            int rowY = y + row * ROW_HEIGHT;
            ResourceLocation id = attribute.id();
            addRenderableWidget(Button.builder(Component.literal("-"), button -> PacketDistributor.sendToServer(new DndSixAttributesAdjustPacket(id, -1)))
                .bounds(x + panelWidth - 44, rowY + 3, 20, 20)
                .build());
            addRenderableWidget(Button.builder(Component.literal("+"), button -> PacketDistributor.sendToServer(new DndSixAttributesAdjustPacket(id, 1)))
                .bounds(x + panelWidth - 22, rowY + 3, 20, 20)
                .build());
            row++;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);

        int panelWidth = 300;
        int x = (width - panelWidth) / 2;
        int y = 48;
        int row = 0;
        for (var attribute : SixAttributeRegistry.all()) {
            int rowY = y + row * ROW_HEIGHT;
            graphics.fill(x, rowY, x + panelWidth - 48, rowY + 24, 0x66000000);
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
            graphics.fill(x + 4, rowY + 4, x + 20, rowY + 20, iconColor(attribute.id()));
            graphics.drawString(font, Component.translatable(attribute.translationKey()), x + 26, rowY + 4, 0xFFFFFF, false);
            graphics.drawString(font, Component.translatable("screen.dnd_six_attributes.value_allocated", value, allocated), x + 112, rowY + 4, 0xD6E6FF, false);
            graphics.drawString(font, Component.literal(String.format("%+.0f%%", bonus)).withStyle(bonus >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED), x + 205, rowY + 4, 0xFFFFFF, false);
            if (!Boolean.getBoolean("dndSixAttributes.screenshot") && mouseX >= x && mouseX <= x + panelWidth - 48 && mouseY >= rowY && mouseY <= rowY + 24) {
                graphics.renderTooltip(font, Component.translatable(attribute.effectKey()), mouseX, mouseY);
            }
            row++;
        }
    }

    private static int iconColor(ResourceLocation id) {
        if (id.equals(DndSixAttributes.STRENGTH)) return 0xFFB54A35;
        if (id.equals(DndSixAttributes.DEXTERITY)) return 0xFF6DBB45;
        if (id.equals(DndSixAttributes.CONSTITUTION)) return 0xFFD45252;
        if (id.equals(DndSixAttributes.INTELLIGENCE)) return 0xFF4F7EDB;
        if (id.equals(DndSixAttributes.WISDOM)) return 0xFF8E63C8;
        if (id.equals(DndSixAttributes.CHARISMA)) return 0xFFE0B84F;
        return 0xFFAAAAAA;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
