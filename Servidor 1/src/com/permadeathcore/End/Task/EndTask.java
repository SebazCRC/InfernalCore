package com.permadeathcore.End.Task;

import com.mysql.fabric.xmlrpc.base.Array;
import com.permadeathcore.End.Util.DemonCurrentAttack;
import com.permadeathcore.End.Util.DemonPhase;
import com.permadeathcore.Main;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EndTask extends BukkitRunnable {

    private Entity enderDragon;
    private boolean isDied;
    private Main main;
    int timeForTnT = 30;
    int nextDragonAttack = 20;
    int timeInPortal = 0;
    private Map<Location, Integer> regenTime = new HashMap<>();
    private boolean attack360 = false;

    boolean lightingRain = false;
    int lightingDuration = 5;

    MovesTask currentMovesTask = null;

    int timeToInvulnerableEndermans;

    boolean nightVision = false;
    int nightVisionDuration = 5;

    int timeForEnd360 = 20;
    int currentPitchRotation = -360;
    boolean spawnedPaticles = false;

    int timeForChange = 15;

    private DemonCurrentAttack currentAttack = DemonCurrentAttack.NONE;
    private DemonPhase currentDemonPhase = DemonPhase.NORMAL;

    private boolean canMakeAnAttack = true;

    private Location teleportLocation;

    public EndTask(Main pl, Entity enderDragon) {

        main = pl;

        this.isDied = false;
        this.enderDragon = enderDragon;

        ((LivingEntity)enderDragon).setHealth(1350.0D);

        Location egg = new Location(main.endWorld, 0, 0, 0);
        Location withY = main.endWorld.getHighestBlockAt(egg).getLocation();
        teleportLocation = withY.add(0, 2, 0);
        teleportLocation.setPitch(enderDragon.getLocation().getPitch());

        for (Entity all : enderDragon.getWorld().getEntities()) {

            if (all instanceof Ghast) {

                all.remove();
            }
        }
    }

    @Override
    public void run() {

        if (isDied || enderDragon.isDead()) {

            main.setTask(null);
            cancel();
            return;
        }

        if (timeForChange > 0) {
            timeForChange = timeForChange - 1;
        } else {
            main.getEndData().getConfig().set("DecoratedEndSpawn", true);
            main.getEndData().saveFile();
            main.getEndData().reloadFile();
        }

        for (Player p : main.endWorld.getPlayers()) {

            if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != null) {

                if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {

                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20*3, 50));
                }
            }
        }

        timeForTnT = timeForTnT - 1;

        if (timeForTnT == 0) {

            EnderDragon dragon = (EnderDragon) enderDragon;

            if (dragon.getPhase() != EnderDragon.Phase.DYING && !attack360 && dragon.getPhase() != EnderDragon.Phase.FLY_TO_PORTAL && dragon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL && dragon.getPhase() != EnderDragon.Phase.LEAVE_PORTAL) {



                TNTPrimed tnt1 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(3, 0, -3), EntityType.PRIMED_TNT);
                tnt1.setFuseTicks(60);
                tnt1.setCustomName("dragontnt");
                tnt1.setCustomNameVisible(false);

                TNTPrimed tnt2 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(3, 0, 3), EntityType.PRIMED_TNT);
                tnt2.setFuseTicks(60);
                tnt2.setCustomName("dragontnt");
                tnt2.setCustomNameVisible(false);

                TNTPrimed tnt3 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(3, 0, 0), EntityType.PRIMED_TNT);
                tnt3.setFuseTicks(60);
                tnt3.setCustomName("dragontnt");
                tnt3.setCustomNameVisible(false);

                //

                TNTPrimed tnt4 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(-3, 0, 3), EntityType.PRIMED_TNT);
                tnt4.setFuseTicks(60);
                tnt4.setCustomName("dragontnt");
                tnt4.setCustomNameVisible(false);

                TNTPrimed tnt5 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(-3, 0, -3), EntityType.PRIMED_TNT);
                tnt5.setFuseTicks(60);
                tnt5.setCustomName("dragontnt");
                tnt5.setCustomNameVisible(false);

                TNTPrimed tnt6 = (TNTPrimed) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(-3, 0, 0), EntityType.PRIMED_TNT);
                tnt6.setFuseTicks(60);
                tnt6.setCustomName("dragontnt");
                tnt6.setCustomNameVisible(false);
            }

            timeForTnT = 60;

        }

        if (lightingRain) {

            if (lightingDuration >= 1) {

                canMakeAnAttack = false;
                lightingDuration = lightingDuration - 1;

                for (Player all : main.endWorld.getPlayers()) {

                    main.endWorld.strikeLightning(all.getLocation());

                    if (currentDemonPhase == DemonPhase.ENRAGED) {

                        all.damage(1.0D);
                    }
                }
            } else {

                lightingRain = false;
                lightingDuration = 5;

                canMakeAnAttack = true;
            }
        }

        if (nightVision) {

            if (nightVisionDuration >= 1) {

                nightVisionDuration = nightVisionDuration - 1;
            } else {

                for (Player all : main.endWorld.getPlayers()) {

                    if (currentDemonPhase == DemonPhase.NORMAL) {

                        Location highest = main.endWorld.getHighestBlockAt(all.getLocation()).getLocation().add(0, 1, 0);

                        AreaEffectCloud eff = (AreaEffectCloud) main.endWorld.spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);

                        eff.setParticle(Particle.DAMAGE_INDICATOR);
                        eff.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20*5, 1), false);
                        eff.setRadius(3.0F);
                    } else {

                        Location highest = main.endWorld.getHighestBlockAt(all.getLocation()).getLocation();

                        AreaEffectCloud eff = (AreaEffectCloud) main.endWorld.spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);

                        eff.setParticle(Particle.DAMAGE_INDICATOR);
                        eff.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20*5, 1), false);
                        eff.setRadius(3.0F);
                    }
                }

                nightVision = false;
                canMakeAnAttack = true;
            }
        }

        Location egg = new Location(main.endWorld, 0, 0, 0);
        Location withY = main.endWorld.getHighestBlockAt(egg).getLocation();

        if (enderDragon.getLocation().distance(withY) <= 5.0) {

            if (new Random().nextBoolean()) {
                start360attack();
            }
        }

        if (attack360) {

            canMakeAnAttack = false;

            if (timeForEnd360 >= 1) {

                timeForEnd360 = timeForEnd360 - 1;
            }

            if (timeForEnd360 >= 16) {
                EnderDragon dragon = (EnderDragon) enderDragon;
                if (dragon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL) {
                    dragon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);
                }
                dragon.teleport(teleportLocation);
            }

            if (timeForEnd360 == 15) {
                this.currentMovesTask = new MovesTask(main, (EnderDragon) enderDragon, teleportLocation);
                currentMovesTask.runTaskTimer(main, 5L, 5L);
            }

            if (timeForEnd360 == 0) {

                if (currentMovesTask != null) {

                    currentMovesTask.cancel();
                    currentMovesTask = null;
                }

                canMakeAnAttack = true;
                timeForEnd360 = 20;
                attack360 = false;
                spawnedPaticles = false;

                EnderDragon dragon = (EnderDragon) enderDragon;
                dragon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
            }
        }

        if (nextDragonAttack >= 1) {

            nextDragonAttack = nextDragonAttack - 1;

        } else if (nextDragonAttack == 0) {
            if (getCurrentDemonPhase() == DemonPhase.NORMAL) {

                nextDragonAttack = 60;
            } else {

                nextDragonAttack = 40;
            }

            if (canMakeAnAttack) {

                chooseAnAttack();
            } else {

                currentAttack = DemonCurrentAttack.NONE;
            }

            if (currentAttack == DemonCurrentAttack.NONE) {

                return;
            }

            if (currentAttack == DemonCurrentAttack.ENDERMAN_BUFF) {

                int endermanschoosed = 0;
                ArrayList<Enderman> endermen = new ArrayList<>();

                for (Entity entity : main.endWorld.getEntities()) {

                    if (entity instanceof Enderman) {

                        Enderman man = (Enderman) entity;

                        Location backUp = man.getLocation();
                        backUp.setY(0);

                        if (egg.distance(backUp) <= 35) {

                            if (endermanschoosed < 4) {

                                endermanschoosed = endermanschoosed + 1;
                                endermen.add(man);
                            }
                        }
                    }
                }

                if (!endermen.isEmpty()) {

                    for (Enderman mans : endermen) {

                        AreaEffectCloud a = (AreaEffectCloud) main.endWorld.spawnEntity(main.endWorld.getHighestBlockAt(mans.getLocation()).getLocation().add(0, 1, 0), EntityType.AREA_EFFECT_CLOUD);
                        a.setRadius(10.0F);
                        a.setParticle(Particle.VILLAGER_HAPPY);
                        a.setColor(Color.GREEN);

                        a.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 999999, 0), false);

                        mans.setInvulnerable(true);
                    }
                }
            } else if (currentAttack == DemonCurrentAttack.LIGHTING_RAIN) {

                lightingRain = true;
                lightingDuration = 5;
            } else if (currentAttack == DemonCurrentAttack.NIGHT_VISION) {

                nightVision = true;
                nightVisionDuration = 5;

                for (Player all : main.endWorld.getPlayers()) {

                    all.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*7, 0));
                }
            }
        }

        if (currentDemonPhase == DemonPhase.ENRAGED) {

            EnderDragon dragon = (EnderDragon) enderDragon;
            dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 7));
            dragon.setCustomName(main.format("&6&lENRAGED PERMADEATH DEMON"));
        } else {

            EnderDragon dragon = (EnderDragon) enderDragon;
            dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5));
        }

            int eFound = 0;
            ArrayList<Location> locations = new ArrayList<>();

            for (Entity e : main.endWorld.getEntities()) {

                if (e instanceof Enderman) {

                    if (eFound < 4) {

                        e.getWorld().strikeLightning(e.getLocation().add(5, 0, 0));
                        eFound = eFound + 1;
                    }
                }
            }

        if (!regenTime.isEmpty()) {

            for (org.bukkit.Location loc : regenTime.keySet()) {

                int time = regenTime.get(loc);

                if (time >= 1) {

                    regenTime.replace(loc, time, time - 1);
                } else {

                    loc.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
                    regenTime.remove(loc);

                    if (loc.getWorld().getBlockAt(loc) != null) {

                        if (loc.getWorld().getBlockAt(loc).getType() == Material.BEDROCK || loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {

                            return;
                        }

                        loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                    }
                }
            }
        }
    }

    public void chooseAnAttack() {

        int ran = new Random().nextInt(25);

        if (ran <= 3) {

            currentAttack = DemonCurrentAttack.LIGHTING_RAIN;
        } else if (ran >= 4 && ran <= 15) {

            currentAttack = DemonCurrentAttack.ENDERMAN_BUFF;
        } else if (ran >= 15 && ran <= 25) {

            currentAttack = DemonCurrentAttack.NIGHT_VISION;
        }
    }


    public DemonCurrentAttack getCurrentAttack() {

        return this.currentAttack;
    }

    public void clearAttack() {

        this.currentAttack = DemonCurrentAttack.NONE;
    }

    public Map<Location, Integer> getRegenTime() {
        return regenTime;
    }

    public void setEnderDragon(Entity enderDragon) {
        this.enderDragon = enderDragon;
    }

    public void setDied(boolean died) {
        isDied = died;
    }


    public Entity getEnderDragon() {
        return enderDragon;
    }

    public boolean isDied() {
        return isDied;
    }

    public Main getMain() {
        return main;
    }

    public void start360attack() {

        this.attack360 = true;
    }

    public DemonPhase getCurrentDemonPhase() {
        return currentDemonPhase;
    }

    public void setCurrentDemonPhase(DemonPhase currentDemonPhase) {
        this.currentDemonPhase = currentDemonPhase;
    }
}
