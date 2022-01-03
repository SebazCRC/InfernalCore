package com.permadeathcore.Util;

import com.permadeathcore.End.Util.NMSAccesor;
import com.permadeathcore.Entity.EvokerSkeleton;
import com.permadeathcore.Entity.HealerSkeleton;
import com.permadeathcore.Entity.RaiderSkeleton;
import com.permadeathcore.Entity.VindicatorSkeleton;
import com.permadeathcore.Main;
import com.permadeathcore.Task.SkeletonRaids.RaidTask;
import com.permadeathcore.Task.SkeletonRaids.UpdatePhaseTask;
import net.minecraft.server.v1_15_R1.EntitySkeleton;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVex;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import org.bukkit.block.Bell;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SkeletonRaid implements Listener {

    private ArrayList<Entity> raiders;

    private ArrayList<Player> players;

    private int currentPhase;
    private WitherSkeleton raidLeader;
    private Location spawnLocation;

    private String bossBarTitle;
    private BossBar bossBar;

    private Player raidStarter;

    private boolean givenRewards = false;

    private Map<Skeleton, Integer> nextEvokerAttack = new HashMap<>();
    private Map<Skeleton, Integer> evokerMeleeCooldown = new HashMap<>();
    private Map<Skeleton, Integer> nextEvokerSummon = new HashMap<>();
    private Map<Skeleton, Integer> healers = new HashMap<>();
    private Map<Vex, Integer> vexesMap = new HashMap<>();

    private RaidState currentState;

    private Main main;
    private RaidTask task;

    private int starterRaiders = 0;

    private String raidID = "";

    public SkeletonRaid(Main main, Location location, Player starter) {
        this.main = main;
        this.spawnLocation = location.add(0, 2, 0);
        this.currentPhase = 1;
        this.bossBarTitle = main.format("&f&lSkeleton Raid " + getCurrentPhase() + "/5, &eRestantes: &b");
        this.bossBar = Bukkit.createBossBar(main.format(bossBarTitle), BarColor.RED, BarStyle.SEGMENTED_10);

        this.raiders = new ArrayList<>();
        this.players = new ArrayList<>();
        this.raidStarter = starter;

        if (!main.getRaids().contains(this)) {

            main.getRaids().add(this);
        }

        this.raidID = "SkeletonRaid-" + main.getRaids().size();

        this.currentState = RaidState.FIGHTING;

        if (!getPlayers().contains(starter)) {

            getPlayers().add(starter);

        }

        getBossBar().addPlayer(starter);

        spawnRaidLeader();
        spawnRaiders();

        this.starterRaiders = getRaiders().size();

        for (Entity entity : getRaiders()) {

            main.getNmsAccesor().getAtribute(entity, GenericAttributes.FOLLOW_RANGE).setValue(50.0D);
            main.getNmsAccesor().moveTo(entity, starter.getLocation(), 1.0D);

            if (entity instanceof Skeleton) {


                if (entity.getCustomName() != null) {

                    if (!entity.getCustomName().contains(Main.format("&6Evoker Skeleton")) && !entity.getCustomName().contains(Main.format("&6Medic Skeleton"))) {

                        ((Skeleton) entity).setTarget(starter);
                    }
                }
            }
        }

        this.task = new RaidTask(this, main);
        task.runTaskTimer(main, 20L, 20L);

        main.getServer().getPluginManager().registerEvents(this, main);

        for (Entity n : starter.getNearbyEntities(50, 50, 50)) {

            if (n instanceof Player) {

                Player player = (Player) n;

                player.playSound(getSpawnLocation(), Sound.EVENT_RAID_HORN, 1000.0F, 20.0F);

                String ServerMessageTitle = "&c&lSKELETON RAID";
                String ServerMessageSubtitle = "En camino";
                starter.sendTitle(ChatColor.translateAlternateColorCodes('&', ServerMessageTitle), ChatColor.translateAlternateColorCodes('&', ServerMessageSubtitle));
            }
        }
    }

    public String getRaidID() {
        return raidID;
    }

    public Map<Skeleton, Integer> getNextEvokerAttack() {
        return nextEvokerAttack;
    }

    public Map<Skeleton, Integer> getEvokerNextVexSummon() {
        return nextEvokerSummon;
    }

    public Map<Skeleton, Integer> getEvokerMeleeCooldown() {
        return evokerMeleeCooldown;
    }

    public Map<Skeleton, Integer> getHealers() {
        return healers;
    }

    public Map<Vex, Integer> getVexesMap() {
        return vexesMap;
    }

    @EventHandler
    public void onTargetTeam(EntityTargetLivingEntityEvent e) {

        if (getRaiders().contains(e.getEntity()) && getRaiders().contains(e.getTarget())) {
            e.setCancelled(true);
        }

        if (getRaiders().contains(e.getEntity()) && e.getTarget() instanceof Player) {

            Player p = (Player) e.getTarget();

            if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player j = e.getPlayer();

        for (Entity near : j.getNearbyEntities(15, 15, 15)) {

            if (near instanceof Skeleton) {

                Skeleton skeleton = (Skeleton) near;

                if (getEvokerNextVexSummon().containsKey(skeleton)) {

                    int time = getEvokerNextVexSummon().get(skeleton);

                    if (time <= 0) {

                        ArrayList<Player> nPlayers = new ArrayList<>();

                        for (Entity nearby : skeleton.getNearbyEntities(15, 15, 15)) {

                            if (nearby instanceof Player) {

                                nPlayers.add((Player) nearby);
                            }
                        }

                        if (!nPlayers.isEmpty()) {

                            for (Player n : nPlayers) {

                                //n.playSound(n.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 5.0F, 5.0F);
                                n.playSound(n.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
                            }

                            Player p = nPlayers.get(new Random().nextInt(nPlayers.size()));

                            skeleton.teleport(main.getNmsAccesor().faceLocation(skeleton, p.getLocation()));

                            for (int i = 0; i < 2; i++) {

                                Vex vex = (Vex) main.getNmsAccesor().craftNewEntity(new EntityVex(EntityTypes.VEX, main.getNmsAccesor().craftWorld(getSpawnLocation())), skeleton.getLocation().add(0, 3, 0), CreatureSpawnEvent.SpawnReason.NATURAL).getBukkitEntity();
                                vex.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
                                vex.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));

                                vex.getEquipment().setHelmetDropChance(0);
                                vex.getEquipment().setItemInOffHandDropChance(0);

                                vex.setTarget(p);

                                getVexesMap().put(vex, 60);

                                skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 1, false, true));

                            }

                            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                                @Override
                                public void run() {

                                    if (!skeleton.isDead()) {

                                        if (skeleton.hasPotionEffect(PotionEffectType.SPEED)) {

                                            skeleton.removePotionEffect(PotionEffectType.SPEED);
                                        }
                                    }
                                }
                            }, 20 * 2L);
                        }

                        getEvokerNextVexSummon().replace(skeleton, 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageBE(EntityDamageByEntityEvent e) {

        if (getCurrentState() == RaidState.FINISHED) return;

        if (getRaiders().contains(e.getEntity()) && getRaiders().contains(e.getDamager())) {
            e.setCancelled(true);
        }

        if (e.getDamager() instanceof EvokerFangs && getRaiders().contains(e.getEntity())) {

            e.setCancelled(true);
        }

        if (e.getDamager() instanceof EvokerFangs && e.getEntity() instanceof Vex) {

            e.setCancelled(true);
        }


        if (e.getEntity() instanceof Vex && getRaiders().contains(e.getDamager())) {

            e.setCancelled(true);
        }

        if (e.getEntity().getType() == EntityType.SKELETON) {

            Skeleton skeleton = (Skeleton) e.getEntity();

            if (getEvokerMeleeCooldown().containsKey(skeleton)) {

                if (getEvokerMeleeCooldown().get(skeleton) <= 0) {

                    //raid.

                    boolean canDoAttack = true;

                    Player p = null;
                    ArrayList<Player> nearbyPlayers = new ArrayList<>();

                    for (Entity c : skeleton.getNearbyEntities(25, 25, 25)) {

                        if (c instanceof Player) {

                            nearbyPlayers.add((Player) c);
                        }
                    }

                    if (!nearbyPlayers.isEmpty()) {

                        for (Player player : nearbyPlayers) {

                            player.playSound(skeleton.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.AMBIENT, 1.0F, 50.0F);
                        }

                        p = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
                    } else {

                        canDoAttack = false;
                    }

                    if (!canDoAttack) return;


                    Vector bt = p.getLocation().toVector().subtract(skeleton.getLocation().toVector()).multiply(-1);

                    Location loc = skeleton.getLocation().setDirection(bt);

                    skeleton.teleport(loc);

                    Location start = skeleton.getLocation();
                    Vector direction = start.getDirection();

                    if (start.getY() - p.getLocation().getY() > 3) {

                        return;
                    }

                    ArrayList<Location> locations = new ArrayList<>();

                    locations.add(skeleton.getLocation().add(1, 0, 0));
                    locations.add(skeleton.getLocation().add(-1, 0, 0));
                    locations.add(skeleton.getLocation().add(-1, 0, 1));
                    locations.add(skeleton.getLocation().add(1, 0, -1));

                    locations.add(skeleton.getLocation().add(2, 0, 0));
                    locations.add(skeleton.getLocation().add(-2, 0, 0));
                    locations.add(skeleton.getLocation().add(-2, 0, 2));
                    locations.add(skeleton.getLocation().add(2, 0, -2));

                    locations.add(skeleton.getLocation().add(1, 0, 2));
                    locations.add(skeleton.getLocation().add(1, 0, -2));
                    locations.add(skeleton.getLocation().add(-1, 0, 2));
                    locations.add(skeleton.getLocation().add(-1, 0, -2));

                    for (Location spawn : locations) {

                        EvokerFangs fangs = (EvokerFangs) p.getWorld().spawnEntity(spawn, EntityType.EVOKER_FANGS);
                        fangs.setOwner(skeleton);
                    }

                    getEvokerMeleeCooldown().replace(skeleton, 10);
                }
            }

            if (getEvokerNextVexSummon().containsKey(skeleton) && e.getDamager() instanceof Player) {

                int time = getEvokerNextVexSummon().get(skeleton);

                if (time <= 0) {

                    ArrayList<Player> nPlayers = new ArrayList<>();

                    for (Entity nearby : skeleton.getNearbyEntities(15, 15, 15)) {

                        if (nearby instanceof Player) {

                            nPlayers.add((Player) nearby);
                        }
                    }

                    if (!nPlayers.isEmpty()) {

                        for (Player n : nPlayers) {

                            //n.playSound(n.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 5.0F, 5.0F);
                            n.playSound(n.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 5.0F, 5.0F);
                        }

                        Player p = nPlayers.get(new Random().nextInt(nPlayers.size()));

                        for (int i = 0; i < 2; i++) {

                            Vex vex = (Vex) main.getNmsAccesor().craftNewEntity(new EntityVex(EntityTypes.VEX, main.getNmsAccesor().craftWorld(getSpawnLocation())), skeleton.getLocation().add(0, 3, 0), CreatureSpawnEvent.SpawnReason.NATURAL).getBukkitEntity();
                            vex.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
                            vex.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));

                            vex.getEquipment().setHelmetDropChance(0);
                            vex.getEquipment().setItemInOffHandDropChance(0);

                            vex.setTarget(p);

                            getVexesMap().put(vex, 60);

                            skeleton.teleport(main.getNmsAccesor().faceLocation(skeleton, p.getLocation()));

                            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 1, false, true));

                            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                                @Override
                                public void run() {

                                    if (!skeleton.isDead()) {

                                        if (skeleton.hasPotionEffect(PotionEffectType.SPEED)) {

                                            skeleton.removePotionEffect(PotionEffectType.SPEED);
                                        }
                                    }
                                }
                            }, 20 * 2L);
                        }
                    }

                    getEvokerNextVexSummon().replace(skeleton, 20);
                }
            }
        }

        if (e.getDamager() instanceof TNTPrimed && getRaiders().contains(e.getEntity())) {
            e.setCancelled(true);
        }

        if (e.getDamager() instanceof Projectile) {

            if (((Projectile) e.getDamager()).getShooter() != null) {

                if (getRaiders().contains(((Projectile) e.getDamager()).getShooter()) && getRaiders().contains(e.getEntity())) {

                    e.setCancelled(true);
                }

                if (getRaiders().contains(((Projectile) e.getDamager()).getShooter()) && e.getEntity() instanceof Vex) {

                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        if (getCurrentState() == RaidState.FINISHED || getCurrentState() == RaidState.WAITTING) return;

        if (getPlayers().contains(e.getPlayer())) {

            boolean b = true;

            if (getRaidStarter() != null) {

                if (getRaidStarter().getName().contains(e.getPlayer().getName())) {

                    b = false;
                }
            }

            if (b) {

                if (!e.getPlayer().getWorld().getName().equalsIgnoreCase(getSpawnLocation().getWorld().getName())) {

                    getPlayers().remove(e.getPlayer());

                    if (getBossBar().getPlayers().contains(e.getPlayer())) {

                        getBossBar().removePlayer(e.getPlayer());
                    }
                }
            }
        }

        if (getRaidStarter() != null) {

            if (!getRaidStarter().isOnline()) return;

            if (!getRaidStarter().getWorld().getName().equalsIgnoreCase(getSpawnLocation().getWorld().getName())) {

                e.getPlayer().sendMessage(main.format("&6&lSKELETON RAIDS >> &eTe has alejado mucho, por lo que la Raid se ha teletransportado hacia ti."));

                setSpawnLocation(e.getPlayer().getLocation().add(5, 0, 5));

                for (Entity raider : getRaiders()) {

                    raider.teleport(getSpawnLocation());
                }
                return;
            }

            if (getRaidStarter().getName().contains(e.getPlayer().getName())) {

                if (getSpawnLocation().distance(e.getPlayer().getLocation()) > 100) {

                    e.getPlayer().sendMessage(main.format("&6&lSKELETON RAIDS >> &eTe has alejado mucho, por lo que la Raid se ha teletransportado hacia ti."));

                    setSpawnLocation(e.getPlayer().getLocation().add(5, 0, 5));

                    for (Entity raider : getRaiders()) {

                        raider.teleport(getSpawnLocation());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {


        if (getPlayers().contains(e.getPlayer())) {

            getPlayers().remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e) {

        if (getRaidStarter() != null) {

            if (getRaidStarter().getName().contains(e.getEntity().getPlayer().getName())) {

                this.raidStarter = null;
            }
        }

        if (getPlayers().contains(e.getEntity())) {

            getPlayers().remove(e.getEntity());
        }

        if (getBossBar().getPlayers().contains(e.getEntity())) {

            getBossBar().removePlayer(e.getEntity());
        }
    }

    @EventHandler
    public void onChange(PlayerChangedWorldEvent e) {


        if (getPlayers().contains(e.getPlayer())) {

            getPlayers().remove(e.getPlayer());
        }

        if (getBossBar().getPlayers().contains(e.getPlayer())) {

            getBossBar().getPlayers().remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onTP(PlayerTeleportEvent e) {

        if (getPlayers().contains(e.getPlayer())) {

            getPlayers().remove(e.getPlayer());
        }

        if (getBossBar().getPlayers().contains(e.getPlayer())) {

            getBossBar().removePlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (getPlayers().contains(e.getPlayer())) {

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {

                if (e.getClickedBlock().getType() == Material.BELL || e.getClickedBlock().getState() instanceof Bell) {

                    Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                        @Override
                        public void run() {

                            for (Entity raider : getRaiders()) {

                                if (raider instanceof LivingEntity) {

                                    ((LivingEntity) raider).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10*20, 0));
                                }
                            }
                        }
                    }, 20*3);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        if (getNextEvokerAttack().containsKey(e.getEntity())) {

            getNextEvokerAttack().remove(e.getEntity());
        }

        if (getVexesMap().containsKey(e.getEntity())) {

            getVexesMap().remove(e.getEntity());
        }

        if (getEvokerNextVexSummon().containsKey(e.getEntity())) {

            getEvokerNextVexSummon().remove(e.getEntity());
        }

        if (getHealers().containsKey(e.getEntity())) {

            getHealers().remove(e.getEntity());
        }

        if (getEvokerMeleeCooldown().containsKey(e.getEntity())) {

            getEvokerMeleeCooldown().remove(e.getEntity());
        }

        if (e.getEntity().getCustomName() != null) {

            if (e.getEntity().getCustomName().contains(Main.format("&6Evoker Skeleton"))) {

                e.getDrops().clear();
                e.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
        }

        if (getRaiders().contains(e.getEntity())) {

            getRaiders().remove(e.getEntity());

            if (getRaiders().size() == 0) {

                if (getCurrentPhase() == 6) {

                    if (getCurrentState() == RaidState.FINISHED) return;

                    setCurrentState(RaidState.FINISHED);
                    updateProgress();
                    setupTitle();
                    return;
                }

                if (getCurrentState() != RaidState.FINISHED) {

                    changePhase();
                    setupTitle();
                    updateProgress();
                }
            }
        }
    }

    public void changePhase() {

        if (currentPhase == 5) {
            currentPhase = 6;

            setCurrentState(RaidState.FINISHED);
            updateProgress();
            setupTitle();
            return;
        }


        setCurrentPhase(getCurrentPhase() + 1);
        setCurrentState(RaidState.WAITTING);

        new UpdatePhaseTask(this, main).runTaskTimer(main, 0, 10L);
    }

    public int getStarterRaiders() {
        return starterRaiders;
    }

    public void setStarterRaiders(int starterRaiders) {
        this.starterRaiders = starterRaiders;
    }

    public void spawnRaidLeader() {

        raidLeader = (WitherSkeleton) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.WITHER_SKELETON);
        raidLeader.setCustomName(main.format("&6Raid Leader"));

        EntityEquipment eq = raidLeader.getEquipment();

        ItemStack banner = new ItemStack(Material.ORANGE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();

        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.MOJANG));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
        banner.setItemMeta(meta);

        eq.setHelmet(banner);

        eq.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
        eq.setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
        eq.setBoots(new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());

        eq.setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 10).addEnchant(Enchantment.ARROW_FIRE, 2).build());

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta pmeta = (PotionMeta) arrow.getItemMeta();
        pmeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 1), false);
        arrow.setItemMeta(pmeta);

        eq.setItemInOffHand(arrow);

        eq.setItemInMainHandDropChance(0);
        eq.setItemInOffHandDropChance(0);

        main.getNmsAccesor().setMaxHealth(raidLeader, 40.0D, true);

        getRaiders().add(raidLeader);
        raidLeader.setRemoveWhenFarAway(false);
    }

    public void spawnRavagers() {

        for (int i = 0; i < 1; i++) {

            Skeleton skeleton = (Skeleton) main.getNmsAccesor().craftNewEntity(new EntitySkeleton(EntityTypes.SKELETON, main.getNmsAccesor().craftWorld(getSpawnLocation())), getSpawnLocation(), CreatureSpawnEvent.SpawnReason.NATURAL).getBukkitEntity();

            if (skeleton.getEquipment().getItemInMainHand() == null) {

                skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 5).build());
                skeleton.getEquipment().setItemInMainHandDropChance(0);
            } else {

                if (skeleton.getEquipment().getItemInMainHand().getType() == Material.BOW) {
                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 5).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);
                }
            }

            Ravager ravager = (Ravager) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.RAVAGER);

            ravager.addPassenger(skeleton);

            skeleton.setRemoveWhenFarAway(false);
            ravager.setRemoveWhenFarAway(false);

            getRaiders().add(skeleton);
            getRaiders().add(ravager);
        }

        if (getPlayers().size() >= 1) {

            for (Entity raiders : getRaiders()) {

                if (raiders instanceof Skeleton) {

                    ((Skeleton) raiders).setTarget(getPlayers().get(0));
                }
            }
        }
    }



    public void spawnRaiders() {

        int r = new Random().nextInt(5);
        int evokers = 0;
        int healers = 0;

        if (getCurrentPhase() >= 1 && getCurrentPhase() <= 3) {

            evokers = 1;
            healers = 1;
        }

        if (getCurrentPhase() >= 4) {

            evokers = 2;
            healers = 3;
        }

        if (getCurrentPhase() <= 3) {

            r = r + 3;
        }

        if (getCurrentPhase() >= 4) {

            r = r + 6;
        }

        if (evokers > 0) {

            for (int i = 0; i < evokers; i++) {

                Skeleton skeleton = (Skeleton) main.getNmsAccesor().craftNewEntity(new EvokerSkeleton(getSpawnLocation()), getSpawnLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                skeleton.getEquipment().setItemInMainHand(null);

                EntityEquipment eq = skeleton.getEquipment();

                eq.setChestplate(new ItemBuilder(Material.GOLDEN_CHESTPLATE).setUnbrekeable(true).build());
                eq.setChestplateDropChance(0);

                eq.setLeggings(new ItemBuilder(Material.GOLDEN_LEGGINGS).setUnbrekeable(true).build());
                eq.setLeggingsDropChance(0);

                skeleton.setRemoveWhenFarAway(false);

                getRaiders().add(skeleton);
                getNextEvokerAttack().put(skeleton, 5);
                getEvokerMeleeCooldown().put(skeleton, 5);
                getEvokerNextVexSummon().put(skeleton, 15);
            }
        }

        if (healers > 0) {

            for (int i = 0; i < healers; i++) {

                Skeleton skeleton = (Skeleton) main.getNmsAccesor().craftNewEntity(new HealerSkeleton(getSpawnLocation()), getSpawnLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                skeleton.getEquipment().setItemInMainHand(null);

                new LeatherArmorBuilder(skeleton, Color.WHITE, false, null);

                skeleton.setRemoveWhenFarAway(false);

                getRaiders().add(skeleton);
                getHealers().put(skeleton, 15);
                skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.SPLASH_POTION));
            }
        }

        for (int b = 0; b < r; b++) {

            Skeleton skeleton;

            if (new Random().nextInt(99) <= 9) {

                skeleton = (Skeleton) main.getNmsAccesor().craftNewEntity(new VindicatorSkeleton(getSpawnLocation()), getSpawnLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                EntityEquipment eq = skeleton.getEquipment();

                eq.setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
                eq.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
                eq.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
                eq.setBoots(new ItemBuilder(Material.IRON_BOOTS).build());

                eq.setItemInMainHand(new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.FIRE_ASPECT, 2).addEnchant(Enchantment.DAMAGE_ALL, 3).build());
                eq.setItemInMainHandDropChance(0);

            } else {

                skeleton = (Skeleton) main.getNmsAccesor().craftNewEntity(new RaiderSkeleton(getSpawnLocation()), getSpawnLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                int clase = ThreadLocalRandom.current().nextInt(1, 4 + 1);
                NMSAccesor accesor = main.getNmsAccesor();

                String customName = "";

                if (clase == 1) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Blitzkeleton";

                    if (main.getDays() >= 5 && main.getDays() < 10) {

                        new LeatherArmorBuilder(skeleton, Color.YELLOW, false, null);
                        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                    }

                    if (main.getDays() >= 10) {

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

                    if (main.getDays() >= 5) {

                        new LeatherArmorBuilder(skeleton, Color.PURPLE, false, null);
                    }
                }

                if (clase == 3) {

                    accesor.setMaxHealth(skeleton, 30.0D, true);
                    customName = "&6Lifeless Magician";

                    if (main.getDays() >= 5) {

                        new LeatherArmorBuilder(skeleton, Color.BLUE, false, null);
                    }


                    ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
                    PotionMeta meta = (PotionMeta) arrow.getItemMeta();

                    if (main.getDays() >= 5 && main.getDays() < 10) {

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

                    if (main.getDays() >= 8 && main.getDays() < 15) {
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

                    if (main.getDays() >= 15) {

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

                if (clase == 4) {

                    accesor.setMaxHealth(skeleton, 40.0D, true);
                    customName = "&6Tactical Skeleton";

                    skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 20).build());
                    skeleton.getEquipment().setItemInMainHandDropChance(0);

                    if (main.getDays() >= 5) {

                        new LeatherArmorBuilder(skeleton, Color.BLACK, false, null);
                    }
                }

                skeleton.setCustomName(main.format(customName));
                setArrow(skeleton);

                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {

                        if (skeleton.getCustomName() == null) return;

                        if (skeleton.getCustomName().contains(main.format("&6Lifeless Magician"))) {

                            if (skeleton.getEquipment().getItemInOffHand() != null) {

                                if (skeleton.getEquipment().getItemInOffHand().getType() == Material.TIPPED_ARROW) {

                                    ItemStack arrow = skeleton.getEquipment().getItemInOffHand();
                                    PotionMeta pmeta = (PotionMeta) arrow.getItemMeta();
                                    pmeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 0), false);
                                    arrow.setItemMeta(pmeta);
                                }
                            }
                        } else {

                            ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
                            PotionMeta pmeta = (PotionMeta) arrow.getItemMeta();
                            pmeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
                            pmeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 0), false);
                            arrow.setItemMeta(pmeta);
                        }
                    }
                }, 5L);
            }

            skeleton.setRemoveWhenFarAway(false);
            getRaiders().add(skeleton);
        }

        if (getPlayers().size() >= 1) {

            for (Entity raiders : getRaiders()) {

                if (raiders instanceof Skeleton) {

                    if (raiders.getCustomName() != null) {

                        if (raiders.getCustomName().contains(main.format("&6Evoker Skeleton")) || raiders.getCustomName().contains("&6Medic Skeleton")) {

                            return;
                        }

                        ((Skeleton) raiders).setTarget(getPlayers().get(0));
                    }
                }
            }
        }
    }

    public void setArrow(Skeleton skeleton) {

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 5, 1), true);
        arrow.setItemMeta(meta);

        if (skeleton.getCustomName() == null) return;
        if (skeleton.getCustomName().contains(main.format("&6Lifeless Magician"))) return;

        skeleton.getEquipment().setItemInOffHand(arrow);
        skeleton.getEquipment().setItemInOffHandDropChance(0);
    }

    public void findForPlayers() {

        if (getCurrentState() == RaidState.FINISHED) return;

        for (Player on : getSpawnLocation().getWorld().getPlayers()) {

            if (on.getLocation().distance(getSpawnLocation()) <= 100) {

                if (!getPlayers().contains(on)) {
                    getPlayers().add(on);

                    if (!getBossBar().getPlayers().contains(on)) {

                        getBossBar().addPlayer(on);
                    }
                }
            }
        }
    }

    public void checkIfPlayerIsNearby() {

        if (getCurrentState() == RaidState.FINISHED) return;
        if (getPlayers().isEmpty()) return;

        for (int i = 0; i < getPlayers().size(); i++) {

            Player all = getPlayers().get(i);

            if (!all.getWorld().getName().equalsIgnoreCase(getSpawnLocation().getWorld().getName())) {
                getPlayers().remove(all);
                return;
            }

            if (all.getLocation().distance(getSpawnLocation()) >= 101) {

                getPlayers().remove(all);

                if (getBossBar().getPlayers().contains(all)) {

                    getBossBar().removePlayer(all);
                }
            }
        }
    }

    public void setupTitle() {

        for (Player all : getPlayers()) {

            if (getBossBar().getPlayers().contains(all)) return;
            getBossBar().addPlayer(all);
        }

        for (Player all : getBossBar().getPlayers()) {

            if (!getPlayers().contains(all)) {

                getBossBar().removePlayer(all);
            }
        }
    }

    public void updateProgress() {

        int reaming = getRaiders().size();

        Double progress = 0.0;

        if (reaming == starterRaiders) {

            progress = 1.0;
        }

        // Fromula para sacar el % restantes
        // starterRaiders = 100%
        // reaming = x %

        double porcent = (reaming*100.0D/starterRaiders)/100.0D;


        if (getCurrentState() == RaidState.FIGHTING) {

            if (reaming == 0) {

                getBossBar().setProgress(0.0);
            } else {

                getBossBar().setProgress(progress);
            }

            this.bossBarTitle = main.format("&f&lSkeleton Raid " + getCurrentPhase() + "/5, &eRestantes: &b");
            getBossBar().setTitle(getBossBarTitle() + getRaiders().size());
        }

        if (currentState == RaidState.WAITTING) {

            this.bossBarTitle = main.format("&f&lSkeleton Raid " + getCurrentPhase() + "/5, &e&lComenzando Oleada...");
            getBossBar().setTitle(getBossBarTitle());
        }

        if (currentState == RaidState.FINISHED) {
            getBossBar().setProgress(1.0);

            this.bossBarTitle = main.format("&f&lSkeleton Raid &b&l¡Acabada!");
            getBossBar().setTitle(getBossBarTitle());
            giveRewards();
            
            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {
                    
                    for (Player player : getBossBar().getPlayers()) {
                        
                        getBossBar().removePlayer(player);
                    }

                    if (main.getRaids().contains(this)) {

                        main.getRaids().remove(this);
                    }
                }
            }, 100L);
        }
    }

    public void giveRewards() {

        if (givenRewards) return;

        for (Player p : getPlayers()) {

            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5.0F, 5.0F);
            p.sendMessage(main.format("&eHas completado la &6Skeleton Raid"));
        }
        givenRewards = true;

        getPlayers().clear();
        getRaiders().clear();

        getNextEvokerAttack().clear();
        getEvokerMeleeCooldown().clear();
        getEvokerNextVexSummon().clear();
        getVexesMap().clear();
        getHealers().clear();
    }

    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setCurrentState(RaidState currentState) {
        this.currentState = currentState;
    }

    public RaidState getCurrentState() {
        return currentState;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public ArrayList<Entity> getRaiders() {
        return raiders;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public WitherSkeleton getRaidLeader() {
        return raidLeader;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public String getBossBarTitle() {
        return bossBarTitle;
    }

    public Player getRaidStarter() {
        return raidStarter;
    }
}
