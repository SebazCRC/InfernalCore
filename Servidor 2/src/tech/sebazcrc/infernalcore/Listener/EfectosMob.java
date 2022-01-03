package tech.sebazcrc.infernalcore.Listener;

import com.mojang.datafixers.kinds.IdF;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.infernalcore.Entity.MobFactory;
import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Manager.GhastExplosion;
import tech.sebazcrc.infernalcore.Util.Item.CustomItems;
import tech.sebazcrc.infernalcore.Util.Item.ItemBuilder;

import java.util.ArrayList;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

public class EfectosMob implements Listener {

    private Main instance;
    private SplittableRandom random;

    public EfectosMob(Main instance) {
        this.instance = instance;
        this.random = new SplittableRandom();
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        LivingEntity liv = e.getEntity();
        CreatureSpawnEvent.SpawnReason r = e.getSpawnReason();
        EntityType type = e.getEntityType();
        Location l = e.getLocation();
        World w = l.getWorld();

        if (e.isCancelled()) return;

        if (instance.getDays() >= 10) {

            if (type == EntityType.CREEPER && checkProb(3)) {

                ((Creeper)liv).setPowered(true);
            }

            if (type == EntityType.GHAST && getBiomeAt(l) == Biome.MUSHROOM_FIELDS || getBiomeAt(l) == Biome.MUSHROOM_FIELD_SHORE) {

                if (!instance.getNightmareEvent().isRunning()) {

                    e.setCancelled(true);
                    return;
                }

                if (l.getBlock().getLightLevel() > 7) {
                    e.setCancelled(true);
                    return;
                }

                if (random.nextInt(5) + 1 != 1) {
                    e.setCancelled(true);
                    return;
                }

                liv.setCustomName(instance.format("&6Alma en Desgracia"));
                liv.setMaxHealth(liv.getMaxHealth() * 2);
                liv.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
                liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                addID(liv, "almas");

                w.playSound(l, Sound.ENTITY_WITHER_SPAWN, 100.0F, 100.0F);
            }

            if (type == EntityType.GHAST && r == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {

                liv.setCustomName(instance.format("&6Alma en Desgracia"));
                liv.setMaxHealth(liv.getMaxHealth() * 2);
                liv.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
                liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                addID(liv, "almas");
                w.playSound(l, Sound.ENTITY_WITHER_SPAWN, 100.0F, 100.0F);
            }

            if (type == EntityType.ZOMBIE || type == EntityType.HUSK) {
                if (checkProb(3) && instance.getNightmareEvent().isRunning() && w.getHighestBlockAt(l).getLocation().distance(l) <= 5) {
                    Ghast g = w.spawn(l, Ghast.class);
                    g.setCustomName(instance.format("&6Alma en Desgracia"));
                    g.setMaxHealth(g.getMaxHealth() * 2);
                    g.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
                    g.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                    addID(g, "almas");
                    w.playSound(l, Sound.ENTITY_WITHER_SPAWN, 100.0F, 100.0F);
                    e.setCancelled(true);
                }

                if (checkProb(5) && type == EntityType.ZOMBIE) {

                    w.spawn(l, Husk.class);

                    liv.remove();
                    e.setCancelled(true);
                }
            }

            if (type == EntityType.PIG_ZOMBIE) {
                PigZombie z = (PigZombie) liv;

                if (z.isZombifiedPiglin()) {
                    z.setAngry(true);
                }

                z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0D);
                z.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(16.0D);
                z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);

                if (z.hasPotionEffect(PotionEffectType.SPEED)) {
                    z.removePotionEffect(PotionEffectType.SPEED);
                }

                if (z.isPiglinBrute()) return;

                if (checkProb(30)) {
                    liv.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
                    ItemStack s = liv.getEquipment().getHelmet();
                    ItemMeta meta = s.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(4) + 1, true);
                    s.setItemMeta(meta);
                    liv.getEquipment().setHelmet(s);
                }

                if (checkProb(30)) {

                    liv.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));

