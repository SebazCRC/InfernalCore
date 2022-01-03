package com.permadeathcore.End.Entities;

import net.minecraft.server.v1_15_R1.EntityShulker;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.entity.Entity;

public class ShulkerTnt extends EntityShulker {

    public ShulkerTnt(EntityTypes<? extends EntityShulker> type, World world) {
        super(type, world);

        PathfinderGoalSelector goalSelector = this.goalSelector;
        PathfinderGoalSelector targetSelector = this.targetSelector;

        this.setNoAI(false);

        Entity bukkitEntity = this.getBukkitEntity();
    }
}