package net.ip.zip.fog;

import net.ip.zip.ZIP;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.FogStateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID)
public class ServerFogManager {
    public static boolean active = false;
    public static float transition = 0.0f;
    private static final float TRANSITION_SPEED = 0.005f;

    private static final float BASE_START = 0.1f;
    private static final float BASE_END = 0.5f;
    private static final float BASE_R = 0xad / 255f;
    private static final float BASE_G = 0xd4 / 255f;
    private static final float BASE_B = 0xff / 255f;

    private static final float TARGET_START = 0.0f;
    private static final float TARGET_END = 0.1f;

    private static final float DAY_R = 0xad / 255f;
    private static final float DAY_G = 0xd4 / 255f;
    private static final float DAY_B = 0xff / 255f;

    private static final float NIGHT_R = 0x18 / 255f;
    private static final float NIGHT_G = 0x1f / 255f;
    private static final float NIGHT_B = 0x30 / 255f;

    private static float currStart = BASE_START;
    private static float currEnd = BASE_END;
    private static float currR = BASE_R;
    private static float currG = BASE_G;
    private static float currB = BASE_B;

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();
        if (server == null) return;

        if (active) {
            transition = Math.min(1.0f, transition + TRANSITION_SPEED);
        } else {
            transition = Math.max(0.0f, transition - TRANSITION_SPEED);
        }

        if (transition <= 0.0f && !active) {
            currStart = lerp(0.05f, currStart, BASE_START);
            currEnd = lerp(0.05f, currEnd, BASE_END);
            currR = lerp(0.05f, currR, BASE_R);
            currG = lerp(0.05f, currG, BASE_G);
            currB = lerp(0.05f, currB, BASE_B);
        } else {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            if (overworld != null) {
                long time = overworld.getDayTime() % 24000L;
                float dayNightBlend = calculateDayNightBlend(time);

                boolean raining = overworld.isRaining() || overworld.isThundering();
                float rain = raining ? 1.0f : 0.0f;

                float calcR = Mth.lerp(dayNightBlend, DAY_R, NIGHT_R);
                float calcG = Mth.lerp(dayNightBlend, DAY_G, NIGHT_G);
                float calcB = Mth.lerp(dayNightBlend, DAY_B, NIGHT_B);
                float calcStart = TARGET_START;
                float calcEnd = TARGET_END;

                if (rain > 0.0f) {
                    calcEnd *= (1.0f - 0.5f * rain);

                    calcR = Mth.lerp(rain, calcR, 0.20f);
                    calcG = Mth.lerp(rain, calcG, 0.22f);
                    calcB = Mth.lerp(rain, calcB, 0.25f);
                }

                float tStart = lerp(transition, BASE_START, calcStart);
                float tEnd = lerp(transition, BASE_END, calcEnd);
                float tR = lerp(transition, BASE_R, calcR);
                float tG = lerp(transition, BASE_G, calcG);
                float tB = lerp(transition, BASE_B, calcB);

                currStart = lerp(0.05f, currStart, tStart);
                currEnd = lerp(0.05f, currEnd, tEnd);
                currR = lerp(0.05f, currR, tR);
                currG = lerp(0.05f, currG, tG);
                currB = lerp(0.05f, currB, tB);
            }
        }

        tickCounter++;
        if (tickCounter >= 5) {
            tickCounter = 0;
            FogStateS2CPacket pkt = new FogStateS2CPacket(currStart, currEnd, currR, currG, currB, transition);
            for (ServerLevel level : server.getAllLevels()) {
                for (var player : level.players()) {
                    ModMessages.sendToPlayer(pkt, player);
                }
            }
        }
    }

    private static float calculateDayNightBlend(long time) {
        if (time >= 1000 && time < 11000) {
            return 0.0f;
        } else if (time >= 11000 && time < 13000) {
            return (time - 11000) / 2000.0f;
        } else if (time >= 13000 && time < 23000) {
            return 1.0f;
        } else if (time >= 23000) {
            return 1.0f - (time - 23000) / 1000.0f;
        } else {
            return 1.0f - time / 1000.0f;
        }
    }

    private static float lerp(float t, float a, float b) {
        return a + (b - a) * t;
    }
}