                    ItemStack s = liv.getEquipment().getChestplate();
                    ItemMeta meta = s.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(4) + 1, true);
                    s.setItemMeta(meta);
                    liv.getEquipment().setChestplate(s);
                }

                if (checkProb(30)) {

                    liv.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));

                    ItemStack s = liv.getEquipment().getLeggings();
                    ItemMeta meta = s.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(4) + 1, true);
                    s.setItemMeta(meta);
                    liv.getEquipment().setLeggings(s);
                }

                if (checkProb(30)) {

                    liv.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));

                    ItemStack s = liv.getEquipment().getBoots();
                    ItemMeta meta = s.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(4) + 1, true);
                    s.setItemMeta(meta);
                    liv.getEquipment().setBoots(s);
                }
            }

            if (liv instanceof Zombie) {

                Zombie z = (Zombie) liv;
                if (z.isBaby()) {

                    z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                    z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                }
            }
        }

        if (instance.getDays() >= 15) {
            if (type == EntityType.CREEPER && instance.getNightmareEvent().isRunning() && checkProb(10)) {
                MobFactory.craftGhoulCreeper(((Creeper)liv), l);
            }
            if (type == EntityType.ENDERMAN && checkProb(5)) {
                MobFactory.craftExploderEnderman(((Enderman)liv), l);
            }
            if (type == EntityType.GHAST && w.getEnvironment() == World.Environment.NETHER && checkProb(10)) {
                MobFactory.craftAlmaEnDesgracia(((Ghast)liv), l);
            }
            if (type == EntityType.ZOMBIE) {
                addPotionEffect(liv, PotionEffectType.INCREASE_DAMAGE);
                addPotionEffect(liv, PotionEffectType.SPEED);
                addPotionEffect(liv, PotionEffectType.DAMAGE_RESISTANCE);
            }
            if (type == EntityType.SKELETON && r == CreatureSpawnEvent.SpawnReason.NATURAL) {
                EntityEquipment eq = liv.getEquipment();

                eq.setHelmet(new ItemBuilder(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
                eq.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
                eq.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
                eq.setBoots(new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());

                ItemStack s = new ItemStack(Material.BOW);
                if (s != null) {
                    try {
                        ItemMeta meta = s.getItemMeta();
                        meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
                        s.setItemMeta(meta);
                        eq.setItemInMainHand(s);
                    } catch (Exception x) {}
                }
            }
            if (type == EntityType.PHANTOM) {
                MobFactory.spawnPhantomClass((Phantom) liv, l);
            }
            if (liv instanceof Spider) {
                if (liv.getPassengers().isEmpty()) {
                    Skeleton s = (Skeleton) instance.getNmsHandler().spawnNMSEntity("Skeleton", EntityType.SKELETON, l, CreatureSpawnEvent.SpawnReason.NATURAL);
                    liv.addPassenger(s);
                }
            }
        }
    }

    private void addPotionEffect(LivingEntity liv, PotionEffectType effect) {
        addPotionEffect(liv, effect, 0);
    }

    private void addPotionEffect(LivingEntity liv, PotionEffectType effect, int amplifier) {
        liv.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
    }

    @EventHandler
    public void onBurn(EntityCombustEvent e){

        if (instance.getDays() >= 15 && e.getEntity() instanceof Phantom) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onConvert(EntityTransformEvent e) {

        if (e.getTransformReason() == EntityTransformEvent.TransformReason.INFECTION && instance.getDays() >= 10) {

            if (!checkProb(20)) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDead(EntityDeathEvent e) {

        LivingEntity liv = e.getEntity();
        EntityType type = e.getEntityType();
        Location l = liv.getLocation();
        World w = l.getWorld();

        if (instance.getDays() >= 10) {

            if (type == EntityType.WITHER_SKELETON) {

                boolean contains = false;

                for (ItemStack s : e.getDrops()) {

                    if (s != null) {
                        if (s.getType() == Material.WITHER_SKELETON_SKULL) {

                            e.getDrops().remove(s);
                            contains = true;
                        }
                    }
                }

                if (contains) {

                    if (random.nextInt(70) + 1 == 1) {

                        e.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                    }
                }
            }

            if (type == EntityType.ZOMBIE) {

                boolean contains = false;

                for (ItemStack s : e.getDrops()) {

                    if (s != null) {
                        if (s.getType() == Material.ENCHANTED_GOLDEN_APPLE) {

                            e.getDrops().remove(s);
                            contains = true;
                        }
                    }
                }

                if (contains) {

                    if (checkProb(25)) {

                        e.getDrops().add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                    }
                }
            }
        }

        if (instance.getDays() >= 15) {

            if (type == EntityType.GHAST) {

                if (has(liv, "almas")) {

                    e.getDrops().clear();

                    if (checkProb(10)) {
                        e.getDrops().add(new ItemStack(Material.GUNPOWDER, 32));
                    }

                    if (checkProb(70)) {
                        e.getDrops().add(CustomItems.createLagrimaDeAlma());
                    }
                }
            }

            if (type == EntityType.PHANTOM && has(liv, "kamikaze")) {
                if (!liv.getPassengers().isEmpty()) {
                    for (Entity passengers : liv.getPassengers()) {
                        if (passengers.getType() == EntityType.MINECART_TNT) {
                            liv.removePassenger(passengers);
                            passengers.remove();

                            TNTPrimed primed = w.spawn(l, TNTPrimed.class);
                            primed.setFuseTicks(40);
                        }
                    }
                }
            }
            if (type == EntityType.CREEPER && has(liv, "ghoul_creeper")) {
                e.getDrops().clear();
                e.getDrops().add(CustomItems.createCreeperPowerSource());
            }

            if (type == EntityType.PIG_ZOMBIE) {

                PigZombie z = (PigZombie) liv;
                if (!z.isZombifiedPiglin()) {
                    e.getDrops().clear();
                }
            }

            if (type == EntityType.ENDERMAN && has(liv, "exploding_enderman")) {
                TNTPrimed primed = l.getWorld().spawn(l, TNTPrimed.class);
                primed.setFuseTicks(80);

                try {
                    for (ItemStack s : e.getDrops()) {
                        if (s != null) {
                            if (s.getType() == Material.TNT) {
                                e.getDrops().remove(s);
                            }
                        }
                    }
                } catch (Exception c) {
                }
            }

            if (type == EntityType.VINDICATOR && has(liv, "legendary_vindicator")) {
                if (checkProb(30)) {
                    e.getDrops().add(CustomItems.createLegendaryAxe());
                }
            }

        }
    }

    private Biome getBiomeAt(Location l) {
        return l.getWorld().getBiome((int)l.getX(), (int)l.getY(), (int)l.getZ());
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e) {

        if (e.getEntity() instanceof Fireball && e.getEntity().getShooter() instanceof Ghast) {

            Ghast g = (Ghast) e.getEntity().getShooter();
            Fireball f = (Fireball) e.getEntity();

            if (has(g, "almas")) {

                f.setYield(0);
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {

        if (e.getEntity() instanceof Fireball && e.getEntity().getShooter() instanceof Ghast) {

            Ghast g = (Ghast) e.getEntity().getShooter();
            Fireball f = (Fireball) e.getEntity();

            if (has(g, "almas")) {

                Location l = e.getHitEntity() != null ? e.getHitEntity().getLocation() : e.getHitBlock().getLocation();

                if (l.getWorld().getEnvironment() != World.Environment.NETHER) {
                    new GhastExplosion(l.getWorld(), g, l, 20.0f);
                } else {
                    new GhastExplosion(l.getWorld(), g, l, 25.0f);
                }
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {

        if (e.getEntity() instanceof Fireball) {

            Fireball f = (Fireball) e.getEntity();

            if (f.getShooter() instanceof Ghast) {
                Ghast g = (Ghast) f.getShooter();

                if (has(g, "almas")) {

                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Fireball) {

            if (!(((Fireball) e.getEntity()).getShooter() instanceof Ghast)) return;

            Ghast g = (Ghast) ((Fireball) e.getEntity()).getShooter();
            Fireball f = (Fireball) e.getEntity();
            if (has(g, "almas")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpawner(SpawnerSpawnEvent e) {

        if (instance.getDays() >= 10) {

            if (e.getEntity() instanceof Guardian) {
                e.getEntity().setCustomName(instance.format("&6Dungeon Guardian"));
                addID(e.getEntity(), "dungeon_elder_guardian");
            }

            if (e.getEntity() instanceof Skeleton) {

                Skeleton s = (Skeleton) e.getEntity();

                if (s.getEquipment().getItemInMainHand() != null) {

                    if (s.getEquipment().getItemInMainHand().getType() == Material.DIAMOND_AXE) {

                        if (random.nextInt(20) + 1 == 1) {

                            if (s.getCustomName() == null) {
                                if (!s.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {

                                    s.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                                }
                                s.setCustomName(instance.format("&6Skeleton +"));
                                addID(e.getEntity(), "esqueleto_especial");
                            } else {
                                if (!s.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {

                                    s.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2));
                                }

                                addID(e.getEntity(), "esqueleto_tinieblas");
                            }

                            s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                            s.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                        } else {

                            e.setCancelled(true);
                        }
                    }
                }
            }

            if (e.getEntity() instanceof CaveSpider && e.getEntity().getCustomName() != null) {

                CaveSpider spider = (CaveSpider) e.getEntity();

                if (random.nextInt(15) + 1 <= 3) {
                    Creeper c = e.getLocation().getWorld().spawn(e.getLocation(), Creeper.class);
                    c.setCustomName(instance.format("&6Mythic Creeper"));
                    c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                    c.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                    addID(c, "mythic_creeper");
                    c.setPowered(true);
                }

                if (random.nextInt(15) == 1) {

                    ArrayList<String> effectList = new ArrayList<String>();

                    effectList.add("SPEED");
                    effectList.add("REGENERATION");
                    effectList.add("INCREASE_DAMAGE");
                    effectList.add("INVISIBILITY");
                    effectList.add("JUMP");
                    effectList.add("SLOW_FALLING");
                    effectList.add("DAMAGE_RESISTANCE");


                    for (int i = 0; i < 5; i++) {

                        int randomIndex = random.nextInt(effectList.size());
                        String randomEffectName = effectList.get(randomIndex);

                        if (randomEffectName.equals("SPEED")) { // Velocidad III
                            int effectLevel = 2;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("REGENERATION")) { // Regeneración IV
                            int effectLevel = 3;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("INCREASE_DAMAGE")) { // Fuerza IV
                            int effectLevel = 3;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("INVISIBILITY")) { // Invisibilidad
                            int effectLevel = 0;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("JUMP")) { // Salto V
                            int effectLevel = 4;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("SLOW_FALLING")) { // Caida lenta
                            int effectLevel = 0;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("GLOWING")) { // Brillo
                            int effectLevel = 0;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999999, effectLevel));
                        }

                        if (randomEffectName.equals("DAMAGE_RESISTANCE")) { // Resistencia III
                            int effectLevel = 2;
                            spider.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, effectLevel));
                        }
                    }

                    addID(e.getEntity(), "swamp_spider");
                } else {

                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {

        if (instance.getDays() >= 10) {

            if (e.getEntity() instanceof Player && e.getDamager() instanceof Guardian) {

                if (has(e.getDamager(), "dungeon_elder_guardian")) {

                    Player p = (Player) e.getEntity();
                    if (p.getHealth() > 11.0D) {
                        try {
                            p.damage(10.0D);
                        } catch (Exception x) {}
                    } else {
                        try {
                            p.damage(4.0D);
                        } catch (Exception x) {}
                    }

                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0));
                }
            }

            if (e.getEntity() instanceof Player && e.getDamager() instanceof Husk) {

                Player p = (Player) e.getEntity();

                Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {

                        if (p.hasPotionEffect(PotionEffectType.HUNGER)) {

                            p.removePotionEffect(PotionEffectType.HUNGER);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10*60*20, 0));
                        }
                    }
                }, 5L);
            }

            if (e.getEntity() instanceof Zombie && e.getDamager() instanceof Projectile) {

                //Skeir - Smukler

                if (!(((Projectile) e.getDamager()).getShooter() instanceof Skeleton)) return;

                Zombie z = (Zombie) e.getEntity();
                Skeleton s = (Skeleton) ((Projectile) e.getDamager()).getShooter();

                if (z.getCustomName() != null && s.getCustomName() != null) {

                    boolean hasItem = z.getEquipment().getItemInOffHand() == null ? false : z.getEquipment().getItemInOffHand().getType() == Material.ENCHANTED_GOLDEN_APPLE;
                    boolean hasTorch = s.getEquipment().getItemInOffHand() == null ? false : s.getEquipment().getItemInOffHand().getType() == Material.REDSTONE_TORCH;
                    boolean isBoss = z.getCustomName().contains("Smukler") || has(z, "dungeon_boss") || hasItem;
                    boolean isSkeir = s.getCustomName().contains("Skeir") || has(s, "dungeon_skeir") || hasTorch;

                    if (isBoss && isSkeir) {
                        if (!has(z, "dungeon_boss")) {
                            addID(z, "dungeon_boss");
                        }
                        if (!has(s, "dungeon_skeir")) {
                            addID(s, "dungeon_skeir");
                        }

                        if (z.isArmsRaised()) {
                            z.setArmsRaised(false);
                        }

                        e.setCancelled(true);
                    }
                }
            }

            if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {

                //Skeir - Smukler

                if (!(((Projectile) e.getDamager()).getShooter() instanceof Skeleton)) return;

                Player p = (Player) e.getEntity();
                Skeleton s = (Skeleton) ((Projectile) e.getDamager()).getShooter();

                if (s.getCustomName() != null) {

                    boolean hasTorch = s.getEquipment().getItemInOffHand() == null ? false : s.getEquipment().getItemInOffHand().getType() == Material.REDSTONE_TORCH;
                    boolean isSkeir = s.getCustomName().contains("Skeir") || has(s, "dungeon_skeir") || hasTorch;

                    if (isSkeir) {
                        try {

                            p.damage(2.0D);
                        } catch (Exception x) {}
                    }
                }
            }

            if (e.getEntity() instanceof Zombie || e.getEntity() instanceof Skeleton) {


                if (!(e.getDamager() instanceof Projectile)) return;
                LivingEntity liv = (LivingEntity) e.getEntity();
                Projectile p = (Projectile) e.getDamager();

                if (p.getShooter() instanceof Player && liv.getCustomName() != null) {

                    boolean hasItem = liv.getEquipment().getItemInOffHand() == null ? false : liv.getEquipment().getItemInOffHand().getType() == Material.ENCHANTED_GOLDEN_APPLE;
                    boolean hasTorch = liv.getEquipment().getItemInOffHand() == null ? false : liv.getEquipment().getItemInOffHand().getType() == Material.REDSTONE_TORCH;
                    boolean isBoss = liv.getCustomName().contains("Smukler") || has(liv, "dungeon_boss") || hasItem;
                    boolean isSkeir = liv.getCustomName().contains("Skeir") || has(liv, "dungeon_skeir") || hasTorch;

                    if (isBoss || isSkeir) {

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (e.getEntity() instanceof Zombie) {

            if (e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                Zombie liv = (Zombie) e.getEntity();
                Zombie z = (Zombie) e.getEntity();

                if (z.getCustomName() == null) return;
                boolean hasItem = z.getEquipment().getItemInOffHand() == null ? false : z.getEquipment().getItemInOffHand().getType() == Material.ENCHANTED_GOLDEN_APPLE;
                boolean isBoss = liv.getCustomName().contains("Smukler") || has(liv, "dungeon_boss") || hasItem;

                if (isBoss) {

                    e.setCancelled(true);
                }
            }
        }

        if (e.getEntity() instanceof Creeper && has(e.getEntity(), "ghoul_creeper")) {

            if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {

        if (instance.getDays() >= 10 && e.getEntity() instanceof Zombie && e.getTarget() instanceof Skeleton) {

            //Skeir - Smukler

            Zombie z = (Zombie) e.getEntity();
            Skeleton s = (Skeleton) e.getTarget();

            if (z.getCustomName() != null && s.getCustomName() != null) {

                boolean hasItem = z.getEquipment().getItemInOffHand() == null ? false : z.getEquipment().getItemInOffHand().getType() == Material.ENCHANTED_GOLDEN_APPLE;
                boolean hasTorch = s.getEquipment().getItemInOffHand() == null ? false : s.getEquipment().getItemInOffHand().getType() == Material.REDSTONE_TORCH;
                boolean isBoss = z.getCustomName().contains("Smukler") || has(z, "dungeon_boss") || hasItem;
                boolean isSkeir = s.getCustomName().contains("Skeir") || has(s, "dungeon_skeir") || hasTorch;

                if (isBoss && isSkeir) {
                    if (!has(z, "dungeon_boss")) {
                        addID(z, "dungeon_boss");
                    }
                    if (!has(s, "dungeon_skeir")) {
                        addID(s, "dungeon_skeir");
                    }

                    if (z.isArmsRaised()) {
                        z.setArmsRaised(false);
                    }

                    e.setCancelled(true);
                }
            }
        }
    }

    public boolean has(Entity entity, String id) {

        return entity.getPersistentDataContainer().has(new NamespacedKey(instance, id), PersistentDataType.BYTE);
    }

    public void addID(Entity entity, String id) {
        entity.getPersistentDataContainer().set(new NamespacedKey(instance, id), PersistentDataType.BYTE, (byte) 1);
    }

    public boolean checkProb(int i) {

        return random.nextInt(100) + 1 <= i;
    }
}
