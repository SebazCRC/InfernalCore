package tech.sebazcrc.infernalcore.Manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class GhastExplosion {

    private final boolean a;
    private final Explosion.Effect b;
    private final Random c = new Random();
    private final net.minecraft.server.v1_15_R1.World world;
    private final double posX;
    private final double posY;
    private final double posZ;
    public final net.minecraft.server.v1_15_R1.Entity source;
    private final float size;
    private final List<BlockPosition> blocks = Lists.newArrayList();
    public boolean wasCanceled = false;
    private boolean boolea = true;
    private final Explosion nmsExplosion;

    public GhastExplosion(World w, org.bukkit.entity.Entity entity, org.bukkit.Location l, Float power) {
        this(((CraftWorld)w).getHandle(), ((CraftEntity)entity).getHandle()
        , (int)l.getX(), (int)l.getY(), (int)l.getZ(), power, false, Explosion.Effect.DESTROY);
    }

    public GhastExplosion(net.minecraft.server.v1_15_R1.World world, @Nullable net.minecraft.server.v1_15_R1.Entity entity, double d0, double d1, double d2, float f, boolean flag, Explosion.Effect explosion_effect) {
        this.world = world;
        this.source = entity;
        this.size = (float)Math.max((double)f, 0.0D);
        this.posX = d0;
        this.posY = d1;
        this.posZ = d2;
        this.a = flag;
        this.b = explosion_effect;

        this.nmsExplosion = new Explosion(world, entity, d0, d1, d2, f, flag, explosion_effect);

        a();
        nmsExplosion.a(true);
    }

    public void a() {
        if (this.size >= 0.1F) {
            Set<BlockPosition> set = Sets.newHashSet();

            int i;
            int j;
            for (int k = 0; k < 16; ++k) {
                for (i = 0; i < 16; ++i) {
                    for (j = 0; j < 16; ++j) {
                        if (k == 0 || k == 15 || i == 0 || i == 15 || j == 0 || j == 15) {
                            double d0 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                            double d1 = (double) ((float) i / 15.0F * 2.0F - 1.0F);
                            double d2 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
                            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                            d0 /= d3;
                            d1 /= d3;
                            d2 /= d3;
                            float f = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                            double d4 = this.posX;
                            double d5 = this.posY;
                            double d6 = this.posZ;

                            for (float var21 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                                BlockPosition blockposition = new BlockPosition(d4, d5, d6);
                                IBlockData iblockdata = this.world.getType(blockposition);
                                if (iblockdata.isDestroyable()) {
                                    Fluid fluid = iblockdata.getFluid();
                                    if (!iblockdata.isAir() || !fluid.isEmpty()) {
                                        float f2 = Math.max(iblockdata.getBlock().getDurability(), fluid.k());
                                        f -= (f2 + 0.3F) * 0.3F;
                                    }

                                    if (f > 0.0F && (this.source == null || this.boolea) && blockposition.getY() < 256 && blockposition.getY() >= 0) {
                                        set.add(blockposition);
                                    }

                                    d4 += d0 * 0.30000001192092896D;
                                    d5 += d1 * 0.30000001192092896D;
                                    d6 += d2 * 0.30000001192092896D;
                                }
                            }
                        }
                    }
                }
            }

            this.blocks.addAll(set);

            try {

                final Field f = Explosion.class.getDeclaredField("blocks");
                f.setAccessible(true);

                f.set(this.nmsExplosion, this.blocks);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
