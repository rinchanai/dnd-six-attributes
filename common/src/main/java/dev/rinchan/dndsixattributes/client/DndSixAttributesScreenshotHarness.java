package dev.rinchan.dndsixattributes.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.tutorial.TutorialSteps;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class DndSixAttributesScreenshotHarness {
    private static int clientTicks;
    private static int inWorldTicks;
    private static boolean connecting;
    private static boolean inventoryOpened;
    private static boolean inventoryCaptured;
    private static boolean attributesOpened;
    private static boolean attributesCaptured;

    private DndSixAttributesScreenshotHarness() {
    }

    public static void register() {
        System.out.println("DnD Six Attributes screenshot client harness registered");
        NeoForge.EVENT_BUS.addListener(DndSixAttributesScreenshotHarness::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        clientTicks++;
        if (!connecting && minecraft.player == null && minecraft.level == null && clientTicks >= 40 && minecraft.screen != null) {
            connecting = true;
            System.out.println("DnD Six Attributes screenshot harness connecting to localhost from " + minecraft.screen.getClass().getName());
            ConnectScreen.startConnecting(
                minecraft.screen,
                minecraft,
                ServerAddress.parseString("localhost"),
                new ServerData("DnD Six Attributes Screenshot", "localhost", ServerData.Type.OTHER),
                false,
                null
            );
        }
        if (minecraft.player == null || minecraft.level == null || minecraft.getMainRenderTarget() == null) {
            return;
        }
        inWorldTicks++;
        minecraft.options.tutorialStep = TutorialSteps.NONE;
        minecraft.options.hideGui = false;
        minecraft.options.pauseOnLostFocus = false;
        minecraft.gui.getChat().clearMessages(false);
        minecraft.getToasts().clear();

        if (!inventoryOpened && inWorldTicks >= 120) {
            minecraft.setScreen(new InventoryScreen(minecraft.player));
            inventoryOpened = true;
            System.out.println("DnD Six Attributes screenshot opened inventory screen");
        }
        if (!inventoryCaptured && inWorldTicks >= 160 && minecraft.screen instanceof InventoryScreen) {
            save(minecraft, "dnd-six-attributes-inventory-entry.png");
            inventoryCaptured = true;
            System.out.println("DnD Six Attributes screenshot saved inventory entry");
        }
        if (!attributesOpened && inWorldTicks >= 200) {
            minecraft.setScreen(new DndSixAttributesScreen());
            attributesOpened = true;
            System.out.println("DnD Six Attributes screenshot opened attributes screen");
        }
        if (!attributesCaptured && inWorldTicks >= 240 && minecraft.screen instanceof DndSixAttributesScreen) {
            save(minecraft, "dnd-six-attributes-screen.png");
            attributesCaptured = true;
            System.out.println("DnD Six Attributes screenshot saved attributes screen");
        }
        if ((inventoryCaptured && attributesCaptured && inWorldTicks >= 280) || inWorldTicks >= 600) {
            minecraft.stop();
        }
    }

    private static void save(Minecraft minecraft, String fileName) {
        try {
            Path outputDir = Path.of(System.getProperty("dndSixAttributes.screenshot.dir", minecraft.gameDirectory.getAbsolutePath()));
            Files.createDirectories(outputDir);
            try (NativeImage image = Screenshot.takeScreenshot(minecraft.getMainRenderTarget())) {
                image.writeToFile(outputDir.resolve(fileName));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save screenshot " + fileName, e);
        }
    }
}
