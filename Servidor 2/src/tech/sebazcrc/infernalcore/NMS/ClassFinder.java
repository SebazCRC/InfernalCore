package tech.sebazcrc.infernalcore.NMS;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public interface ClassFinder {

    Object findNmsHandler();
    Object findNmsAccesor();
}