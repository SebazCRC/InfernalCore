package tech.sebazcrc.infernalcore.NMS.EntityRegistry;

import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.NMS.VersionManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

public class PeaceToHostileManager implements Listener {

    private Main instance;
    private ArrayList<Entity> peaceMobs = new ArrayList<>();

    public PeaceToHostileManager(Main instance) {
        this.instance = instance;

        initialize();
    }

    public void initialize() {

        if (instance.getDays() >= 20) {

            for (World w : Bukkit.getWorlds()) {

                for (Entity entity : w.getEntities()) {

                    EntityType type = entity.getType();

                    if (!isHostileMob(type) && entity instanceof LivingEntity) {

                        if (type == EntityType.ENDERMAN || type == EntityType.WITHER || type == EntityType.ENDER_DRAGON) {
                            return;
                        }

                        if (type == EntityType.DOLPHIN || type == EntityType.FOX || type == EntityType.WOLF || type == EntityType.CAT || type == EntityType.OCELOT || type == EntityType.PANDA
                                || type == EntityType.POLAR_BEAR || type == EntityType.SNOWMAN) {

                            instance.getNmsAccesor().injectHostilePathfinders(entity);
                            addMob(entity);
                            return;
                        }

                        instance.getNmsAccesor().injectHostilePathfinders(entity);
                        instance.getNmsAccesor().registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 8.0D, entity);

                        addMob(entity);

                    }
                }
            }
        }
    }

    private void addMob(Entity entity) {
    }

    public void disable() {
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.isCancelled()) return;

        if (instance.getDays() >= 20) {

            //spawnEntity(e.getEntity(), e.getLocation(), e);

            LivingEntity entity = e.getEntity();

            if (entity instanceof LivingEntity) {

                if (entity instanceof Player) return;

                if (!isHostileMob(e.getEntityType())) {

                    EntityType type = e.getEntityType();

                    if (type == EntityType.ENDERMAN || type == EntityType.WITHER || type == EntityType.ENDER_DRAGON) {
                        return;
                    }

                    if (type == EntityType.DOLPHIN || type == EntityType.FOX || type == EntityType.WOLF || type == EntityType.CAT || type == EntityType.OCELOT || type == EntityType.PANDA
                            || type == EntityType.POLAR_BEAR|| type == EntityType.SNOWMAN) {

                        instance.getNmsAccesor().injectHostilePathfinders(entity);
                        addMob(entity);
                        return;
                    }

                    instance.getNmsAccesor().injectHostilePathfinders(entity);
                    instance.getNmsAccesor().registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 8.0D, entity);
                    addMob(entity);
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {

        if (instance.getDays() < 20) return;
        if (e.isNewChunk()) return;

        if (e.getChunk().getEntities().length < 1) return;
        for (Entity entity : e.getChunk().getEntities()) {

            boolean isNull = false;

            if (entity == null) {

                isNull = true;
                return;
            }

            if (!entity.isValid() || entity.isDead()) {

                isNull = true;
            }

            if (entity instanceof LivingEntity && !isNull) {

                if (entity instanceof Player) return;

                EntityType type = entity.getType();

                if (!isHostileMob(type)) {

                    if (type == EntityType.ENDERMAN || type == EntityType.WITHER || type == EntityType.ENDER_DRAGON) {
                        return;
                    }

                    if (type == EntityType.DOLPHIN || type == EntityType.FOX || type == EntityType.WOLF || type == EntityType.CAT || type == EntityType.OCELOT || type == EntityType.PANDA
                            || type == EntityType.POLAR_BEAR|| type == EntityType.SNOWMAN) {

                        instance.getNmsAccesor().injectHostilePathfinders(entity);
                        addMob(entity);
                        return;
                    }

                    instance.getNmsAccesor().injectHostilePathfinders(entity);
                    instance.getNmsAccesor().registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 8.0D, entity);
                    addMob(entity);
                }
            }
        }
    }

    /**
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {

        if (instance.getDays() < 20) return;

        if (e.getChunk().getEntities().length < 1) return;
        for (Entity entity : e.getChunk().getEntities()) {

            boolean isNull = false;

            if (entity == null) {

                isNull = true;
                return;
            }

            if (!entity.isValid() || entity.isDead()) {

                isNull = true;
            }

            if (entity instanceof LivingEntity && !isNull) {

                if (entity instanceof Player) return;

                EntityType type = entity.getType();

                if (!isHostileMob(type)) {

                    if (type == EntityType.ENDERMAN || type == EntityType.WITHER || type == EntityType.ENDER_DRAGON) {
                        return;
                    }

                    if (type == EntityType.DOLPHIN || type == EntityType.FOX || type == EntityType.WOLF || type == EntityType.CAT || type == EntityType.OCELOT || type == EntityType.PANDA
                            || type == EntityType.POLAR_BEAR|| type == EntityType.SNOWMAN) {

                        instance.getNmsAccesor().unregisterHostilePathfinders(entity);
                        return;
                    }

                    instance.getNmsAccesor().unregisterHostilePathfinders(entity);
                    instance.getNmsAccesor().unregisterAttributes(entity);
                }
            }
        }
    }
    */

    private void spawnEntity(LivingEntity entity, org.bukkit.Location where, CreatureSpawnEvent event) {

        String name;
        boolean isBeeHive = false;
        boolean isNull = false;

        if (entity == null) {

            isNull = true;
        }

        if (!entity.isValid() || entity.isDead()) {

            isNull = true;
        }

        if (event != null) {

            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BEEHIVE) {

                isBeeHive = true;
            }
        }

        if (isBeeHive || isNull) return;

        if (entity.getType() == EntityType.VILLAGER) {

            String s = entity.getType().toString().toLowerCase();

            if (s.contains("_")) {

                String[] split = s.split("_");

                String wordOne = split[0].toLowerCase();
                String wordTwo = split[1].toLowerCase();

                name = StringUtils.capitalize(wordOne) + StringUtils.capitalize(wordTwo);
            } else {

                name = StringUtils.capitalize(s);
            }

            Villager v = (Villager) entity;

            boolean classExists = true;
            try {

                Class c = Class.forName("tech.sebazcrc.infernalcore.NMS.EntityRegistry.Entities.v" + VersionManager.getVersion() + ".Hostile" + name);
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

                Class c = Class.forName("tech.sebazcrc.infernalcore.NMS.EntityRegistry.Entities.v" + VersionManager.getVersion() + ".Hostile" + name);
                c.getConstructor(org.bukkit.Location.class, Main.getInstance().getNmsHandler().getNMSClass("EntityTypes"), WanderingTrader.class).newInstance(where, Main.getInstance().getNmsHandler().convertBukkitToNMS(entity.getType()), v);

            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                classExists = false;
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            } catch (InvocationTargetException ex) {}

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

                if (name.toLowerCase().contains("bee") && VersionManager.getVersion().equalsIgnoreCase("1_14_R1")) {
                    return;
                }

                if (name.toLowerCase().contains("cod") && instance.getDays() >= 50) {
                    return;
                }

                Class c = Class.forName("tech.sebazcrc.infernalcore.NMS.EntityRegistry.Entities.v" + VersionManager.getVersion() + ".Hostile" + name);

                if (name.toLowerCase().contains("bat") || name.toLowerCase().contains("cod") || name.toLowerCase().contains("salmon") || name.toLowerCase().contains("squid") || name.toLowerCase().contains("pufferfish") || name.toLowerCase().contains("tropicalfish")) {

                    if (new Random().nextInt(499) <= 10) {

                        c.getConstructor(org.bukkit.Location.class, Main.getInstance().getNmsHandler().getNMSClass("EntityTypes")).newInstance(where, Main.getInstance().getNmsHandler().convertBukkitToNMS(entity.getType()));
                    }

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

    public boolean isHostileMob(EntityType type) {
        if (type == EntityType.ENDER_DRAGON || type == EntityType.WITHER || type == EntityType.BLAZE ||type == EntityType.CREEPER ||type == EntityType.GHAST ||type == EntityType.MAGMA_CUBE ||type == EntityType.SILVERFISH ||type == EntityType.SKELETON ||type == EntityType.SLIME ||type == EntityType.ZOMBIE ||type == EntityType.ZOMBIE_VILLAGER ||type == EntityType.DROWNED ||type == EntityType.WITHER_SKELETON ||type == EntityType.WITCH ||type == EntityType.PILLAGER ||type == EntityType.EVOKER ||type == EntityType.VINDICATOR ||type == EntityType.RAVAGER ||type == EntityType.VEX ||type == EntityType.GUARDIAN ||type == EntityType.ELDER_GUARDIAN ||type == EntityType.SHULKER ||type == EntityType.HUSK ||type == EntityType.STRAY ||type == EntityType.PHANTOM) {
            return true;
        } else {
            return false;
        }
    }
}
