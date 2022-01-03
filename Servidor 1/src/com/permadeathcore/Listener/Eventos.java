package com.permadeathcore.Listener;

import static com.permadeathcore.Main.format;
import static com.permadeathcore.Main.instance;
import static org.bukkit.Bukkit.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.permadeathcore.Main;
import com.permadeathcore.Manager.PlayerDataManager;
import com.permadeathcore.Task.RuletaMortal;
import com.permadeathcore.Util.ItemBuilder;
import com.permadeathcore.Util.Maldicion;
import com.permadeathcore.Util.SkeletonRaid;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class Eventos implements Listener{
    private final Main plugin;
    private LocalDate fechaActual;
    private LocalDate fechaInicio;
    World world;
    String tag;

    ArrayList<Player> sleeping = new ArrayList<>();
    ArrayList<Player> globalSleeping = new ArrayList<>();

    long stormTicks;
    long stormHours;

    public Eventos(Main instance) {
        this.plugin = instance;
        this.world = instance.world;

        this.tag = Main.instance.tag;
        this.stormTicks = plugin.getDays() * 3600L;
        this.stormHours = this.stormTicks / 60L / 60L;

        this.fechaInicio = LocalDate.parse(Main.instance.fecha);
        this.fechaActual = LocalDate.now();

        loadTicks();
    }

    public void loadTicks() {
        if (plugin.getDays() <= 10) {

            this.stormTicks = plugin.getDays() * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDays() > 10) {

            long define = plugin.getDays() - 10;

            this.stormTicks = define * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (instance.getDays() >= 10) {

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {

                if (e.getClickedBlock().getType() == Material.ENDER_CHEST || e.getClickedBlock().getState() instanceof EnderChest) {

                    if (instance.world.hasStorm()) {

                        e.getPlayer().sendMessage(instance.format("&cNo puedes abrir cofres de ender, hay una tormenta activa."));
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0F, 1.0F);

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {

        if (e.getEntity() instanceof WitherSkeleton && e.getEntity().getCustomName() != null && e.getEntity().getKiller() != null) {

            if (e.getEntity().getCustomName().contains(instance.format("&6Patrol Skeleton"))) {

                Player on = e.getEntity().getKiller();

                boolean isFromRaid = false;

                for (SkeletonRaid raid : instance.getRaids()) {

                    if (raid.getRaiders().contains(e.getEntity())) {

                        isFromRaid = true;
                    }
                }

                if (!isFromRaid) {

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

                    while (altura < 257 && on.getWorld().getBlockAt(new Location(on.getWorld(), randomX, altura, randomZ)).getType() != Material.AIR) {

                        altura++;
                    }

                    if (altura != startingY && altura < 257) {

                        loc.setY(altura);
                    }

                    instance.getRaids().add(new SkeletonRaid(instance, loc, on));
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        if (e.getPlayer().getWorld().hasStorm() && instance.getDays() >= 10 && new Random().nextInt(4999) == 5 && e.getPlayer().getWorld().getName().equalsIgnoreCase(instance.world.getName())) {

            e.getPlayer().getWorld().strikeLightningEffect(e.getPlayer().getLocation());

            Double damage = 10.0D;

            if (e.getPlayer().getHealth() - damage > 1.0) {

                e.getPlayer().damage(damage);
            } else {

                e.getPlayer().setHealth(2.0D);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0F, 5.0F);
            }
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


        new BukkitRunnable(){
            @Override
            public void run(){

                if (PlayerY3 > 0) {

                    Block skullBlock =  p.getWorld().getBlockAt(PlayerLocX,PlayerY3,PlayerLocZ);

                    skullBlock.setType(Material.PLAYER_HEAD);
                    BlockState state = skullBlock.getState();
                    Skull skullState = (Skull) state;
                    UUID uuid = p.getUniqueId();
                    skullState.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));
                    skullState.update();

                    p.getWorld().getBlockAt(PlayerLocX,PlayerY1,PlayerLocZ).setType(Material.BEDROCK);
                    p.getWorld().getBlockAt(PlayerLocX,PlayerY2,PlayerLocZ).setType(Material.NETHER_BRICK_FENCE);
                } else {

                    Block skullBlock =  p.getWorld().getBlockAt(PlayerLocX, 10,PlayerLocZ);

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
        }.runTaskLater(plugin,10);

        boolean weather = world.hasStorm();

        String victim = e.getEntity().getPlayer().getName();
        int Dx = e.getEntity().getPlayer().getLocation().getBlockX();
        int Dy = e.getEntity().getPlayer().getLocation().getBlockY();
        int Dz = e.getEntity().getPlayer().getLocation().getBlockZ();

        String DeathChatMessage = instance.getConfig().getString("Server-Messages.DeathMessageChat");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',DeathChatMessage.replace("%player%",victim)));

        // Mensaje Custom

        if (instance.getConfig().contains("Server-Messages.CustomDeathMessages." + p.getName())) {

            Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("Server-Messages.CustomDeathMessages." + p.getName())));
        } else {

            Bukkit.broadcastMessage(instance.format(instance.getConfig().getString("Server-Messages.DefaultDeathMessage").replace("%player%", p.getName())));
        }

        if(instance.getConfig().getBoolean("Server-Messages.coords-msg-enable")){
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "X: " + Dx + " || Y: " + Dy + " || Z: " + Dz + ChatColor.RESET);
        }

        loadTicks();

        int stormDuration = world.getWeatherDuration();
        int stormTicksToSeconds = stormDuration / 20;
        long stormIncrement = stormTicksToSeconds + this.stormTicks;
        int intsTicks = (int)this.stormTicks;
        int inc = (int)stormIncrement;

        if (!e.getEntity().hasPermission("permadeathcore.banoverride")) {
            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "minecraft:weather thunder");
            if (weather) {
                world.setWeatherDuration(inc * 20);
            } else {
                world.setWeatherDuration(intsTicks * 20);
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
                // Delay al enviar el mensaje del incremento de horas en tormenta.
                @Override
                public void run() {

                    String DeathTrainMessage = instance.getConfig().getString("Server-Messages.DeathTrainMessage");

                    loadTicks();
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', DeathTrainMessage.replace("%tiempo%", Long.toString(stormHours))));

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_HORSE_DEATH, 10, 1);
                    }

                    int prob = new Random().nextInt(99);
                    prob = prob + 1;

                    if (instance.getDays() < 5) {

                        if (prob <= 15) {
                            new RuletaMortal(instance, e.getEntity()).runTaskTimer(instance, 20L, 20L);
                        }
                    }

                    if (instance.getDays() >= 5) {

                        if (prob <= 30) {
                            new RuletaMortal(instance, e.getEntity()).runTaskTimer(instance, 20L, 20L);
                            Bukkit.broadcastMessage(instance.format("&7Probabilidad de la ruleta: &b" + prob + " &ees menor o igual a &b30 (gira la ruleta)."));
                        }

                        if (prob > 30) {

                            Bukkit.broadcastMessage(instance.format("&7Probabilidad de la ruleta: &b" + prob + " &ees mayor a &b30 (no gira)&e."));
                        }
                    }

                    if (instance.getDays() >= 15) {

                        new RuletaMortal(instance, e.getEntity()).runTaskTimer(instance, 20L, 20L);
                    }
                }
            }, 75L);
        } else {

            Bukkit.broadcastMessage(instance.format("&eEl jugador &b" + e.getEntity().getName() + " &eno puede dar tormenta ni girar la ruleta."));
        }

        for(Player player: Bukkit.getOnlinePlayers()){

            String ServerMessageTitle = instance.getConfig().getString("Server-Messages.DeathMessageTitle");
            String ServerMessageSubtitle = instance.getConfig().getString("Server-Messages.DeathMessageSubtitle");

            player.sendTitle(ChatColor.translateAlternateColorCodes('&',ServerMessageTitle),ChatColor.translateAlternateColorCodes('&',ServerMessageSubtitle.replace("%player%", victim)));
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, -5);
        }
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent e) {

        if (instance.getDays() >= 10) {

            if (new Random().nextInt(99) <= 6) {

                Player on = e.getPlayer();

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

                while (altura < 257 && on.getWorld().getBlockAt(new Location(on.getWorld(), randomX, altura, randomZ)).getType() != Material.AIR) {

                    altura++;
                }

                if (altura != startingY && altura < 257) {

                    loc.setY(altura);
                }

                instance.getRaids().add(new SkeletonRaid(instance, loc, on));

                e.setCancelled(true);
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
    public void onEntityDeath(EntityDeathEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        BukkitScheduler scheduler = getServer().getScheduler();

        scheduler.runTaskLater(instance, new Runnable() {
            @Override
            public void run() {

                player.spigot().respawn();

                if (event instanceof PlayerDeathEvent && instance.getConfig().getBoolean("ban-enabled") && !player.hasPermission("permadeathcore.banoverride")) {
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }, 3L);

        if (!player.hasPermission("permadeathcore.banoverride")) {
            if (event instanceof PlayerDeathEvent && instance.getConfig().getBoolean("ban-enabled") && !event.getEntity().hasPermission("permadeathcore.banoverride")) {
                scheduler.runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {
                        player.kickPlayer(ChatColor.RED + "Has sido INFERNABANEADO");
                        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), ChatColor.RED + "Has sido INFERNABANEADO", null, "console");
                    }
                }, 20 * 5);
            }
        }
    }

    @EventHandler
    public void playerSleep(PlayerBedEnterEvent event) {

        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.getPlayer().sendMessage(instance.format("&cSolo puedes dormir en el OverWorld."));
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {

            event.getPlayer().sendMessage(instance.format("&cNo puedes dormir ahora."));
            return;
        }

        if (instance.getDays() >= 15) {

            event.getPlayer().sendMessage(instance.format("&cNo puedes dormir ahora."));
            event.setCancelled(true);
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

        // 1 persona necesaria.
        if (plugin.getDays() < 10 && time >= 13000) {

            ArrayList<Player> sent = new ArrayList<>();

            Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {

                @Override
                public void run() {

                    event.getPlayer().getWorld().setTime(0L);
                    player.setStatistic(Statistic.TIME_SINCE_REST, 0);

                    if (!sent.contains(player)) {

                        Bukkit.broadcastMessage(instance.format(Objects.requireNonNull(instance.getConfig().getString("Server-Messages.Sleep").replace("%player%", player.getName()))));
                        sent.add(player);
                        player.damage(0.1);
                    }
                }
            }, 60L);
        }



        if (plugin.getDays() >= 10 && plugin.getDays() <= 19 && time >= 13000) {

            globalSleeping.add(player);

            if (globalSleeping.size() >= neededPlayers && globalSleeping.size() < Bukkit.getOnlinePlayers().size()) {

                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {

                        if (globalSleeping.size() >= 4) {

                            event.getPlayer().getWorld().setTime(0L);

                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (all.isSleeping() || all.isSleepingIgnored()) {

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

                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().getWorld().setTime(0L);

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            all.setStatistic(Statistic.TIME_SINCE_REST, 0);
                            all.damage(0.1);
                            Bukkit.broadcastMessage(instance.format(Objects.requireNonNull(instance.getConfig().getString("Server-Messages.Sleep").replace("%player%", all.getName()))));
                        }

                        Bukkit.broadcastMessage(instance.format("&eHan dormido todos los jugadores."));
                        globalSleeping.clear();
                    }
                }, 10L);
            }
        }


        if (plugin.getDays() >= 20) {
            player.setStatistic(Statistic.TIME_SINCE_REST, 0);

            Location playerbed = event.getBed().getLocation().add(0, 1, 0);

            world.playEffect(playerbed, Effect.GHAST_SHOOT, 100);
            world.spawnParticle(Particle.EXPLOSION_HUGE, playerbed, 1);

            event.setCancelled(true);
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
    }

    @EventHandler
    public void EDBE(EntityDamageByEntityEvent e) {

        if (e.getEntity() instanceof Player && instance.getDays() >= 8) {

            Player p = (Player) e.getEntity();

            if (p.isBlocking()) {

                boolean foundIM = false;

                for (ItemStack s : p.getInventory().getContents()) {

                    if (s != null) {

                        if (s.getType() == Material.SHIELD) {

                            if (s.hasItemMeta()) {

                                if (s.getItemMeta().getDisplayName().contains(instance.format("&6&lEscudo Reforzado"))) {

                                    foundIM = true;
                                }
                            }
                        }
                    }
                }

                if (!foundIM) {

                    p.setCooldown(Material.SHIELD, 100);
                    p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0F, 5.0F);
                }
            }
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Husk && instance.getDays() >= 10) {

            ((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*3, 0));
        }
    }

    @EventHandler
    public void onFireDamage(EntityDamageEvent e) {

        if (instance.getDays() >= 8) {

            if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {

                e.setDamage(4.0D);
            }
        }
    }

    // Recetas del día #8

    @EventHandler(priority = EventPriority.HIGHEST)
    public void restrictCrafting(PrepareItemCraftEvent event) {

        if (instance.getDays() < 8) return;

        if (event.getInventory().getResult() != null) {

            ItemStack result = event.getInventory().getResult();

            if (instance.getDiamondSet().isPiece(result)) {

                int netherStarsFound = 0;

                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item != null) {
                        if (item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();
                            if (item.getType() == Material.ORANGE_DYE) {
                                if (meta.isUnbreakable()) {
                                    netherStarsFound++;
                                }
                            }
                        }
                    }
                }

                if (netherStarsFound < 1) {
                    event.getInventory().setResult(null);
                }

                if (netherStarsFound >= 1) {

                    Material mat = result.getType();

                    if (mat == Material.DIAMOND_HELMET) {
                        event.getInventory().setResult(instance.getDiamondSet().craftHelmet());
                    }

                    if (mat == Material.DIAMOND_CHESTPLATE) {
                        event.getInventory().setResult(instance.getDiamondSet().craftChestplate());
                    }

                    if (mat == Material.DIAMOND_LEGGINGS) {
                        event.getInventory().setResult(instance.getDiamondSet().craftLegs());
                    }

                    if (mat == Material.DIAMOND_BOOTS) {
                        event.getInventory().setResult(instance.getDiamondSet().craftBoots());
                    }
                }
            }

            if (result.hasItemMeta()) {

                boolean is = false;

                if (result.getItemMeta().hasDisplayName()) {

                    if (result.getItemMeta().getDisplayName().contains(instance.format("&6&lEscudo Reforzado"))) {

                        is = true;
                    }
                }

                if (!is) {

                    return;
                }

                int netherStarsFound = 0;

                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item != null) {
                        if (item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();
                            if (item.getType() == Material.ORANGE_DYE) {
                                if (meta.isUnbreakable()) {
                                    netherStarsFound++;
                                }
                            }
                        }
                    }
                }

                if (netherStarsFound < 1) {
                    event.getInventory().setResult(null);
                }
            }

            // Tridente
            if (result.hasItemMeta()) {

                boolean is = false;

                if (result.getItemMeta().hasDisplayName()) {

                    if (result.getItemMeta().getDisplayName().contains(instance.format("&6Tridente mejorado"))) {

                        is = true;
                    }
                }

                if (!is) {

                    return;
                }

                int netherStarsFound = 0;

                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item != null) {
                        if (item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();
                            if (item.getType() == Material.ORANGE_DYE) {
                                if (meta.isUnbreakable()) {
                                    netherStarsFound++;
                                }
                            }
                        }
                    }
                }

                if (netherStarsFound < 1) {
                    event.getInventory().setResult(null);
                }
            }

            // Nether star

            if (result.hasItemMeta()) {

                boolean is = false;

                if (result.getItemMeta().hasDisplayName()) {

                    if (result.getItemMeta().getDisplayName().contains(instance.format("&6Reliquia del Nether"))) {

                        is = true;
                    }
                }

                if (!is) {

                    return;
                }

                int booksFound = 0;
                int rodsFound = 0;

                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item != null) {
                        if (item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();
                            if (item.getType() == Material.BOOK) {
                                if (meta.isUnbreakable()) {
                                    booksFound++;
                                }
                            }

                            if (item.getType() == Material.BLAZE_ROD) {

                                if (meta.getDisplayName().contains(instance.format("&d&lEnchanted Blaze Rod"))) {

                                    rodsFound++;
                                }
                            }
                        }
                    }
                }

                if (booksFound < 1 || rodsFound < 4) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }

    // Mensaje al entrar un usuario
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.setResourcePack("https://www.dropbox.com/s/73uzlvrfrwc9ipi/Custom_RP_V1.5.rar?dl=0");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100.0F, 100.0F);

        String getname = player.getName();
        String JoinMessage = instance.getConfig().getString("Server-Messages.OnJoin");

        if (instance.isCanCompleteZombie() && instance.getDays() >= 10) {

            instance.getBossBar().addPlayer(e.getPlayer());
        }

        if (instance.isWitherEvent() && instance.getDays() >= 5) {

            instance.getBossBar().addPlayer(e.getPlayer());
        }

        for (String k : instance.getMaldiciones().keySet()) {

            String name = k.split(";")[0];
            Maldicion maldicion = Maldicion.valueOf(k.split(";")[1]);
            int tiempo = instance.getMaldiciones().get(k);

            if (maldicion == Maldicion.HP_LOST && name.contains(e.getPlayer().getName())) {

                Double d = Double.valueOf(k.split(";")[2]);
                Double i = instance.getNmsAccesor().getMaxHealth(e.getPlayer()) / 2;

                if (d != i) {
                    instance.getMaldiciones().replace(player.getName() + ";" + Maldicion.HP_LOST.toString() + ";" + i, tiempo);

                    if (e.getPlayer().getHealth() > i) {
                        e.getPlayer().setHealth(i);
                    }
                    e.getPlayer().setHealthScale(i);
                    instance.getNmsAccesor().setMaxHealth(e.getPlayer(), i, false);
                }
            }
        }
    }

    // Mensaje al salir un usuario
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String getname = player.getName();
        String LeaveMessage = instance.getConfig().getString("Server-Messages.OnLeave");
        e.setQuitMessage(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', LeaveMessage.replace("%player%", getname)));
    }

    // Cuando pasa la tormenta lanza un mensaje
    @EventHandler
    public void onWeatherStorm(WeatherChangeEvent event) {
        boolean weather = event.getWorld().hasStorm(); // Variable que detecta si hay tormenta


        if (weather){
            String StormMessage = instance.getConfig().getString("Server-Messages.StormEnd");
            Bukkit.broadcastMessage(tag + ChatColor.translateAlternateColorCodes('&', StormMessage));


        }
    }

    @EventHandler
    public void totemNerf(EntityResurrectEvent event){

        if (!(event.getEntity() instanceof Player)) return;

        if (((Player) event.getEntity()).getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING || ((Player) event.getEntity()).getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {


            if (!instance.getConfig().getBoolean("TotemFail.Enable")) {

                return;
            }

            Player p = (Player) event.getEntity();
            String player = p.getName();

            int failProb = 0;
            boolean containsDay = false;

            if (plugin.getConfig().contains("TotemFail.FailProbs." + plugin.getDays())) {

                failProb = Objects.requireNonNull(Objects.requireNonNull(instance.getConfig().getInt("TotemFail.FailProbs." + plugin.getDays())));
                containsDay = true;
            } else {

                System.out.println("[INFO] La probabilidad del tótem se encuentra desactivada para el día: " + plugin.getDays());
                containsDay = false;
            }

            String totemFail = Objects.requireNonNull(instance.getConfig().getString("TotemFail.ChatMessage"));
            String totemMessage = Objects.requireNonNull(instance.getConfig().getString("TotemFail.PlayerUsedTotemMessage"));

            if (plugin.getDays() >= 15) {

                totemMessage = Objects.requireNonNull(instance.getConfig().getString("TotemFail.PlayerUsedTotemsMessage").replace("{ammount}", "dos").replace("%player", player));
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

                Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(100)).replace("%number%", String.valueOf(failProb))));
                Bukkit.broadcastMessage(instance.format(tag + totemFail.replace("%player%", player)));
                event.setCancelled(true);

            } else {

                if (instance.getDays() < 15) {

                    int random = new Random().nextInt(99);
                    random = random + 1;

                    int resta = 100 - failProb;

                    int toShow = resta;

                    if (resta == random) {

                        toShow = toShow - 1;
                    }

                    int raShow = random;

                    if (random == resta) {

                        raShow = raShow - 1;
                    }

                    if (random > resta) {

                        //Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(resta)).replace("%number%", String.valueOf(toShow))));
                        Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        Bukkit.broadcastMessage(instance.format(tag + totemFail.replace("%player%", player)));
                        event.setCancelled(true);
                    } else {

                        Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "!=").replace("%totem_fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(resta))));
                    }
                } else {

                    // DÍA 15, 2 TÓTEMS

                    int random = new Random().nextInt(99);
                    random = random + 1;

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

                        Bukkit.broadcastMessage(instance.format(tag + instance.getConfig().getString("TotemFail.NotEnoughTotems").replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
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


                    if (random > resta) {

                        //Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(resta)).replace("%number%", String.valueOf(toShow))));
                        Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "=").replace("%totem_fail%", String.valueOf(toShow)).replace("%number%", String.valueOf(resta))));
                        Bukkit.broadcastMessage(instance.format(tag + tag + instance.getConfig().getString("TotemFail.ChatMessageTotems").replace("%player%", player)));
                        event.setCancelled(true);
                    } else {

                        Bukkit.broadcastMessage(instance.format(tag + totemMessage.replace("%player%", player).replace("%porcent%", "!=").replace("%totem_fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(resta))));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin2(PlayerJoinEvent e) {

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
    public void onLeave2(PlayerQuitEvent e) {

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
}
