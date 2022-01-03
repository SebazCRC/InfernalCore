package tech.sebazcrc.infernalcore.Util.Item;

import tech.sebazcrc.infernalcore.Main;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ObsidianSet implements Listener {
    private final Main plugin;
    private Color color;

    private String helmetName;
    private String chestName;
    private String legName;
    private String bootName;

    public ObsidianSet(Main instance) {
        plugin = instance;
        this.color = Color.fromRGB(6116957);

        this.helmetName = instance.format("&dObsidian Helmet");
        this.chestName = instance.format("&dObsidian Chestplate");
        this.legName = instance.format("&dObsidian Leggings");
        this.bootName = instance.format("&dObsidian Boots");
    }


    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.getInventory().getType() == InventoryType.ANVIL) {

            if(e.getSlotType() == InventoryType.SlotType.RESULT) {

                if (e.getCurrentItem().getItemMeta() == null) return;

                if (e.getCurrentItem().getItemMeta().hasDisplayName()) {

                    if (e.getCurrentItem().getType() == Material.DIAMOND_HELMET || e.getCurrentItem().getType() == Material.DIAMOND_CHESTPLATE || e.getCurrentItem().getType() == Material.DIAMOND_LEGGINGS || e.getCurrentItem().getType() == Material.DIAMOND_BOOTS) {

                        LeatherArmorMeta meta = (LeatherArmorMeta) e.getCurrentItem().getItemMeta();
                        ItemStack item = e.getCurrentItem();
                        String name = "";
                        Material type = item.getType();

                        if (meta.isUnbreakable() && type == Material.LEATHER_HELMET) {
                            name = helmetName;
                        } else if (meta.isUnbreakable() && type == Material.LEATHER_CHESTPLATE) {
                            name = chestName;
                        } else if (meta.isUnbreakable() && type == Material.LEATHER_LEGGINGS) {
                            name = legName;
                        } else if (meta.isUnbreakable() && type == Material.LEATHER_BOOTS) {
                            name = bootName;
                        }

                        if (!name.isEmpty()) {
                            meta.setDisplayName(name);
                            e.getCurrentItem().setItemMeta(meta);
                        }
                    }
                } else {

                    if (e.getCurrentItem().getType() == Material.DIAMOND_HELMET || e.getCurrentItem().getType() == Material.DIAMOND_CHESTPLATE || e.getCurrentItem().getType() == Material.DIAMOND_LEGGINGS || e.getCurrentItem().getType() == Material.DIAMOND_BOOTS) {

                        if (e.getCurrentItem().getItemMeta().isUnbreakable()) {

                            LeatherArmorMeta meta = (LeatherArmorMeta) e.getCurrentItem().getItemMeta();
                            ItemStack item = e.getCurrentItem();
                            String name = "";
                            Material type = item.getType();

                            if (meta.isUnbreakable() && type == Material.LEATHER_HELMET) {
                                name = helmetName;
                            } else if (meta.isUnbreakable() && type == Material.LEATHER_CHESTPLATE) {
                                name = chestName;
                            } else if (meta.isUnbreakable() && type == Material.LEATHER_LEGGINGS) {
                                name = legName;
                            } else if (meta.isUnbreakable() && type == Material.LEATHER_BOOTS) {
                                name = bootName;
                            }

                            if (!name.isEmpty()) {
                                meta.setDisplayName(name);
                                e.getCurrentItem().setItemMeta(meta);
                            }
                        }
                    }
                }
            }
        }
    }

    // Prevenir tintes de armadura
    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {

        if (e.getInventory().getResult() == null) return;

        if (e.getInventory().getResult().getType() == Material.LEATHER_HELMET || e.getInventory().getResult().getType() == Material.LEATHER_CHESTPLATE || e.getInventory().getResult().getType() == Material.LEATHER_LEGGINGS || e.getInventory().getResult().getType() == Material.LEATHER_BOOTS) {

            LeatherArmorMeta meta = (LeatherArmorMeta) e.getInventory().getResult().getItemMeta();

            if (meta.getColor() != color && !meta.isUnbreakable()) {

                e.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    public ItemStack craftObsidianHelmet() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_HELMET).setDisplayName(helmetName).setCustomModelData(5).build();

        ItemMeta meta = item.getItemMeta();

        EquipmentSlot slot = EquipmentSlot.HEAD;
        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier2);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack craftObsidianChest() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(chestName).setCustomModelData(5).build();

        ItemMeta meta = item.getItemMeta();

        EquipmentSlot slot = EquipmentSlot.CHEST;
        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier2);


        //AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", 2, AttributeModifier.Operation.ADD_NUMBER, slot);
        //meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, modifier3);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack craftObsidianLegs() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_LEGGINGS).setDisplayName(legName).setCustomModelData(5).build();

        ItemMeta meta = item.getItemMeta();

        EquipmentSlot slot = EquipmentSlot.LEGS;
        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier2);

        //AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", 2, AttributeModifier.Operation.ADD_NUMBER, slot);
        //meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, modifier3);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack craftObsidianBoots() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_BOOTS).setDisplayName(bootName).setCustomModelData(5).build();

        ItemMeta meta = item.getItemMeta();

        EquipmentSlot slot = EquipmentSlot.FEET;
        // CASCO 3, PECHERA 8, PANTALONES 6, BOTAS 3

        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, slot);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier2);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    public boolean isObsidianPiece(ItemStack s) {
        if (s == null) return false;

        if (s.hasItemMeta()) {

            if (s.getItemMeta().isUnbreakable() && ChatColor.stripColor(s.getItemMeta().getDisplayName()).startsWith("Obsidian")) {

                return true;
            }
        }

        return false;
    }

    public void setupObsidianSet(Player p) {
        int currentObsidianPieces = 0;
        for (ItemStack contents : p.getInventory().getArmorContents()) {
            if (isObsidianPiece(contents)) {
                currentObsidianPieces= currentObsidianPieces + 1;
            }
        }
        if (currentObsidianPieces >= 4) {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(28.0);
            p.setHealthScale(28.0);
        } else {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            p.setHealthScale(20.0);
        }
    }
}
