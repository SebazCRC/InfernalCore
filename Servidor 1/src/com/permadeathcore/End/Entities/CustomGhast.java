package com.permadeathcore.End.Entities;

import net.minecraft.server.v1_15_R1.EntityGhast;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class CustomGhast extends EntityGhast {

    public CustomGhast(EntityTypes<? extends EntityGhast> type, World world) {
        super(type, world);
        this.setNoAI(false);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(200.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(70.0D);

        Entity bukkitEntity = this.getBukkitEntity();

        if (bukkitEntity instanceof LivingEntity) {
            LivingEntity e2 = (LivingEntity) bukkitEntity;
            e2.setHealth(200.0D);
            e2.setCustomName("§6Ender Ghast");
            e2.setCustomNameVisible(false);
        }
    }
}