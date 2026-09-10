package net.ip.zip.item;

import net.ip.zip.ZIP;
import net.ip.zip.item.weapons.BaseballBatItem;
import net.ip.zip.item.weapons.GlockItem;
import net.ip.zip.item.weapons.KitchenKnifeItem;
import net.ip.zip.item.weapons.MacheteItem;
import net.ip.zip.item.weapons.StopSignItem;
import net.ip.zip.item.weapons.YamatoItem;
import net.ip.zip.item.weapons.CrowbarItem;
import net.ip.zip.item.weapons.TrapItem;
import net.ip.zip.item.weapons.FlashlightItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WeaponItem {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ZIP.MOD_ID);

    public static final RegistryObject<Item> Pitchfork = ITEMS.register("pitchfork",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> Yamato = ITEMS.register("yamato",
            () -> new YamatoItem(new Item.Properties()));

    public static final RegistryObject<Item> WoodenBat = ITEMS.register("wooden_bat",
            () -> new BaseballBatItem("wooden_bat", Tiers.WOOD, 3, -2.6f, new Item.Properties()));

    public static final RegistryObject<Item> IronBat = ITEMS.register("iron_bat",
            () -> new BaseballBatItem("iron_bat", Tiers.IRON, 5, -2.8f, new Item.Properties()));

    public static final RegistryObject<Item> Machete = ITEMS.register("machete",
            () -> new MacheteItem(Tiers.IRON, 4, -2.4f, new Item.Properties()));

    public static final RegistryObject<Item> KitchenKnife = ITEMS.register("kitchen_knife",
            () -> new KitchenKnifeItem(Tiers.IRON, 2, -1.8f, new Item.Properties()));

    public static final RegistryObject<Item> StopSign = ITEMS.register("stop_sign",
            () -> new StopSignItem(new Item.Properties()));

    public static final RegistryObject<Item> Glock = ITEMS.register("glock",
            () -> new GlockItem(new Item.Properties()));

    public static final RegistryObject<Item> Crowbar = ITEMS.register("crowbar",
            () -> new CrowbarItem(new Item.Properties().durability(500)));

    public static final RegistryObject<Item> Trap = ITEMS.register("trap",
            () -> new TrapItem(new Item.Properties().durability(300)));

    public static final RegistryObject<Item> Flashlight = ITEMS.register("flashlight",
            () -> new FlashlightItem(new Item.Properties().durability(300)));

    public static final RegistryObject<Item> Bullet = ITEMS.register("bullet",
            () -> new BulletItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}