package com.permadeathcore.End.Entities;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;

public class CustomCreeper extends EntityCreeper {

    public CustomCreeper(EntityTypes<? extends EntityCreeper> type, World world, boolean quantum, boolean enderQuantum) {
        super(type, world);

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

        this.setNoAI(false);

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(70.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(50.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30);

        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSwell(this));
        this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(4, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(5, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomLookaround(this));

        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));

        Entity bukkitEntity = this.getBukkitEntity();

        if (bukkitEntity instanceof LivingEntity) {
            Creeper creeper = (Creeper) bukkitEntity;
            creeper.setPowered(true);

            if (!quantum && !enderQuantum) {

                creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2, false, false));
                creeper.setCustomName("§6Ender Creeper");
                this.setSilent(true);
            }

            if (quantum) {
                creeper.setCustomName("§6Quantum Creeper");
                creeper.setExplosionRadius(8);
                this.setSilent(false);
            }

            if (enderQuantum) {
                creeper.setCustomName("§6Ender Quantum Creeper");
                creeper.setExplosionRadius(8);
                this.setSilent(true);
            }
        }
    }
}