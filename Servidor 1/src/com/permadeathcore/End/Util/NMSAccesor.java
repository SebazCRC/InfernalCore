package com.permadeathcore.End.Util;

import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;

public class NMSAccesor {

    private Main plugin;

    public NMSAccesor(Main plugin) {
        this.plugin = plugin;
    }

    public void addGoal(EntityInsentient insentient, PathfinderGoal goal) {

        insentient.goalSelector.a(goal);
    }

    public void addTargetGoal(EntityInsentient insentient, PathfinderGoal goal) {

        insentient.targetSelector.a(goal);
    }

    public Double getMaxHealthOf(LivingEntity entity) {

        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) entity).getHandle();

        if (nms instanceof EntityInsentient) {

            EntityInsentient in = (EntityInsentient) nms;
            return in.getAttributeInstance(GenericAttributes.MAX_HEALTH).getBaseValue();
        }

        return null;
    }

    public Location faceLocation(Entity entity, Location to) {
        if (entity.getWorld() != to.getWorld()) {
            return null;
        }
        Location fromLocation = entity.getLocation();

        double xDiff = to.getX() - fromLocation.getX();
        double yDiff = to.getY() - fromLocation.getY();
        double zDiff = to.getZ() - fromLocation.getZ();

        double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);

        double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
        double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0D;
        if (zDiff < 0.0D) {
            yaw += Math.abs(180.0D - yaw) * 2.0D;
        }
        Location loc = entity.getLocation();
        loc.setYaw((float) (yaw - 90.0F));
        loc.setPitch((float) (pitch - 90.0F));
        return loc;
    }

    public void setMaxHealth(Entity entity, Double health, boolean setHealth) {

        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) entity).getHandle();

        if (nms instanceof EntityInsentient) {

            EntityInsentient in = (EntityInsentient) nms;
            in.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(health);

            if (setHealth && entity instanceof LivingEntity) {

                ((LivingEntity) entity).setHealth(health);
            }
        }
    }

    public Double getMaxHealth(Entity entity) {

        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) entity).getHandle();

        if (nms instanceof EntityInsentient) {

            return getAtribute((EntityInsentient) nms, GenericAttributes.MAX_HEALTH).getValue();
        }

        return 20.0D;
    }

    public AttributeInstance getAtribute(EntityInsentient insentient, IAttribute at) {

        return insentient.getAttributeInstance(at);
    }

    public AttributeInstance getAtribute(Entity en, IAttribute at) {

        net.minecraft.server.v1_15_R1.Entity entity = ((CraftEntity) en).getHandle();

        if (entity instanceof EntityInsentient) {

            EntityInsentient insentient = (EntityInsentient) entity;
            return insentient.getAttributeInstance(at);
        }

        return null;
    }

    public AttributeMapBase getAtributeMap(EntityInsentient insentient) {

        return insentient.getAttributeMap();
    }


    public void moveTo(EntityInsentient insentient, Location location, Double speed) {

        // LA DISTANCIA NO PUEDE SER MAYOR A 20, SI NO ME EQUIVOCO

        PathEntity path = insentient.getNavigation().a(location.getX(), location.getY(), location.getZ(), 2);

        if (path != null) {

            insentient.getNavigation().a(path, speed);
        }
    }

    public void moveTo(Entity entity, Location location, Double speed) {

        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) entity).getHandle();

        if (nms instanceof EntityInsentient) {

            EntityInsentient insentient = (EntityInsentient) nms;

            PathEntity path = insentient.getNavigation().a(location.getX(), location.getY(), location.getZ(), 2);

            if (path != null) {

                insentient.getNavigation().a(path, speed);
            }
        }
    }

    public void removeGoals(EntityInsentient insentient) {

        PathfinderGoalSelector goalSelector = insentient.goalSelector;
        PathfinderGoalSelector targetSelector = insentient.targetSelector;

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
    }

    public net.minecraft.server.v1_15_R1.Entity craftNewEntity(net.minecraft.server.v1_15_R1.Entity entity, Location loc, CreatureSpawnEvent.SpawnReason reason) {

        entity.setPosition(loc.getX(), loc.getY(), loc.getZ());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity, reason);

        return entity;
    }

    public World craftWorld(Location loc) {
        return ((CraftWorld)loc.getWorld()).getHandle();
    }

    public World craftWorld(Entity loc) {
        return ((CraftWorld)loc.getWorld()).getHandle();
    }

    public World craftWorld(org.bukkit.World loc) {
        return ((CraftWorld)loc).getHandle();
    }
}
