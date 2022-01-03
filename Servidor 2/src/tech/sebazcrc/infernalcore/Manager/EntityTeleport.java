package tech.sebazcrc.infernalcore.Manager;

import tech.sebazcrc.infernalcore.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.SplittableRandom;

public class EntityTeleport {

    private Entity c;
    private EntityDamageEvent e;
    private World world;
    private Main main;
    private double locX;
    private double locY;
    private double locZ;

    private SplittableRandom random;

    public EntityTeleport(Entity c, EntityDamageEvent e) {
        this.c = c;
        this.e = e;
        this.world = c.getWorld();
        this.main = Main.getInstance();
        this.locX = c.getLocation().getX();
        this.locY = c.getLocation().getY();
        this.locZ = c.getLocation().getZ();
        this.random = new SplittableRandom();

        teleport();
    }

    public boolean teleport() {

        //if (main.getFactory().hasData(e.getEntity(), "ender_quantum_creeper") && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.FIRE) {
            for (int i = 0; i < 64; ++i) {
                if (eq()) {
                    return true;
                }
            }
        //}

        //if (main.getFactory().hasData(e.getEntity(), "ender_ghast") && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.FIRE && random.nextInt(100) + 1 <= 20) {
            for (int i = 0; i < 64; ++i) {
                if (eq()) {
                    return true;
                }
            }
        //}

        //if (main.getFactory().hasData(e.getEntity(), "tp_ghast") && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.FIRE && random.nextInt(100) + 1 <= 80) {
            for (int i = 0; i < 64; ++i) {
                if (eq()) {
                    return true;
                }
            }
        //}

        return false;
    }

    private boolean eq() {
        double d0 = locX + (this.random.nextDouble() - 0.5D) * 64.0D;
        double d1 = locY + (double) (this.random.nextInt(64) - 32);
        double d2 = locZ + (this.random.nextDouble() - 0.5D) * 64.0D;
        return this.o(d0, d1, d2);
    }

    private boolean o(double x, double y, double z) {

        BlockActuallyPosition act = new BlockActuallyPosition(this.world.getBlockAt((int)x, (int)y, (int)z));
        Block b = act.getBlock();

        while (b.getY() > 0 && !b.getType().isSolid()) {
            act = act.goDeeper();
            b = act.getBlock();
        }

        if (b.getY() == 0) {
            return false;
        }
        
        return this.c.teleport(new Location(this.world, x, b.getY() + 1, z));
    }

    private class BlockActuallyPosition {

        private Block start;

        public BlockActuallyPosition(Block start) {
            this.start = start;
        }

        public BlockActuallyPosition goDeeper() {
            this.start = this.start.getRelative(BlockFace.DOWN);
            return this;
        }

        public Block getBlock() {
            return start;
        }
    }
}

