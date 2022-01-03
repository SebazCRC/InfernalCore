package com.permadeathcore.Listener;

import com.permadeathcore.End.Util.NMSAccesor;
import com.permadeathcore.Entity.HostileIronGolem;
import com.permadeathcore.Entity.HostileVillager;
import com.permadeathcore.Entity.SpecialBee;
import com.permadeathcore.Main;
import com.permadeathcore.Util.HiddenStringUtils;
import com.permadeathcore.Util.ItemBuilder;
import net.minecraft.server.v1_15_R1.EntityBee;
import net.minecraft.server.v1_15_R1.EntitySkeleton;
import net.minecraft.server.v1_15_R1.EntitySkeletonWither;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.permadeathcore.Main.format;
import static com.permadeathcore.Main.instance;

public class EfectosMob implements Listener {

    private Main instance;

    public EfectosMob(Main instance) {
        this.instance = instance;
        initialize();
    }

    public void initialize() {

        long d = instance.getDays();
        NMSAccesor nmsAccesor = instance.getNmsAccesor();

        for (World world : Bukkit.getWorlds()) {

            for (Entity entity : world.getEntities()) {

                if (!(entity instanceof LivingEntity)) return;

                LivingEntity liv = (LivingEntity) entity;

                EntityType type = entity.getType();

                if (d >= 5) {

                    if (type == EntityType.SKELETON) {

                        EntitySkeleton nS = (EntitySkeleton) nmsAccesor.craftNewEntity(new EntitySkeleton(EntityTypes.SKELETON, nmsAccesor.craftWorld(world)), entity.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                        entity.remove();
                    }

                    if (type == EntityType.BEE) {

                        nmsAccesor.craftNewEntity(new SpecialBee(entity.getLocation()), entity.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                        entity.remove();
                    }

                    if (type == EntityType.VILLAGER) {

                        if (instance.getDays() >= 15) return;

                        if (entity.getCustomName() == null) return;

                        if (entity.getCustomName().contains(instance.format("&6Aldeano Conspirador"))) {

                            Villager villager = (Villager) instance.getNmsAccesor().craftNewEntity(new HostileVillager(entity.getLocation(), (Villager) entity), entity.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                            for (Entity near : villager.getNearbyEntities(30, 30, 30)) {

                                if (near instanceof Player) {

                                    villager.setTarget(((Player)near));
                                }
                            }
                            entity.remove();
                        }
                    }
                }

                setupDay8(d, liv, entity, type, nmsAccesor);
            }
        }
    }

    private void setupDay8(long d, LivingEntity liv, Entity entity, EntityType type, NMSAccesor nmsAccesor) {

        if (d >= 8) {

            if (type == EntityType.PIG_ZOMBIE && d < 15) {

                PigZombie pigman = (PigZombie) entity;

                pigman.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
                pigman.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
                pigman.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
                pigman.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
            }

            if (type == EntityType.VINDICATOR) {

                liv.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));

                nmsAccesor.setMaxHealth(liv, nmsAccesor.getMaxHealth(liv) + 10.0D, true);
            }

            if (type == EntityType.VEX) {

                liv.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));

            }

            if (type == EntityType.PILLAGER) {

                liv.getEquipment().setItemInMainHand(new ItemBuilder(Material.CROSSBOW).addEnchant(Enchantment.QUICK_CHARGE, 3).addEnchant(Enchantment.PIERCING, 2).build());
            }
        }
    }

    private void setupDay15(long d, LivingEntity liv, Entity entity, EntityType type, NMSAccesor nmsAccesor) {

        if (d >= 15) {

            if (liv.getType() == EntityType.SLIME) {

                Double maxHealth = nmsAccesor.getMaxHealthOf(liv);

                nmsAccesor.setMaxHealth(liv, maxHealth * 2, true); // Recordemos que tienen el doble de vida de un slime normal.
                ((Slime) liv).setSize(15);
                liv.setCustomName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Giga Slime");
                liv.setCustomNameVisible(false);
            }

            if (liv.getType() == EntityType.MAGMA_CUBE) {


                Double maxHealth = nmsAccesor.getMaxHealthOf(liv);
                ((Slime) liv).setSize(16);
                nmsAccesor.setMaxHealth(liv, maxHealth * 2, true);
                liv.setCustomName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Giga MagmaCube");
                liv.setCustomNameVisible(false);
            }

            if (liv.getType() == EntityType.GHAST) {

                if (liv.getWorld().getName().endsWith("nether") || liv.getWorld().getName().equalsIgnoreCase(Main.getInstance().world.getName())) {

                    Double HPGenerator = ThreadLocalRandom.current().nextDouble(40, 60 + 1);
                    nmsAccesor.setMaxHealth(liv, HPGenerator, true);
                    liv.setCustomName(ChatColor.GOLD + "Ghast Demoníaco");
                }
            }

            if (liv.getType() == EntityType.CREEPER) {

                Creeper c = (Creeper) liv;

                c.setPowered(true);

                c.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                c.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            }

            if (liv.getType() == EntityType.WITHER_SKELETON) {

                liv.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2));
                liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
            }

            if (type == EntityType.ZOMBIE) {

                Zombie z = (Zombie) liv;

                z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3));
                z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                z.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));

            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        if (instance.getDays() >= 5) {

            if (e.getEntity() instanceof Bee && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                instance.getNmsAccesor().craftNewEntity(new SpecialBee(e.getLocation()), e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                e.setCancelled(true);
            }

            if (e.getEntity() instanceof Zombie) {

                int prob = new Random().nextInt(14);
                int highestY = e.getLocation().getWorld().getHighestBlockAt(e.getLocation()).getY();

                if (prob == 5 && e.getLocation().getWorld().getTime() > 13000 && e.getLocation().getY() >= highestY) {

                    instance.getNmsAccesor().craftNewEntity(new SpecialBee(e.getLocation()), e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                }
            }

            if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING && e.getEntity() instanceof Villager && new Random().nextInt(99) <= 9 && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {

                Entity savedEntity = e.getEntity();
                Location savedLoc = e.getLocation();

                Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {

                        if (savedEntity == null) {
                            Villager villager = (Villager) instance.getNmsAccesor().craftNewEntity(new HostileVillager(e.getLocation(), (Villager) savedEntity), savedLoc, CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();
                            for (Entity near : villager.getNearbyEntities(30, 30, 30)) {

                                if (near instanceof Player) {

                                    villager.setTarget(((Player) near));
                                }
                            }
                            return;
                        }

                        if (savedEntity.isDead()) {

                            return;
                        }

                        Villager villager = (Villager) instance.getNmsAccesor().craftNewEntity(new HostileVillager(e.getLocation(), (Villager) savedEntity), savedLoc, CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                        for (Entity near : villager.getNearbyEntities(30, 30, 30)) {

                            if (near instanceof Player) {

                                villager.setTarget(((Player) near));
                            }
                        }
                        savedEntity.remove();
                    }
                }, 60 * 20 * 20);
            }

            if (e.getEntity() instanceof WitherSkeleton) {

                e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                e.getEntity().getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));

                if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {

                    for (int i = 0; i < 4; i++) {

                        WitherSkeleton w = (WitherSkeleton) instance.getNmsAccesor().craftNewEntity(new EntitySkeletonWither(EntityTypes.WITHER_SKELETON, instance.getNmsAccesor().craftWorld(e.getLocation())), e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                        w.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    }
                }
            }

            if (e.getEntity() instanceof Wither && instance.getDays() >= 5 && instance.isWitherEvent() && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_WITHER) {


                Wither w = (Wither) e.getEntity();
                w.setCustomName(instance.format("&d&lWither Especial"));
            }

            if (e.getEntity() instanceof IronGolem && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && new Random().nextInt(99) <= 9) {

                IronGolem golem = (IronGolem) instance.getNmsAccesor().craftNewEntity(new HostileIronGolem(e.getLocation()), e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();
                Villager villager = (Villager) instance.getNmsAccesor().craftNewEntity(new HostileVillager(e.getLocation(), null), e.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM).getBukkitEntity();

                Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {
                        golem.addPassenger(villager);
                    }
                }, 3L);


                e.setCancelled(true);
            }

            if (e.getEntity() instanceof Phantom) {

                Phantom p = (Phantom) e.getEntity();

                if (new Random().nextBoolean()) {

                    p.setCustomName(instance.format("&6Giga Phantom"));
                    p.setSize(9);

                } else {

                    Skeleton skeleton = (Skeleton) instance.getNmsAccesor().craftNewEntity(new EntitySkeleton(EntityTypes.SKELETON, instance.getNmsAccesor().craftWorld(e.getLocation())), e.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL).getBukkitEntity();

                    Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                        @Override
                        public void run() {

                            skeleton.teleport(p.getLocation());
                            skeleton.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 5).build());
                            p.addPassenger(skeleton);
                        }
                    }, 3L);
                }
            }
        }

        if (instance.getDays() >= 8) {

            setupDay8(instance.getDays(), e.getEntity(), e.getEntity(), e.getEntityType(), instance.getNmsAccesor());

            if (e.getEntity() instanceof Blaze && new Random().nextInt(9) == 5) {

                Blaze b = (Blaze) e.getEntity();

                b.setCustomName(instance.format("&6Blaze Knight"));

                instance.getNmsAccesor().setMaxHealth(b, b.getHealth() + 10.0D, true);

                b.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                b.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
            }
        }

        if (instance.getDays() >= 8) {

            if (instance.isCanCompleteZombie() && e.getEntity().getType() == EntityType.ZOMBIE && new Random().nextInt(29) == 1 && e.getLocation().getWorld().getBiome((int) e.getLocation().getX(), (int) e.getLocation().getY(), (int) e.getLocation().getZ()) == Biome.SWAMP) {

                Drowned zombie = (Drowned) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.DROWNED);
                zombie.setBaby(false);
                EntityEquipment eq = zombie.getEquipment();

                zombie.setCustomName(instance.format("&6Ahogado del Pantano"));

                ItemStack banner = new ItemStack(Material.ORANGE_BANNER);
                BannerMeta meta = (BannerMeta) banner.getItemMeta();

                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.MOJANG));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
                banner.setItemMeta(meta);


                eq.setHelmet(banner);
                eq.setHelmetDropChance(0);
                eq.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                eq.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                eq.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());

                eq.setItemInMainHand(new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 7).build());
                eq.setItemInMainHandDropChance(0);

                zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

                instance.getNmsAccesor().setMaxHealth(zombie, 60.0D, true);
                e.setCancelled(true);
            }
        }

        if (instance.getDays() >= 10) {

            Random rand = new Random();

            ArrayList<String> effectList = new ArrayList<>();

            effectList.add("SPEED");
            effectList.add("REGENERATION");
            effectList.add("INCREASE_DAMAGE");
            effectList.add("INVISIBILITY");
            effectList.add("JUMP");
            effectList.add("SLOW_FALLING");
            effectList.add("DAMAGE_RESISTANCE");

            if (instance.getDays() < 50) {

                effectList.add("GLOWING");
            }

            if (e.getEntity() instanceof Spider || e.getEntity() instanceof CaveSpider) {

                int randomEffectsTimes = ThreadLocalRandom.current().nextInt(1, 3 + 1);

                for (int i = 0; i < randomEffectsTimes; i++) {

                    int randomIndex = rand.nextInt(effectList.size());
                    String randomEffectName = effectList.get(randomIndex);

                    if (randomEffectName.equals("SPEED")) { // Velocidad III
                        int effectLevel = 2;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("REGENERATION")) { // Regeneración IV
                        int effectLevel = 3;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("INCREASE_DAMAGE")) { // Fuerza IV
                        int effectLevel = 3;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("INVISIBILITY")) { // Invisibilidad
                        int effectLevel = 0;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("JUMP")) { // Salto V
                        int effectLevel = 4;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("SLOW_FALLING")) { // Caida lenta
                        int effectLevel = 0;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("GLOWING")) { // Brillo
                        int effectLevel = 0;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999999, effectLevel));
                    }

                    if (randomEffectName.equals("DAMAGE_RESISTANCE")) { // Resistencia III
                        int effectLevel = 2;
                        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, effectLevel));
                    }
                }
            }
        }

        if (instance.getDays() >= 15) {

            if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;
            setupDay15(instance.getDays(), e.getEntity(), e.getEntity(), e.getEntityType(), instance.getNmsAccesor());
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {

        if (e.getEntity() instanceof Zombie && e.getTarget() instanceof Villager) {

            if (e.getTarget().getCustomName() == null) return;

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        for (Entity en : e.getPlayer().getNearbyEntities(30, 30, 30)) {

            if (en instanceof Villager) {

                Villager v = (Villager) en;

                if (v.getCustomName() == null) return;

                if (v.getTarget() == null) {

                    v.setTarget(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onEntityBurn(EntityCombustEvent e) {

        if (e.getEntity().getType() == EntityType.DROWNED && e.getEntity().getCustomName() != null) {

            e.setCancelled(true);
        }

        if (e.getEntity().getType() == EntityType.SKELETON && e.getEntity().getCustomName() != null) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onED(EntityDeathEvent e) {

        if (instance.getDays() >= 15) {

            if (e.getEntity().getType() == EntityType.IRON_GOLEM || e.getEntity().getType() == EntityType.CREEPER || e.getEntity().getType() == EntityType.GHAST
                    || e.getEntity().getType() == EntityType.EVOKER || e.getEntity().getType() == EntityType.MAGMA_CUBE || e.getEntity().getType() == EntityType.PHANTOM
                    || e.getEntity().getType() == EntityType.RAVAGER) {

                e.getDrops().clear();
            }

            if (e.getEntity().getType() == EntityType.SKELETON) {

                if (e.getEntity().getCustomName() == null) return;

                if (e.getEntity().getCustomName().contains(instance.format("&6Evoker Skeleton"))) {

                    e.getDrops().clear();
                }
            }
        }

        if (instance.isCanCompleteZombie() && e.getEntity().getCustomName() != null && instance.getDays() >= 8 && e.getEntity().getType() == EntityType.DROWNED && new Random().nextInt(99) <= 49) {

            ItemStack s = instance.crearOrbeBendito();
            e.getDrops().add(s);
        }

        if (instance.getDays() >= 8 && e.getEntity() instanceof Evoker) {

            int prob = new Random().nextInt(99);

            if (prob <= 9) {

                e.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
        }

        if (instance.getDays() >= 8 && e.getEntity() instanceof Blaze) {

            if (e.getEntity().getCustomName() == null) return;
            if (e.getEntity().getCustomName().contains(instance.format("&6Blaze Knight"))) {

                e.getDrops().clear();

                if (new Random().nextInt(99) <= 69) {
                    e.getDrops().add(new ItemBuilder(Material.BLAZE_ROD).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setDisplayName(instance.format("&d&lEnchanted Blaze Rod")).setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}"))).addItemFlag(ItemFlag.HIDE_ENCHANTS).build());
                }
            }
        }

        if (instance.isWitherEvent() && e.getEntity().getType() == EntityType.WITHER_SKELETON && instance.getDays() >= 5) {

            boolean containsWitherSkull = false;

            for (ItemStack s : e.getDrops()) {

                if (s != null) {

                    if (s.getType() == Material.WITHER_SKELETON_SKULL) {

                        containsWitherSkull = true;
                    }
                }
            }

            if (containsWitherSkull) {

                int prob = new Random().nextInt(49);
                int extra = new Random().nextInt(49);
                String texto = "";

                if (prob <= 9) {

                    e.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                    texto = "&6&lFIEBRE DE WITHER >> &eHas encontrado &b1 &ecabeza extra.";

                    if (extra == 5) {

                        texto = "&6&lFIEBRE DE WITHER >> &eHas encontrado &b2 &ecabezas extras.";
                    }
                }

                if (e.getEntity().getKiller() != null && !texto.isEmpty()) {

                    e.getEntity().getKiller().sendMessage(instance.format(texto));
                    e.getEntity().getKiller().playSound(e.getEntity().getKiller().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100.0F, 100.0F);
                }
            }
        }

        if (instance.getDays() >= 5 && e.getEntity() instanceof Wither) {

            if (e.getEntity().getCustomName() != null) {

                if (e.getEntity().getCustomName().contains(instance.format("&6Withum"))) {

                    e.getDrops().clear();
                }

                if (e.getEntity().getCustomName().contains(instance.format("&d&lWither Especial"))) {

                    e.getDrops().add(new ItemStack(Material.NETHER_STAR));
                }
            }
        }
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Bee && e.getEntity() instanceof Player && instance.getDays() >= 5) {

            if (instance.getDays() >= 15) {
                e.getDamager().getWorld().createExplosion(e.getDamager().getLocation(), 8.0F);
                return;
            }

            if (e.getDamager().getCustomName() == null) return;
            Player p = (Player) e.getEntity();

            Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                @Override
                public void run() {
                    if (p.getActivePotionEffects().size() >= 1) {
                        for (PotionEffect effect : p.getActivePotionEffects()) {

                            if (effect.getType() == PotionEffectType.POISON) {
                                p.removePotionEffect(effect.getType());
                            }
                        }
                    }

                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15*20, 1));
                }
            }, 3L);
        }
    }

    @EventHandler
    public void onDeathTrainActive(CreatureSpawnEvent e) {

        if (e.isCancelled()) return;

        if (instance.getDays() >= 10) {

            if (e.getLocation().getWorld().hasStorm()) {

                if (e.getLocation().getWorld().getName().endsWith("end") || e.getLocation().getWorld().getName().endsWith("beginning"))
                    return;

                if (isHostileMob(e.getEntity().getType())) {

                    e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                    e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                    e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                }
            }
        }
    }

    @EventHandler
    public void onWC(WeatherChangeEvent e) {
        if (instance.getDays() < 10) {
            return;
        }

        if (e.getWorld().getName().equalsIgnoreCase(instance.world.getName())) {

            if (!e.getWorld().hasStorm()) {

                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {

                        for (World w : Bukkit.getWorlds()) {

                            for (Entity ent : w.getEntities()) {

                                if (ent instanceof LivingEntity && isHostileMob(ent.getType())) {

                                    LivingEntity liv = (LivingEntity) ent;

                                    for (PotionEffect eff : liv.getActivePotionEffects()) {

                                        if (eff.getAmplifier() != 1) return;

                                        if (eff.getType() == PotionEffectType.INCREASE_DAMAGE || eff.getType() == PotionEffectType.SPEED || eff.getType() == PotionEffectType.DAMAGE_RESISTANCE) {

                                            liv.removePotionEffect(eff.getType());
                                        }
                                    }
                                }
                            }
                        }

                        System.out.println("[PermadeathCore] Se han eliminado todos los efectos de poción del Death Train");
                    }
                }, 200L);
            } else {

                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {
                        for (World w : Bukkit.getWorlds()) {

                            if (e.getWorld().getName().endsWith("end") || e.getWorld().getName().endsWith("beginning")) return;

                            for (Entity ent : w.getEntities()) {

                                if (ent instanceof LivingEntity && isHostileMob(ent.getType())) {

                                    LivingEntity liv = (LivingEntity) ent;

                                    if (isHostileMob(ent.getType())) {

                                        liv.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                                        liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                                        liv.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                                    }
                                }
                            }
                        }

                        System.out.println("[PermadeathCore] Se han otorgado los efectos de poción debido al Death Train");
                    }
                }, 200L);
            }
        }
    }

    private boolean isHostileMob(EntityType type) {
        if (type == EntityType.BLAZE ||type == EntityType.CREEPER ||type == EntityType.GHAST ||type == EntityType.MAGMA_CUBE ||type == EntityType.SILVERFISH ||type == EntityType.SKELETON ||type == EntityType.SLIME ||type == EntityType.ZOMBIE ||type == EntityType.ZOMBIE_VILLAGER ||type == EntityType.DROWNED ||type == EntityType.WITHER_SKELETON ||type == EntityType.WITCH ||type == EntityType.PILLAGER ||type == EntityType.EVOKER ||type == EntityType.VINDICATOR ||type == EntityType.RAVAGER ||type == EntityType.VEX ||type == EntityType.GUARDIAN ||type == EntityType.ELDER_GUARDIAN ||type == EntityType.SHULKER ||type == EntityType.HUSK ||type == EntityType.STRAY ||type == EntityType.PHANTOM) {
            return true;
        } else {
            return false;
        }
    }
}
