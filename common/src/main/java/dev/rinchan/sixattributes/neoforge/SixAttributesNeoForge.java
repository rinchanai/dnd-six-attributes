package dev.rinchan.sixattributes.neoforge;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.rinchan.sixattributes.SixAttributeData;
import dev.rinchan.sixattributes.SixAttributeEffects;
import dev.rinchan.sixattributes.SixAttributes;
import dev.rinchan.sixattributes.SixAttributesAdjustPacket;
import dev.rinchan.sixattributes.SixAttributesNetworking;
import dev.rinchan.sixattributes.SixAttributesSyncPacket;
import dev.rinchan.sixattributes.api.SixAttributeRegistry;
import dev.rinchan.sixattributes.client.SixAttributesClient;
import dev.rinchan.sixattributes.client.SixAttributesClientState;
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

@Mod(SixAttributes.MOD_ID)
public class SixAttributesNeoForge {
    public SixAttributesNeoForge(IEventBus modBus) {
        SixAttributes.init();
        modBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(this::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            SixAttributesClient.register();
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(SixAttributesSyncPacket.TYPE, SixAttributesSyncPacket.CODEC, (packet, context) -> context.enqueueWork(() -> SixAttributesClientState.apply(packet)).exceptionally(throwable -> null));
        registrar.playToServer(SixAttributesAdjustPacket.TYPE, SixAttributesAdjustPacket.CODEC, (packet, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && Math.abs(packet.delta()) == 1) {
                SixAttributeData data = SixAttributeData.get(player);
                if (data.adjust(packet.attributeId(), packet.delta())) {
                    SixAttributeData.set(player, data);
                    SixAttributeEffects.apply(player);
                    SixAttributesNetworking.sync(player);
                }
            }
        }).exceptionally(throwable -> null));
    }

    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SixAttributeEffects.apply(player);
            SixAttributesNetworking.sync(player);
        }
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SixAttributeData.set(player, SixAttributeData.get(event.getOriginal()));
            SixAttributeEffects.apply(player);
            SixAttributesNetworking.sync(player);
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
        event.getDispatcher().register(Commands.literal("sixattributes")
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
                                SixAttributesNetworking.sync(player);
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
                                SixAttributesNetworking.sync(player);
                                return 1;
                            }))))));
    }
}
