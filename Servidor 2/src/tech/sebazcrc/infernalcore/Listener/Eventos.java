package tech.sebazcrc.infernalcore.Listener;

import static tech.sebazcrc.infernalcore.Main.*;
import static org.bukkit.Bukkit.*;

import java.time.LocalDate;
import java.util.*;

import org.bukkit.entity.Phantom;
import tech.sebazcrc.infernalcore.Manager.Data.PlayerDataManager;
import tech.sebazcrc.infernalcore.Task.Ruleta;
import tech.sebazcrc.infernalcore.Util.Item.CustomItems;
import tech.sebazcrc.infernalcore.Util.Messages.MessageUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tech.sebazcrc.infernalcore.Main;


public class Eventos implements Listener {
    private final Main plugin;
    private LocalDate fechaActual;
    private LocalDate fechaInicio;
    World world;
    String tag;

    ArrayList<Player> sleeping = new ArrayList<>();
    ArrayList<Player> globalSleeping = new ArrayList<>();

    long stormTicks;
    long stormHours;

    boolean deathTrain = false;

    public Eventos(Main instance) {
        this.plugin = instance;
        this.world = instance.world;

        this.tag = Main.instance.tag;
        this.stormTicks = plugin.getDays() * 3600L;
        this.stormHours = this.stormTicks / 60L / 60L;

        this.fechaInicio = instance.fechaInicio;
        this.fechaActual = LocalDate.now();

        loadTicks();
    }

