package tech.sebazcrc.infernalcore.Util.Item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomItems {

    public static ItemStack createDungeonCompass() {

        return new ItemBuilder(Material.COMPASS).addEnchant(Enchantment.ARROW_FIRE, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_UNBREAKABLE).setUnbrekeable(true).setDisplayName(format("&6Dungeon Tracker (Click derecho para redireccionar)")).build();
    }

    public static ItemStack createCryingObsidian() {
        return new ItemBuilder(Material.OBSIDIAN).setUnbrekeable(true).setDisplayName(format("&dCrying Obsidian")).addItemFlag(ItemFlag.HIDE_UNBREAKABLE).addItemFlag(ItemFlag.HIDE_ENCHANTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
    }

    public static ItemStack createLegendaryAxe() {
        return new ItemBuilder(Material.DIAMOND_AXE).setCustomModelData(2).setDisplayName(format("&6Legendary Axe")).build();
    }

    public static ItemStack createCreeperPowerSource() {
        return new ItemBuilder(Material.BLAZE_POWDER).setDisplayName(format("&dCreeper's Power Source")).setUnbrekeable(true).build();
    }

    public static ItemStack createLagrimaDeAlma() {
        return new ItemBuilder(Material.GHAST_TEAR).setDisplayName(format("&dLágrima de Alma en Desgracia")).setCustomModelData(2).setUnbrekeable(true).build();
    }

    private static String format(String texto) {

        return ChatColor.translateAlternateColorCodes('&', texto);
    }
}
