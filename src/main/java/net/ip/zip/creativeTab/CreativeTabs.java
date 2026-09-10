package net.ip.zip.creativeTab;

import net.ip.zip.ZIP;
import net.ip.zip.block.ModBlocks;
import net.ip.zip.item.ModItem;
import net.ip.zip.item.WeaponItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ZIP.MOD_ID);

    public static final RegistryObject<CreativeModeTab> IngredientsTab = CREATIVE_MODE_TABS.register("ingredients_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItem.AluminumIngot.get()))
                    .title(Component.translatable("creative_tab.ingredients_zip"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItem.AluminumIngot.get());
                        pOutput.accept(ModItem.AluminumNugget.get());
                        pOutput.accept(ModItem.AluminumRaw.get());
                        pOutput.accept(ModItem.Canned.get());
                        pOutput.accept(ModItem.Battery.get());
                        pOutput.accept(ModBlocks.AluminumBlock.get());
                        pOutput.accept(ModBlocks.AluminumOre.get());
                        pOutput.accept(ModBlocks.DeepslateAluminumOre.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> WeaponTab = CREATIVE_MODE_TABS.register("weapon_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(WeaponItem.Pitchfork.get()))
                    .title(Component.translatable("creative_tab.weapon_item"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItem.Grenade.get());
                        pOutput.accept(ModItem.Molotov.get());
                        pOutput.accept(ModItem.SmokeGrenade.get());
                        pOutput.accept(ModItem.StunGrenade.get());
                        pOutput.accept(WeaponItem.Pitchfork.get());
                        pOutput.accept(WeaponItem.Yamato.get());
                        pOutput.accept(WeaponItem.WoodenBat.get());
                        pOutput.accept(WeaponItem.IronBat.get());
                        pOutput.accept(WeaponItem.Machete.get());
                        pOutput.accept(WeaponItem.KitchenKnife.get());
                        pOutput.accept(WeaponItem.StopSign.get());
                        pOutput.accept(WeaponItem.Glock.get());
                        pOutput.accept(WeaponItem.Crowbar.get());
                        pOutput.accept(WeaponItem.Trap.get());
                        pOutput.accept(WeaponItem.Flashlight.get());
                        pOutput.accept(WeaponItem.Bullet.get());
                        pOutput.accept(ModItem.BulletproofVest.get());
                        pOutput.accept(ModItem.Respirator.get());
                        pOutput.accept(ModItem.ProtectiveRespirator.get());
                        pOutput.accept(ModItem.Binoculars.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> FoodsTab = CREATIVE_MODE_TABS.register("foods_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItem.CannedTomato.get()))
                    .title(Component.translatable("creative_tab.foods_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItem.RottenTomato.get());
                        pOutput.accept(ModItem.Tomato.get());
                        pOutput.accept(ModItem.CannedTomato.get());
                        pOutput.accept(ModItem.Soda.get());
                        pOutput.accept(ModItem.ChocolateBar.get());
                        pOutput.accept(ModItem.WaterBottle.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> MedicalTab = CREATIVE_MODE_TABS.register("medical_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItem.Medkit.get()))
                    .title(Component.translatable("creative_tab.medical_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItem.Medkit.get());
                        pOutput.accept(ModItem.Bandage.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}