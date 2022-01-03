package com.permadeathcore.NMS.EntityRegistry.Entities.v1_15_R1;

import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class HostileParrot extends EntityParrot {

    private String name;

    public HostileParrot(Location loc, EntityTypes type) {

        super(type, ((CraftWorld) loc.getWorld()).getHandle());

        this.name = Main.getInstance().getNmsHandler().convertEntityType(type).toString().toLowerCase();

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

        this.goalSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    @Override
    public void b(NBTTagCompound compound) {
        super.b(compound);
        compound.setString("id", "hostile_" + name);
    }
}