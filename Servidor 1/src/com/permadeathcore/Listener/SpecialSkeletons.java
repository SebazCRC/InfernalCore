package com.permadeathcore.Listener;

import com.permadeathcore.End.Util.NMSAccesor;
import com.permadeathcore.Main;
import com.permadeathcore.Task.FollowTask;
import com.permadeathcore.Util.ItemBuilder;
import com.permadeathcore.Util.LeatherArmorBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SpecialSkeletons implements Listener {

    private Main instance;
    private HashMap<Skeleton, Player> skeletonMap = new HashMap<>();

    public SpecialSkeletons(Main instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        if (e.getEntity().getType() == EntityType.SKELETON && instance.getDays() >= 5) {

            int clase = ThreadLocalRandom.current().nextInt(1, 5 + 1);

            Skeleton skeleton = (Skeleton) e.getEntity();
            NMSAccesor accesor = instance.getNmsAccesor();

            String customName = "";

            if (new Random().nextInt(99) == 5) {

                Skeleton raidLeader = (WitherSkeleton) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
                raidLeader.setCustomName(instance.format("&6Patrol Skeleton"));

                EntityEquipment eq = raidLeader.getEquipment();

                ItemStack banner = new ItemStack(Material.ORANGE_BANNER);
                BannerMeta meta = (BannerMeta) banner.getItemMeta();

                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.MOJANG));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
                banner.setItemMeta(meta);

                eq.setHelmet(banner);

                eq.setChestplate(new ItemBuilder(Material.GOLDEN_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                eq.setLeggings(new ItemBuilder(Material.GOLDEN_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                eq.setBoots(new ItemBuilder(Material.GOLDEN_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());

                eq.setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 10).build());

                ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
                PotionMeta pmeta = (PotionMeta) arrow.getItemMeta();
                pmeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
                pmeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 1), false);
                arrow.setItemMeta(pmeta);

                eq.setItemInOffHand(arrow);

                eq.setItemInMainHandDropChance(0);
                eq.setItemInOffHandDropChance(0);

                instance.getNmsAccesor().setMaxHealth(raidLeader, 40.0D, true);
                raidLeader.setRemoveWhenFarAway(true);

                e.setCancelled(true);

            } else {

                if (clase == 1) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Blitzkeleton";

                    if (instance.getDays() >= 5 && instance.getDays() < 10) {

                        new LeatherArmorBuilder(skeleton, Color.YELLOW, false, null);
                        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                    }

                    if (instance.getDays() >= 10) {

                        Map<Enchantment, Integer> enchantments = new HashMap<>();
                        enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

                        new LeatherArmorBuilder(skeleton, Color.YELLOW, false, enchantments);
                        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    }

                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 20).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);
                }

                if (clase == 2) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Raijin Shooter";
                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 20).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);

                    if (instance.getDays() >= 5) {

                        new LeatherArmorBuilder(skeleton, Color.PURPLE, false, null);
                    }
                }

                if (clase == 3) {

                    accesor.setMaxHealth(skeleton, 30.0D, true);
                    customName = "&6Exploder Skeleton";

                    if (instance.getDays() >= 5) {

                        Map<Enchantment, Integer> enchantments = new HashMap<>();
                        enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);

                        new LeatherArmorBuilder(skeleton, Color.RED, false, enchantments);
                    }

                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 20).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);
                }

                if (clase == 4) {

                    accesor.setMaxHealth(skeleton, 30.0D, true);
                    customName = "&6Lifeless Magician";

                    if (instance.getDays() >= 5) {

                        new LeatherArmorBuilder(skeleton, Color.BLUE, false, null);
                    }



                    ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
                    PotionMeta meta = (PotionMeta) arrow.getItemMeta();

                    if (instance.getDays() >= 5 && instance.getDays() < 10) {

                        Map<PotionEffectType, Integer> negativeEffects = new HashMap();

                        negativeEffects.put(PotionEffectType.BLINDNESS, 0);
                        negativeEffects.put(PotionEffectType.CONFUSION, 0);
                        negativeEffects.put(PotionEffectType.LEVITATION, 0);
                        negativeEffects.put(PotionEffectType.POISON, 0);
                        negativeEffects.put(PotionEffectType.SLOW_DIGGING, 0);
                        negativeEffects.put(PotionEffectType.SLOW, 0);
                        negativeEffects.put(PotionEffectType.WEAKNESS, 1);
                        negativeEffects.put(PotionEffectType.WITHER, 0);

                        ArrayList<PotionEffectType> tempList = new ArrayList<>();

                        for (PotionEffectType type : negativeEffects.keySet()) {

                            tempList.add(type);
                        }

                        int random = new Random().nextInt(tempList.size());
                        meta.addCustomEffect(new PotionEffect(tempList.get(random), 20 * 10, negativeEffects.get(tempList.get(random))), false);
                    }

                    if (instance.getDays() >= 8 && instance.getDays() < 15) {
                        Map<PotionEffectType, Integer> negativeEffects = new HashMap();

                        negativeEffects.put(PotionEffectType.BLINDNESS, 0);
                        negativeEffects.put(PotionEffectType.CONFUSION, 0);
                        negativeEffects.put(PotionEffectType.LEVITATION, 0);
                        negativeEffects.put(PotionEffectType.POISON, 0);
                        negativeEffects.put(PotionEffectType.SLOW_DIGGING, 0);
                        negativeEffects.put(PotionEffectType.SLOW, 0);
                        negativeEffects.put(PotionEffectType.WEAKNESS, 1);
                        negativeEffects.put(PotionEffectType.WITHER, 0);

                        ArrayList<PotionEffectType> tempList = new ArrayList<>();

                        for (PotionEffectType type : negativeEffects.keySet()) {

                            tempList.add(type);
                        }

                        int random = new Random().nextInt(tempList.size());
                        int random2 = new Random().nextInt(tempList.size());

                        for (int i = 0; i < 20; i++) {
                            if (random == random2) {
                                random2 = new Random().nextInt(tempList.size());
                            }
                        }

                        meta.addCustomEffect(new PotionEffect(tempList.get(random), 20 * 15, negativeEffects.get(tempList.get(random))), false);
                        meta.addCustomEffect(new PotionEffect(tempList.get(random2), 20 * 15, negativeEffects.get(tempList.get(random2))), false);
                    }

                    if (instance.getDays() >= 15) {

                        Map<PotionEffectType, Integer> negativeEffects = new HashMap();

                        negativeEffects.put(PotionEffectType.BLINDNESS, 0);
                        negativeEffects.put(PotionEffectType.CONFUSION, 1);
                        negativeEffects.put(PotionEffectType.HUNGER, 2);
                        negativeEffects.put(PotionEffectType.LEVITATION, 1);
                        negativeEffects.put(PotionEffectType.POISON, 0);
                        negativeEffects.put(PotionEffectType.SLOW_DIGGING, 2);
                        negativeEffects.put(PotionEffectType.SLOW, 1);
                        negativeEffects.put(PotionEffectType.WEAKNESS, 2);
                        negativeEffects.put(PotionEffectType.WITHER, 1);

                        ArrayList<PotionEffectType> tempList = new ArrayList<>();

                        for (PotionEffectType type : negativeEffects.keySet()) {

                            tempList.add(type);
                        }

                        int random = new Random().nextInt(tempList.size());
                        int random2 = new Random().nextInt(tempList.size());
                        int random3 = new Random().nextInt(tempList.size());

                        for (int i = 0; i < 20; i++) {
                            if (random == random2 || random2 == random3) {
                                random2 = new Random().nextInt(tempList.size());
                            }
                        }
                        meta.addCustomEffect(new PotionEffect(tempList.get(random), 20 * 20, negativeEffects.get(tempList.get(random))), false);
                        meta.addCustomEffect(new PotionEffect(tempList.get(random2), 20 * 20, negativeEffects.get(tempList.get(random2))), false);
                        meta.addCustomEffect(new PotionEffect(tempList.get(random3), 20 * 20, negativeEffects.get(tempList.get(random3))), false);
                    }

                    arrow.setItemMeta(meta);

                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 20).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);
                    skeleton.getEquipment().setItemInOffHand(arrow);
                    skeleton.getEquipment().setItemInOffHandDropChance(0);
                }

                if (clase == 5) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Tactical Skeleton";

                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 20).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);

                    if (instance.getDays() >= 5) {

                        new LeatherArmorBuilder(skeleton, Color.BLACK, false, null);
                    }
                }

                if (clase == 6) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Warrior Skeleton";

                    EntityEquipment eq = skeleton.getEquipment();

                    eq.setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
                    eq.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
                    eq.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
                    eq.setBoots(new ItemBuilder(Material.IRON_BOOTS).build());

                    eq.setItemInMainHand(new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.FIRE_ASPECT, 2).addEnchant(Enchantment.DAMAGE_ALL, 2).build());
                    eq.setItemInMainHandDropChance(0);
                }

                if (clase == 7) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Skeleton++";

                    WitherSkeleton d = (WitherSkeleton) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
                    EntityEquipment eq = d.getEquipment();

                    eq.setHelmet(new ItemBuilder(Material.CHAINMAIL_HELMET).build());
                    eq.setChestplate(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).build());
                    eq.setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).build());
                    eq.setBoots(new ItemBuilder(Material.CHAINMAIL_BOOTS).build());

                    eq.setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 3).build());
                    //eq.setItemInOffHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 3).build());
                    eq.setItemInMainHandDropChance(0);
                    //eq.setItemInOffHandDropChance(0);
                }

                if (!customName.isEmpty()) {
                    skeleton.setCustomName(instance.format(customName));
                }

                setArrow(skeleton);
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e) {

        if (e.getEntity() instanceof Arrow && e.getEntity().getShooter() instanceof Skeleton && instance.getDays() >= 5) {

            Skeleton skeleton = (Skeleton) e.getEntity().getShooter();

            if (skeleton.getCustomName() == null) return;

            if (skeleton.getCustomName().contains(instance.format("&6Tactical Skeleton")) || skeleton.getCustomName().contains(instance.format("&6Blitzkeleton"))) {

                if (skeletonMap.containsKey(skeleton)) {

                    Player p = skeletonMap.get(skeleton);
                    new FollowTask((Arrow) e.getEntity(), p, instance);
                }
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {

        if (e.getEntity().getType() == EntityType.SKELETON && e.getTarget() instanceof Player) {

            if (e.getEntity().getCustomName() == null) return;

            Skeleton skeleton = (Skeleton) e.getEntity();

            if (skeleton.getCustomName() == null) return;

            if (skeleton.getCustomName().contains(instance.format("&6Tactical Skeleton"))) {

                if (skeletonMap.containsKey(skeleton)) {

                    Player player = skeletonMap.get(skeleton);
                    Player p = (Player) e.getTarget();

                    if (player.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString())) {

                        return;
                    }

                    skeletonMap.remove(skeleton);
                    skeletonMap.put(skeleton, p);
                }

                skeletonMap.put(skeleton, (Player) e.getTarget());
            }
        }
    }

    @EventHandler
    public void onPH(ProjectileHitEvent e) {

        if (e.getHitBlock() != null && instance.getDays() >= 5) {

            Block b = e.getHitBlock();

            if (e.getEntity().getShooter() instanceof Skeleton) {

                Skeleton skeleton = (Skeleton) e.getEntity().getShooter();

                if (skeleton.getCustomName() == null) return;

                if (skeleton.getCustomName().contains(instance.format("&6Raijin Shooter"))) {

                    org.bukkit.Location teleport = b.getLocation();

                    if (e.getHitBlockFace() == BlockFace.EAST) {

                        teleport = e.getHitBlock().getRelative(BlockFace.EAST).getLocation();
                    }

                    if (e.getHitBlockFace() == BlockFace.UP) {

                        teleport = e.getHitBlock().getRelative(BlockFace.UP).getLocation();
                    }

                    if (e.getHitBlockFace() == BlockFace.DOWN) {

                        teleport = e.getHitBlock().getRelative(BlockFace.DOWN).getLocation();
                    }

                    if (e.getHitBlockFace() == BlockFace.NORTH) {

                        teleport = e.getHitBlock().getRelative(BlockFace.NORTH).getLocation().add(0, 1, 0);
                    }

                    if (e.getHitBlockFace() == BlockFace.SOUTH) {

                        teleport = e.getHitBlock().getRelative(BlockFace.SOUTH).getLocation().add(0, 1, 0);
                    }

                    skeleton.teleport(teleport);
                }

                if (skeleton.getCustomName().contains(instance.format("&6Exploder Skeleton"))) {

                    if (e.getHitBlock().getType() != Material.BEDROCK) {

                        e.getHitBlock().setType(Material.AIR);

                        TNTPrimed tnt = (TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getHitBlock().getLocation().add(0, 2, 0), EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(60);

                        e.getEntity().remove();
                    }
                }
            }
        }

        if (e.getHitEntity() != null && instance.getDays() >= 5) {

            if (e.getEntity().getShooter() instanceof Skeleton && e.getHitEntity() instanceof Player) {

                Skeleton skeleton = (Skeleton) e.getEntity().getShooter();

                if (skeleton.getCustomName() == null) return;

                if (skeleton.getCustomName().contains(instance.format("&6Exploder Skeleton"))) {

                    if (!((Player) e.getHitEntity()).isBlocking()) {

                        TNTPrimed tnt = (TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getHitEntity().getLocation(), EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(60);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSkeletonDamagePlayer(EntityDamageByEntityEvent e) {

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && instance.getDays() >= 5) {

            if (((Arrow) e.getDamager()).getShooter() instanceof Skeleton) {

                Skeleton skeleton = (Skeleton) ((Arrow) e.getDamager()).getShooter();
                Player p = (Player) e.getEntity();


                if (skeleton.getCustomName() == null) return;

                if (skeleton.getCustomName().contains(instance.format("&6Blitzkeleton"))) {

                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 5.0F, 5.0F);

                    if (p.getNearbyEntities(15, 15, 15).size() >= 1) {

                        for (Entity n : p.getNearbyEntities(15, 15, 15)) {

                            if (n instanceof Player) {

                                ((Player) n).playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 5.0F, 5.0F);
                            }
                        }
                    }

                    if (!p.isBlocking()) {


                        p.setVelocity(p.getVelocity().normalize());

                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*3, 0));

                        Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                            @Override
                            public void run() {

                                p.setVelocity(p.getVelocity().normalize());
                                org.bukkit.Location loc = skeleton.getLocation();
                                Vector v = loc.toVector().subtract(p.getLocation().toVector()).normalize();
                                p.setVelocity(v.multiply(2));
                            }
                        }, 1L);
                    }
                }

                if (skeleton.getCustomName().contains(instance.format("&6Lifeless Magician"))) {

                    ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
                    PotionMeta meta = (PotionMeta) arrow.getItemMeta();

                    if (instance.getDays() >= 5 && instance.getDays() < 10) {

                        Map<PotionEffectType, Integer> negativeEffects = new HashMap();

                        negativeEffects.put(PotionEffectType.BLINDNESS, 0);
                        negativeEffects.put(PotionEffectType.CONFUSION, 0);
                        negativeEffects.put(PotionEffectType.LEVITATION, 0);
                        negativeEffects.put(PotionEffectType.POISON, 0);
                        negativeEffects.put(PotionEffectType.SLOW_DIGGING, 0);
                        negativeEffects.put(PotionEffectType.SLOW, 0);
                        negativeEffects.put(PotionEffectType.WEAKNESS, 1);
                        negativeEffects.put(PotionEffectType.WITHER, 0);

                        ArrayList<PotionEffectType> tempList = new ArrayList<>();

                        for (PotionEffectType type : negativeEffects.keySet()) {

                            tempList.add(type);
                        }

                        int random = new Random().nextInt(tempList.size());
                        meta.addCustomEffect(new PotionEffect(tempList.get(random), 20 * 10, negativeEffects.get(tempList.get(random))), false);
                    }

                    if (instance.getDays() >= 10 && instance.getDays() < 15) {
                        Map<PotionEffectType, Integer> negativeEffects = new HashMap();

                        negativeEffects.put(PotionEffectType.BLINDNESS, 0);
                        negativeEffects.put(PotionEffectType.CONFUSION, 0);
                        negativeEffects.put(PotionEffectType.LEVITATION, 0);
                        negativeEffects.put(PotionEffectType.POISON, 0);
                        negativeEffects.put(PotionEffectType.SLOW_DIGGING, 0);
                        negativeEffects.put(PotionEffectType.SLOW, 0);
                        negativeEffects.put(PotionEffectType.WEAKNESS, 1);
                        negativeEffects.put(PotionEffectType.WITHER, 0);

                        ArrayList<PotionEffectType> tempList = new ArrayList<>();

                        for (PotionEffectType type : negativeEffects.keySet()) {

                            tempList.add(type);
                        }

                        int random = new Random().nextInt(tempList.size());
                        int random2 = new Random().nextInt(tempList.size());

                        for (int i = 0; i < 20; i++) {
                            if (random == random2) {
                                random2 = new Random().nextInt(tempList.size());
                            }
                        }
                        meta.addCustomEffect(new PotionEffect(tempList.get(random), 20 * 15, negativeEffects.get(tempList.get(random))), false);
                        meta.addCustomEffect(new PotionEffect(tempList.get(random2), 20 * 15, negativeEffects.get(tempList.get(random2))), false);
                    }

                    if (instance.getDays() >= 15) {

                        Map<PotionEffectType, Integer> negativeEffects = new HashMap();

                        negativeEffects.put(PotionEffectType.BLINDNESS, 0);
                        negativeEffects.put(PotionEffectType.CONFUSION, 1);
                        negativeEffects.put(PotionEffectType.HUNGER, 2);
                        negativeEffects.put(PotionEffectType.LEVITATION, 1);
                        negativeEffects.put(PotionEffectType.POISON, 0);
                        negativeEffects.put(PotionEffectType.SLOW_DIGGING, 2);
                        negativeEffects.put(PotionEffectType.SLOW, 1);
                        negativeEffects.put(PotionEffectType.WEAKNESS, 2);
                        negativeEffects.put(PotionEffectType.WITHER, 1);

                        ArrayList<PotionEffectType> tempList = new ArrayList<>();

                        for (PotionEffectType type : negativeEffects.keySet()) {

                            tempList.add(type);
                        }

                        int random = new Random().nextInt(tempList.size());
                        int random2 = new Random().nextInt(tempList.size());
                        int random3 = new Random().nextInt(tempList.size());

                        for (int i = 0; i < 20; i++) {
                            if (random == random2 || random2 == random3) {
                                random2 = new Random().nextInt(tempList.size());
                            }
                        }
                        meta.addCustomEffect(new PotionEffect(tempList.get(random), 20 * 20, negativeEffects.get(tempList.get(random))), false);
                        meta.addCustomEffect(new PotionEffect(tempList.get(random2), 20 * 20, negativeEffects.get(tempList.get(random2))), false);
                        meta.addCustomEffect(new PotionEffect(tempList.get(random3), 20 * 20, negativeEffects.get(tempList.get(random3))), false);
                    }

                    arrow.setItemMeta(meta);

                    skeleton.getEquipment().setItemInOffHand(arrow);
                    skeleton.getEquipment().setItemInOffHandDropChance(0);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

        if (e.getEntity().getType() == EntityType.SKELETON) {

            Skeleton skeleton = (Skeleton) e.getEntity();

            if (skeletonMap.containsKey(skeleton)) {

                skeletonMap.remove(skeleton);
            }
        }
    }

    public void setArrow(Skeleton skeleton) {

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
        //meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 1), true);
        arrow.setItemMeta(meta);

        if (skeleton.getCustomName() == null) return;
        if (skeleton.getCustomName().contains(instance.format("&6Lifeless Magician"))) return;

        skeleton.getEquipment().setItemInOffHand(arrow);
        skeleton.getEquipment().setItemInOffHandDropChance(0);
    }
}
