package net.ip.zip.client;

import com.legacy.lucent.api.plugin.ILucentPlugin;
import com.legacy.lucent.api.plugin.LucentPlugin;
import com.legacy.lucent.api.registry.EntityLightSourcePosRegistry;
import com.legacy.lucent.api.registry.EntityLightingRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ip.zip.item.WeaponItem;

@LucentPlugin
public class ModLucentPlugin implements ILucentPlugin {

    @Override
    public String ownerModID() {
        return "zip";
    }

    @Override
    public void registerEntityLightings(EntityLightingRegistry registry) {
        registry.register(EntityType.PLAYER, player -> {
            ItemStack mainHand = player.getMainHandItem();

            if (mainHand.is(WeaponItem.Flashlight.get()) && mainHand.hasTag()) {
                var tag = mainHand.getTag();
                if (tag.getBoolean("isOn") && tag.getInt("charge") > 0) {
                    return 15;
                }
            }
            return 0;
        });
    }

    @Override
    public void registerEntityLightSourcePositionGetter(EntityLightSourcePosRegistry registry) {
        registry.register(EntityType.PLAYER, player -> {
            ItemStack mainHand = player.getMainHandItem();

            if (mainHand.is(WeaponItem.Flashlight.get()) && mainHand.hasTag()) {
                var tag = mainHand.getTag();
                if (tag.getBoolean("isOn") && tag.getInt("charge") > 0) {

                    Vec3 start = player.getEyePosition(1.0F);
                    Vec3 end = start.add(player.getViewVector(1.0F).scale(15.0));

                    BlockHitResult result = player.level().clip(new ClipContext(
                            start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player
                    ));

                    return result.getType() == HitResult.Type.MISS ? end : result.getLocation();
                }
            }
            return player.position();
        });
    }
}
