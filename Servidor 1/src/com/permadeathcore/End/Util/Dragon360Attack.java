package com.permadeathcore.End.Util;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import com.destroystokyo.paper.event.entity.EnderDragonFlameEvent;
import com.destroystokyo.paper.event.entity.WitchReadyPotionEvent;
import com.destroystokyo.paper.event.entity.WitchThrowPotionEvent;
import com.permadeathcore.Main;
import net.minecraft.server.v1_15_R1.Potions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

public class Dragon360Attack implements Listener {

    private Main main;

    public Dragon360Attack(Main main) {
        this.main = main;
    }

    /**
    @EventHandler
    public void onEnderDragonFlame(EnderDragonFlameEvent e) {

        e.getAreaEffectCloud().remove();

        EnderDragon dragon = e.getEntity();

        int probToMake360 = new Random().nextInt(9);

        if (probToMake360 == 5) {

            main.getTask().start360attack();
        }
    }
    */

    @EventHandler
    public void onProjectileHit(EnderDragonFireballHitEvent e) {

        AreaEffectCloud a = e.getAreaEffectCloud();

        if (main.getTask() != null) {

            if (main.getTask().getCurrentDemonPhase() == DemonPhase.NORMAL) {

                Location highest = main.endWorld.getHighestBlockAt(a.getLocation()).getLocation();
                Block b = main.endWorld.getHighestBlockAt(a.getLocation());

                if (highest.getY() != -1.0) {

                    int structure = new Random().nextInt(4);

                    ArrayList<Block> toChange = new ArrayList<>();

                    if (structure == 0) {

                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.SOUTH_EAST));
                        toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                        toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
                    } else if (structure == 1) {

                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                        toChange.add(b);
                    } else if (structure == 2) {

                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                        toChange.add(b);
                    } else if (structure == 3) {

                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                        toChange.add(b);
                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.EAST));
                    } else if (structure == 4) {

                        toChange.add(b.getRelative(BlockFace.SOUTH));
                        toChange.add(b.getRelative(BlockFace.NORTH_WEST));
                        toChange.add(b);
                        toChange.add(b.getRelative(BlockFace.NORTH));
                        toChange.add(b.getRelative(BlockFace.WEST));
                    }

                    for (Block all : toChange) {

                        Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();

                        Block now = main.endWorld.getBlockAt(used);

                        if (now.getType() != Material.AIR) {

                            now.setType(Material.BEDROCK);
                        }
                    }
                }
            } else {

                int prob = new Random().nextInt(2);

                if (prob == 0) {

                    a.setParticle(Particle.SMOKE_NORMAL);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20, 1), false);

                } else {

                    Location highest = main.endWorld.getHighestBlockAt(a.getLocation()).getLocation();
                    Block b = main.endWorld.getHighestBlockAt(a.getLocation());

                    if (highest.getY() != -1.0) {

                        int structure = new Random().nextInt(4);

                        ArrayList<Block> toChange = new ArrayList<>();

                        if (structure == 0) {

                            toChange.add(b.getRelative(BlockFace.NORTH));
                            toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                            toChange.add(b.getRelative(BlockFace.SOUTH));
                            toChange.add(b.getRelative(BlockFace.SOUTH_EAST));
                            toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                            toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                            toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                            toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
                        } else if (structure == 1) {

                            toChange.add(b.getRelative(BlockFace.NORTH));
                            toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                            toChange.add(b);
                        } else if (structure == 2) {

                            toChange.add(b.getRelative(BlockFace.SOUTH));
                            toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                            toChange.add(b);
                        } else if (structure == 3) {

                            toChange.add(b.getRelative(BlockFace.NORTH));
                            toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                            toChange.add(b);
                            toChange.add(b.getRelative(BlockFace.SOUTH));
                            toChange.add(b.getRelative(BlockFace.EAST));
                        } else if (structure == 4) {

                            toChange.add(b.getRelative(BlockFace.SOUTH));
                            toChange.add(b.getRelative(BlockFace.NORTH_WEST));
                            toChange.add(b);
                            toChange.add(b.getRelative(BlockFace.NORTH));
                            toChange.add(b.getRelative(BlockFace.WEST));
                        }

                        for (Block all : toChange) {

                            Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();

                            Block now = main.endWorld.getBlockAt(used);

                            if (now.getType() != Material.AIR) {

                                now.setType(Material.BEDROCK);
                            }
                        }
                    }
                }
            }
        }
    }
}
