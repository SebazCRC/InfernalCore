package com.permadeathcore.Listener;

import com.permadeathcore.Main;
import com.permadeathcore.Manager.PlayerDataManager;
import com.permadeathcore.Util.HiddenStringUtils;
import com.permadeathcore.Util.ItemBuilder;
import net.minecraft.server.v1_15_R1.EntityPig;
import net.minecraft.server.v1_15_R1.EntityPigZombie;
import net.minecraft.server.v1_15_R1.EntitySkeletonWither;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class VoidEvents implements Listener {

    private Main main;

    public VoidEvents(Main main) {
        this.main = main;
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        if (e.isCancelled()) return;

        if (e.getItemDrop().getItemStack() != null) {

            if (e.getItemDrop().getItemStack().getType() == Material.STRUCTURE_VOID) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {

        if (e.isCancelled()) return;

        if (e.getItem().getItemStack() != null && e.getEntity() instanceof Player) {

            if (e.getItem().getItemStack().getType() == Material.STRUCTURE_VOID) {
                e.getItem().remove();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickVoid(InventoryClickEvent e) {

        if (e.isCancelled()) return;

        if (e.getCurrentItem() != null) {

            if (e.getCurrentItem().getType() == Material.STRUCTURE_VOID) {


                e.setCancelled(true);
            }
        }

        if (e.getCursor() != null) {

            if (e.getCursor().getType() == Material.STRUCTURE_VOID) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        if (e.getBlock().getType() == Material.STRUCTURE_VOID) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {

        if (e.isCancelled()) return;

        if (e.getOffHandItem() != null) {

            if (e.getOffHandItem().getType() == Material.STRUCTURE_VOID) {

                e.setCancelled(true);
            }
        }

        if (e.getMainHandItem() != null) {

            if (e.getMainHandItem().getType() == Material.STRUCTURE_VOID) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMoveItem(InventoryMoveItemEvent e) {

        if (e.isCancelled()) return;

        if (e.getItem() != null) {

            if (e.getItem().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onII(InventoryPickupItemEvent e) {

        if (e.isCancelled()) return;

        if (e.getItem().getItemStack() != null) {

            if (e.getItem().getItemStack().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onD(PlayerDeathEvent e) {

        for (ItemStack s : e.getDrops()) {

            if (s != null) {

                if (s.getType() == Material.STRUCTURE_VOID) {

                    e.getDrops().remove(s);
                }
            }
        }
    }

    @EventHandler
    public void onD(InventoryDragEvent e) {
        if (!e.getNewItems().isEmpty()) {
            for (int i : e.getNewItems().keySet()) {

                ItemStack s = e.getNewItems().get(i);

                if (s != null) {

                    if (s.getType() == Material.STRUCTURE_VOID) {

                        e.getInventory().removeItem(s);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onIntWithR(PlayerInteractEvent e) {

        if (e.getPlayer().getInventory().getItemInMainHand() != null) {

            if (esReliquia(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand())) {

                e.setCancelled(true);
            }
        }

        if (e.getPlayer().getInventory().getItemInOffHand() != null) {

            if (esReliquia(e.getPlayer(), e.getPlayer().getInventory().getItemInOffHand())) {

                e.setCancelled(true);
            }
        }
    }

    public boolean esReliquia(Player p, ItemStack stack) {

        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        if (stack.getType() == Material.LIGHT_BLUE_DYE && stack.getItemMeta().getDisplayName().endsWith(main.format("&6Reliquia Del Fin"))) {
            return true;
        }
        return false;
    }

    private ItemStack crearReliquia() {

        ItemStack s = new ItemBuilder(Material.LIGHT_BLUE_DYE).setDisplayName(main.format("&6Reliquia Del Fin")).build();

        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}")));
        s.setItemMeta(meta);

        return s;

    }
}
