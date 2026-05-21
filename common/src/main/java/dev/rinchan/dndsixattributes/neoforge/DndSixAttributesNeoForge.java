package dev.rinchan.dndsixattributes.neoforge;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.rinchan.dndsixattributes.SixAttributeData;
import dev.rinchan.dndsixattributes.SixAttributeEffects;
import dev.rinchan.dndsixattributes.DndSixAttributes;
import dev.rinchan.dndsixattributes.DndSixAttributesAdjustPacket;
import dev.rinchan.dndsixattributes.DndSixAttributesNetworking;
import dev.rinchan.dndsixattributes.DndSixAttributesSyncPacket;
import dev.rinchan.dndsixattributes.api.SixAttributeRegistry;
import dev.rinchan.dndsixattributes.client.DndSixAttributesClient;
import dev.rinchan.dndsixattributes.client.DndSixAttributesClientState;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(DndSixAttributes.MOD_ID)
public class DndSixAttributesNeoForge {
    public DndSixAttributesNeoForge(IEventBus modBus) {
        DndSixAttributes.init();
        modBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(this::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            DndSixAttributesClient.register();
            if (Boolean.getBoolean("dndSixAttributes.screenshot")) {
                dev.rinchan.dndsixattributes.client.DndSixAttributesScreenshotHarness.register();
            }
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(DndSixAttributesSyncPacket.TYPE, DndSixAttributesSyncPacket.CODEC, (packet, context) -> context.enqueueWork(() -> DndSixAttributesClientState.apply(packet)).exceptionally(throwable -> null));
        registrar.playToServer(DndSixAttributesAdjustPacket.TYPE, DndSixAttributesAdjustPacket.CODEC, (packet, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && Math.abs(packet.delta()) == 1) {
                SixAttributeData data = SixAttributeData.get(player);
                if (data.adjust(packet.attributeId(), packet.delta())) {
                    SixAttributeData.set(player, data);
                    SixAttributeEffects.apply(player);
                    DndSixAttributesNetworking.sync(player);
                }
            }
        }).exceptionally(throwable -> null));
    }

    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SixAttributeEffects.apply(player);
            DndSixAttributesNetworking.sync(player);
        }
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SixAttributeData.set(player, SixAttributeData.get(event.getOriginal()));
            SixAttributeEffects.apply(player);
            DndSixAttributesNetworking.sync(player);
        }
    }

    private void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % 100 == 0) {
            SixAttributeEffects.apply(player);
        }
    }

    private void onIncomingDamage(LivingIncomingDamageEvent event) {
        SixAttributeEffects.onIncomingDamage(event);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("dndsixattributes")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("points")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> {
                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                            for (ServerPlayer player : EntityArgument.getPlayers(ctx, "targets")) {
                                SixAttributeData data = SixAttributeData.get(player);
                                data.setAvailablePoints(amount);
                                SixAttributeData.set(player, data);
                                DndSixAttributesNetworking.sync(player);
                            }
                            return 1;
                        }))))
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.player())
                    .then(Commands.argument("attribute", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                        .then(Commands.argument("allocated", IntegerArgumentType.integer(0))
                            .executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "target");
                                ResourceLocation id = net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "attribute");
                                if (SixAttributeRegistry.get(id) == null) {
                                    return 0;
                                }
                                int targetAllocated = IntegerArgumentType.getInteger(ctx, "allocated");
                                SixAttributeData data = SixAttributeData.get(player);
                                int current = data.allocated(id);
                                if (targetAllocated > current) {
                                    data.setAvailablePoints(data.availablePoints() + targetAllocated - current);
                                    while (data.allocated(id) < targetAllocated && data.adjust(id, 1)) {}
                                } else {
                                    while (data.allocated(id) > targetAllocated && data.adjust(id, -1)) {}
                                }
                                SixAttributeData.set(player, data);
                                SixAttributeEffects.apply(player);
                                DndSixAttributesNetworking.sync(player);
                                return 1;
                            }))))));
    }
}
