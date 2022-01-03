package tech.sebazcrc.infernalcore.Manager.Data;

import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Util.Item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EndWorldManager implements Listener {

    private Main main;

    public EndWorldManager(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onWC(PlayerChangedWorldEvent e) {

        if (e.getPlayer().getWorld().getName().equalsIgnoreCase(main.endWorld.getName())) {
            createRegenZone(e.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onEndCityLoad(ChunkPopulateEvent e) {

        if (main.getDays() < 40) return;

        if (e.getChunk().getWorld().getName().equalsIgnoreCase(main.endWorld.getName())) {

            for (Entity entity : e.getChunk().getEntities()) {

                if (entity instanceof ItemFrame) {

                    ItemFrame frame = (ItemFrame) entity;

                    if (frame.getItem() != null) {

                        if (frame.getItem().getType() == Material.ELYTRA) {

                            ItemStack s = new ItemBuilder(Material.ELYTRA).setDurability(431).build();
                            frame.setItem(s);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(EntityExplodeEvent e) {

        if (main.getEndData() != null) {

            EndDataManager ma = main.getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                for (Block b : e.blockList()) {

                    if (b.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                        if (b.getLocation().distance(loc) <= 10) {

                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent e) {

        if (main.getEndData() != null) {

            EndDataManager ma = main.getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 10) {

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {

        if (main.getEndData() != null) {

            EndDataManager ma = main.getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 4) {

                        e.setCancelled(true);
                        e.getPlayer().sendMessage(main.format("&cNo puedes romper bloques cerca de la Zona de Regeneración."));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockCombust(BlockIgniteEvent e) {

        if (main.getEndData() != null) {

            EndDataManager ma = main.getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 3) {

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {

        if (main.getEndData() != null) {

            EndDataManager ma = main.getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 3) {

                        e.setCancelled(true);
                        e.getPlayer().sendMessage(main.format("&cNo puedes colocar bloques cerca de la Zona de Regeneración."));
                    }
                }
            }
        }
    }


    private void createRegenZone(Location playerZone) {

        EndDataManager ma = main.getEndData();

        if (!ma.getConfig().getBoolean("CreatedRegenZone")) {

            Location added = playerZone.add(-10, 0, 0);
            Location toGenerate = main.endWorld.getHighestBlockAt(added).getLocation();

            // * * *
            // * * *
            // * * *

            if (toGenerate.getY() == -1) {

                toGenerate.setY(playerZone.getY());
            }

            Block centerBlock = main.endWorld.getBlockAt(toGenerate);
            generateBlocks(true, toGenerate);
            generateBlocks(false, toGenerate);

            centerBlock.getRelative(BlockFace.UP).setType(Material.RED_CARPET);
            centerBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.SEA_LANTERN);
            centerBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.RED_CARPET);

            AreaEffectCloud a = (AreaEffectCloud) main.endWorld.spawnEntity(centerBlock.getRelative(BlockFace.UP).getLocation(), EntityType.AREA_EFFECT_CLOUD);
            a.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 0), false);
            a.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 0), false);
            a.setDuration(999999);
            a.setParticle(Particle.BLOCK_CRACK, Material.AIR.createBlockData());
            a.setRadius(4.0F);

            ma.getConfig().set("CreatedRegenZone", true);
            ma.getConfig().set("RegenZoneLocation", locationToString(a.getLocation()));
            ma.saveFile();
            ma.reloadFile();

            System.out.println("[INFO] Se ha creado la zona de regeneración en el END en " + centerBlock.getRelative(BlockFace.UP).getLocation().getX() + ", " + centerBlock.getRelative(BlockFace.UP).getLocation().getY() + ", " + centerBlock.getRelative(BlockFace.UP).getLocation().getZ());

            Bukkit.getServer().getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {

                    for (Entity ents : main.endWorld.getEntities()) {

                        if (ents.getType() == EntityType.ENDERMAN || ents.getType() == EntityType.CREEPER) {

                            Block b = ents.getLocation().getBlock().getRelative(BlockFace.DOWN);

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

                                if (now.getType() == Material.END_STONE) {

                                    now.setType(Material.END_STONE_BRICKS);
                                }
                            }
                        }
                    }

                    System.out.println("[INFO] Se han colocado ladrillos de END STONE por el Spawn del Mundo.");
                }
            }, 100L);
        }
    }

    private void generateBlocks(boolean b, Location toGenerate) {

        if (b) {

            ArrayList<Block> blocks = new ArrayList<>();

            Block centerBlock = main.endWorld.getBlockAt(toGenerate);
            blocks.add(centerBlock);

            blocks.add(main.endWorld.getBlockAt(toGenerate).getRelative(BlockFace.EAST));
            blocks.add(main.endWorld.getBlockAt(toGenerate).getRelative(BlockFace.WEST));

            blocks.add(centerBlock.getRelative(BlockFace.NORTH));
            blocks.add(centerBlock.getRelative(BlockFace.NORTH_WEST));
            blocks.add(centerBlock.getRelative(BlockFace.NORTH_EAST));

            blocks.add(centerBlock.getRelative(BlockFace.SOUTH));
            blocks.add(centerBlock.getRelative(BlockFace.SOUTH_WEST));
            blocks.add(centerBlock.getRelative(BlockFace.SOUTH_EAST));

            for (Block all : blocks) {

                all.setType(Material.RED_WOOL);
            }
        } else {

            ArrayList<Block> blocks = new ArrayList<>();
            Block centerBlockOfWool = main.endWorld.getBlockAt(toGenerate);

            Block corner1 = centerBlockOfWool.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);

            blocks.add(corner1);
            blocks.add(corner1.getRelative(BlockFace.WEST));
            blocks.add(corner1.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));
            blocks.add(corner1.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));

            // CORNER 2
            blocks.add(corner1.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));

            blocks.add(corner1.getRelative(BlockFace.SOUTH));
            blocks.add(corner1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH));
            blocks.add(corner1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH));

            // CORNER 3
            Block southC = corner1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH);
            blocks.add(southC);

            blocks.add(southC.getRelative(BlockFace.WEST));
            blocks.add(southC.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));
            blocks.add(southC.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));

            // CORNER 4
            Block finalC = southC.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST);
            blocks.add(finalC);

            blocks.add(finalC.getRelative(BlockFace.NORTH));
            blocks.add(finalC.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
            blocks.add(finalC.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));

            for (Block all : blocks) {

                all.setType(Material.RED_GLAZED_TERRACOTTA);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        if (e.isCancelled()) return;

        if (main.endWorld.getName().equalsIgnoreCase(e.getBlock().getLocation().getWorld().getName())) {

            if (e.getBlock().getType() == Material.OBSIDIAN) {


                EndDataManager ma = main.getEndData();

                ArrayList<String> list = (ArrayList<String>) ma.getConfig().getStringList("PlacedObsidian");
                list.add(locationToString(e.getBlock().getLocation()));

                ma.getConfig().set("PlacedObsidian", list);

                ma.saveFile();
                ma.reloadFile();
            }
        }
    }

    private Location buildLocation(String s) {

        // X;Y;Z;WORLD
        String[] split = s.split(";");

        Double x = Double.valueOf(split[0]);
        Double y = Double.valueOf(split[1]);
        Double z = Double.valueOf(split[2]);
        World w = Bukkit.getWorld(split[3]);

        return new Location(w, x, y, z);
    }

    private String locationToString(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getName();
    }
}
 