    public void loadTicks() {

        this.fechaInicio = instance.fechaInicio;
        this.fechaActual = LocalDate.now();

        if (plugin.getDays() <= 25) {

            this.stormTicks = plugin.getDays() * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDays() > 25 && plugin.getDays() < 50) {

            long define = plugin.getDays() - 25;

            this.stormTicks = define * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDays() == 50) {

            this.stormTicks = 3600;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDays() > 50 && plugin.getDays() < 75) {

            long define = plugin.getDays() - 50;

            this.stormTicks = define * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();
        
        int PlayerLocX = p.getLocation().getBlockX();
        int PlayerLocZ = p.getLocation().getBlockZ();

        int PlayerY1 = p.getLocation().getBlockY() - 1;
        int PlayerY2 = PlayerY1 + 1;
        int PlayerY3 = PlayerY2 + 1;

        Location loc = p.getLocation();


        new BukkitRunnable() {
            @Override
            public void run() {

                if (PlayerY3 > 0) {

                    Block skullBlock = loc.getWorld().getBlockAt(PlayerLocX, PlayerY3, PlayerLocZ);

                    skullBlock.setType(Material.PLAYER_HEAD);
                    BlockState state = skullBlock.getState();
                    Skull skullState = (Skull) state;
                    UUID uuid = p.getUniqueId();
                    skullState.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));
                    skullState.update();

                    loc.getWorld().getBlockAt(PlayerLocX, PlayerY1, PlayerLocZ).setType(Material.BEDROCK);
                    loc.getWorld().getBlockAt(PlayerLocX, PlayerY2, PlayerLocZ).setType(Material.NETHER_BRICK_FENCE);
                } else {

                    Block skullBlock = loc.getWorld().getBlockAt(PlayerLocX, 10, PlayerLocZ);

                    skullBlock.setType(Material.PLAYER_HEAD);
                    BlockState state = skullBlock.getState();
                    Skull skullState = (Skull) state;
                    UUID uuid = p.getUniqueId();
                    skullState.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));
                    skullState.update();

                    skullBlock.getRelative(BlockFace.DOWN).setType(Material.NETHER_BRICK_FENCE);
                    skullBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).setType(Material.BEDROCK);
                }
            }
        }.runTaskLater(plugin, 10);

        boolean weather = world.hasStorm();

        String victim = e.getEntity().getPlayer().getName();
        int Dx = e.getEntity().getPlayer().getLocation().getBlockX();
        int Dy = e.getEntity().getPlayer().getLocation().getBlockY();
        int Dz = e.getEntity().getPlayer().getLocation().getBlockZ();

        //String DeathChatMessage = instance.getConfig().getString("Server-Messages.DeathMessageChat");

        Bukkit.getOnlinePlayers().forEach(o -> {
            String msg = getMessage("DeathMessageChat", o).replace("%player%", victim);
            o.sendMessage(msg);

        });

        sendConsole(getMsgForConsole("DeathMessageChat").replace("%player%", victim));

        //Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', DeathChatMessage.replace("%player%", victim)));

        if (instance.getConfig().contains("Server-Messages.CustomDeathMessages." + p.getName())) {

            Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("Server-Messages.CustomDeathMessages." + p.getName()).replace("%player%", p.getName())));
        } else {

            Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("Server-Messages.DefaultDeathMessage").replace("%player%", p.getName())));
        }

        if (instance.getConfig().getBoolean("Server-Messages.coords-msg-enable")) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "X: " + Dx + " || Y: " + Dy + " || Z: " + Dz + ChatColor.RESET);
        }

        loadTicks();

        int stormDuration = world.getWeatherDuration();
        int stormTicksToSeconds = stormDuration / 20;
        long stormIncrement = stormTicksToSeconds + this.stormTicks;
        int intsTicks = (int) this.stormTicks;
        int inc = (int) stormIncrement;

        if (!e.getEntity().hasPermission("permadeathcore.banoverride")) {

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:weather thunder");

            if (weather) {
                world.setWeatherDuration(inc * 20);
                //Bukkit.getPluginManager().callEvent(new DeathTrainStartEvent(instance.world, inc * 20, instance.getDays(), true));
            } else {
                world.setWeatherDuration(intsTicks * 20);
                //Bukkit.getPluginManager().callEvent(new DeathTrainStartEvent(instance.world, intsTicks * 20, instance.getDays(), false));
            }
        }

        PlayerDataManager man = new PlayerDataManager(e.getEntity().getPlayer().getName(), instance);
        man.setAutoDeathCause(e.getEntity().getPlayer().getLastDamageCause().getCause());
        man.setDeathTime();
        man.setDeathDay();
        man.setDeathCoords(e.getEntity().getPlayer().getLocation());

        BukkitScheduler scheduler = getServer().getScheduler();

        if (!e.getEntity().hasPermission("permadeathcore.banoverride")) {
            scheduler.scheduleSyncDelayedTask(instance, new Runnable() {

                @Override
                public void run() {

                    deathTrain = true;

                    loadTicks();

                    Bukkit.getOnlinePlayers().forEach(p -> {
                        String msg = getMessage("DeathTrainMessage", p).replace("%tiempo%", String.valueOf(stormHours));

                        p.sendMessage(msg);

                    });

                    sendConsole(getMsgForConsole("DeathTrainMessage").replace("%tiempo%", String.valueOf(stormHours)));

                    //Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', DeathTrainMessage.replace("%tiempo%", Long.toString(stormHours))));

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_HORSE_DEATH, 10, 1);
                    }

                    int random = new SplittableRandom().nextInt(100) + 1;

                    String s = "";

                    if (random <= 50) {

                        s = " <= 50";
                    }

                    if (random > 50) {

                        s = " > 50";
                    }

                    Bukkit.broadcastMessage(instance.format("&7Probabilidad de girar la ruleta: " + random + s));

                    if (random <= 50) {
                        new Ruleta(instance, p).runTaskTimer(instance, 20L, 20L);
                    }
                }
            }, 75L);
        } else {

            Bukkit.broadcastMessage(instance.format("&eEl jugador &b" + e.getEntity().getName() + " &eno puede dar más horas de tormenta."));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            //String ServerMessageTitle = instance.getConfig().getString("Server-Messages.DeathMessageTitle");
            //String ServerMessageSubtitle = instance.getConfig().getString("Server-Messages.DeathMessageSubtitle");

            String ServerMessageTitle = getMessage("DeathMessageTitle", player);
            String ServerMessageSubtitle = getMessage("DeathMessageSubtitle", player);

            player.sendTitle(ServerMessageTitle, ServerMessageSubtitle.replace("%player%", victim));
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, -5);
        }


        scheduler.runTaskLater(instance, new Runnable() {
            @Override
            public void run() {

                p.spigot().respawn();

                if (instance.getConfig().getBoolean("ban-enabled") && !p.hasPermission("permadeathcore.banoverride")) {
                    p.setGameMode(GameMode.SPECTATOR);
                } else {
                    p.setGameMode(GameMode.SURVIVAL);
                }
            }
        }, 3L);

        if (!p.hasPermission("permadeathcore.banoverride")) {
            if (instance.getConfig().getBoolean("ban-enabled")) {
                scheduler.runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {
                        p.kickPlayer(ChatColor.RED + "Has sido INFERNABANEADO");
                        Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), ChatColor.RED + "Has sido INFERNABANEADO", null, "console");
                    }
                }, 20 * 5);
            }
        }
    }

    @EventHandler
    public void onBreakSkull(EntityPickupItemEvent e) {

        if (e.isCancelled()) return;

        if (e.getEntity() instanceof Player) {
            ItemStack i = e.getItem().getItemStack();

            if (i.getType() == Material.PLAYER_HEAD) {

                SkullMeta meta = (SkullMeta) i.getItemMeta();

                PlayerDataManager man = new PlayerDataManager(meta.getOwner(), instance);
                man.craftHead(i);
            }
        }
    }

    @EventHandler
    public void playerSleep(PlayerBedEnterEvent event) {

        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.getPlayer().sendMessage(instance.format("&cSolo puedes dormir en el Overworld."));
            return;
        }

        if (plugin.getNightmareEvent().isRunning()) {

            event.getPlayer().sendMessage(instance.format(instance.tag + "&c¡No puedes dormir en el evento de &4&lNIGHTMARE&c!"));
            event.setCancelled(true);
        }

        if (plugin.getDays() >= 20) {
            event.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);

            Location playerbed = event.getBed().getLocation().add(0, 1, 0);

            world.playSound(playerbed, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
            //world.playEffect(playerbed, Effect.GHAST_SHOOT, 100);
            world.spawnParticle(Particle.EXPLOSION_HUGE, playerbed, 1);

            event.setCancelled(true);
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {

            event.getPlayer().sendMessage(instance.format("&cNo puedes dormir ahora."));
            return;
        }

        Player player = event.getPlayer();
        long time = instance.world.getTime();

        int neededPlayers = 1;

        if (instance.getDays() >= 10) {

            neededPlayers = 4;
        }

        if (Bukkit.getOnlinePlayers().size() < neededPlayers) {

            player.sendMessage(format("&cNo puedes dormir porque no hay suficientes personas en línea (" + neededPlayers + ")."));
            event.setCancelled(true);
            return;
        }

        if (time < 13000) {

            player.sendMessage(instance.format("&cSolo puedes dormir de noche"));
            event.setCancelled(true);
            return;
        }

        if (plugin.getDays() < 10 && time >= 13000) {

            ArrayList<Player> sent = new ArrayList<>();

            Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {

                @Override
                public void run() {

                    event.getPlayer().getWorld().setTime(0L);
                    player.setStatistic(Statistic.TIME_SINCE_REST, 0);

                    if (!sent.contains(player)) {

                        //Bukkit.broadcastMessage(instance.format(Objects.requireNonNull(instance.getConfig().getString("Server-Messages.Sleep").replace("%player%", player.getName()))));

                        Bukkit.getOnlinePlayers().forEach(p -> {

                            String msg = getMessage("Sleep", p).replace("%player%", player.getName());

                            p.sendMessage(msg);

                        });

                        sendConsole(getMsgForConsole("Sleep").replace("%player%", player.getName()));

                        sent.add(player);
                        player.damage(0.1);

                        if (new SplittableRandom().nextInt(100) + 1 <= 5) {

                            for (int i = 0; i < 2; i++) {

                                Phantom p = world.spawn(player.getLocation().add(0, 10, 0), Phantom.class);
                                p.setSize(9);
                                p.setCustomName(instance.format("&6Giga Phantom"));
                            }
                        }
                    }
                }
            }, 60L);
        }

        if (plugin.getDays() >= 10 && plugin.getDays() <= 19 && time >= 13000) {

            globalSleeping.add(player);
            //Bukkit.broadcastMessage(instance.format("&7" + player.getName() + " &eestá durmiendo (&b" + globalSleeping.size() + "&e/&b4&e)"));

            Bukkit.getOnlinePlayers().forEach(p -> {

                String msg = getMessage("Sleeping", p).replace("%needed%", String.valueOf(4)).replace("%players%", String.valueOf(globalSleeping.size())).replace("%player%", player.getName());

                p.sendMessage(msg);

            });

            sendConsole(getMsgForConsole("Sleeping").replace("%needed%", String.valueOf(4)).replace("%players%", String.valueOf(globalSleeping.size())).replace("%player%", player.getName()));

            if (globalSleeping.size() >= neededPlayers && globalSleeping.size() < Bukkit.getOnlinePlayers().size()) {

                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {

                        if (globalSleeping.size() >= 4) {

                            event.getPlayer().getWorld().setTime(0L);

                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (all.isSleeping()) {

                                    all.setStatistic(Statistic.TIME_SINCE_REST, 0);
                                    all.damage(0.1);
                                    Bukkit.broadcastMessage(instance.format(Objects.requireNonNull(instance.getConfig().getString("Server-Messages.Sleep").replace("%player%", all.getName()))));
                                }
                            }

                            Bukkit.broadcastMessage(instance.format("&eHan dormido suficientes jugadores (&b4&e)."));
                            globalSleeping.clear();
                        }
                    }
                }, 40L);
            }

            if (globalSleeping.size() == Bukkit.getOnlinePlayers().size()) {

                event.getPlayer().getWorld().setTime(0L);

                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.setStatistic(Statistic.TIME_SINCE_REST, 0);
                    all.damage(0.1);
                    Bukkit.broadcastMessage(instance.format(Objects.requireNonNull(instance.getConfig().getString("Server-Messages.Sleep").replace("%player%", all.getName()))));
                }

                Bukkit.broadcastMessage(instance.format("&eHan dormido todos los jugadores."));

                globalSleeping.clear();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent e) {

        Player p = e.getPlayer();

        if (p.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        if (sleeping.contains(p)) {

            sleeping.remove(p);
        }

        if (globalSleeping.contains(p)) {

            globalSleeping.remove(p);
        }

        if (p.getWorld().getTime() >= 0 && p.getWorld().getTime() < 13000) {

            return;
        }

        p.sendMessage(instance.format("&eHas abandonado la cama, ya no contarás para pasar la noche."));
    }

    @EventHandler
    public void onLeaveForBed(PlayerQuitEvent e) {

        Player p = e.getPlayer();

        if (sleeping.contains(p)) {

            sleeping.remove(p);
        }

        if (globalSleeping.contains(p)) {

            globalSleeping.remove(p);
        }

        if (Bukkit.getOnlinePlayers().size() == 0) {

            plugin.getEndManager().despawnEntities();
        }

        for (Player all : Bukkit.getOnlinePlayers()) {

            Scoreboard s = all.getScoreboard();
            if (s.getTeam("jugadores") == null) {
                s.registerNewTeam("jugadores");
            }
            Team team = s.getTeam("jugadores");

            team.setCanSeeFriendlyInvisibles(true);

            if (team.hasEntry(e.getPlayer().getName())) {
                team.removeEntry(e.getPlayer().getName());
            }
        }
    }

    // Mensaje al entrar un usuario
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String getname = player.getName();

        e.setJoinMessage(null);

        Bukkit.getOnlinePlayers().forEach(p -> {

            String JoinMessage = getMessage("OnJoin", p).replace("%player%", e.getPlayer().getName());

            p.sendMessage(JoinMessage);

        });

        sendConsole(getMsgForConsole("OnJoin").replace("%player%", player.getName()));

        //e.setJoinMessage(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', JoinMessage.replace("%player%", getname)));

        MessageUtil.sendWelcomeMessage(player);

        PlayerDataManager dataManager = new PlayerDataManager(e.getPlayer().getName(), instance);
        dataManager.setLastDay(instance.getDays());

        Player p = e.getPlayer();

        for (Player all : Bukkit.getOnlinePlayers()) {

            Scoreboard s = all.getScoreboard();
            if (s.getTeam("jugadores") == null) {
                s.registerNewTeam("jugadores");
            }
            Team team = s.getTeam("jugadores");

            team.setCanSeeFriendlyInvisibles(true);

            if (!team.hasEntry(p.getName())) {
                team.addEntry(p.getName());
            }
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {

        if (instance.getConfig().getBoolean("anti-afk-enabled")) {

            if (instance.getConfig().getStringList("AntiAFK.Bypass").contains(e.getName())) {

                return;
            }

            PlayerDataManager dataManager = new PlayerDataManager(e.getName(), instance);

            long actualDay = instance.getDays();
            long lastConection = dataManager.getLastDay();

            if (actualDay < lastConection) {

                dataManager.setLastDay(actualDay);
                return;
            }

            OfflinePlayer off = Bukkit.getOfflinePlayer(e.getName());

            if (off == null) return;

            if (!Bukkit.getWhitelistedPlayers().contains(off) || Bukkit.getBannedPlayers().contains(off)) return;

            long result = actualDay - lastConection;

            if (result >= instance.getConfig().getInt("AntiAFK.DaysForBan")) {

                String reason = instance.format(
                        "&c&lHas sido INFERNABANEADO\n" +
                                "&eRazón: AFK\n" +
                                "&7Si crees que es un\n" +
                                "&7error, contacta un\n" +
                                "&7administrador."
                );

                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
                Bukkit.getBanList(BanList.Type.NAME).addBan(e.getName(), reason, null, "console");
            }
        }
    }

    // Mensaje al salir un usuario
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String getname = player.getName();

        e.setQuitMessage(null);

        Bukkit.getOnlinePlayers().forEach(p -> {

            String JoinMessage = getMessage("OnLeave", p).replace("%player%", e.getPlayer().getName());

            p.sendMessage(JoinMessage);

        });

        sendConsole(getMsgForConsole("OnLeave").replace("%player%", e.getPlayer().getName()));
    }

    @EventHandler
    public void onWeatherStorm(WeatherChangeEvent event) {
        boolean weather = event.getWorld().hasStorm();

        if (weather) {
            //String StormMessage = instance.getConfig().getString("Server-Messages.StormEnd");
            //Bukkit.broadcastMessage(tag + ChatColor.translateAlternateColorCodes('&', StormMessage));

            Bukkit.getOnlinePlayers().forEach(p -> {

                String msg = tag + getMessage("StormEnd", p);

                p.sendMessage(msg);

            });

            sendConsole(getMsgForConsole("StormEnd"));
        }
    }

    @EventHandler
    public void totemNerf(EntityResurrectEvent event){

        if (!(event.getEntity() instanceof Player)) return;

        if (instance.getDays() >= 10) {

            Player p = (Player) event.getEntity();

            String s = "";

            int random = new SplittableRandom().nextInt(100) + 1;

            if (random == 1) {

                s = "1 = 1";

                new Ruleta(instance, p).runTaskTimer(instance, 20L, 20L);

            } else {

                s = random + " != 1";
            }

            Bukkit.broadcastMessage(instance.format("&7" + p.getName() + " ha consumido un tótem, probabilidad de activar la ruleta: " + s));

        }

        if (((Player) event.getEntity()).getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING || ((Player) event.getEntity()).getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {


            if (!instance.getConfig().getBoolean("TotemFail.Enable")) {

                return;
            }

            Player p = (Player) event.getEntity();
            String player = p.getName();

            int failProb = 0;
            boolean containsDay;

            if (plugin.getConfig().contains("TotemFail.FailProbs." + plugin.getDays())) {

                failProb = Objects.requireNonNull(Objects.requireNonNull(instance.getConfig().getInt("TotemFail.FailProbs." + plugin.getDays())));
                containsDay = true;
            } else {

                System.out.println("[INFO] La probabilidad del tótem se encuentra desactivada para el día: " + plugin.getDays());
                containsDay = false;
            }

            String totemFail = Objects.requireNonNull(instance.getConfig().getString("TotemFail.ChatMessage"));
            String totemMessage = Objects.requireNonNull(instance.getConfig().getString("TotemFail.PlayerUsedTotemMessage"));

            if (plugin.getDays() >= 40) {

                totemMessage = Objects.requireNonNull(instance.getConfig().getString("TotemFail.PlayerUsedTotemsMessage").replace("{ammount}", "dos").replace("%player%", player));
            }


            for (String k : instance.getConfig().getConfigurationSection("TotemFail.FailProbs").getKeys(false)) {

                try {

                    int i = Integer.valueOf(k);

                    if (i == plugin.getDays()) {

                        containsDay = true;
                    }

                } catch (NumberFormatException e) {

                    System.out.println("[ERROR] Ha ocurrido un error al cargar la probabilidad de tótem del día '" + k + "'");
                }
            }

            if (!containsDay) return;

            if (failProb >= 101) {

                failProb = 100;
            }

            if (failProb < 0) {

                failProb = 1;
            }

            if (failProb == 100) {

                Bukkit.broadcastMessage(instance.format(totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(100)).replace("%number%", String.valueOf(failProb))));
                Bukkit.broadcastMessage(instance.format(totemFail.replace("%player%", player)));
                event.setCancelled(true);

            } else {

                if (instance.getDays() < 40) {

                    int random = (int) (Math.random() * 100) + 1;
                    int resta = 100 - failProb;

                    int toShow = resta;

                    if (resta == random) {

                        toShow = toShow - 1;
                    }

                    int raShow = random;

                    if (random == resta) {

                        raShow = raShow - 1;
                    }

                    boolean tieneMedalla = false;

                    if (p.getInventory().getItemInOffHand() != null) {

                        if (p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING && p.getInventory().getItemInOffHand().getItemMeta().isUnbreakable()) {

                            tieneMedalla = true;
                        }
                    }

                    if (p.getInventory().getItemInMainHand() != null) {

                        if (p.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING && p.getInventory().getItemInMainHand().getItemMeta().isUnbreakable()) {

                            tieneMedalla = true;
                        }
                    }


                    if (tieneMedalla) {

                        Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("TotemFail.Medalla").replace("%player%", p.getName())));
                        return;
                    }

                    if (random > resta) {

                        Bukkit.broadcastMessage(instance.format(totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        Bukkit.broadcastMessage(instance.format(totemFail.replace("%player%", player)));
                        event.setCancelled(true);
                    } else {

                        Bukkit.broadcastMessage(instance.format(totemMessage.replace("%player%", player).replace("%porcent%", "!=").replace("%totem_fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(resta))));
                    }
                } else {

                    int random = (int) (Math.random() * 100) + 1;

                    int resta = 100 - failProb;

                    int toShow = resta;

                    if (resta == random) {

                        toShow = toShow - 1;
                    }

                    int raShow = random;

                    if (random == resta) {

                        raShow = raShow - 1;
                    }

                    int totems = 0;

                    for (ItemStack s : p.getInventory().getContents()) {

                        if (s != null) {

                            if (s.getType() == Material.TOTEM_OF_UNDYING) {

                                totems = totems + 1;
                            }
                        }
                    }

                    if (p.getInventory().getItemInOffHand() != null) {

                        if (p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {

                            totems = totems + 1;
                        }
                    }

                    if (totems < 2) {

                        Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("TotemFail.NotEnoughTotems").replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        event.setCancelled(true);
                        return;
                    }

                    int removedTotems = 0;

                    for (ItemStack s : p.getInventory().getContents()) {

                        if (s != null) {

                            if (s.getType() == Material.TOTEM_OF_UNDYING) {

                                if (removedTotems < 2) {
                                    p.getInventory().removeItem(s);
                                    removedTotems = removedTotems + 1;
                                }
                            }
                        }
                    }

                    if (p.getInventory().getItemInOffHand() != null) {

                        if (removedTotems < 2) {

                            if (p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {

                                p.getInventory().setItemInOffHand(null);
                                removedTotems = removedTotems + 1;
                            }
                        }
                    }

                    boolean tieneMedalla = false;

                    if (p.getInventory().getItemInOffHand() != null) {

                        if (p.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING && p.getInventory().getItemInOffHand().getItemMeta().isUnbreakable()) {

                            tieneMedalla = true;
                        }
                    }

                    if (p.getInventory().getItemInMainHand() != null) {

                        if (p.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING && p.getInventory().getItemInMainHand().getItemMeta().isUnbreakable()) {

                            tieneMedalla = true;
                        }
                    }


                    if (tieneMedalla) {

                        Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("TotemFail.Medalla").replace("%player%", p.getName())));
                        return;
                    }

                    if (random > resta) {

                        Bukkit.broadcastMessage(instance.format(totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("TotemFail.ChatMessageTotems").replace("%player%", player)));
                        event.setCancelled(true);
                    } else {

                        Bukkit.broadcastMessage(instance.format(totemMessage.replace("%player%", player).replace("%porcent%", "!=").replace("%totem_fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(resta))));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMilk(PlayerItemConsumeEvent e) {

        if (plugin.getDays() >= 10) {

            if (e.getItem() != null) {

                if (e.getItem().getType() == Material.MILK_BUCKET) {

                    if (e.getPlayer().hasPotionEffect(PotionEffectType.HUNGER)) {

                        PotionEffect effect = e.getPlayer().getPotionEffect(PotionEffectType.HUNGER);

                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {

                                e.getPlayer().addPotionEffect(effect);
                            }
                        }, 10L);
                    }

                    if (new SplittableRandom().nextInt(100) + 1 <= 5) {
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 15*20, 0));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent e) {

        if (!e.isCancelled() && instance.getDays() >= 10) {

            if (new SplittableRandom().nextInt(100) + 1 <= 3) {

                e.getPlayer().sendMessage(format(instance.tag + "&e¡Tu tridente ha fallado!"));
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                e.getPlayer().setCooldown(Material.TRIDENT, 20*30);

                e.setCancelled(true);
            }
        }
    }

    public String getMessage(String path, Player player) {

        return instance.getMessages().getMessageByPlayer("Server-Messages." + path, player.getName());
    }

    public String getMessage(String path, Player player, List l) {

        return instance.getMessages().getMessageByPlayer("Server-Messages." + path, player.getName(), l);
    }

    public String getMsgForConsole(String path) {

        return instance.getMessages().getMessageForConsole("Server-Messages." + path);
    }

    public void sendConsole(String mensaje) {

        Bukkit.getConsoleSender().sendMessage(mensaje);
    }
}
