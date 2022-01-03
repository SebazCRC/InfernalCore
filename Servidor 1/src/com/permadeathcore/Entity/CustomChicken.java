package com.permadeathcore.Entity;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class CustomChicken extends EntityChicken {

    public CustomChicken(Location loc) {
        super(EntityTypes.CHICKEN, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

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

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_CHICKEN_AMBIENT;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_CHICKEN_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_CHICKEN_DEATH;
    }

    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        tag.setString("id", "entity_" + UUID.randomUUID().toString());
    }

    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
    }
}