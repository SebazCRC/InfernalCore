package com.permadeathcore.Task.SkeletonRaids;

import com.permadeathcore.Main;
import com.permadeathcore.Util.ItemBuilder;
import com.permadeathcore.Util.RaidState;
import com.permadeathcore.Util.SkeletonRaid;
import net.minecraft.server.v1_15_R1.EntitySkeleton;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RaidTask extends BukkitRunnable {

    private SkeletonRaid raid;
    private Main instance;

    private Map<Skeleton, Skeleton> healerTeammate = new HashMap<>();
    private Map<Skeleton, String> pendingAtt = new HashMap<>();

    public RaidTask(SkeletonRaid raid, Main instance) {
        this.raid = raid;
        this.instance = instance;
    }

    @Override
    public void run() {

        if (raid.getCurrentState() == RaidState.FINISHED) {

            raid.findForPlayers();
            raid.checkIfPlayerIsNearby();
            raid.setupTitle();
            raid.updateProgress();
            healerTeammate.clear();
            pendingAtt.clear();
            cancel();
            return;
        }

        raid.findForPlayers();
        raid.checkIfPlayerIsNearby();
        raid.setupTitle();
        raid.updateProgress();

        for (Entity raider : raid.getRaiders()) {

            if (raider instanceof Skeleton) {

                Skeleton skeleton = (Skeleton) raider;

                if (raider.getCustomName() == null) return;

                if (skeleton.getCustomName().contains(instance.format("&6Vindicator Skeleton"))) {

                    EntityEquipment eq = skeleton.getEquipment();

                    if (skeleton.getTarget() == null) {

                        if (eq.getItemInMainHand() != null) {

                            eq.setItemInMainHand(new ItemStack(Material.AIR));
                        }

                        if (skeleton.hasPotionEffect(PotionEffectType.SPEED)) {

                            skeleton.removePotionEffect(PotionEffectType.SPEED);
                        }

                    } else {

                        if (eq.getItemInMainHand() == null) {

                            eq.setItemInMainHand(new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.FIRE_ASPECT, 2).addEnchant(Enchantment.DAMAGE_ALL, 3).build());
                            eq.setItemInMainHandDropChance(0);
                        }

                        if (!skeleton.hasPotionEffect(PotionEffectType.SPEED)) {

                            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                        }
                    }
                }

                boolean b = true;

                if (skeleton.getTarget() == null) {

                    if (raider.getCustomName().contains(Main.format("&6Medic Skeleton")) || raider.getCustomName().contains(Main.format("&6Evoker Skeleton"))) {

                        if (raid.getNextEvokerAttack().containsKey(skeleton)) {

                            if (raid.getNextEvokerAttack().get(skeleton) <= 0) {

                                b = false;
                            }
                        }

                        if (raid.getEvokerNextVexSummon().containsKey(skeleton)) {

                            if (raid.getEvokerNextVexSummon().get(skeleton) <= 0) {

                                b = false;
                            }
                        }

                        if (this.healerTeammate.containsKey(skeleton)) {

                            if (this.healerTeammate.get(skeleton) == null) {

                                b = true;
                                return;
                            }

                            if (skeleton.getLocation().distance(healerTeammate.get(skeleton).getLocation()) < 16) return;
                            skeleton.teleport(healerTeammate.get(skeleton));
                        }

                        if (raid.getNextEvokerAttack().containsKey(skeleton)) {

                            if (raid.getNextEvokerAttack().get(skeleton) <= 0) {

                                b = false;
                            }
                        }
                    }

                    if (b) {

                        instance.getNmsAccesor().moveTo(skeleton, raid.getSpawnLocation(), 1.0D);
                    }
                }
            }

            if (raider instanceof Ravager) {

                Ravager skeleton = (Ravager) raider;

                if (skeleton.getTarget() == null) {

                    if (skeleton.getLocation().distance(raid.getSpawnLocation()) >= 15) {

                        instance.getNmsAccesor().moveTo(skeleton, raid.getSpawnLocation(), 1.0D);
                    }
                }
            }
        }


        for (Player player : raid.getPlayers()) {

            if (!raid.getBossBar().getPlayers().contains(player)) {

                raid.getBossBar().addPlayer(player);
            }
        }

        for (Skeleton evoker : raid.getEvokerNextVexSummon().keySet()) {

            int time = raid.getEvokerNextVexSummon().get(evoker);

            if (time > 0) {

                time = time - 1;
                raid.getEvokerNextVexSummon().replace(evoker, time);
            }
        }

        for (Skeleton evoker : raid.getNextEvokerAttack().keySet()) {

            int time = raid.getNextEvokerAttack().get(evoker);

            if (time > 0) {

                time = time - 1;
                raid.getNextEvokerAttack().replace(evoker, time);
            }

            if (time == 3) {

                Player p = null;
                ArrayList<Player> nearbyPlayers = new ArrayList<>();

                for (Entity c : evoker.getNearbyEntities(35, 35, 35)) {

                    if (c instanceof Player) {

                        nearbyPlayers.add((Player) c);
                    }
                }

                if (!nearbyPlayers.isEmpty()) {

                    for (Player player : nearbyPlayers) {

                        player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    }

                    p = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
                }

                if (p != null) {

                    new LookPlayerTask(evoker, p, raid).runTaskTimer(instance, 0L, 1L);
                }
            }


            //TEMPORAL
            if (time == 999) {
                if (raid.getNextEvokerAttack().get(evoker) <= 0) {
                    //raid.

                    boolean canDoAttack = true;

                    Player p = null;
                    ArrayList<Player> nearbyPlayers = new ArrayList<>();

                    for (Entity c : evoker.getNearbyEntities(35, 35, 35)) {

                        if (c instanceof Player) {

                            nearbyPlayers.add((Player) c);
                        }
                    }

                    if (!nearbyPlayers.isEmpty()) {
                        for (Player player : nearbyPlayers) {
                            player.playSound(evoker.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.AMBIENT, 1.0F, 50.0F);
                        }
                        p = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
                    } else {
                        canDoAttack = false;
                    }

                    if (!canDoAttack) return;

                    Vector bt = p.getLocation().toVector().subtract(evoker.getLocation().toVector());
                    Location eLoc = evoker.getLocation();

                    eLoc.setDirection(bt);
                    evoker.teleport(eLoc);

                    if (eLoc.getY() - p.getLocation().getY() > 5) {
                        return;
                    }

                    evoker.setTarget(p);

                    Location start = evoker.getLocation();
                    Vector direction = start.getDirection();

                    int distance = (int) p.getLocation().distance(evoker.getLocation()) + 3;

                    for (int i = 1; i <= distance; i++) {

                        //start.clone().add(bt.clone().multiply(i))
                        EvokerFangs fangs = (EvokerFangs) p.getWorld().spawnEntity(start.clone().add(direction.clone().multiply(i)), EntityType.EVOKER_FANGS);
                        Location newL = evoker.getLocation().clone().setDirection(direction.clone());
                        evoker.teleport(newL);
                        fangs.setOwner(evoker);
                    }

                    raid.getNextEvokerAttack().replace(evoker, 10);
                    evoker.setTarget(null);


                    //Location entityLoc = evoker.getLocation();
                    //Location playerLoc = p.getLocation();
                    //float yaw = (float) Math.toDegrees(Math.atan2(playerLoc.getZ() - entityLoc.getZ(), playerLoc.getX() - entityLoc.getX())) - 90;
                    //entityLoc.setYaw(yaw);
                    //evoker.teleport(entityLoc);
                }
            }
        }

        for (Skeleton eMelee : raid.getEvokerMeleeCooldown().keySet()) {
            int time = raid.getEvokerMeleeCooldown().get(eMelee);

            if (time > 0) {
                time = time - 1;
                raid.getEvokerMeleeCooldown().replace(eMelee, time);
            }
        }

        for (Skeleton healer : raid.getHealers().keySet()) {

            int time = raid.getHealers().get(healer);
            if (time > 0) {
                time = time - 1;
                raid.getHealers().replace(healer, time);
            }

            if (time == 0) {
                boolean noCuro = true;

                if (raid.getRaiders().size() > 1) {
                    for (Entity nearby : healer.getNearbyEntities(10, 5, 10)) {
                        if (raid.getRaiders().contains(nearby)) {
                            if (noCuro) {
                                instance.getNmsAccesor().moveTo(healer, nearby.getLocation(), 2.0D);
                                if (nearby instanceof Skeleton) {
                                    if (!healerTeammate.containsKey(healer)) {
                                        healerTeammate.put(healer, (Skeleton) nearby);
                                    } else {
                                        healerTeammate.replace(healer, (Skeleton) nearby);
                                    }
                                }
                                ThrownPotion tp = healer.launchProjectile(ThrownPotion.class);

                                ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
                                PotionMeta meta = (PotionMeta) potion.getItemMeta();
                                meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
                                meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 0), false);
                                potion.setItemMeta(meta);

                                tp.setItem(potion);

                                tp.setVelocity(tp.getVelocity().normalize());
                                tp.teleport(nearby.getLocation());

                                noCuro = false;
                            }
                        }
                    }
                }

                raid.getHealers().replace(healer, 5);
            }
        }

        for (Skeleton healer : healerTeammate.keySet()) {

            Skeleton teamMate = healerTeammate.get(healer);

            if (healer.isDead()) {

                healerTeammate.remove(healer);
            }

            if (teamMate.isDead() && !healerTeammate.containsKey(healer)) {

                healerTeammate.remove(healer);
                return;
            }

            instance.getNmsAccesor().moveTo(healer, teamMate.getLocation(), 1.50D);
        }

        if (!raid.getVexesMap().keySet().isEmpty()) {

            if (raid.getVexesMap().keySet() == null) return;

            for (Vex vex : raid.getVexesMap().keySet()) {

                int liveTime = raid.getVexesMap().get(vex);

                if (!vex.isDead()) {

                    if (liveTime > 0) {

                        liveTime = liveTime - 1;
                        raid.getVexesMap().replace(vex, liveTime);
                    }

                    if (liveTime <= 0) {

                        Double damageAmount = 4.0D;

                        if (vex.getHealth() - damageAmount > 1.0D) {

                            vex.damage(damageAmount);
                        } else {

                            raid.getVexesMap().remove(vex);
                            vex.remove();
                        }
                    }

                } else {

                    raid.getVexesMap().remove(vex);
                }
            }

        }
    }
}
