package tech.sebazcrc.infernalcore.Task;

import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Util.Maldicion;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityWither;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;

public class Ruleta extends BukkitRunnable {

    private Main instance;
    private Player p;
    private long days;

    int time = 5;

    public Ruleta(Main instance, Player p) {
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

            if (instance.getDays() >= 10) {

                SplittableRandom sr = new SplittableRandom();
                int random = sr.nextInt(6) + 1; // Esto daría un 6

                Player p = null;
                ArrayList<Player> on = new ArrayList<>();

                Bukkit.getOnlinePlayers().forEach(o -> {

                    if (o.getGameMode() == GameMode.SPECTATOR) return;

                    on.add(o);

                    o.playSound(o.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10.0F, 10.0F);
                });

                while (p == null) {

                    Player r = on.get(sr.nextInt(on.size()));
                    p = r;
                }



                if (p == null) return;

                String maldicion = "";
                Bukkit.broadcastMessage(instance.format("&eLa ruleta del destino giró en torno a: &b" + p.getName()));

                if (random == 0) {

                    maldicion = "&eLentitud II &b3 minutos";
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60*3*20, 1));
                }

                if (random == 1) {

                    maldicion = "&eDebilidad I &b1 minuto";
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60*20, 0));
                }

                if (random == 2) {

                    maldicion = "&eCeguera &b2 minutos";
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60*2*20, 0));
                }

                if (random == 3) {

                    maldicion = "&eSe bloquean temporalmente 5 slots del inventario (30 minutos).";
                    ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                    if (p.getInventory().getItemInOffHand() != null) {

                        if (p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                        }
                    }

                    if (p.getInventory().getItem(4) != null) {

                        if (p.getInventory().getItem(4).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(4));
                        }
                    }

                    if (p.getInventory().getItem(13) != null) {

                        if (p.getInventory().getItem(13).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(13));
                        }
                    }

                    if (p.getInventory().getItem(22) != null) {

                        if (p.getInventory().getItem(22).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(22));
                        }
                    }

                    if (p.getInventory().getItem(31) != null) {

                        if (p.getInventory().getItem(31).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(31));
                        }
                    }

                    instance.getMaldiciones().put(p.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 30);

                    p.getInventory().setItemInOffHand(stack);
                    p.getInventory().setItem(4, stack);
                    p.getInventory().setItem(13, stack);
                    p.getInventory().setItem(22, stack);
                    p.getInventory().setItem(31, stack);
                    p.updateInventory();
                }

                if (random == 4) {

                    maldicion = "&eSe bloquean temporalmente 5 slots del inventario (60 minutos).";
                    ItemStack stack = new ItemStack(Material.STRUCTURE_VOID);

                    if (p.getInventory().getItemInOffHand() != null) {

                        if (p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                        }
                    }

                    if (p.getInventory().getItem(4) != null) {

                        if (p.getInventory().getItem(4).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(4));
                        }
                    }

                    if (p.getInventory().getItem(13) != null) {

                        if (p.getInventory().getItem(13).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(13));
                        }
                    }

                    if (p.getInventory().getItem(22) != null) {

                        if (p.getInventory().getItem(22).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(22));
                        }
                    }

                    if (p.getInventory().getItem(31) != null) {

                        if (p.getInventory().getItem(31).getType() != Material.AIR) {

                            p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItem(31));
                        }
                    }

                    instance.getMaldiciones().put(p.getName() + ";" + Maldicion.INVENTORY_LOCK.toString(), 60 * 60);

                    p.getInventory().setItemInOffHand(stack);
                    p.getInventory().setItem(4, stack);
                    p.getInventory().setItem(13, stack);
                    p.getInventory().setItem(22, stack);
                    p.getInventory().setItem(31, stack);
                    p.updateInventory();
                }

                if (random == 5) {

                    maldicion = "&eFatiga minera III &b3 minutos";
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 60*3*20, 2));
                }

                if (random == 6) {
                    maldicion = "&eEfecto Wither &b10 segundos &ey Lentitud II &b2 minutos";
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10*20, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60*2*20, 1));
                }

                Bukkit.broadcastMessage(instance.format(maldicion));
            }

            cancel();
        }
    }
}
