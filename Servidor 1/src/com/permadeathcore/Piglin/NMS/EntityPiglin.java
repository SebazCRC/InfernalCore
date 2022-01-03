package com.permadeathcore.Piglin.NMS;

import com.permadeathcore.Main;
import com.permadeathcore.Util.ItemBuilder;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

public class EntityPiglin extends EntityPigZombie implements ICrossbow, IRangedEntity {

    private boolean isBrute;
    private boolean goldInHand = false;
    private boolean isCrossbow;
    private boolean setupItems = false;

    private static final DataWatcherObject<Boolean> dw;

    private ArrayList<Player> hitBy = new ArrayList<>();

    public boolean canAttackPlayers = false;

    static {
        dw = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    }

    public EntityPiglin(EntityTypes<? extends EntityPigZombie> entitytypes, World world) {
        this(world);
    }

    public EntityPiglin(World world) {
        super(EntityTypes.ZOMBIE_PIGMAN, world);

        this.setCanPickupLoot(true);
        this.isBrute = false;
        this.isCrossbow = (double)this.random.nextFloat() < 0.5D;

        this.a(PathType.DANGER_FIRE, 16.0F);
        this.a(PathType.DAMAGE_FIRE, -1.0F);

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


        if (isCrossbow) {
            this.goalSelector.a(0, new PathfinderGoalCrossbowAttack(this, 1.0D, 8.0F));
        } else {
            this.goalSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        }

        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(3, new PathfinderGoalRandomStrollLand(this, 1.0D));

        this.targetSelector.a(0, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[]{EntityPiglin.class}));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntitySkeletonWither.class, true));

        this.check();
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(32.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3499999940395355D);
    }

    @Override
    protected void initDatawatcher() {

        super.initDatawatcher();
        this.datawatcher.register(dw, false);
    }

    @Override
    protected void mobTick() {

        if (isBrute) {

            super.mobTick();
            return;
        }


        if (getBukkitEntity() == null) return;

        PigZombie piglin = (PigZombie) getBukkitEntity();
        boolean hasGold = false;

        if (piglin.getTarget() != null) {

            if (piglin.getTarget() instanceof Player) {

                Player p = (Player) piglin.getTarget();

                if (this.isBaby()) {

                    piglin.setTarget(null);
                    return;
                }

                boolean doPlayerHasGoldenArmor = false;

                for (org.bukkit.inventory.ItemStack s : p.getInventory().getArmorContents()) {

                    if (s != null) {

                        if (s.getType() == org.bukkit.Material.GOLDEN_HELMET || s.getType() == org.bukkit.Material.GOLDEN_CHESTPLATE
                                || s.getType() == org.bukkit.Material.GOLDEN_LEGGINGS || s.getType() == org.bukkit.Material.GOLDEN_BOOTS) {

                            doPlayerHasGoldenArmor = true;
                        }
                    }
                }

                if (doPlayerHasGoldenArmor) {

                    if (!canAttackPlayers && !getHitBy().contains(p)) {

                        piglin.setTarget(null);
                    }
                }
            }
        }

        if (piglin.getEquipment().getItemInMainHand() != null) {

            if (piglin.getEquipment().getItemInMainHand().getType() == org.bukkit.Material.GOLD_INGOT) {

                hasGold = true;
            }
        }

        this.goldInHand = hasGold;

        if (hasGold) {

            org.bukkit.Location under = piglin.getLocation().getBlock().getRelative(BlockFace.NORTH).getLocation();

            Vector bt = under.toVector().subtract(piglin.getLocation().toVector());

            Location eLoc = piglin.getLocation();

            eLoc.setDirection(bt);

            piglin.teleport(eLoc);

            return;
        }

        org.bukkit.entity.Item pick = null;

        for (org.bukkit.entity.Item item : piglin.getWorld().getEntitiesByClass(org.bukkit.entity.Item.class)) {

            if (item.getLocation().distanceSquared(piglin.getLocation()) <= 25.0D) {

                if (pick == null) {

                    pick = item;
                }
            }
        }

        if (pick != null) {

            this.setGoalTarget(null);
            piglin.setTarget(null);

            Main.getInstance().getNmsAccesor().moveTo(piglin, pick.getLocation(), 1.50D);

            if (pick.getLocation().distance(piglin.getLocation()) <= 1.0D) {

                EntityPickupItemEvent event = new EntityPickupItemEvent(piglin, pick, 0);
                this.world.getServer().getPluginManager().callEvent(event);

                pick.remove();
            }
        }

        super.mobTick();
    }



    @Override
    public void setSlot(EnumItemSlot slot, ItemStack s) {
        super.setSlot(slot, s);

        if (!this.world.isClientSide) {

            this.check();
        }
    }

    public void check() {
        if (this.world != null && !this.world.isClientSide) {
        }
    }

    private ItemStack eU() {
        return isCrossbow ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    @Nullable
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn != EnumMobSpawn.STRUCTURE) {
            if (generatoraccess.getRandom().nextFloat() < 0.2F) {
                this.a(true);
            } else if (!this.em()) {
                this.setSlot(EnumItemSlot.MAINHAND, this.eU());
            }
        }

        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (!this.isBaby()) {
            this.d(EnumItemSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.d(EnumItemSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.d(EnumItemSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.d(EnumItemSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }
    }

    private void d(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (this.world.random.nextFloat() < 0.1F) {
            this.setSlot(enumitemslot, itemstack);
        }
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEFINED;
    }

    public void setCanAttackPlayers(boolean canAttackPlayers) {
        this.canAttackPlayers = canAttackPlayers;
    }

    @Override
    public void a(boolean flag) {
        this.datawatcher.set(dw, flag);
    }

    @Override
    public void a(EntityLiving entityLiving, ItemStack itemStack, IProjectile iProjectile, float v) {


        Entity entity = (Entity)iProjectile;
        double d0 = entityLiving.locX() - this.locX();
        double d1 = entityLiving.locZ() - this.locZ();
        double d2 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
        double d3 = entityLiving.e(0.3333333333333333D) - entity.locY() + d2 * 0.20000000298023224D;
        Vector3fa vector3fa = this.a(new Vec3D(d0, d3, d1), f);
        iProjectile.shoot((double)vector3fa.a(), (double)vector3fa.b(), (double)vector3fa.c(), 1.6F, (float)(14 - this.world.getDifficulty().a() * 4));
        this.a(SoundEffects.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private Vector3fa a(Vec3D vec3D, int f) {
        Vec3D vec3d1 = vec3D.d();
        Vec3D vec3d2 = vec3d1.c(new Vec3D(0.0D, 1.0D, 0.0D));
        if (vec3d2.g() <= 1.0E-7D) {
            vec3d2 = vec3d1.c(this.i(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3fa(vec3d2), 90.0F, true);
        Vector3fa vector3fa = new Vector3fa(vec3d1);
        vector3fa.a(quaternion);
        Quaternion quaternion1 = new Quaternion(vector3fa, f, true);
        Vector3fa vector3fa1 = new Vector3fa(vec3d1);
        vector3fa1.a(quaternion1);
        return vector3fa1;
    }

    // RANGED ENTITY
    @Override
    public void a(EntityLiving entityLiving, float v) {
        EnumHand enumhand = ProjectileHelper.a(this, Items.CROSSBOW);
        ItemStack itemstack = this.b((EnumHand)enumhand);
        if (this.a((Item)Items.CROSSBOW)) {
            ItemCrossbow.a(this.world, this, enumhand, itemstack, 1.6F, (float)(14 - this.world.getDifficulty().a() * 4));
        }

        this.ticksFarFromPlayer = 0;
    }

    public ArrayList<Player> getHitBy() {
        return hitBy;
    }

    @Override
    public void a(NBTTagCompound compound) {
        super.a(compound);

        if (this.isBaby()) {
            compound.setBoolean("IsBaby", true);
        }

        compound.setBoolean("Crossbow", isCrossbow);
        compound.setBoolean("Brute", isBrute);
        compound.setBoolean("GoldInHand", goldInHand);
        compound.setBoolean("SetupItems", setupItems);

        this.check();
    }

    @Override
    public void b(NBTTagCompound compound) {
        super.b(compound);

        this.isCrossbow = compound.getBoolean("Crossbow");
        this.isBrute = compound.getBoolean("Brute");
        this.goldInHand = compound.getBoolean("GoldInHand");
        this.setupItems = compound.getBoolean("SetupItems");
    }
}
