package net.ip.zip.item;

import net.ip.zip.ZIP;
import net.ip.zip.item.grenades.Grenade;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItem {
    // Ingridients

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ZIP.MOD_ID);

    public static final RegistryObject<Item> AluminumIngot = ITEMS.register("aluminum_ingot",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> AluminumNugget = ITEMS.register("aluminum_nugget",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> AluminumRaw = ITEMS.register("aluminum_raw",
            () ->  new Item(new Item.Properties()));

    // Common Item

    public static final RegistryObject<Item> Canned = ITEMS.register("canned",
            () ->  new Item(new Item.Properties()));

    // Foods and Drink

    // Foods

    public static final RegistryObject<Item> RottenTomato = ITEMS.register("rotten_tomato",
            () ->  new Item(new Item.Properties().food(ModFoods.RottenTomato)));

    public static final RegistryObject<Item> Tomato = ITEMS.register("tomato",
            () ->  new Item(new Item.Properties().food(ModFoods.Tomato)));

    public static final RegistryObject<Item> CannedTomato = ITEMS.register("canned_tomato",
            () ->  new Item(new Item.Properties().food(ModFoods.CannedTomato)));

    public static final RegistryObject<Item> Soda = ITEMS.register("soda",
            () ->  new Item(new Item.Properties().food(ModFoods.Soda)));

    // Special Items

    // Grenades

    public static final RegistryObject<Item> Grenade = ITEMS.register("grenade",
            () ->  new Grenade(new Item.Properties().durability(100)));

    public static final RegistryObject<Item> Molotov = ITEMS.register("molotov",
            () ->  new Grenade(new Item.Properties().durability(100)));

    // Medical

    public static final RegistryObject<Item> Medkit = ITEMS.register("medkit",
            () -> new MedicalItem("medkit", "medkit_use", 16.0f, 58, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> Bandage = ITEMS.register("bandage",
            () -> new MedicalItem("bandage", "bandage_use", 6.0f, 58, new Item.Properties().stacksTo(16)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}