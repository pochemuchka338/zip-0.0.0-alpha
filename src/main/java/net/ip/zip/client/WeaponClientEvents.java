package net.ip.zip.client;

import net.ip.zip.ZIP;
import net.ip.zip.client.animation.AnimationHandler;
import net.ip.zip.item.WeaponItem;
import net.ip.zip.item.weapons.BaseballBatItem;
import net.ip.zip.item.weapons.KitchenKnifeItem;
import net.ip.zip.item.weapons.MacheteItem;
import net.ip.zip.item.weapons.StopSignItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.WeaponAttackC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class WeaponClientEvents {

    static {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(WeaponClientEvents::onClientSetup);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    WeaponItem.Flashlight.get(),
                    new ResourceLocation(ZIP.MOD_ID, "is_on"),
                    (stack, world, entity, id) -> stack.getOrCreateTag().getBoolean("isOn") ? 1.0F : 0.0F
            );
        });
    }

    private static final class Config {
        final String dataKey;
        final int cooldown;
        final int heavyCharge;
        final String[] hits;
        final String heavy;
        final String block;
        final String blockDamage;
        final String movePrefix;
        final boolean hasBlock;
        final Class<? extends Item> clazz;
        final Set<String> oneShots;

        Config(String dataKey, int cooldown, int heavyCharge, String[] hits, String heavy,
               String block, String blockDamage, String movePrefix, boolean hasBlock, Class<? extends Item> clazz) {
            this.dataKey = dataKey;
            this.cooldown = cooldown;
            this.heavyCharge = heavyCharge;
            this.hits = hits;
            this.heavy = heavy;
            this.block = block;
            this.blockDamage = blockDamage;
            this.movePrefix = movePrefix;
            this.hasBlock = hasBlock;
            this.clazz = clazz;
            Set<String> set = new HashSet<>();
            set.add(heavy);
            for (String h : hits) set.add(h);
            if (blockDamage != null) set.add(blockDamage);
            this.oneShots = Collections.unmodifiableSet(set);
        }
    }

    private static final class State {
        Config config;
        String currentAnim = "";
        int hitCounter = 0;
        int attackHeldTicks = 0;
        boolean wasAttackDown = false;
        int lastAttackTick = -1000;

        State(Config config) {
            this.config = config;
        }
    }

    private static final List<Config> CONFIGS = List.of(
            new Config("bat_animation", 16, 10, new String[]{"bat_hit", "bat_hit2"}, "bat_heavy_blow", "bat_block", "bat_block_damage", "bat_", true, BaseballBatItem.class),
            new Config("kitchen_knife_animation", 10, 10, new String[]{"kitchen_knife_hit", "kitchen_knife_hit2"}, "kitchen_knife_alt_hit", null, null, "kitchen_knife_", false, KitchenKnifeItem.class),
            new Config("machete_animation", 12, 10, new String[]{"machete_hit", "machete_alt_hit"}, "machete_heavy_blow", null, null, "machete_", false, MacheteItem.class),
            new Config("stop_sign_animation", 14, 10, new String[]{"stop_sign_hit", "stop_sign_hit2"}, "stop_sign_heavy_blow", null, null, "stop_sign_", false, StopSignItem.class)
    );

    private static final Map<UUID, State> STATES = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        AbstractClientPlayer player = mc.player;
        ItemStack stack = player.getMainHandItem();
        UUID uuid = player.getUUID();
        Config config = getConfig(stack);
        State state = STATES.get(uuid);

        if (config == null) {
            if (state != null) {
                AnimationHandler.stop(player, state.config.dataKey);
                STATES.remove(uuid);
            }
            return;
        }

        if (state == null || state.config != config) {
            if (state != null) AnimationHandler.stop(player, state.config.dataKey);
            state = new State(config);
            STATES.put(uuid, state);
        }

        boolean attackDown = mc.options.keyAttack.isDown();
        boolean useDown = mc.options.keyUse.isDown();
        boolean wasDown = state.wasAttackDown;
        int heldTicks = state.attackHeldTicks;
        String curAnim = state.currentAnim;

        if (config.heavy.equals(curAnim)) {
            if (AnimationHandler.isPlaying(player, config.dataKey)) return;
            state.currentAnim = "";
            curAnim = "";
        }

        if (config.oneShots.contains(curAnim) && AnimationHandler.isPlaying(player, config.dataKey)) {
            return;
        }

        if (!attackDown && wasDown) {
            if (player.tickCount - state.lastAttackTick >= config.cooldown) {
                boolean heavy = heldTicks >= config.heavyCharge;
                int targetId = -1;
                if (mc.hitResult instanceof EntityHitResult entityHit) {
                    targetId = entityHit.getEntity().getId();
                }
                ModMessages.sendToServer(new WeaponAttackC2SPacket(targetId, heavy));
                state.lastAttackTick = player.tickCount;
                if (heavy) {
                    playOneShot(player, state, config.heavy, true);
                } else {
                    String anim = config.hits[state.hitCounter % config.hits.length];
                    state.hitCounter++;
                    playOneShot(player, state, anim, true);
                }
            }
            state.attackHeldTicks = 0;
            state.wasAttackDown = false;
            return;
        }

        if (attackDown) {
            state.attackHeldTicks = heldTicks + 1;
        } else {
            state.attackHeldTicks = 0;
        }
        state.wasAttackDown = attackDown;

        if (config.hasBlock && useDown) {
            if (!config.block.equals(curAnim)) {
                AnimationHandler.play(player, state.config.dataKey, config.block, false);
                state.currentAnim = config.block;
            }
            return;
        }

        double dx = player.getDeltaMovement().x;
        double dz = player.getDeltaMovement().z;
        boolean isMoving = dx * dx + dz * dz > 0.001;
        boolean isSprinting = player.isSprinting();
        String moveAnim;
        if (isSprinting) {
            moveAnim = config.movePrefix + "sprinting";
        } else if (isMoving) {
            moveAnim = config.movePrefix + "running";
        } else {
            moveAnim = config.movePrefix + "idle";
        }

        if (!moveAnim.equals(curAnim)) {
            AnimationHandler.play(player, state.config.dataKey, moveAnim, false);
            state.currentAnim = moveAnim;
        }
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (getConfig(mc.player.getMainHandItem()) != null && event.isAttack()) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    private static void playOneShot(AbstractClientPlayer player, State state, String animName, boolean firstPerson) {
        AnimationHandler.play(player, state.config.dataKey, animName, firstPerson);
        state.currentAnim = animName;
    }

    private static Config getConfig(ItemStack stack) {
        Item item = stack.getItem();
        for (Config c : CONFIGS) {
            if (c.clazz.isInstance(item)) return c;
        }
        return null;
    }
}