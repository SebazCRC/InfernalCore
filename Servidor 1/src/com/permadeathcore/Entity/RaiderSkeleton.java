package com.permadeathcore.Entity;

import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class RaiderSkeleton extends EntitySkeleton {

    public RaiderSkeleton(Location loc) {
        super(EntityTypes.SKELETON, ((CraftWorld) loc.getWorld()).getHandle());
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

        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(32.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3499999940395355D);

        ((LivingEntity)getBukkitEntity()).setHealth(32.0D);

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));

        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true));
    }

    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_SKELETON_AMBIENT;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_SKELETON_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_SKELETON_DEATH;
    }

    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        tag.setString("id", "minecraft:skeleton");
    }

    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
    }
}