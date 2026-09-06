package net.ip.zip.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties RottenTomato = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 70), 0.1f)
            .build();

    public static final FoodProperties Tomato = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 70), 0.3f)
            .build();

    public static final FoodProperties CannedTomato = new FoodProperties.Builder()
            .nutrition(5)
            .saturationMod(0.5f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 70), 0.5f)
            .build();

    public static final FoodProperties Soda = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 600, 1), 0.3f)
            .alwaysEat()
            .build();

    public static final FoodProperties ChocolateBar = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1), 1.0f)
            .alwaysEat()
            .build();

    public static final FoodProperties WaterBottle = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.1f)
            .alwaysEat()
            .build();
}