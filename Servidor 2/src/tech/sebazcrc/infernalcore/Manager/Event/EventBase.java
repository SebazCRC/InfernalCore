package tech.sebazcrc.infernalcore.Manager.Event;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import tech.sebazcrc.infernalcore.Main;

public abstract class EventBase {

    private Main instance;
    private boolean running;

    private int timeLeft;
    private BossBar bossBar;
    private String title;

    public EventBase(Main instance, int time, BossBar bossBar) {
        this.instance = instance;
        this.timeLeft = time;
        this.bossBar = bossBar;
        this.title = instance.format(bossBar.getTitle());
    }

    public void addPlayer(Player p) {
        bossBar.addPlayer(p);
    }

    public void clearPlayers() {

        for (Player p : bossBar.getPlayers()) {

            bossBar.removePlayer(p);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        this.bossBar.setTitle(title);
    }

    public String getDefaultTitle() {
        return null;
    }

    public Main getInstance() {
        return instance;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public String getTitle() {
        return title;
    }

    public boolean containsPlayer(Player p) {

        return this.bossBar.getPlayers().contains(p);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void reduceTime() {
        this.timeLeft = timeLeft - 1;
    }

    public void removePlayer(Player player) {

        if (bossBar.getPlayers().contains(player)) return;

        bossBar.addPlayer(player);
    }
}
