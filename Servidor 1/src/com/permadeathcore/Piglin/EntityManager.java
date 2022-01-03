package com.permadeathcore.Piglin;

import com.permadeathcore.Main;
import com.permadeathcore.Piglin.EntityRegistry.CustomEntityType;
import com.permadeathcore.Piglin.NMS.EntityPiglin;
import com.permadeathcore.Piglin.NMS.EntityPiglinBrute;
import com.permadeathcore.Util.ItemBuilder;
import net.minecraft.server.v1_15_R1.EntityPig;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.mysql.fabric.xmlrpc.base.Array;
import com.permadeathcore.Main;
import com.permadeathcore.Util.ItemBuilder;
import net.minecraft.server.v1_15_R1.EntityPose;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EntityManager implements Listener {

    private Main main;
    private CustomEntityType<EntityPiglin> nmsPiglin;
    private CustomEntityType<EntityPiglinBrute> piglinBrute;

    private Map<PigZombie, Player> piglinTarget = new HashMap();
    private Map<PigZombie, ItemStack> itemInHandMap = new HashMap();
    private Map<Player, ArrayList<Item>> dropsMap = new HashMap();

    private ArrayList<ItemStack> trades = new ArrayList<>();


    public EntityManager(Main main) {
        this.main = main;

        trades.add(new ItemStack(Material.MAGMA_CREAM, 2));
        trades.add(new ItemStack(Material.STRING, 12));
        trades.add(new ItemStack(Material.GLOWSTONE_DUST, 4));
        trades.add(new ItemStack(Material.OBSIDIAN, 10));
        trades.add(new ItemStack(Material.QUARTZ, 15));
        trades.add(new ItemStack(Material.LEATHER, 16));
        trades.add(new ItemStack(Material.FIRE_CHARGE, 19));
        trades.add(new ItemStack(Material.NETHER_BRICK, 14));
        trades.add(new ItemStack(Material.GRAVEL, 6));
        trades.add(new ItemStack(Material.ENDER_PEARL, 4));
        trades.add(new ItemStack(Material.ROTTEN_FLESH, 10));
        trades.add(new ItemStack(Material.FLINT, 1));
        trades.add(new ItemStack(Material.BROWN_MUSHROOM, 1));
        trades.add(new ItemStack(Material.RED_MUSHROOM, 1));
        trades.add(new ItemStack(Material.SOUL_SAND, 5));
    }

    public void registerEntities() {

        this.nmsPiglin = new CustomEntityType<EntityPiglin>("piglin", EntityPiglin.class, EntityTypes.ZOMBIE_PIGMAN, EntityPiglin::new);
        nmsPiglin.register();
    }

    @EventHandler
    public void onPigmanAngry(PigZombieAngerEvent e) {

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {

        if (e.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {

            if (dropsMap.containsKey(e.getPlayer())) {

                ArrayList<Item> items = dropsMap.get(e.getPlayer());
                items.add(e.getItemDrop());

                dropsMap.replace(e.getPlayer(), items);
            } else {

                ArrayList<Item> items = new ArrayList<>();
                items.add(e.getItemDrop());

                dropsMap.put(e.getPlayer(), items);
            }


        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        if (dropsMap.containsKey(e.getPlayer())) {

            dropsMap.remove(e.getPlayer());
        }

        for (PigZombie z : piglinTarget.keySet()) {

            Player p = piglinTarget.get(z);

            if (p.getUniqueId().toString().equalsIgnoreCase(e.getPlayer().getUniqueId().toString())) {

                piglinTarget.remove(z);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        if (e.getEntity() instanceof Player) {

            Player d = (Player) e.getEntity();

            if (dropsMap.containsKey(d)) {

                dropsMap.remove(d);
            }

            for (PigZombie z : piglinTarget.keySet()) {

                Player p = piglinTarget.get(z);

                if (p.getUniqueId().toString().equalsIgnoreCase(d.getUniqueId().toString())) {

                    piglinTarget.remove(z);
                }
            }
        }

        if (e.getEntity() instanceof PigZombie) {

            PigZombie z = (PigZombie) e.getEntity();

            if (piglinTarget.containsKey(z)) {

                piglinTarget.remove(z);
            }

            e.getDrops().clear();
        }
    }

    @EventHandler
    public void onPiglinPickUpGold(EntityPickupItemEvent e) {

        if (e.getEntity() instanceof PigZombie) {

            if (e.getItem() == null) return;
            if (e.getEntity().getCustomName() == null) return;
            if (e.getEntity().getCustomName().contains(main.format("&6Piglin")) && !isBrute((PigZombie) e.getEntity()) && e.getItem().getItemStack().getType() == Material.GOLD_INGOT) {

                final PigZombie piglin = (PigZombie) e.getEntity();

                final ItemStack previous = piglin.getEquipment().getItemInMainHand();

                boolean doContainsPlayer = false;

                Player d = null;

                for (Player p : dropsMap.keySet()) {

                    ArrayList<Item> items = dropsMap.get(p);

                    if (items.contains(e.getItem())) {

                        d = p;
                        doContainsPlayer = true;

                        items.remove(e.getItem());

                        dropsMap.replace(p, items);
                    }
                }

                if (!doContainsPlayer) return;
                if (d == null) return;

                final Player p = d;

                piglinTarget.put(piglin, p);
                piglin.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 4));

                if (previous != null) {

                    itemInHandMap.put(piglin, previous);
                }

                piglin.getEquipment().setItemInMainHand(null);
                piglin.getEquipment().setItemInMainHand(e.getItem().getItemStack());

                e.getItem().remove();

                e.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {

                        boolean finishedTrade = true;

                        if (piglinTarget.containsKey(piglin)) {

                            piglinTarget.remove(piglin);
                        } else {

                            finishedTrade = false;
                        }

                        if (piglin == null || p == null) {
                            return;
                        }

                        if (piglin.isDead() || p.isDead()) {
                            return;
                        }

                        if (piglin.hasPotionEffect(PotionEffectType.SLOW)) {

                            piglin.removePotionEffect(PotionEffectType.SLOW);
                        }

                        if (itemInHandMap.containsKey(piglin)) {

                            ItemStack old = itemInHandMap.get(piglin);

                            if (piglin.getEquipment().getItemInMainHand() == null) {

                                piglin.getEquipment().setItemInMainHand(old);
                                return;
                            }

                            if (!piglin.getEquipment().getItemInMainHand().isSimilar(old)) {

                                piglin.getEquipment().setItemInMainHand(old);
                            }

                            itemInHandMap.remove(piglin);
                        }

                        if (!p.isOnline()) return;
                        if (p.getGameMode() == GameMode.SPECTATOR) return;

                        if (!finishedTrade) return;

                        finishTrade(piglin, p);

                        for (Player near : p.getWorld().getEntitiesByClass(Player.class)) {

                            if (near.getLocation().distanceSquared(piglin.getLocation()) <= 15) {

                                near.playSound(piglin.getLocation(), Sound.ENTITY_PIG_AMBIENT, 2.0F, 1000.0F);
                            }
                        }

                        p.playSound(piglin.getLocation(), Sound.ENTITY_PIG_AMBIENT, 2.0F, 1000.0F);

                    }
                }, 100L);
            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && e.getEntity().getType() == EntityType.PIG_ZOMBIE) {

            if (new Random().nextInt(199) <= 9) {

                spawnPiglin(e.getLocation(), false);
            }
            e.setCancelled(true);
        }

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && e.getEntity().getType() == EntityType.PIG_ZOMBIE) {

            PigZombie piglin = (PigZombie) e.getEntity();

            piglin.setCustomName(main.format("&6Piglin"));
            piglin.setRemoveWhenFarAway(true);
        }
    }

    @EventHandler
    public void onD(EntityDamageByEntityEvent e) {

        if (e.getEntity() instanceof PigZombie && e.getDamager() instanceof Player) {

            Player p = (Player) e.getDamager();
            PigZombie z = (PigZombie) e.getEntity();

            net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity)z).getHandle();

            if (nms instanceof EntityPiglin || nms.getClass().equals(EntityPiglin.class)) {

                ((EntityPiglin)nms).setCanAttackPlayers(true);
                ((EntityPiglin)nms).getHitBy().add(p);
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {

        if (e.getTarget() instanceof Player && e.getEntity() instanceof PigZombie) {

            PigZombie piglin = (PigZombie) e.getEntity();

            if (e.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY) {

                if (piglinTarget.containsKey(piglin)) {

                    piglinTarget.remove(piglin);
                }

                if (itemInHandMap.containsKey(piglin)) {

                    ItemStack old = itemInHandMap.get(piglin);

                    if (piglin.getEquipment().getItemInMainHand() == null) {

                        piglin.getEquipment().setItemInMainHand(old);
                        return;
                    }

                    if (!piglin.getEquipment().getItemInMainHand().isSimilar(old)) {

                        piglin.getEquipment().setItemInMainHand(old);
                    }

                    itemInHandMap.remove(piglin);
                }

                if (piglin.hasPotionEffect(PotionEffectType.SLOW)) {

                    piglin.removePotionEffect(PotionEffectType.SLOW);
                }


                net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity)piglin).getHandle();

                if (nms instanceof EntityPiglin || nms.getClass().equals(EntityPiglin.class)) {

                    ((EntityPiglin)nms).setCanAttackPlayers(true);
                }

                e.setCancelled(false);
                return;
            }

            if (piglinTarget.containsKey(piglin)) {

                e.setCancelled(true);
                return;
            }

            if (checkForPlayer((Player) e.getTarget())) {

                e.setCancelled(true);
            }
        }
    }

    public boolean checkForPlayer(Player p) {

        boolean doPlayerHasGoldenArmor = false;

        for (ItemStack s : p.getInventory().getArmorContents()) {

            if (s != null) {

                if (s.getType() == Material.GOLDEN_HELMET || s.getType() == Material.GOLDEN_CHESTPLATE
                        || s.getType() == Material.GOLDEN_LEGGINGS || s.getType() == Material.GOLDEN_BOOTS) {

                    doPlayerHasGoldenArmor = true;
                }
            }
        }

        return doPlayerHasGoldenArmor;
    }

    public void spawnPiglin(Location l, boolean brute) {

        if (brute) {

            Entity mob = main.getManager().getPiglin().spawn(l);

            if (mob == null) return;

            PigZombie piglin = (PigZombie) mob;

            piglin = (PigZombie) getPiglinBrute().spawn(l);
            piglin.setCustomName(main.format("&6Piglin Brute"));
        } else {
            PigZombie z = (PigZombie) getPiglin().spawn(l);

            z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            z.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));

            for (ItemStack s : z.getEquipment().getArmorContents()) {

                if (s != null) {

                    try {

                        if (s.getType() == Material.GOLDEN_HELMET || s.getType() == Material.GOLDEN_LEGGINGS || s.getType() == Material.GOLDEN_CHESTPLATE || s.getType() == Material.GOLDEN_BOOTS) {

                            ItemMeta meta = s.getItemMeta();
                            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, new Random().nextInt(3) + 1, true);
                            s.setItemMeta(meta);
                        }
                    }catch (IllegalArgumentException x) {
                    }
                }
            }
        }
    }

    public boolean isBrute(PigZombie piglin) {

        if (piglin.getCustomName() == null) return false;
        return piglin.getCustomName().equalsIgnoreCase(main.format("&6Piglin Brute"));
    }

    public void finishTrade(PigZombie piglin, Player p) {

        if (p == null) return;
        if (piglin == null) return;
        if (piglin.isDead()) return;
        if (!p.isOnline()) return;

        Vector bt = p.getLocation().toVector().subtract(piglin.getLocation().toVector());
        Location eLoc = piglin.getLocation();
        eLoc.setDirection(bt);

        piglin.teleport(eLoc);

        if (new Random().nextInt(499) == 1) {

            Item i = piglin.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.TOTEM_OF_UNDYING));
            return;
        }

        if (new Random().nextInt(999) == 1) {

            Item i = piglin.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
            return;
        }

        Item i = piglin.getWorld().dropItem(p.getEyeLocation(), trades.get(new Random().nextInt(trades.size())));
    }


    public CustomEntityType<EntityPiglin> getPiglin() {
        return nmsPiglin;
    }

    public CustomEntityType<EntityPiglinBrute> getPiglinBrute() {
        return piglinBrute;
    }
}
