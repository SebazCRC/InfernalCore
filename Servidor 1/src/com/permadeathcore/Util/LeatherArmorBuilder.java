package com.permadeathcore.Util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Map;

public class LeatherArmorBuilder extends ItemBuilder {
  private LeatherArmorMeta lm;

  public LeatherArmorBuilder(ItemStack itemStack) {
    super(itemStack);
  }

  public LeatherArmorBuilder(Material material, int amount) {
    super(material, amount);
  }

  public LeatherArmorBuilder(Entity entity, Color color, boolean drops, Map<Enchantment, Integer> enchants) {

    if (entity instanceof LivingEntity) {

      LivingEntity liv = (LivingEntity) entity;

      for (int i = 0; i < 4; i++) {

        if (i == 0) {

          ItemStack s = new ItemStack(Material.LEATHER_HELMET);

          LeatherArmorMeta meta = (LeatherArmorMeta) s.getItemMeta();
          meta.setColor(color);
          s.setItemMeta(meta);

          if (!drops) {

            liv.getEquipment().setHelmetDropChance(0);
          }

          if (enchants != null) {

            for (Enchantment enchantment : enchants.keySet()) {

              int level = enchants.get(enchantment);
              meta.addEnchant(enchantment, level, true);
              s.setItemMeta(meta);
            }
          }

          liv.getEquipment().setHelmet(s);
        }

        if (i == 1) {


          ItemStack s = new ItemStack(Material.LEATHER_CHESTPLATE);

          LeatherArmorMeta meta = (LeatherArmorMeta) s.getItemMeta();
          meta.setColor(color);
          s.setItemMeta(meta);

          if (!drops) {

            liv.getEquipment().setChestplateDropChance(0);
          }

          if (enchants != null) {

            for (Enchantment enchantment : enchants.keySet()) {

              int level = enchants.get(enchantment);
              meta.addEnchant(enchantment, level, true);
              s.setItemMeta(meta);
            }
          }

          liv.getEquipment().setChestplate(s);
        }

        if (i == 2) {

          ItemStack s = new ItemStack(Material.LEATHER_LEGGINGS);

          LeatherArmorMeta meta = (LeatherArmorMeta) s.getItemMeta();
          meta.setColor(color);
          s.setItemMeta(meta);

          if (!drops) {

            liv.getEquipment().setLeggingsDropChance(0);
          }

          if (enchants != null) {

            for (Enchantment enchantment : enchants.keySet()) {

              int level = enchants.get(enchantment);
              meta.addEnchant(enchantment, level, true);
              s.setItemMeta(meta);
            }
          }

          liv.getEquipment().setLeggings(s);
        }

        if (i == 3) {

          ItemStack s = new ItemStack(Material.LEATHER_BOOTS);
          LeatherArmorMeta meta = (LeatherArmorMeta) s.getItemMeta();
          meta.setColor(color);
          s.setItemMeta(meta);

          if (!drops) {

            liv.getEquipment().setBootsDropChance(0);
          }

          if (enchants != null) {

            for (Enchantment enchantment : enchants.keySet()) {

              int level = enchants.get(enchantment);
              meta.addEnchant(enchantment, level, true);
              s.setItemMeta(meta);
            }
          }

          liv.getEquipment().setBoots(s);
        }
      }
    }
  }

  public LeatherArmorBuilder setColor(Color color) {
    this.lm = (LeatherArmorMeta) this.is.getItemMeta();
    this.lm.setColor(color);
    this.is.setItemMeta((ItemMeta) this.lm);
    return this;
  }
}
