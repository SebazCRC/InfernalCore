package com.permadeathcore.Task;

import com.permadeathcore.Main;
import com.permadeathcore.Util.Maldicion;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityWither;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RuletaMortal extends BukkitRunnable {

    private Main instance;
    private Player p;
    private long days;

    int time = 5;

    public RuletaMortal(Main instance, Player p) {
        this.instance = instance;
        this.p = p;
        this.days = instance.getDays();
        this.time = 5;
    }

    @Override
    public void run() {

        if (time > 0) {

            for (Player on : Bukkit.getOnlinePlayers()) {

                String color = "";

                if (time == 5) {

                    color = "&e&l";
                }

                if (time == 4) {

                    color = "&c&l";
                }

                if (time == 3) {

                    color = "&b&l";
                }

                if (time == 2) {

                    color = "&6&l";
                }

                if (time == 1) {

                    color = "&2&l";
                }

                on.sendMessage(instance.format(color + "La ruleta del destino tomará su desición en: &7&n" + time));
                on.playSound(on.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            }

            time = time - 1;
        }

        if (time == 0) {

            if (instance.getDays() <= 4) {

                ArrayList<Player> playerList = new ArrayList<>();

                for (Player on : Bukkit.getOnlinePlayers()) {
                    if (!playerList.contains(on) && on.getGameMode() != GameMode.SPECTATOR) {
                        playerList.add(on);
                    }

                    on.playSound(on.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10.0F, 10.0F);
                }

                Player player = playerList.get(new Random().nextInt(playerList.size()));
                Bukkit.broadcastMessage(instance.format("&eLa ruleta del destino giró en torno a: &b" + player.getName()));

                int random = new Random().nextInt(4);
                String maldicion = "";

                if (random == 0) {

                    maldicion = "&eLentitud I (10 minutos) y Ceguera (3 minutos)";

                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 60 * 20, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 60 * 20, 0));
                }

                if (random == 1) {

                    maldicion = "&eDebilidad II (10 minutos) y Hambre I (3 minutos)";
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 60 * 20, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3 * 60 * 20, 0));
                }

                if (random == 2) {

                    maldicion = "&eDebilidad IV (5 minutos) y Ceguera (3 minutos)";
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 60 * 20, 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 60 * 20, 0));
                }

                if (random == 3) {

                    maldicion = "&eSe bloquean temporalmente 5 slots del inventario (30 minutos).";
                    ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                    if (player.getInventory().getItemInOffHand() != null) {

                        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItemInOffHand());
                        }
                    }

                    if (player.getInventory().getItem(4) != null) {

                        if (player.getInventory().getItem(4).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(4));
                        }
                    }

                    if (player.getInventory().getItem(13) != null) {

                        if (player.getInventory().getItem(13).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(13));
                        }
                    }

                    if (player.getInventory().getItem(22) != null) {

                        if (player.getInventory().getItem(22).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(22));
                        }
                    }

                    if (player.getInventory().getItem(31) != null) {

                        if (player.getInventory().getItem(31).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(31));
                        }
                    }

                    instance.getMaldiciones().put(player.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 30);

                    player.getInventory().setItemInOffHand(stack);
                    player.getInventory().setItem(4, stack);
                    player.getInventory().setItem(13, stack);
                    player.getInventory().setItem(22, stack);
                    player.getInventory().setItem(31, stack);
                    player.updateInventory();
                }

                if (random == 4) {

                    maldicion = "&eSe bloquean temporalmente 5 slots del inventario (60 minutos).";
                    ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                    if (player.getInventory().getItemInOffHand() != null) {

                        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItemInOffHand());
                        }
                    }

                    if (player.getInventory().getItem(4) != null) {

                        if (player.getInventory().getItem(4).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(4));
                        }
                    }

                    if (player.getInventory().getItem(13) != null) {

                        if (player.getInventory().getItem(13).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(13));
                        }
                    }

                    if (player.getInventory().getItem(22) != null) {

                        if (player.getInventory().getItem(22).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(22));
                        }
                    }

                    if (player.getInventory().getItem(31) != null) {

                        if (player.getInventory().getItem(31).getType() != Material.AIR) {

                            player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(31));
                        }
                    }

                    instance.getMaldiciones().put(player.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 60);

                    player.getInventory().setItemInOffHand(stack);
                    player.getInventory().setItem(4, stack);
                    player.getInventory().setItem(13, stack);
                    player.getInventory().setItem(22, stack);
                    player.getInventory().setItem(31, stack);
                    player.updateInventory();
                }

                Bukkit.broadcastMessage(instance.format(maldicion));
            }

            if (instance.getDays() >= 15) {

                ArrayList<Player> playerList = new ArrayList<>();

                for (Player on : Bukkit.getOnlinePlayers()) {
                    if (!playerList.contains(on) && on.getGameMode() != GameMode.SPECTATOR) {
                        playerList.add(on);
                    }

                    on.playSound(on.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10.0F, 10.0F);
                }

                ArrayList<Player> hechizados = new ArrayList<>();

                Random pr = new Random();

                Player randomPlayer = playerList.get(pr.nextInt(playerList.size()));
                Player randomPlayer2 = playerList.get(pr.nextInt(playerList.size()));

                while (randomPlayer2 == randomPlayer && playerList.size() > 1) {
                    randomPlayer2 = playerList.get(pr.nextInt(playerList.size()));
                }

                hechizados.add(randomPlayer);
                hechizados.add(randomPlayer2);

                Bukkit.broadcastMessage(instance.format("&eLa ruleta del destino giró en torno a: &b" + randomPlayer.getName() + " &ey &b" + randomPlayer2.getName()));

                for (Player player : hechizados) {

                    int random = new Random().nextInt(7);
                    String maldicion = "";

                    if (random == 0) {

                        maldicion = "&eLentitud I (10 minutos) y Ceguera (3 minutos)";

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 60 * 20, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 60 * 20, 0));
                    }

                    if (random == 1) {

                        maldicion = "&eDebilidad II (10 minutos) y Hambre I (5 minutos)";
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 60 * 20, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 5 * 60 * 20, 0));
                    }

                    if (random == 2) {

                        maldicion = "&eDebilidad II (5 minutos) y Ceguera (10 minutos)";
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 60 * 20, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 60 * 20, 0));
                    }

                    if (random == 3) {

                        maldicion = "&eSe bloquean temporalmente 5 slots del inventario (60 minutos).";
                        ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                        if (player.getInventory().getItemInOffHand() != null) {

                            if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItemInOffHand());
                            }
                        }

                        if (player.getInventory().getItem(4) != null) {

                            if (player.getInventory().getItem(4).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(4));
                            }
                        }

                        if (player.getInventory().getItem(13) != null) {

                            if (player.getInventory().getItem(13).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(13));
                            }
                        }

                        if (player.getInventory().getItem(22) != null) {

                            if (player.getInventory().getItem(22).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(22));
                            }
                        }

                        if (player.getInventory().getItem(31) != null) {

                            if (player.getInventory().getItem(31).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(31));
                            }
                        }

                        instance.getMaldiciones().put(player.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 60);

                        player.getInventory().setItemInOffHand(stack);
                        player.getInventory().setItem(4, stack);
                        player.getInventory().setItem(13, stack);
                        player.getInventory().setItem(22, stack);
                        player.getInventory().setItem(31, stack);
                        player.updateInventory();
                    }

                    if (random == 4) {

                        maldicion = "&eSe bloquean temporalmente 5 slots del inventario (120 minutos).";
                        ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                        if (player.getInventory().getItemInOffHand() != null) {

                            if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItemInOffHand());
                            }
                        }

                        if (player.getInventory().getItem(4) != null) {

                            if (player.getInventory().getItem(4).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(4));
                            }
                        }

                        if (player.getInventory().getItem(13) != null) {

                            if (player.getInventory().getItem(13).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(13));
                            }
                        }

                        if (player.getInventory().getItem(22) != null) {

                            if (player.getInventory().getItem(22).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(22));
                            }
                        }

                        if (player.getInventory().getItem(31) != null) {

                            if (player.getInventory().getItem(31).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(31));
                            }
                        }

                        instance.getMaldiciones().put(player.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 120);

                        player.getInventory().setItemInOffHand(stack);
                        player.getInventory().setItem(4, stack);
                        player.getInventory().setItem(13, stack);
                        player.getInventory().setItem(22, stack);
                        player.getInventory().setItem(31, stack);
                        player.updateInventory();
                    }

                    if (random == 5) {

                        maldicion = "&eMaldición de Withum, (será atacado por 1 Wither, además no dropea Nether Star).";

                        Wither wither = (Wither) instance.getNmsAccesor().craftNewEntity(new EntityWither(EntityTypes.WITHER, instance.getNmsAccesor().craftWorld(player.getWorld())), player.getLocation().add(0, 10, 0), CreatureSpawnEvent.SpawnReason.BUILD_WITHER).getBukkitEntity();

                        wither.setCustomName(instance.format("&6Withum"));
                    }

                    if (random == 6 || random == 7) {

                        maldicion = "&eBad Omen VIII (3 horas)";
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 60 * 60 * 3 * 20, 7));
                    }

                    Bukkit.broadcastMessage(instance.format("&61. Maldición para &b" + player.getName() + "&7: " + maldicion));
                }
                for (Player player : hechizados) {

                    int random = new Random().nextInt(7);
                    String maldicion = "";

                    if (random == 0) {

                        maldicion = "&eLentitud I (10 minutos) y Ceguera (3 minutos)";

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 60 * 20, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 60 * 20, 0));
                    }

                    if (random == 1) {

                        maldicion = "&eDebilidad II (10 minutos) y Hambre I (5 minutos)";
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 60 * 20, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 5 * 60 * 20, 0));
                    }

                    if (random == 2) {

                        maldicion = "&eDebilidad II (5 minutos) y Ceguera (10 minutos)";
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 60 * 20, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 60 * 20, 0));
                    }

                    if (random == 3) {

                        maldicion = "&eSe bloquean temporalmente 5 slots del inventario (60 minutos).";
                        ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                        if (player.getInventory().getItemInOffHand() != null) {

                            if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItemInOffHand());
                            }
                        }

                        if (player.getInventory().getItem(4) != null) {

                            if (player.getInventory().getItem(4).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(4));
                            }
                        }

                        if (player.getInventory().getItem(13) != null) {

                            if (player.getInventory().getItem(13).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(13));
                            }
                        }

                        if (player.getInventory().getItem(22) != null) {

                            if (player.getInventory().getItem(22).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(22));
                            }
                        }

                        if (player.getInventory().getItem(31) != null) {

                            if (player.getInventory().getItem(31).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(31));
                            }
                        }

                        instance.getMaldiciones().put(player.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 60);

                        player.getInventory().setItemInOffHand(stack);
                        player.getInventory().setItem(4, stack);
                        player.getInventory().setItem(13, stack);
                        player.getInventory().setItem(22, stack);
                        player.getInventory().setItem(31, stack);
                        player.updateInventory();
                    }

                    if (random == 4) {

                        maldicion = "&eSe bloquean temporalmente 5 slots del inventario (120 minutos).";
                        ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                        if (player.getInventory().getItemInOffHand() != null) {

                            if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItemInOffHand());
                            }
                        }

                        if (player.getInventory().getItem(4) != null) {

                            if (player.getInventory().getItem(4).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(4));
                            }
                        }

                        if (player.getInventory().getItem(13) != null) {

                            if (player.getInventory().getItem(13).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(13));
                            }
                        }

                        if (player.getInventory().getItem(22) != null) {

                            if (player.getInventory().getItem(22).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(22));
                            }
                        }

                        if (player.getInventory().getItem(31) != null) {

                            if (player.getInventory().getItem(31).getType() != Material.AIR) {

                                player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), player.getInventory().getItem(31));
                            }
                        }

                        instance.getMaldiciones().put(player.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 120);

                        player.getInventory().setItemInOffHand(stack);
                        player.getInventory().setItem(4, stack);
                        player.getInventory().setItem(13, stack);
                        player.getInventory().setItem(22, stack);
                        player.getInventory().setItem(31, stack);
                        player.updateInventory();
                    }

                    if (random == 5) {

                        maldicion = "&eMaldición de Withum, (será atacado por 1 Wither, además no dropea Nether Star).";

                        Wither wither = (Wither) instance.getNmsAccesor().craftNewEntity(new EntityWither(EntityTypes.WITHER, instance.getNmsAccesor().craftWorld(player.getWorld())), player.getLocation().add(0, 10, 0), CreatureSpawnEvent.SpawnReason.BUILD_WITHER).getBukkitEntity();

                        wither.setCustomName(instance.format("&6Withum"));
                    }

                    if (random == 6 || random == 7) {

                        maldicion = "&eBad Omen VIII (3 horas)";
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 60 * 60 * 3 * 20, 7));
                    }

                    Bukkit.broadcastMessage(instance.format("&62. Maldición para &b" + player.getName() + "&7: " + maldicion));
                }
            }

            cancel();
        }
    }
}
