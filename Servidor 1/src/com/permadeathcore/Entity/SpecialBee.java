 package com.permadeathcore.Entity;

import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Pig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

public class SpecialBee extends EntityBee {

    public SpecialBee(Location loc) {
        super(EntityTypes.BEE, ((CraftWorld) loc.getWorld()).getHandle());
        this.getAttributeInstance(GenericAttributes.FLYING_SPEED).setValue(0.6000301938418579D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30001219192092896D);

        Bee bee = (Bee) getBukkitEntity();

        bee.setAnger(10000);

        if (Main.getInstance().getDays() >= 5 && Main.getInstance().getDays() < 15) {

            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(25.0D);
            bee.setCustomName(Main.getInstance().format("&6Avispa Asesina"));
            bee.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        } else {

            if (Main.getInstance().getDays() >= 5 && Main.getInstance().getDays() < 15) {

                bee.setCustomName(Main.getInstance().format("&6Abeja Explosiva"));
                this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
                this.getAttributeInstance(GenericAttributes.FLYING_SPEED).setValue(0.9000301938418579D);
            }
        }

        this.goalSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(30.0D);
    }

    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_BEE_LOOP_AGGRESSIVE;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_BEE_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_BEE_DEATH;
    }

    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        tag.setString("id", "minecraft:bee");
        tag.setInt("Anger", 10000);
    }

    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
        this.setAnger(10000);
    }
}