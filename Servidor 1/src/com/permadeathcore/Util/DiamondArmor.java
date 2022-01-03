package com.permadeathcore.Util;

import com.permadeathcore.Main;
import com.permadeathcore.Manager.PlayerDataManager;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DiamondArmor implements Listener {
    private final Main plugin;

    public DiamondArmor(Main instance){
        plugin=instance;
    }

    private Double getMaxHealthOf(LivingEntity entity) {

        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity)entity).getHandle();

        if (nms instanceof EntityInsentient) {

            EntityInsentient in = (EntityInsentient) nms;
            return in.getAttributeInstance(GenericAttributes.MAX_HEALTH).getBaseValue();
        }

        return null;
    }

    private void setMaxHealth(LivingEntity entity, Double health) {

        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) entity).getHandle();

        if (nms instanceof EntityInsentient) {

            EntityInsentient in = (EntityInsentient) nms;
            in.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(health);
        }
    }

    @EventHandler
    public void onWearArmorPiece(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        setupSet(p);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        setupSet(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        setupSet(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        setupSet(e.getPlayer());
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.getInventory().getType() == InventoryType.ANVIL) {

            if (e.getSlotType() == InventoryType.SlotType.RESULT) {

                if (e.getCurrentItem().getItemMeta() == null) return;

                if (e.getCurrentItem().getItemMeta().hasDisplayName()) {

                    if (isPiece(e.getCurrentItem())) {

                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        ItemStack item = e.getCurrentItem();
                        String name = "";
                        Material type = item.getType();

                        if (meta.isUnbreakable() && type == Material.DIAMOND_BOOTS) {

                            name = craftBoots().getItemMeta().getDisplayName();

                        } else if (meta.isUnbreakable() && type == Material.DIAMOND_HELMET) {

                            name = craftHelmet().getItemMeta().getDisplayName();

                        } else if (meta.isUnbreakable() && type == Material.DIAMOND_CHESTPLATE) {

                            name = craftChestplate().getItemMeta().getDisplayName();

                        } else if (meta.isUnbreakable() && type == Material.DIAMOND_BOOTS) {

                            name = craftBoots().getItemMeta().getDisplayName();
                        }

                        if (!name.isEmpty()) {
                            meta.setDisplayName(plugin.format(name));
                            e.getCurrentItem().setItemMeta(meta);
                        }
                    }

                    if (e.getCurrentItem().getType() == Material.TRIDENT && e.getCurrentItem().getItemMeta().hasEnchant(Enchantment.RIPTIDE)) {

                        if (e.getCurrentItem().getItemMeta().getEnchantLevel(Enchantment.RIPTIDE) >= 4) {

                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.setUnbreakable(true);
                            meta.setDisplayName(plugin.format("&6Tridente Mejorado"));
                            e.getCurrentItem().setItemMeta(meta);
                        }
                    }

                    if (e.getCurrentItem().getType() == Material.SHIELD && e.getCurrentItem().getItemMeta().isUnbreakable()) {

                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        meta.setUnbreakable(true);
                        meta.setDisplayName(plugin.format("&6&lEscudo Reforzado"));
                        e.getCurrentItem().setItemMeta(meta);
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

            if (meta.getColor() != Color.GRAY && !meta.isUnbreakable()) {

                e.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (e.isCancelled()) return;

        if (e.getMessage().startsWith("give diamondset")) {

            if (p.isOp()) {

                p.getInventory().addItem(craftHelmet());
                p.getInventory().addItem(craftChestplate());
                p.getInventory().addItem(craftLegs());
                p.getInventory().addItem(craftBoots());
                e.setCancelled(true);
            }
        }
    }

    public ItemStack craftHelmet() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_HELMET, 1)
                .setDisplayName(plugin.format("&d&lCasco de Diamante Reforzado"))
                .setLore(Arrays.asList(plugin.format(" ")))
                .setUnbrekeable(true)
                .build();

        return item;
    }

    public ItemStack craftChestplate() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1)
                .setDisplayName(plugin.format("&d&lPechera de Diamante Reforzada"))
                .setLore(Arrays.asList(plugin.format(" ")))
                .setUnbrekeable(true)
                .build();

        return item;
    }

    public ItemStack craftLegs() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_LEGGINGS, 1)
                .setDisplayName(plugin.format("&d&lPantalones de Diamante Ref."))
                .setLore(Arrays.asList(plugin.format(" ")))
                .setUnbrekeable(true)
                .build();

        return item;
    }

    public ItemStack craftBoots() {

        ItemStack item = new ItemBuilder(Material.DIAMOND_BOOTS, 1)
                .setDisplayName(plugin.format("&d&lBotas de Diamante Reforzadas"))
                .setLore(Arrays.asList(plugin.format(" ")))
                .setUnbrekeable(true)
                .build();

        return item;
    }

    public boolean isPiece(ItemStack s) {
        if (s == null) return false;

        if (s.hasItemMeta()) {

            if (s.getType() == Material.DIAMOND_HELMET || s.getType() == Material.DIAMOND_CHESTPLATE || s.getType() == Material.DIAMOND_LEGGINGS || s.getType() == Material.DIAMOND_BOOTS) {

                if (s.getItemMeta().isUnbreakable()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void setupSet(Player p) {

        int pieces = 0;

        for (ItemStack contents : p.getInventory().getArmorContents()) {

            if (isPiece(contents)) {

                pieces = pieces + 1;
            }
        }

        if (pieces >= 4) {

            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            p.setHealthScale(20.0);
        } else {

            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(12.0);
            p.setHealthScale(12.0);
        }
    }
}
