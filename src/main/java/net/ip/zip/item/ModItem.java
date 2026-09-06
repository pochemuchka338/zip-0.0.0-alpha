package net.ip.zip.item;

import net.ip.zip.item.armor.BulletproofVestItem;
import net.ip.zip.item.armor.ProtectiveRespiratorItem;
import net.ip.zip.item.armor.RespiratorItem;
import net.ip.zip.item.grenades.Grenade;
import net.ip.zip.item.grenades.Molotov;
import net.ip.zip.item.grenades.SmokeGrenade;
import net.ip.zip.item.grenades.StunGrenade;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, net.ip.zip.ZIP.MOD_ID);

    public static final RegistryObject<Item> AluminumIngot = ITEMS.register("aluminum_ingot",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> AluminumNugget = ITEMS.register("aluminum_nugget",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> AluminumRaw = ITEMS.register("aluminum_raw",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> Canned = ITEMS.register("canned",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> RottenTomato = ITEMS.register("rotten_tomato",
            () ->  new Item(new Item.Properties().food(ModFoods.RottenTomato)));

    public static final RegistryObject<Item> Tomato = ITEMS.register("tomato",
            () ->  new Item(new Item.Properties().food(ModFoods.Tomato)));

    public static final RegistryObject<Item> CannedTomato = ITEMS.register("canned_tomato",
            () ->  new Item(new Item.Properties().food(ModFoods.CannedTomato)));

    public static final RegistryObject<Item> Soda = ITEMS.register("soda",
            () ->  new Item(new Item.Properties().food(ModFoods.Soda)));

    public static final RegistryObject<Item> ChocolateBar = ITEMS.register("chocolate_bar",
            () -> new ChocolateBarItem(new Item.Properties()));

    public static final RegistryObject<Item> WaterBottle = ITEMS.register("water_bottle",
            () -> new WaterBottleItem(new Item.Properties()));

    public static final RegistryObject<Item> Grenade = ITEMS.register("grenade",
            () ->  new Grenade(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> Molotov = ITEMS.register("molotov",
            () ->  new Molotov(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> SmokeGrenade = ITEMS.register("smoke_grenade",
            () ->  new SmokeGrenade(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> StunGrenade = ITEMS.register("stun_grenade",
            () ->  new StunGrenade(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> Medkit = ITEMS.register("medkit",
            () -> new MedicalItem("medkit", "medkit_use", 16.0f, 58, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> Bandage = ITEMS.register("bandage",
            () -> new MedicalItem("bandage", "bandage_use", 6.0f, 58, new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BulletproofVest = ITEMS.register("bulletproof_vest",
            () -> new BulletproofVestItem(ModArmorMaterials.BULLETPROOF, new Item.Properties()));

    public static final RegistryObject<Item> Respirator = ITEMS.register("respirator",
            () -> new RespiratorItem(ModArmorMaterials.RESPIRATOR, new Item.Properties()));

    public static final RegistryObject<Item> ProtectiveRespirator = ITEMS.register("protective_respirator",
            () -> new ProtectiveRespiratorItem(ModArmorMaterials.PROTECTIVE_RESPIRATOR, new Item.Properties()));

    public static final RegistryObject<Item> Binoculars = ITEMS.register("binoculars",
            () -> new BinocularsItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}