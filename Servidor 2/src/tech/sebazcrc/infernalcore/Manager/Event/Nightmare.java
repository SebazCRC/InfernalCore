package tech.sebazcrc.infernalcore.Manager.Event;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import tech.sebazcrc.infernalcore.Main;

public class Nightmare extends EventBase {

    public Nightmare(Main instance) {
        super(instance, 60*60*5, Bukkit.createBossBar("&4&lNightmare MODE &b&n", BarColor.PURPLE, BarStyle.SEGMENTED_6));
    }

    @Override
    public String getDefaultTitle() {
        return this.getInstance().format("&4&lNightmare MODE &b&n");
    }
}
