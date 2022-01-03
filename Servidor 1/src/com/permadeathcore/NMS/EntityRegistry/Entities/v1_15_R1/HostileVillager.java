package com.permadeathcore.NMS.EntityRegistry.Entities.v1_15_R1;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class HostileVillager extends EntityVillager {

    private Villager importFrom;
    private String name;

    public HostileVillager(Location loc, EntityTypes type, Villager importFrom) {

        super(type, ((CraftWorld) loc.getWorld()).getHandle());

        this.name = "villager";
        this.importFrom = importFrom;

        spawnEntity(loc);
    }

    public void spawnEntity(Location loc) {

        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        if (importFrom != null) {

            Villager v = (Villager) getBukkitEntity();

            v.setVillagerType(importFrom.getVillagerType());
            v.setProfession(importFrom.getProfession());
            v.setVillagerExperience(importFrom.getVillagerExperience());
            v.setRecipes(importFrom.getRecipes());
            v.setVillagerLevel(importFrom.getVillagerLevel());
        }

        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    protected void initAttributes() {

        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(8.0D);
    }

    protected void initPathfinder() {

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(5, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(5, new PathfinderGoalLookAtTradingPlayer(this));

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    @Override
    public void b(NBTTagCompound compound) {
        super.b(compound);
        compound.setString("id", "hostile_" + name);
    }
}