package com.permadeathcore.Task.SkeletonRaids;

import com.permadeathcore.Main;
import com.permadeathcore.Util.RaidState;
import com.permadeathcore.Util.SkeletonRaid;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class UpdatePhaseTask extends BukkitRunnable {

    private SkeletonRaid raid;
    private Main instance;

    int timer = 10;

    public UpdatePhaseTask(SkeletonRaid raid, Main instance) {
        this.raid = raid;
        this.instance = instance;
    }

    @Override
    public void run() {

        if (raid.getCurrentState() == RaidState.FINISHED) {

            cancel();
            return;
        }

        if (timer > 0) {

            timer = timer - 1;

            if (timer == 9) {

                raid.getBossBar().setProgress(0.1);
            }

            if (timer == 8) {

                raid.getBossBar().setProgress(0.2);
            }

            if (timer == 7) {

                raid.getBossBar().setProgress(0.3);
            }

            if (timer == 6) {

                raid.getBossBar().setProgress(0.4);
            }

            if (timer == 5) {

                raid.getBossBar().setProgress(0.5);
            }

            if (timer == 4) {

                raid.getBossBar().setProgress(0.6);
            }

            if (timer == 3) {

                raid.getBossBar().setProgress(0.7);
            }

            if (timer == 2) {

                raid.getBossBar().setProgress(0.8);
            }

            if (timer == 1) {

                raid.getBossBar().setProgress(0.9);
            }


        }

        if (timer == 0) {

            raid.getBossBar().setProgress(1.0);

            raid.setCurrentState(RaidState.FIGHTING);

            if (raid.getPlayers().size() >= 1) {

                Player on = raid.getPlayers().get(new Random().nextInt(raid.getPlayers().size()));

                int randomX = new Random().nextInt(5);
                randomX = randomX + 10;

                int randomZ = new Random().nextInt(5);
                randomZ = randomZ + 10;

                if (new Random().nextBoolean()) {
                    randomX = randomX * -1;
                }

                if (new Random().nextBoolean()) {
                    randomZ = randomZ * -1;
                }

                Location loc = on.getLocation().add(randomX, 0, randomZ);

                int startingY = (int) on.getLocation().getY();
                int altura = startingY;

                boolean found = false;

                for (int i = startingY; i < 257; i++) {

                    if (!found) {

                        if (on.getWorld().getBlockAt(new Location(on.getWorld(), randomX, i, randomZ)).getType() == Material.AIR || on.getWorld().getBlockAt(new Location(on.getWorld(), randomX, i, randomZ)).getType().isAir()) {

                            altura = i;
                            found = true;
                        }
                    }
                }

                if (!found) {

                    altura = on.getWorld().getHighestBlockAt(new Location(on.getWorld(), randomX, 50, randomZ)).getY();
                }

                loc.setY(altura + 2);

                raid.setSpawnLocation(loc);
            }

            raid.spawnRaidLeader();
            raid.spawnRaiders();

            raid.setStarterRaiders(raid.getRaiders().size());

            if (raid.getCurrentPhase() >= 3) {

                raid.spawnRavagers();
            }

            for (Player player : raid.getPlayers()) {

                player.playSound(raid.getSpawnLocation(), Sound.EVENT_RAID_HORN, 1000.0F, 20.0F);
            }

            raid.updateProgress();
            this.cancel();
        }
    }
}
