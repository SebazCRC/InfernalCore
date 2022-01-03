package tech.sebazcrc.infernalcore.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Util.Item.HiddenStringUtils;
import tech.sebazcrc.infernalcore.Util.Item.ItemBuilder;

import java.util.Arrays;
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
