package net.ip.zip;

import com.mojang.logging.LogUtils;
import net.ip.zip.block.ModBlocks;
import net.ip.zip.block.entity.ModBlockEntities;
import net.ip.zip.client.ClientEvents;
import net.ip.zip.command.ModCommands;
import net.ip.zip.creativeTab.CreativeTabs;
import net.ip.zip.effect.ModEffects;
import net.ip.zip.entity.ModEntities;
import net.ip.zip.item.ModItem;
import net.ip.zip.item.WeaponItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.particle.ModParticles;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ZIP.MOD_ID)
public class ZIP {
    public static final String MOD_ID = "zip";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ZIP(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModEffects.register(modEventBus);
        CreativeTabs.register(modEventBus);
        WeaponItem.register(modEventBus);
        ModItem.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModParticles.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModCommands.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.INSTANCE);
        modEventBus.register(ClientEvents.ModBusEvents.class);
        modEventBus.addListener(this::addCreative);
        ModMessages.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(WeaponItem.Pitchfork);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}

        @SubscribeEvent
        public static void registerRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.METEOR.get(), net.ip.zip.client.renderer.MeteorRenderer::new);
            event.registerEntityRenderer(ModEntities.GRENADE.get(), net.ip.zip.client.renderer.ThrownItemRenderer::new);
            event.registerEntityRenderer(ModEntities.MOLOTOV.get(), context -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(context));
            event.registerEntityRenderer(ModEntities.SMOKE_GRENADE.get(), net.ip.zip.client.renderer.ThrownItemRenderer::new);
            event.registerEntityRenderer(ModEntities.STUN_GRENADE.get(), net.ip.zip.client.renderer.ThrownItemRenderer::new);
            event.registerEntityRenderer(ModEntities.BULLET.get(), net.ip.zip.client.renderer.BulletEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.STOP_SIGN.get(), context -> new net.ip.zip.client.renderer.StopSignBlockRenderer());
            event.registerBlockEntityRenderer(ModBlockEntities.TRAP.get(), context -> new net.ip.zip.client.renderer.TrapBlockRenderer(context));
        }

        @SubscribeEvent
        public static void registerParticleFactories(net.minecraftforge.client.event.RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(net.ip.zip.particle.ModParticles.SMOKE_GRENADE_SMOKE.get(),
                    net.ip.zip.client.particle.SmokeGrenadeParticle.Provider::new);
        }
    }
}