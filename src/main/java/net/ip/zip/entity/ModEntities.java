package net.ip.zip.entity;

import net.ip.zip.ZIP;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ZIP.MOD_ID);

    public static final RegistryObject<EntityType<MeteorEntity>> METEOR = ENTITIES.register("meteor",
            () -> EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
                    .sized(2.0f, 2.0f)
                    .clientTrackingRange(1024)
                    .updateInterval(1)
                    .build("meteor"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}