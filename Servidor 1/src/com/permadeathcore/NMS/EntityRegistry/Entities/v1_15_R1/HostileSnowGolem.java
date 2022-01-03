package com.permadeathcore.NMS.EntityRegistry.Entities.v1_15_R1;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class HostileSnowGolem extends EntitySnowman {

    private String name;

    public HostileSnowGolem(Location loc, EntityTypes type) {

        super(type, ((CraftWorld) loc.getWorld()).getHandle());

        this.name = "snow_golem";

        spawnEntity(loc);
    }

    public void spawnEntity(Location loc) {

        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    protected void initAttributes() {

        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(8.0D);
    }

    protected void initPathfinder() {

        super.initPathfinder();

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    @Override
    public void b(NBTTagCompound compound) {
        super.b(compound);
        compound.setString("id", "hostile_" + name);
    }
}