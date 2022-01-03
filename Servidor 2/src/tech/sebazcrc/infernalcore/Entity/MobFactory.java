package tech.sebazcrc.infernalcore.Entity;

import net.minecraft.server.v1_15_R1.EntityPhantom;
import net.minecraft.server.v1_15_R1.EntityPigZombie;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.infernalcore.Main;

import java.util.SplittableRandom;

public class MobFactory {

    public static PigZombie craftPigin(Location where, boolean addID) {

        PigZombie z = where.getWorld().spawn(where, PigZombie.class);

        if (addID) {
            addID(z, "entity_piglin");
        }

        z.setNormalPiglin();

        return z;
    }

    public static PigZombie craftPiglinBrute(Location where) {
        PigZombie z = craftPigin(where, false);
        addID(z, "entity_piglin_brute");
        z.setPiglinBrute();

        return z;
    }

    public static PigZombie craftZombifiedPiglin(Location where) {

        EntityPigZombie nms = new EntityPigZombie(EntityTypes.ZOMBIE_PIGMAN, ((CraftWorld)where.getWorld()).getHandle());
        nms.setPosition(where.getX(), where.getY(), where.getZ());
        nms.setZombifiedPiglin(true);
        ((CraftWorld) where.getWorld()).getHandle().addEntity(nms, CreatureSpawnEvent.SpawnReason.NATURAL);

        PigZombie z = (PigZombie) nms.getBukkitEntity();
        addID(z, "entity_zombified_piglin");
        z.setPiglinBrute();

        return z;
    }

    public static Creeper craftGhoulCreeper(Creeper c, Location where) {

        if (c == null) {
            c = where.getWorld().spawn(where, Creeper.class);
        }

        c.setPowered(true);
        c.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
        c.setMaxHealth(c.getMaxHealth() * 2);
        c.setMaxFuseTicks(40);
        c.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        c.setHealth(c.getMaxHealth());

        addID(c, "ghoul_creeper");
        c.setCustomName(Main.format("&6Ghoul Creeper"));

        return c;
    }

    public static Enderman craftExploderEnderman(Enderman e, Location where) {

        if (e == null) {
            e = where.getWorld().spawn(where, Enderman.class);
        }


        addID(e, "exploding_enderman");
        e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        e.setCustomName(Main.format("&6Exploding Enderman"));

        try {
            e.setCarriedMaterial(new ItemStack(Material.TNT).getData());
        } catch (Exception c) {}

        return e;
    }

    public static Ghast craftAlmaEnDesgracia(Ghast g, Location where) {

        if (g == null) {
            g = where.getWorld().spawn(where, Ghast.class);
        }

        LivingEntity liv = g;

        liv.setCustomName(Main.format("&6Alma en Desgracia"));
        liv.setMaxHealth(liv.getMaxHealth() * 2);
        liv.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
        liv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));

        addID(liv, "almas");

        return g;
    }

    public static Phantom spawnPhantomClass(Phantom p, Location l) {
        if (p == null) {
            p = l.getWorld().spawn(l, Phantom.class);
        }

        SplittableRandom random = new SplittableRandom();
        int i = random.nextInt(3) + 1;

        if (i == 1) {
            p.setCustomName(Main.format("&6Phantom Kamikaze"));
            addID(p, "kamikaze");
            p.setSize(7);
            p.setMaxHealth(p.getMaxHealth() * 2);
            p.setHealth(p.getMaxHealth());
            p.addPassenger(l.getWorld().spawnEntity(l, EntityType.MINECART_TNT));
        }
        if (i == 2) {
            p.setCustomName(Main.format("&6Phantom Táctico"));
            addID(p, "tactical");
            p.addPassenger(p.getWorld().spawn(l, Skeleton.class));
        }
        if (i == 3) {
            p.setCustomName(Main.format("&6Phantom Deborador"));
            addID(p, "deborador");
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
            p.setSize(9);
            p.setMaxHealth(p.getMaxHealth() * 2);
            p.setHealth(p.getMaxHealth());
        }

        return p;
    }

    public static void addID(Entity entity, String id) {
        entity.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), id), PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean hasID(Entity entity, String id) {
        return entity.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), id), PersistentDataType.BYTE);
    }
}
