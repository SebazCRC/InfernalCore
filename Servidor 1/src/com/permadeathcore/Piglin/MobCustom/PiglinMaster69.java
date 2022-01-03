package com.permadeathcore.Piglin.MobCustom;

import net.minecraft.server.v1_15_R1.*;

public class PiglinMaster69 extends EntityChicken {

    public PiglinMaster69(EntityTypes<? extends EntityChicken> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {

        super.initPathfinder();

        this.goalSelector.a(new PathfinderGoalMeleeAttack(this, 1.0f, false));
        this.targetSelector.a(new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }
}
