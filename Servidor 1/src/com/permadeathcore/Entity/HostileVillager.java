package com.permadeathcore.Entity;

import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class HostileVillager extends EntityVillager {

    public HostileVillager(Location loc, Villager importFrom) {
        super(EntityTypes.VILLAGER, ((CraftWorld) loc.getWorld()).getHandle());

        PathfinderGoalSelector goalSelector = this.goalSelector;
        PathfinderGoalSelector targetSelector = this.targetSelector;

        try {
            Field dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());

            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            cField.set(goalSelector, new EnumMap<>(PathfinderGoal.Type.class));

            Field fField;
            fField = PathfinderGoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            fField.set(goalSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Field dField;
            dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            dField.set(targetSelector, new LinkedHashSet<>());

            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            Field fField;
            fField = PathfinderGoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(10.0D);

        if (getBukkitEntity() instanceof LivingEntity) {

            LivingEntity liv = (LivingEntity) getBukkitEntity();

            liv.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            liv.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
            liv.setCustomName(Main.format("&6Aldeano Conspirador"));
        }

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));

        if (importFrom != null) {

            boolean hasProffesion = importFrom.getProfession() != Villager.Profession.NONE;

            if (hasProffesion) {

                this.goalSelector.a(5, new PathfinderGoalTradeWithPlayer(this));
                this.goalSelector.a(6, new PathfinderGoalLookAtTradingPlayer(this));
            }

            ((Villager)this.getBukkitEntity()).setVillagerType(importFrom.getVillagerType());

            EntityVillager v = (EntityVillager) ((CraftEntity)importFrom).getHandle();
            this.setVillagerData(v.getVillagerData());
        }
    }

    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_VILLAGER_AMBIENT;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_VILLAGER_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_VILLAGER_DEATH;
    }

    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        tag.setString("id", "minecraft:villager");
    }

    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
    }
}