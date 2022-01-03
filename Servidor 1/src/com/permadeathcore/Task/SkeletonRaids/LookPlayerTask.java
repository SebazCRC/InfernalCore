package com.permadeathcore.Task.SkeletonRaids;

import com.permadeathcore.Main;
import com.permadeathcore.Util.RaidState;
import com.permadeathcore.Util.SkeletonRaid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LookPlayerTask extends BukkitRunnable {

    private Player p;
    private Skeleton evoker;

    int ticksRunning = 0;
    int neededTicks = 20 * 3;

    private Main instance;
    private SkeletonRaid raid;

    public LookPlayerTask(Skeleton entity, Player p, SkeletonRaid raid) {
        this.evoker = entity;
        this.p = p;
        this.instance = Main.getInstance();
        this.raid = raid;
    }

    @Override
    public void run() {

        if (ticksRunning == neededTicks) {

            Vector bt = p.getLocation().toVector().subtract(evoker.getLocation().toVector());

            Location eLoc = evoker.getLocation();

            //bt.multiply(-1);
            eLoc.setDirection(bt);

            evoker.teleport(eLoc);

            if (eLoc.getY() - p.getLocation().getY() > 5) {

                return;
            }

            Location start = evoker.getLocation();
            Vector direction = start.getDirection();

            int distance = (int) p.getLocation().distance(evoker.getLocation()) + 3;

            for (int i = 1; i <= distance; i++) {

                //start.clone().add(bt.clone().multiply(i))
                EvokerFangs fangs = (EvokerFangs) p.getWorld().spawnEntity(start.clone().add(direction.clone().multiply(i)), EntityType.EVOKER_FANGS);
                Location newL = evoker.getLocation().clone().setDirection(direction.clone());
                evoker.teleport(newL);
                fangs.setOwner(evoker);
            }

            raid.getNextEvokerAttack().replace(evoker, 7);
        }

        if (ticksRunning == neededTicks + 20) {

            this.cancel();
            return;
        }

        ticksRunning++;


        Vector bt = p.getLocation().toVector().subtract(evoker.getLocation().toVector());
        Location eLoc = evoker.getLocation();
        eLoc.setDirection(bt);
        evoker.teleport(eLoc);
    }
}
