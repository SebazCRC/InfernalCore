package com.permadeathcore.Entity;

import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class HostileIronGolem extends EntityIronGolem {

    public HostileIronGolem(Location loc) {
        super(EntityTypes.IRON_GOLEM, ((CraftWorld) loc.getWorld()).getHandle());

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

        if (getBukkitEntity() instanceof IronGolem) {

            IronGolem g = (IronGolem) getBukkitEntity();
            g.setCustomName(Main.format("&6Iron Gólem Conspirador"));
        }

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(2, new PathfinderGoalStrollVillage(this, 0.6D));
        this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, false, 4, () -> {
            return false;
        }));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 5, false, false, (entityliving) -> {
            return entityliving instanceof IMonster && !(entityliving instanceof EntityCreeper);
        }));
    }

    protected SoundEffect getSoundAmbient() {
        return null;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_IRON_GOLEM_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        tag.setString("id", "minecraft:villager_golem");
    }

    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
    }
}