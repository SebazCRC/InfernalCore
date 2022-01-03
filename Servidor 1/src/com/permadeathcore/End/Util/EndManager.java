package com.permadeathcore.End.Util;

import com.permadeathcore.End.Entities.CustomCreeper;
import com.permadeathcore.End.Entities.CustomGhast;
import com.permadeathcore.End.Task.EndTask;
import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EndManager implements Listener {

    private Main main;

    private List<Entity> enderCreepers;
    private List<Entity> enderGhasts;

    private ArrayList<Enderman> invulnerable = new ArrayList<>();

    public EndManager(Main main) {
        this.main = main;

        main.getServer().getPluginManager().registerEvents(this, main);

        this.enderCreepers = new ArrayList<>();
        this.enderGhasts = new ArrayList<>();
    }

    @EventHandler
    public void onEXPPrime(ExplosionPrimeEvent e) {

        if (isInEnd(e.getEntity().getLocation())) {

            if (e.getEntity() instanceof TNTPrimed) {

                if (!(e.getEntity() instanceof TNTPrimed)) return;
                if (e.getEntity().getCustomName() == null) return;
                if (!e.getEntity().getCustomName().equalsIgnoreCase("dragontnt")) return;

                e.setRadius(10.0F);
            }
        }
    }

    @EventHandler
    public void onEffectAply(AreaEffectCloudApplyEvent e) {

        AreaEffectCloud area = e.getEntity();

        if (isInEnd(area.getLocation())) {

            if (area.getParticle() == Particle.VILLAGER_HAPPY) {

                for (Entity all : e.getAffectedEntities()) {

                    if (all instanceof Player) {

                        e.setCancelled(true);

                    } else if (all.getType() == EntityType.ENDERMAN) {

                        Enderman man = (Enderman) all;
                        invulnerable.add(man);

                        Bukkit.getServer().getScheduler().runTaskLater(main, new Runnable() {
                            @Override
                            public void run() {

                                invulnerable.remove(man);
                            }
                        }, 20 * 15);

                        e.setCancelled(true);
                    }
                }
            }

            if (area.getParticle() == Particle.SMOKE_NORMAL) {

                for (Entity all : e.getAffectedEntities()) {

                    if (all instanceof Player) {

                        Player p = (Player) all;

                        if (p.getLocation().distance(area.getLocation()) <= 3.0D) {

                            if (p.getActivePotionEffects().size() >= 1) {

                                for (PotionEffect effect : p.getActivePotionEffects()) {

                                    p.removePotionEffect(effect.getType());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageBE(EntityDamageByEntityEvent e) {


        if (isInEnd(e.getEntity().getLocation())) {
            if (e.getDamager() instanceof Creeper) {
                Creeper c = (Creeper) e.getDamager();
                if (e.getEntity().getLocation().distance(c.getLocation()) <= 5) {

                    e.setDamage(e.getDamage() * 1.5);

                } else {
                    e.setDamage(e.getDamage() * 1.3);
                }
            } else if (e.getDamager() instanceof Fireball) {
                e.setDamage(e.getDamage() * 2);
            }

            if (e.getEntity() instanceof Enderman) {
                Enderman man = (Enderman) e.getEntity();
                if (invulnerable.contains(man)) {
                    e.setCancelled(true);
                }
            }
        }

        /**
        if (e.getDamager() instanceof TNTPrimed) {

            TNTPrimed primed = (TNTPrimed) e.getDamager();

            if (primed.getCustomName() == null) return;

            if (primed.getCustomName().equalsIgnoreCase("tnt")) {


            }
        }
         */
    }

    @EventHandler
    public void onEMDamage(EntityDamageEvent e) {

        if (e.getEntity() instanceof Enderman) {
            Enderman man = (Enderman) e.getEntity();
            if (invulnerable.contains(man)) {
                e.setCancelled(true);
            }
        }

        Entity ent = e.getEntity();

        if (isEnderCreeper(ent)) {

            if (!(ent instanceof LivingEntity)) return;
            LivingEntity liv = (LivingEntity) ent;
            if (liv.getHealth() - e.getFinalDamage() < 0.5) {

                ent.remove();
                e.setCancelled(true);

            } else {

                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
                teleport(ent, ent.getWorld(), ent.getLocation(), true, false, false);
            }
        }

        if (isEnderGhast(ent)) {

            if (!(ent instanceof LivingEntity)) return;
            LivingEntity liv = (LivingEntity) ent;
            if (liv.getHealth() - e.getFinalDamage() < 0.5) {


                if (!ent.getWorld().getPlayers().isEmpty()) {

                    for (Entity near : liv.getNearbyEntities(70, 70, 70)) {

                        if (near instanceof Player) {

                            Player p = (Player) near;
                            p.playSound(p.getLocation(), Sound.ENTITY_GHAST_DEATH, 1.0F, 1.0F);
                        }
                    }
                }

                ent.remove();

                e.setCancelled(true);

            } else {

                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
                teleport(ent, ent.getWorld(), ent.getLocation(), false, false, false);
            }
        }
    }

    @EventHandler
    public void onDead(EntityDeathEvent e) {

        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) {
            if (main.getTask() != null) {

                main.getTask().setDied(true);
                for (Player all : main.endWorld.getPlayers()) {
                    spawnFireworks(all.getLocation().add(0, 1, 0), 1);
                }
            }
        }

        Entity entity = e.getEntity();

        if (enderGhasts.contains(entity)) {

            enderGhasts.remove(entity);
            e.getDrops().clear();
            e.setDroppedExp(0);

        }

        if (enderCreepers.contains(entity)) {

            enderCreepers.remove(entity);
            e.getDrops().clear();
            e.setDroppedExp(0);
        }

        if (entity instanceof Shulker) {

            boolean isSure = true;
            for (Entity near : e.getEntity().getNearbyEntities(2, 2, 2)) {

                if (near.getType() == EntityType.PRIMED_TNT) {
                    isSure = false;
                }
            }

            if (isSure) {
                TNTPrimed tnt = (TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(80);

                tnt.setCustomName("tntdeath");
                tnt.setCustomNameVisible(false);
                e.getDrops().clear();
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        if (isInEnd(e.getPlayer().getLocation())) {

            Player p = e.getPlayer();

            if (main.getTask() == null) {

                for (Entity n : p.getNearbyEntities(300, 300, 300)) {

                    if (n.getType() == EntityType.ENDER_DRAGON && n.isValid() && !n.isDead()) {

                        n.setCustomName("§6§lPERMADEATH DEMON");
                        EntityInsentient in = (EntityInsentient) ((CraftEntity) n).getHandle();

                        in.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(1350.0D);

                        main.setTask(new EndTask(main, n));
                        main.getTask().runTaskTimer(main, 20L, 20L);
                    }
                }
            } else {

                if (!main.getTask().isDied()) {

                    for (Entity n : p.getNearbyEntities(300, 300, 300)) {

                        if (n.getType() == EntityType.ENDER_DRAGON && n.isValid() && !n.isDead()) {

                            EnderDragon dragon = (EnderDragon) n;
                            if (dragon.getHealth() <= 400.0) {

                                main.getTask().setCurrentDemonPhase(DemonPhase.ENRAGED);
                            }
                        }
                    }
                }
            }

            for (Entity all : p.getWorld().getEntities()) {

                if (all instanceof Creeper) {

                    Creeper c = (Creeper) all;

                    if (!c.hasPotionEffect(PotionEffectType.INVISIBILITY)) {

                        c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent e) {

        Entity t = e.getEntity();

        if (isInEnd(t.getLocation())) {

            if (e.getEntity().getType() == EntityType.ENDER_CRYSTAL && main.getTask() != null) {

                EnderCrystal c = (EnderCrystal) e.getEntity();

                if (c.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                    int random = new Random().nextInt(main.getEndData().getTimeList().size());
                    main.getTask().getRegenTime().put(c.getLocation(), main.getEndData().getTimeList().get(random));

                    CustomGhast ghast = new CustomGhast(EntityTypes.GHAST, ((CraftWorld) t.getWorld()).getHandle());

                    Location nL = c.getLocation().add(0, 10, 0);
                    ghast.setPosition(nL.getX(), nL.getY(), nL.getZ());

                    ((CraftWorld) c.getWorld()).getHandle().addEntity(ghast);
                    enderGhasts.add(ghast.getBukkitEntity());

                    for (Player all : main.endWorld.getPlayers()) {
                        all.playSound(nL, Sound.ENTITY_WITHER_SPAWN, 100.0F, 100.0F);
                    }
                }
            }
        }

        if (!(e.getEntity() instanceof TNTPrimed)) {
            if (e.getEntity().getCustomName() == null) return;
            if (!e.getEntity().getCustomName().equalsIgnoreCase("dragontnt")) return;


            if (!e.blockList().isEmpty()) {

                List<FallingBlock> fallingBlocks = new ArrayList<>();
                List<Block> blockList = new ArrayList<>(e.blockList());
                for (Block b : blockList) {
                    float x = (float) (-0.2 + (float) (Math.random() * ((0.2 - -0.2) + 0.2)));
                    float y = -1 + (float) (Math.random() * ((1 - -1) + 1));
                    float z = (float) (-0.2 + (float) (Math.random() * ((0.2 - -0.2) + 0.2)));

                    if (b.getType() == Material.END_STONE || b.getType() == Material.END_STONE_BRICKS) {

                        FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getState().getData());
                        b.getState().setData(b.getState().getData());
                        fb.setVelocity(new Vector(x, y, z));
                        fb.setDropItem(false);
                        fb.setMetadata("Exploded", new FixedMetadataValue(main, 0));
                        fallingBlocks.add(fb);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Block b : blockList) {
                                    b.getState().update();
                                    this.cancel();
                                }
                            }
                        }.runTaskLater(main, 2L);
                        e.blockList().clear();
                    }
                }
            }
        }

        if (t instanceof TNTPrimed) {

            TNTPrimed tnt = (TNTPrimed) t;

            if (tnt.getCustomName() == null) return;
            if (tnt.getCustomName().equalsIgnoreCase("tntdeath")) {

                Bukkit.getServer().getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {

                        int randomProb = new Random().nextInt(99);
                        randomProb = randomProb + 1;

                        if (main.getDays() <= 39) {

                            if (randomProb <= 20) {

                                main.endWorld.dropItemNaturally(tnt.getLocation(), new ItemStack(Material.SHULKER_SHELL, 1));
                            }
                        } else if (main.getDays() >= 40) {

                            if (randomProb <= 2) {

                                main.endWorld.dropItemNaturally(tnt.getLocation(), new ItemStack(Material.SHULKER_SHELL, 1));
                            }
                        }
                    }
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        if (e.isCancelled()) return;

        LivingEntity entity = e.getEntity();

        if (isInEnd(entity.getLocation())) {

            if (!(entity instanceof Enderman)) return;

            int creeperProb = new Random().nextInt(30);
            int ghastProb = new Random().nextInt(65);

            int skeletonProb = new Random().nextInt(24);
            int spiderProb = new Random().nextInt(54);

            if (main.getTask() != null) {

                if (main.getTask().getCurrentDemonPhase() == DemonPhase.ENRAGED) {

                    if (skeletonProb <= 2) {

                        main.endWorld.spawnEntity(entity.getLocation(), EntityType.SILVERFISH);
                    }

                    if (spiderProb <= 2) {

                        main.endWorld.spawnEntity(entity.getLocation(), EntityType.CREEPER);
                    }
                }
            }

            if (creeperProb <= 2) {

                teleport(null, main.endWorld, entity.getLocation(), true, false, true);
            }

            if (ghastProb <= 2) {

                boolean dragonDied = true;

                for (Entity all : main.endWorld.getEntities()) {

                    if (all.getType() == EntityType.ENDER_DRAGON) {

                        dragonDied = false;
                    }
                }

                if (dragonDied) {

                    teleport(null, main.endWorld, entity.getLocation(), false, true, true);
                }
            }

            int removeProb = new Random().nextInt(99);

            if (removeProb <= 24) {

                entity.remove();
            }

            if (main.getDays() >= 40) {

                Enderman man = (Enderman) e.getEntity();

                EntityEnderman enderman = (EntityEnderman) ((CraftEntity) man).getHandle();

                int randomProb = new Random().nextInt(99) + 1;

                for (Entity nearby : man.getNearbyEntities(50, 50, 50)) {

                    if (nearby instanceof Player) {

                        Player p = (Player) nearby;

                        if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {

                            return;
                        }

                        EntityPlayer nms = ((CraftPlayer) p).getHandle();

                    }
                }
            }
        }
    }

    @EventHandler
    public void onWC(PlayerChangedWorldEvent e) {

        World prev = e.getFrom();
        World now = e.getPlayer().getWorld();

        if (prev.getName().equalsIgnoreCase(main.endWorld.getName())) {

            if (prev.getPlayers().size() == 0) {

                despawnEntities();
            }
        }
    }

    public void despawnEntities() {

        if (enderCreepers.size() >= 1) {

            for (Entity ent : enderCreepers) {

                ent.remove();
            }
        }

        if (enderGhasts.size() >= 1) {

            for (Entity ent : enderGhasts) {

                ent.remove();
            }
        }

        main.getLogger().info("PDC Se han despawneado todos los EnderCreepers y EnderGhasts");
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {

        if (!isInEnd(e.getEntity().getLocation())) return;

        if (e.getHitBlock() != null) {

            if (e.getEntity() instanceof Fireball && e.getEntity().getShooter() instanceof Ghast) {

                e.getHitBlock().getWorld().createExplosion(e.getHitBlock().getLocation(), 2.0F, true, true, (Entity) e.getEntity().getShooter());
            }

            if (e.getEntity() instanceof ShulkerBullet) {

                ShulkerBullet b = (ShulkerBullet) e.getEntity();

                if (b.getShooter() instanceof Shulker) {

                    Shulker s = (Shulker) b.getShooter();

                    if (s.getLocation().distance(e.getHitBlock().getLocation()) >= 4.0) {

                        e.getHitBlock().setType(Material.AIR);

                        TNTPrimed tnt = (TNTPrimed) s.getWorld().spawnEntity(e.getHitBlock().getLocation(), EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(20);

                        tnt.setCustomName("tnt");
                        tnt.setCustomNameVisible(false);
                    }
                }
            }
        }

        if (e.getHitEntity() != null) {

            if (e.getEntity() instanceof Fireball) {

                Fireball f = (Fireball) e.getEntity();

                if (f.getShooter() instanceof Ghast) {

                    if (e.getHitEntity() != null) {

                        if (e.getHitEntity() instanceof LivingEntity) {

                            LivingEntity l = (LivingEntity) e.getHitEntity();
                            l.damage(10.0D);
                        }
                    }
                }
            }

            if (e.getEntity() instanceof ShulkerBullet) {

                ShulkerBullet b = (ShulkerBullet) e.getEntity();

                if (b.getShooter() instanceof Shulker) {

                    Shulker s = (Shulker) b.getShooter();

                    if (s.getLocation().getX() == e.getHitEntity().getLocation().getX() && s.getLocation().getY() == e.getHitEntity().getLocation().getY() && s.getLocation().getZ() == e.getHitEntity().getLocation().getZ()) {

                        return;
                    }

                    TNTPrimed tnt = (TNTPrimed) s.getWorld().spawnEntity(e.getHitEntity().getLocation(), EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(20);

                    tnt.setCustomName("tnt");
                    tnt.setCustomNameVisible(false);

                }
            }
        }
    }

    public void teleport(Entity entity, World world, Location currentLoc, boolean creeper, boolean ghastUP, boolean isSpawning) {

        Location where = new Location(world, 0, 0, 0);
        boolean foundLocation = false;

        if (creeper) {
            boolean isVoidLocation = false;

            for (int i = 0; i < 1000 && !foundLocation; i++) {

                int prob = new Random().nextInt(99) + 1;
                if (prob <= 20) {

                    if (world.getPlayers().size() >= 1) {
                        int randomP = new Random().nextInt(world.getPlayers().size());
                        Location l = world.getPlayers().get(randomP).getLocation().add(1, 1, 1);
                        where = new Location(l.getWorld(), l.getX(), world.getHighestBlockYAt(l), l.getZ());
                    }
                } else {
                    where.setY(world.getHighestBlockAt(where).getY());
                    where = new Location(currentLoc.getWorld(),
                            currentLoc.getX() + new Random().nextInt(25), where.getY(), currentLoc.getZ() + new Random().nextInt(25));
                    foundLocation = true;
                    if (where.getY() == -1.0D || where.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                        isVoidLocation = true;
                    }
                }
            }

            if (foundLocation) {
                if (isSpawning) {

                    if (isVoidLocation) {
                        return;
                    }

                    net.minecraft.server.v1_15_R1.Entity ogNMS = new CustomCreeper(EntityTypes.CREEPER, ((CraftWorld)where.getWorld()).getHandle(), false, false);
                    ogNMS.setPosition(where.getX(), where.getY(), where.getZ());

                    ((CraftWorld)currentLoc.getWorld()).getHandle().addEntity(ogNMS, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    enderCreepers.add(ogNMS.getBukkitEntity());
                } else {
                    if (!isVoidLocation) {
                        entity.teleport(where);
                    } else {
                        entity.remove();
                    }
                }
            }

        } else {

            for (int i = 0; i < 100 && !foundLocation; i++) {
                if (ghastUP) {
                    where = new Location(currentLoc.getWorld(),
                            currentLoc.getX() + new Random().nextInt(15),
                            currentLoc.getY() + 20,
                            currentLoc.getZ() + new Random().nextInt(15));
                } else {

                    where = new Location(currentLoc.getWorld(),
                            currentLoc.getX() + new Random().nextInt(15),
                            currentLoc.getY() + 10,
                            currentLoc.getZ() + new Random().nextInt(15));
                }
                where.setY(world.getHighestBlockYAt(where));
                foundLocation = true;
            }

            if (foundLocation) {

                net.minecraft.server.v1_15_R1.Entity ogNMS = new CustomGhast(EntityTypes.GHAST, ((CraftWorld)where.getWorld()).getHandle());

                if (isSpawning) {
                    ((CraftWorld)currentLoc.getWorld()).getHandle().addEntity(ogNMS);
                    enderGhasts.add(ogNMS.getBukkitEntity());

                    ogNMS.getBukkitEntity().teleport(where.add(0, 5, 0));

                } else {
                    int ran = new Random().nextInt(99);
                    if (ran <= 39) {
                        entity.teleport(where.add(0, 5, 0));
                    }
                }
            }
        }
    }

    private boolean isEnderCreeper(Entity e) {

        if (!(e instanceof Creeper)) return false;
        LivingEntity liv = (LivingEntity) e;

        if (liv.getCustomName() == null) return false;

        return liv.getCustomName().equalsIgnoreCase(main.format("&6Ender Creeper")) || liv.getCustomName().equalsIgnoreCase(main.format("&6Ender Quantum Creeper"));
    }

    private boolean isEnderGhast(Entity e) {

        if (e.getType() != EntityType.GHAST) {

            return false;
        }

        LivingEntity liv = (LivingEntity) e;

        return isInEnd(liv.getLocation());
    }

    public static void spawnFireworks(Location location, int amount) {
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public boolean isInEnd(Location p) {

        return p.getWorld().getName().endsWith("_the_end");
    }
}
