package tech.sebazcrc.infernalcore.End.Util;

import tech.sebazcrc.infernalcore.Task.*;
import tech.sebazcrc.infernalcore.Main;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static tech.sebazcrc.infernalcore.Main.instance;

public class EndManager implements Listener {

    private Main main;

    private List<Entity> enderCreepers;
    private List<Entity> enderGhasts;

    private ArrayList<Location> alreadyExploded = new ArrayList<>();

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

                e.setRadius(15.0F);
            }
        }
    }

    @EventHandler
    public void onC(EntityCombustEvent e) {

        if (e.getEntity().getWorld().getName().equalsIgnoreCase(main.endWorld.getName()) && !(e.getEntity() instanceof Player)) {

            e.setCancelled(true);
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

                        final Enderman man = (Enderman) all;
                        invulnerable.add(man);

                        Bukkit.getServer().getScheduler().runTaskLater(main, new Runnable() {
                            @Override
                            public void run() {

                                if (man == null) return;

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
    public void onC(AsyncPlayerChatEvent e) {

        if (e.getMessage().startsWith("noVulne")) {


            if (main.getTask() != null) {

                main.getTask().getEnderDragon().setInvulnerable(false);
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEMDamage(EntityDamageEvent e) {

        if (e.getEntity().getType() == EntityType.DROPPED_ITEM && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {

            Item item = (Item) e.getEntity();

            if (item.getItemStack().getType() == Material.SHULKER_SHELL) {

                e.setCancelled(true);
            }
        }

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

                final Location saved = ent.getLocation();

                teleport(ent, ent.getWorld(), ent.getLocation(), false, false, false);

                if (saved.getX() != ent.getLocation().getX() && saved.getY() != ent.getLocation().getY()) {

                    e.setCancelled(true);
                }
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

                int randomProb = new Random().nextInt(99);
                randomProb = randomProb + 1;

                if (main.getDays() <= 39) {

                    if (randomProb <= 20) {

                        //if (instance.getShulkerEvent().isRunning()) {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
                        //} else {

                          //  e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 1));
                        }
                    }
                } else if (main.getDays() >= 40) {

                /**
                    if (randomProb <= 2) {

                        if (instance.getShulkerEvent().isRunning()) {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
                        } else {

                            e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 1));
                        }
                    }
                }

                 */
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent e) {

        Entity t = e.getEntity();

        if (isInEnd(t.getLocation())) {

            if (e.getEntity().getType() == EntityType.ENDER_CRYSTAL && main.getTask() != null) {

                if (alreadyExploded.contains(e.getLocation())) return;

                final EnderCrystal c = (EnderCrystal) e.getEntity();

                if (c.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                    int random = new Random().nextInt(main.getEndData().getTimeList().size());
                    main.getTask().getRegenTime().put(c.getLocation(), main.getEndData().getTimeList().get(random));

                    Location nL = e.getLocation().add(0, 10, 0);
                    Entity g = instance.getNmsHandler().spawnCustomGhast(nL, CreatureSpawnEvent.SpawnReason.CUSTOM);

                    enderGhasts.add(g);

                    final Location loc = e.getLocation();
                    alreadyExploded.add(loc);

                    Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                        @Override
                        public void run() {

                            if (alreadyExploded.contains(loc)) {

                                alreadyExploded.remove(loc);
                            }
                        }
                    }, 20*5);

                    for (Player all : main.endWorld.getPlayers()) {
                        all.playSound(nL, Sound.ENTITY_WITHER_SPAWN, 100.0F, 100.0F);
                    }
                }
            }
        }

        if (e.getEntity() instanceof TNTPrimed) {
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
            if (tnt.getCustomName().equalsIgnoreCase("asdanteseratntdeath")) {

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
                }, 5L);
            }
        }
    }

    @EventHandler
    public void onDragonRegen(EntityRegainHealthEvent e) {

        if (e.getEntity() instanceof EnderDragon) {

            e.setAmount(e.getAmount() / 2);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        if (e.isCancelled()) return;

        LivingEntity entity = e.getEntity();

        if (isInEnd(entity.getLocation())) {

            if (!main.getEndData().getConfig().getBoolean("DecoratedEndSpawn")) {

                LivingEntity ents = e.getEntity();

                if (ents.getType() == EntityType.ENDERMAN || ents.getType() == EntityType.CREEPER) {

                    Block b = ents.getLocation().getBlock().getRelative(BlockFace.DOWN);

                    int structure = new Random().nextInt(4);

                    ArrayList<Block> toChange = new ArrayList<>();

                    if (structure == 0) {

                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.SOUTH_EAST));
                        toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                        toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
                    } else if (structure == 1) {

                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                        toChange.add(b);
                    } else if (structure == 2) {

                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                        toChange.add(b);
                    } else if (structure == 3) {

                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                        toChange.add(b);
                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.EAST));
                    } else if (structure == 4) {

                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.NORTH_WEST));
                        toChange.add(b);
                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.WEST));
                    }

                    for (Block all : toChange) {

                        Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();

                        Block now = main.endWorld.getBlockAt(used);

                        if (now.getType() == Material.END_STONE) {

                            now.setType(Material.END_STONE_BRICKS);
                        }
                    }
                }
            }

            if (main.getTask() == null) {

                for (Entity n : e.getLocation().getWorld().getEntitiesByClass(EnderDragon.class)) {

                    if (n.isValid() && !n.isDead()) {

                        n.setCustomName("§6§lPERMADEATH DEMON");
                        ((LivingEntity) n).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1350.0D);
                        //main.setTask(new EndTask(main, n));
                        main.getTask().runTaskTimer(main, 20L, 20L);
                    }
                }
            } else {

                if (!main.getTask().isDied()) {

                    Entity n = main.getTask().getEnderDragon();

                    if (n.getType() == EntityType.ENDER_DRAGON && n.isValid() && !n.isDead()) {

                        EnderDragon dragon = (EnderDragon) n;
                        if (dragon.getHealth() <= 400.0) {

                            main.getTask().setCurrentDemonPhase(DemonPhase.ENRAGED);
                        }
                    }
                }
            }

            e.getLocation().getWorld().setMonsterSpawnLimit(60);

            if (!(entity instanceof Enderman)) return;

            int creeperProb = new Random().nextInt(14);
            int ghastProb = new Random().nextInt(89);
            int extraProb = new Random().nextInt(9);

            if (creeperProb <= 2) {

                for (int i = 0; i < 1; i++) {

                    main.getNmsHandler().spawnCustomCreeper(e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM, false, false);
                }

                if (main.getTask() != null) {

                    if (main.getTask().getCurrentDemonPhase() == DemonPhase.ENRAGED) {

                        if (extraProb <= 2) {

                            main.getNmsHandler().spawnCustomCreeper(e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM, false, false);
                        }

                    }
                }
            }

            if (ghastProb <= 2) {

                boolean dragonDied = true;

                if (main.endWorld.getEntitiesByClass(EnderDragon.class).size() >= 1) {

                    dragonDied = false;
                }

                if (dragonDied) {

                    teleport(null, main.endWorld, entity.getLocation(), false, true, true);
                }
            }

            int removeProb = new Random().nextInt(49);

            if (removeProb <= 9) {

                e.setCancelled(true);
                return;
            }

            if (main.getDays() >= 40) {

                Enderman man = (Enderman) e.getEntity();

                if (new Random().nextInt(99) == 1) {
                    instance.getNmsAccesor().injectHostilePathfinders(man);
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

            if (e.getEntity() instanceof Fireball && e.getEntity().getShooter() instanceof Ghast && e.getHitEntity() == null) {

                e.getHitBlock().getWorld().createExplosion(e.getHitBlock().getLocation(), 2.0F, true, true, (Entity) e.getEntity().getShooter());
            }

            if (e.getEntity() instanceof ShulkerBullet) {

                ShulkerBullet b = (ShulkerBullet) e.getEntity();

                if (b.getShooter() instanceof Shulker) {

                    Shulker s = (Shulker) b.getShooter();

                    if (s.getLocation().distance(e.getHitBlock().getLocation()) >= 4.0) {

                        Location w = e.getHitBlock().getLocation();

                        if (e.getHitBlockFace() == BlockFace.EAST) {

                            w = e.getHitBlock().getRelative(BlockFace.EAST).getLocation();
                        }

                        if (e.getHitBlockFace() == BlockFace.UP) {

                            w = e.getHitBlock().getRelative(BlockFace.UP).getLocation();
                        }

                        if (e.getHitBlockFace() == BlockFace.DOWN) {

                            w = e.getHitBlock().getRelative(BlockFace.DOWN).getLocation();
                        }

                        if (e.getHitBlockFace() == BlockFace.NORTH) {

                            w = e.getHitBlock().getRelative(BlockFace.NORTH).getLocation().add(0, 1, 0);
                        }

                        if (e.getHitBlockFace() == BlockFace.SOUTH) {

                            w = e.getHitBlock().getRelative(BlockFace.SOUTH).getLocation().add(0, 1, 0);
                        }

                        w.getBlock().setType(Material.AIR);

                        TNTPrimed tnt = (TNTPrimed) s.getWorld().spawnEntity(w, EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(40);

                        tnt.setCustomName("tnt");
                        tnt.setCustomNameVisible(false);
                    }
                }
            }
        }

        if (e.getHitEntity() != null) {

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
                    where = new Location(currentLoc.getWorld(),
                            new Random().nextBoolean() ? currentLoc.getX() + new Random().nextInt(10) : currentLoc.getX() + new Random().nextInt(10) * -1, 100, new Random().nextBoolean() ? currentLoc.getZ() + new Random().nextInt(10) : currentLoc.getZ() + new Random().nextInt(10) * -1);

                    if (world.getHighestBlockAt(where).getY() != -1.0D) {
                        where.setY(world.getHighestBlockAt(where).getY() + 1);
                    }


                    int iy = (int) where.getY();

                    while (where.getY() == -1.0D) {

                        where = new Location(currentLoc.getWorld(),
                                new Random().nextBoolean() ? currentLoc.getX() + new Random().nextInt(10) : currentLoc.getX() + new Random().nextInt(10) * -1, 100, new Random().nextBoolean() ? currentLoc.getZ() + new Random().nextInt(10) : currentLoc.getZ() + new Random().nextInt(10) * -1);
                        where.setY(world.getHighestBlockAt(where).getY());
                    }

                    where.setY(where.getY() + 1);

                    if (iy == -1) {
                        isVoidLocation = true;
                    }

                    foundLocation = true;
                }
            }

            if (foundLocation) {
                if (isSpawning) {

                    if (isVoidLocation) {
                        return;
                    }

                    enderCreepers.add(instance.getNmsHandler().spawnCustomCreeper(where, CreatureSpawnEvent.SpawnReason.CUSTOM, false, false));
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

                if (isSpawning) {

                    Entity ghast = instance.getNmsHandler().spawnCustomGhast(where, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    enderGhasts.add(ghast);

                    ghast.teleport(where.add(0, 5, 0));

                } else {
                    int ran = new Random().nextInt(99);
                    if (ran <= 19) {
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
