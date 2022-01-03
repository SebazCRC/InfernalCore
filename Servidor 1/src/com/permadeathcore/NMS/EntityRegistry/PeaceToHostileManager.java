package com.permadeathcore.NMS.EntityRegistry;

import com.permadeathcore.Main;
import com.permadeathcore.NMS.ReflectionUtils;
import com.permadeathcore.NMS.VersionManager;
import io.netty.util.internal.StringUtil;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class PeaceToHostileManager implements Listener {

    private Main instance;

    public PeaceToHostileManager(Main instance) {
        this.instance = instance;

        initialize();
    }

    public void initialize() {

        if (instance.getDays() >= 15) {

            for (World w : Bukkit.getWorlds()) {

                for (Entity entity : w.getEntities()) {

                    if (entity instanceof LivingEntity) {

                        spawnEntity((LivingEntity) entity, entity.getLocation(), null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        if (instance.getDays() >= 15) {

            spawnEntity(e.getEntity(), e.getLocation(), e);
        }
    }

    private void spawnEntity(LivingEntity entity, org.bukkit.Location where, CreatureSpawnEvent event) {

        String name;

        if (entity.getType() == EntityType.VILLAGER) {

            String s = entity.getType().toString().toLowerCase();

            if (s.contains("_")) {

                String[] split = s.split("_");

                String wordOne = split[0].toLowerCase();
                String wordTwo = split[0].toLowerCase();

                name = StringUtils.capitalize(wordOne) + StringUtils.capitalize(wordTwo);
            } else {

                name = StringUtils.capitalize(s);
            }

            Villager v = (Villager) entity;

            boolean classExists = true;
            try {

                Class c = Class.forName("com.permadeathcore.NMS.EntityRegistry.Entities.v" + VersionManager.getVersion() + ".Hostile" + name);
                c.getConstructor(org.bukkit.Location.class, Main.getInstance().getNmsHandler().getNMSClass("EntityTypes"), Villager.class).newInstance(where, Main.getInstance().getNmsHandler().convertBukkitToNMS(entity.getType()), v);

            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                classExists = false;
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            } catch (InvocationTargetException ex) {
            }


            if (event != null) {

                if (classExists) {

                    event.setCancelled(true);
                }
            } else {

                if (classExists) {

                    entity.remove();
                }
            }
        } else if (entity.getType() == EntityType.WANDERING_TRADER) {

            String s = entity.getType().toString().toLowerCase();

            if (s.contains("_")) {

                String[] split = s.split("_");

                String wordOne = split[0].toLowerCase();
                String wordTwo = split[1].toLowerCase();

                name = StringUtils.capitalize(wordOne) + StringUtils.capitalize(wordTwo);
            } else {

                name = StringUtils.capitalize(s);
            }

            WanderingTrader v = (WanderingTrader) entity;

            boolean classExists = true;
            try {

                Class c = Class.forName("com.permadeathcore.NMS.EntityRegistry.Entities.v" + VersionManager.getVersion() + ".Hostile" + name);
                c.getConstructor(org.bukkit.Location.class, Main.getInstance().getNmsHandler().getNMSClass("EntityTypes"), WanderingTrader.class).newInstance(where, Main.getInstance().getNmsHandler().convertBukkitToNMS(entity.getType()), v);

            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                classExists = false;
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            } catch (InvocationTargetException ex) {            }

            if (event != null) {

                if (classExists) {

                    event.setCancelled(true);
                }
            } else {

                if (classExists) {

                    entity.remove();
                }
            }

        } else {

            String s = entity.getType().toString().toLowerCase();

            if (s.contains("_")) {

                String[] split = s.split("_");

                String wordOne = split[0].toLowerCase();
                String wordTwo = split[1].toLowerCase();

                name = StringUtils.capitalize(wordOne) + StringUtils.capitalize(wordTwo);
            } else {

                name = StringUtils.capitalize(s);
            }

            boolean classExists = true;
            try {

                Class c = Class.forName("com.permadeathcore.NMS.EntityRegistry.Entities.v" + VersionManager.getVersion() + ".Hostile" + name);

                if (name.toLowerCase().contains("bat") || name.toLowerCase().contains("cod") || name.toLowerCase().contains("salmon") || name.toLowerCase().contains("squid") || name.toLowerCase().contains("pufferfish") || name.toLowerCase().contains("tropicalfish")) {

                    if (new Random().nextInt(499) <= 10) {

                        c.getConstructor(org.bukkit.Location.class, Main.getInstance().getNmsHandler().getNMSClass("EntityTypes")).newInstance(where, Main.getInstance().getNmsHandler().convertBukkitToNMS(entity.getType()));
                    }

                    return;
                }

                if (name.toLowerCase().contains("bee")) {
                    return;
                }

                c.getConstructor(org.bukkit.Location.class, Main.getInstance().getNmsHandler().getNMSClass("EntityTypes")).newInstance(where, Main.getInstance().getNmsHandler().convertBukkitToNMS(entity.getType()));

            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                classExists = false;
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            } catch (InvocationTargetException ex) {

            }
            if (event != null) {

                if (classExists) {

                    event.setCancelled(true);
                }

            } else {

                if (classExists) {

                    entity.remove();
                }
            }
        }
    }
}
