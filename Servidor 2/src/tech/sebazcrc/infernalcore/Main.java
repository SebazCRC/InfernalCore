package tech.sebazcrc.infernalcore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Filter;
import java.util.logging.Logger;

import com.destroystokyo.paper.entity.villager.Reputation;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.infernalcore.Configurations.Language;
import tech.sebazcrc.infernalcore.Configurations.Messages;
import tech.sebazcrc.infernalcore.Custom.InfernalDungeon;
import tech.sebazcrc.infernalcore.End.Util.EndManager;
import tech.sebazcrc.infernalcore.Listener.Custom;
import tech.sebazcrc.infernalcore.Listener.EfectosMob;
import tech.sebazcrc.infernalcore.Listener.VoidEvents;
import tech.sebazcrc.infernalcore.Manager.Data.EndDataManager;
import tech.sebazcrc.infernalcore.Manager.Data.PlayerDataManager;
import tech.sebazcrc.infernalcore.Manager.Event.Nightmare;
import tech.sebazcrc.infernalcore.NMS.NMSAccesor;
import tech.sebazcrc.infernalcore.NMS.NMSHandler;
import tech.sebazcrc.infernalcore.NMS.VersionManager;
import tech.sebazcrc.infernalcore.Task.EndTask;
import tech.sebazcrc.infernalcore.Util.*;
import tech.sebazcrc.infernalcore.Util.Extra.NMSFinder;
import tech.sebazcrc.infernalcore.Util.Item.ObsidianSet;
import tech.sebazcrc.infernalcore.Util.Messages.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tech.sebazcrc.infernalcore.Listener.Eventos;
import tech.sebazcrc.infernalcore.Util.Recipe.RecipeManager;

public final class Main extends JavaPlugin implements Listener {

    public static Main instance;

    private HashMap<String, Integer> maldiciones = new HashMap<>();

    private NMSHandler nmsHandler;
    private NMSAccesor nmsAccesor;

    private Eventos eventos;

    private Messages messages;

    public LocalDate fechaInicio;
    public LocalDate fechaActual = LocalDate.now();

    String fecha;
    private long getDays;
    public long stormTicks;
    public long stormHours;

    public static String tag = "" + ChatColor.GOLD + ChatColor.BOLD + "Infernal" + ChatColor.RESET + ChatColor.WHITE + ChatColor.BOLD + "Core " + ChatColor.GRAY + ">> ";

    public World world = Bukkit.getWorld("world");
    public World endWorld = Bukkit.getWorld("world_the_end");

    private EndTask task = null;

    private EndManager endManager;
    private EndDataManager endData;

    public static boolean runningPaperSpigot = false;
    private boolean runningCraftBukkit = false;

    private Map<Integer, Boolean> registeredDays = new HashMap<>();
    private ArrayList<Player> doneEffectPlayers = new ArrayList<>();

    private boolean loaded = false;
    private boolean alreadyRegisteredListeners = false;
    private boolean alreadyRegisteredChanges = false;

    int timeRunning = 0;

    private Nightmare nightmare;

    private ObsidianSet obsidianSet;

    private InfernalDungeon dungeons;
    private SplittableRandom random;
    private RecipeManager recipes;


    @SuppressWarnings("rawtypes")
    @Override
    public void onEnable() {
        instance = this;
        this.random = new SplittableRandom();
        this.dungeons = new InfernalDungeon(this);

        this.messages = new Messages(this);
        this.nightmare = new Nightmare(this);

        saveResource("config.yml", false);

        setupConsoleFilter();

        LocalDate act = LocalDate.now();
        fechaActual = act.minusDays(1);

        int month = fechaActual.getMonthValue();
        int day = fechaActual.getDayOfMonth();

        String s = "";

        if (month < 10) {

            s = fechaActual.getYear() + "-0" + month + "-";
        } else {

            s = fechaActual.getYear() + "-" + month + "-";
        }

        if (day < 10) {

            s = s + "0" + day;
        } else {

            s = s + day;
        }

        if (getConfig().getString("Fecha").isEmpty()) {

            getConfig().set("Fecha", s);
            saveConfig();
            reloadConfig();
        }

        this.fecha = getConfig().getString("Fecha"); // Cogiendo la fecha desde la config.yml
        this.fechaInicio = LocalDate.parse(getConfig().getString("Fecha"));
        this.getDays = fechaInicio.until(fechaActual, ChronoUnit.DAYS);

        if (getDays == 0) {

            this.getDays = 1;
        }

        if (getDays < 1) {
            System.out.println("[ERROR] ¡'Fecha' en el archivo config.yml tiene un valor negativo o incorrecto!");
            System.out.println("[ERROR] ¡La fecha introducida genera un numero negativo!");
        }

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("ic").setExecutor(new InfernalCommand(this));

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {

                if (!loaded) {

                    startPlugin();
                    loaded = true;
                }

                if (timeRunning < 5) {

                    timeRunning++;
                } else {

                    MessageUtil.setupConfig(instance);
                }

                registerListeners();

                long segundosbrutos = world.getWeatherDuration() / 20;

                long hours = segundosbrutos % 86400 / 3600;
                long minutes = (segundosbrutos % 3600) / 60;
                long seconds = segundosbrutos % 60;
                long days = segundosbrutos / 86400;

                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                if (days == 1) {

                    time = days + " día, " + time;
                }

                if (days > 1) {

                    time = days + " días, " + time;
                }

                getDays = fechaInicio.until(fechaActual, ChronoUnit.DAYS);
                fechaActual = LocalDate.now();

                stormTicks = getDays * 3600;
                stormHours = stormTicks / 60 / 60;

                fechaInicio = LocalDate.parse(getConfig().getString("Fecha"));

                //new ChangesManager(instance, time);

                String finalTime = time;

                if (getDays >= 10) {

                    for (String k : getMaldiciones().keySet()) {

                        String uuid = k.split(";")[0];
                        Maldicion maldicion = Maldicion.valueOf(k.split(";")[1]);
                        int res = getMaldiciones().get(k);

                        if (Bukkit.getPlayer(uuid) == null) {

                            return;
                        }

                        Player p = Bukkit.getPlayer(uuid);
                        if (!p.isOnline() || p.isDead()) return;

                        if (res > 0) {
                            res = res - 1;
                            if (maldicion == Maldicion.INVENTORY_LOCK) {
                                setupVoidSlots(p, false);
                                getMaldiciones().replace(uuid + ";" + maldicion.toString(), res);
                            }

                            if (maldicion == Maldicion.HP_LOST) {

                                getMaldiciones().replace(k, res);
                            }
                        }

                        if (res == 0) {
                            getMaldiciones().remove(k);

                            if (maldicion == Maldicion.INVENTORY_LOCK) {

                                setupVoidSlots(p, true);
                            }

                            if (maldicion == Maldicion.HP_LOST) {

                                Double maxHealth = Double.valueOf(k.split(";")[2]);
                                getNmsAccesor().setMaxHealth(p, maxHealth, false);
                                p.setHealthScale(maxHealth);
                            }

                            p.sendMessage(format(tag + "&cTu condena ha acabado."));
                        }
                    }

                    for (Player on : Bukkit.getOnlinePlayers()) {

                        if (getDays >= 15) {

                            if (obsidianSet == null) {
                                obsidianSet = new ObsidianSet(instance);
                                getServer().getPluginManager().registerEvents(obsidianSet, instance);
                            }

                            obsidianSet.setupObsidianSet(on);
                        }

                        if (nightmare.isRunning()) {
                            if (!nightmare.containsPlayer(on)) {
                                nightmare.addPlayer(on);
                            }

                            if (random.nextInt(10) == 1) {

                                int x = random.nextInt(10) + 1;
                                int z = random.nextInt(10) + 1;

                                if (random.nextBoolean()) {
                                    x = x * -1;
                                }

                                if (random.nextBoolean()) {
                                    z = z * -1;
                                }

                                Location l = new Location(world, x, world.getHighestBlockYAt(x, z), z);

                                if (!l.getChunk().isLoaded()) return;

                                world.strikeLightning(l);
                            }

                        } else {
                            if (nightmare.containsPlayer(on)) {
                                nightmare.removePlayer(on);
                            }
                        }

                        boolean contains = false;
                        String te = "";
                        for (String k : getMaldiciones().keySet()) {

                            String name = k.split(";")[0];
                            Maldicion maldicion = Maldicion.valueOf(k.split(";")[1]);

                            if (name.contains(on.getName()) && maldicion == Maldicion.INVENTORY_LOCK) {

                                int res = getMaldiciones().get(k);

                                int hrs = res / 3600;
                                int minAndSec = res % 3600;
                                int min = minAndSec / 60;
                                int sec = minAndSec % 60;

                                contains = true;

                                String tb = "&eTiempo de Bloqueo de Inventario:";

                                PlayerDataManager data = new PlayerDataManager(on.getName(), instance);

                                if (data.getLanguage() == Language.ENGLISH) {

                                    tb = "&eInventory Lock Time Left:";
                                }

                                te = " - " + tb +  " &b" + (hrs > 9 ? hrs : "0" + hrs) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);

                                if (!world.hasStorm()) {

                                    te = tb + " &b" + (hrs > 9 ? hrs : "0" + hrs) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);
                                }
                            }
                        }

                        //String Message = instance.getConfig().getString("Server-Messages.ActionBarMessage");
                        String Message = eventos.getMessage("ActionBarMessage", on);

                        if (world.hasStorm()) {

                            on.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.replace("%tiempo%", time)) + format(te)));
                        }

                        if (!world.hasStorm() && contains) {

                            on.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(format(te)));
                        }
                    }

                    if (nightmare.isRunning()) {

                        if (nightmare.getTimeLeft() > 0) {
                            world.setTime(18000);
                            nightmare.setTimeLeft(nightmare.getTimeLeft() - 1);

                            int res = nightmare.getTimeLeft();

                            int hrs = res / 3600;
                            int minAndSec = res % 3600;
                            int min = minAndSec / 60;
                            int sec = minAndSec % 60;

                            String tiempo = (hrs > 9 ? hrs : "0" + hrs) + "h " + (min > 9 ? min : "0" + min) + "min " + (sec > 9 ? sec : "0" + sec) + "seg";

                            nightmare.setTitle(instance.format("&4&lNightmare MODE &b&n" + tiempo));
                        } else {

                            nightmare.setRunning(false);
                            nightmare.setTimeLeft(60*60*5);
                        }
                    }
                } else {

                    Bukkit.getOnlinePlayers().forEach(player -> {

                        if (world.hasStorm()) {
                            String Message = eventos.getMessage("ActionBarMessage", player).replace("%tiempo%", finalTime);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Message));
                        }

                    });
                }
            }
        }, 0, 20L);

        if (getDays >= 10) {

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    doVillagerCheck();
                }
            }, 0, 26000);
        }
    }

    public void doVillagerCheck() {

        Bukkit.broadcastMessage(tag + format("&e¡Los aldeanos están verificando su estado actual!"));

        Map<UUID, Integer> enfadados = new HashMap<>();
        Map<UUID, Integer> alegres = new HashMap<>();

        for (Villager v : world.getEntitiesByClass(Villager.class)) {

            Location bed = v.getBedLocation();

            if (bed == null) {
                for (Player on : Bukkit.getOnlinePlayers()) {
                    setAngry(v, on);
                }
            }

            if (bed != null) {

                if (this.isF(bed, "ADELANTE") && this.isF(bed, "ATRAS") && this.isF(bed, "IZQUIERDA") && this.isF(bed, "DERECHA")) {

                    for (Player p : Bukkit.getOnlinePlayers()) {

                        if (!alegres.containsKey(p.getUniqueId())) {

                            alegres.put(p.getUniqueId(), 1);
                        } else {

                            int i = alegres.get(p.getUniqueId()) + 1;
                            alegres.replace(p.getUniqueId(), i);
                        }
                    }
                } else {

                    for (Player p : Bukkit.getOnlinePlayers()) {

                        if (!enfadados.containsKey(p.getUniqueId())) {

                            enfadados.put(p.getUniqueId(), 1);
                        } else {

                            int i = enfadados.get(p.getUniqueId()) + 1;
                            enfadados.replace(p.getUniqueId(), i);
                        }

                        setAngry(v, p);
                    }
                }
            }
        }

        if (!enfadados.isEmpty()) {

            for (Player p : Bukkit.getOnlinePlayers()) {

                if (enfadados.containsKey(p.getUniqueId())) {

                    p.sendMessage(tag + format("&e¡Un total de &b" + enfadados.get(p.getUniqueId()) + " &ealdeanos se han enojado contigo!"));
                } else {

                    p.sendMessage(tag + format("&e¡Un total de &b0 &ealdeanos se han enojado contigo!"));
                }
            }
        } else {

            Bukkit.broadcastMessage(tag + format("&e¡Ningún aldeano se ha enojado!"));
        }

        if (!alegres.isEmpty()) {

            for (Player p : Bukkit.getOnlinePlayers()) {

                if (alegres.containsKey(p.getUniqueId())) {

                    p.sendMessage(tag + format("&e¡Un total de &b" + alegres.get(p.getUniqueId()) + " &ealdeanos se han alegrado contigo!"));
                } else {

                    p.sendMessage(tag + format("&e¡Un total de &b0 &ealdeanos se han alegrado contigo!"));
                }
            }
        } else {

            Bukkit.broadcastMessage(tag + format("&e¡Ningún aldeano se ha alegrado!"));
        }
    }

    private void setAngry(Villager v, Player p) {

        EntityVillager villager = ((CraftVillager)v).getHandle();
        EntityLiving liv = ((CraftLivingEntity)p).getHandle();

        if (villager.world instanceof WorldServer && liv != null) {

            for (int i = 0; i < 10; i++) {

                ((WorldServer) villager.world).a(ReputationEvent.c, liv, villager);
                ((WorldServer) villager.world).a(ReputationEvent.d, liv, villager);
            }
        }
    }

    public boolean isF(Location bed, String type) {
        int niceSpots = 0;
        Block b = null;

        for(int i = 0; i < 2; ++i) {
            //Location n = null;
            if (type.equalsIgnoreCase("ADELANTE")) {
                //n = bed.add((double)i, 0.0D, 0.0D);
                b = b == null ? bed.getBlock().getRelative(BlockFace.NORTH) : b.getRelative(BlockFace.NORTH);
            }

            if (type.equalsIgnoreCase("ATRAS")) {
                b = b == null ? bed.getBlock().getRelative(BlockFace.SOUTH) : b.getRelative(BlockFace.SOUTH);
            }

            if (type.equalsIgnoreCase("DERECHA")) {
                b = b == null ? bed.getBlock().getRelative(BlockFace.WEST) : b.getRelative(BlockFace.WEST);
            }

            if (type.equalsIgnoreCase("IZQUIERDA")) {
                b = b == null ? bed.getBlock().getRelative(BlockFace.EAST) : b.getRelative(BlockFace.EAST);
            }

            if (b.getType().isAir() || b.getType() == Material.AIR || b.getType().createBlockData() instanceof Bed || b.getType() == Material.WHITE_BED || b.getType() == Material.CARTOGRAPHY_TABLE || b.getType() == Material.CRAFTING_TABLE || b.getType() == Material.FLETCHING_TABLE || b.getType() == Material.SMITHING_TABLE || b.getType() == Material.LOOM || b.getType() == Material.BARREL || b.getType() == Material.STONECUTTER || b.getType() == Material.GRINDSTONE || b.getType() == Material.BLAST_FURNACE || b.getType() == Material.COMPOSTER || b.getType() == Material.LECTERN) {
                ++niceSpots;
            }
        }

        return niceSpots == 2;
    }

    private void setupVoidSlots(Player p, boolean clear) {

        if (!clear) {

            ItemStack vacio = new ItemStack(Material.STRUCTURE_VOID);

            if (p.getInventory().getItemInOffHand() == null) {

                p.getInventory().setItemInOffHand(vacio);

                p.updateInventory();
            } else {
                if (p.getInventory().getItemInOffHand().getType() == Material.AIR) {
                    p.getInventory().setItemInOffHand(vacio);
                    p.updateInventory();
                }

                if (p.getInventory().getItemInOffHand().getType() != Material.STRUCTURE_VOID && p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                    p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                    p.updateInventory();

                    p.getInventory().setItemInOffHand(vacio);
                    p.updateInventory();
                }
            }

            if (p.getInventory().getItem(4) == null) {

                p.getInventory().setItem(4, vacio);

                p.updateInventory();
            } else {
                if (p.getInventory().getItem(4).getType() == Material.AIR) {
                    p.getInventory().setItem(4, vacio);

                    p.updateInventory();
                }
                if (p.getInventory().getItem(4).getType() != Material.STRUCTURE_VOID && p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                    p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                    p.updateInventory();

                    p.getInventory().setItem(4, vacio);
                    p.updateInventory();
                }
            }

            if (p.getInventory().getItem(13) == null) {

                p.getInventory().setItem(13, vacio);

                p.updateInventory();
            } else {
                if (p.getInventory().getItem(13).getType() == Material.AIR) {
                    p.getInventory().setItem(13, vacio);

                    p.updateInventory();
                }
                if (p.getInventory().getItem(13).getType() != Material.STRUCTURE_VOID && p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                    p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                    p.updateInventory();

                    p.getInventory().setItem(13, vacio);
                    p.updateInventory();
                }
            }

            if (p.getInventory().getItem(22) == null) {

                p.getInventory().setItem(22, vacio);

                p.updateInventory();
            } else {
                if (p.getInventory().getItem(22).getType() == Material.AIR) {
                    p.getInventory().setItem(22, vacio);

                    p.updateInventory();
                }
                if (p.getInventory().getItem(22).getType() != Material.STRUCTURE_VOID && p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                    p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                    p.updateInventory();

                    p.getInventory().setItem(22, vacio);
                    p.updateInventory();
                }
            }

            if (p.getInventory().getItem(31) == null) {

                p.getInventory().setItem(31, vacio);

                p.updateInventory();
            } else {
                if (p.getInventory().getItem(31).getType() == Material.AIR) {
                    p.getInventory().setItem(31, vacio);

                    p.updateInventory();
                }
                if (p.getInventory().getItem(31).getType() != Material.STRUCTURE_VOID && p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                    p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.5, 0), p.getInventory().getItemInOffHand());
                    p.updateInventory();

                    p.getInventory().setItem(31, vacio);
                    p.updateInventory();
                }
            }
        }

        if (clear) {

            for (int i = 0; i < 36; i++) {

                if (p.getInventory().getItem(i) != null) {

                    if (p.getInventory().getItem(i).getType() == Material.STRUCTURE_VOID) {

                        p.getInventory().setItem(i, null);
                        p.updateInventory();
                    }
                }
            }

            if (p.getInventory().getItemInOffHand() != null) {

                if (p.getInventory().getItemInOffHand().getType() == Material.STRUCTURE_VOID) {

                    p.getInventory().setItemInOffHand(null);
                }
            }
        }
    }

    @Override
    public void onLoad() {

        instance = this;

        NMSFinder f = new NMSFinder(this);

        this.nmsHandler = (NMSHandler) f.getNMSHandler();
        this.nmsAccesor = (NMSAccesor) f.getNMSAccesor();

        this.nmsAccesor.registerHostileMobs();
    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage(format("&f&m------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(format("     &7- Desactivando el Plugin."));
        Bukkit.getConsoleSender().sendMessage(format("&f&m------------------------------------------"));

        this.instance = null;
    }

    private void startPlugin() {

        this.world = Bukkit.getWorld("world");
        this.endWorld = Bukkit.getWorld("world_the_end");

        Bukkit.getConsoleSender().sendMessage(format("&f&m------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(format("         &6&lInfernal&f&lCore"));
        Bukkit.getConsoleSender().sendMessage(format("     &7- Creador: &eSebazCRC"));
        Bukkit.getConsoleSender().sendMessage(format("     &7- Versión: &e" + this.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(format("     &7- Versión del Servidor: &e" + VersionManager.getFormatedVersion()));
        Bukkit.getConsoleSender().sendMessage(format("&f&m------------------------------------------"));
        registerListeners();
        registerChanges();
        generateOfflinePlayerData();
    }

    protected void registerListeners() {

        if (!alreadyRegisteredListeners) {

            /**
            getServer().getPluginManager().registerEvents(new EfectosMob(instance), instance);
            getServer().getPluginManager().registerEvents(new ClasesJockeys(instance), instance);
             */

            registeredDays.put(10, false);
            registeredDays.put(15, false);
            registeredDays.put(20, false);

            alreadyRegisteredListeners = true;

        }

        if (this.eventos == null) {

            this.eventos = new Eventos(instance);
            getServer().getPluginManager().registerEvents(eventos, instance);
        }

        if (getDays >= 10 && !registeredDays.get(10)) {

            // Registros día 10

            this.recipes = new RecipeManager(this);
            recipes.registerRecipes();

            getServer().getPluginManager().registerEvents(new Custom(this), this);
            getServer().getPluginManager().registerEvents(new VoidEvents(this), this);
            getServer().getPluginManager().registerEvents(new EfectosMob(this), this);

            registeredDays.replace(10, true);
        }

        if (getDays >= 15 && !registeredDays.get(15)) {

            if (this.obsidianSet == null) {
                this.obsidianSet = new ObsidianSet(this);
                this.getServer().getPluginManager().registerEvents(obsidianSet, this);
            }

            this.recipes.registerC2();
            registeredDays.replace(15, true);
        }
    }

    protected void registerChanges() {

        if (alreadyRegisteredChanges) return;
        alreadyRegisteredChanges = true;
    }

    public HashMap<String, Integer> getMaldiciones() {
        return maldiciones;
    }

    private void generateOfflinePlayerData() {

        for (OfflinePlayer off : Bukkit.getOfflinePlayers()) {

            if (off == null) return;

            PlayerDataManager manager = new PlayerDataManager(off.getName(), this);
            manager.generateDayData();
        }
    }

    protected String setupWorld() {

        if (Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.MainWorld"))) == null) {

            System.out.println("[ERROR] Error al cargar el mundo principal, esto hará que los Death Train no se presenten.");
            System.out.println("[ERROR] Tan solo ve a config.yml y establece el mundo principal en la opción: MainWorld");

            world = Bukkit.getWorlds().get(0);
            System.out.println("[INFO] El plugin utilizará el mundo " + world.getName() + " como mundo principal.");
            System.out.println("[INFO] Si deseas utilizar otro mundo, configura en el archivo config.yml.");

            return "&eError al cargar mundo.";

        } else {

            world = Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.MainWorld")));
        }

        if (Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.EndWorld"))) == null) {

            System.out.println("[ERROR] Error al cargar el mundo del end, esto hará que el end no funcione como debe.");
            System.out.println("[ERROR] Tan solo ve a config.yml y establece el mundo del end en la opción: EndWorld");

            for (World w : Bukkit.getWorlds()) {

                if (w.getName().endsWith("the_end")) {

                    endWorld = world;
                    System.out.println("[INFO] El plugin utilizará el mundo " + w.getName() + " como mundo del End.");
                }
            }

            if (endWorld == null) {

                System.out.println("[ERROR] El plugin no pudo encontrar un mundo para el End, esto generará errores.");
            }

            return "&eError al cargar mundo.";

        } else {

            endWorld = Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.EndWorld")));
        }

        return "&aCorrecto";
    }

    protected void addDays(CommandSender player, String args1) {

        int nD;

        try {

            int d = Integer.parseInt(args1);

            if (d > 120 || d < 0) {
                nD = 0;

            } else {

                nD = d;
            }

        } catch (NumberFormatException ex) {

            player.sendMessage(instance.format("&cNecesitas ingresar un número válido."));
            return;
        }

        if (nD == 0) {

            player.sendMessage(instance.format("&cHas ingresado un número no válido, o ni siquiera un número."));
            return;
        }

        LocalDate add = fechaActual.minusDays(nD + 1);

        int month = add.getMonthValue();
        int day = add.getDayOfMonth();

        String s;

        if (month < 10) {

            s = add.getYear() + "-0" + month + "-";
        } else {

            s = add.getYear() + "-" + month + "-";
        }

        if (day < 10) {

            s = s + "0" + day;
        } else {

            s = s + day;
        }

        instance.getConfig().set("Fecha", s);
        instance.saveConfig();
        instance.reloadConfig();

        player.sendMessage(instance.format("&eSe han actualizado los días a: &7" + nD));
        player.sendMessage(format("&c&lNota importante: &7Algunos cambios pueden requerir un reinicio y la fecha puede no ser exacta."));

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pdc reload");
    }

    protected void reload(CommandSender player) {

        reloadConfig();
        messages.reloadFiles();
        MessageUtil.setupConfig(this);
        MessageUtil.rl(player, this);
        this.fecha = getConfig().getString("Fecha");
        this.fechaInicio = LocalDate.parse(getConfig().getString("Fecha"));
        this.fechaActual = LocalDate.now();
        this.getDays = this.fechaInicio.until(this.fechaActual, ChronoUnit.DAYS) + 1;
        player.sendMessage(format("&c&lNota importante: &7Algunos cambios pueden requerir un reinicio y la fecha puede no ser exacta."));

        instance.setupWorld();
    }

    public static String format(String texto) {

        return ChatColor.translateAlternateColorCodes('&', texto);
    }

    public Messages getMessages() {
        return messages;
    }

    public NMSHandler getNmsHandler() {
        return nmsHandler;
    }

    public EndDataManager getEndData() {
        return endData;
    }

    public EndTask getTask() {
        return task;
    }

    public EndManager getEndManager() {
        return endManager;
    }

    public void setTask(EndTask task) {
        this.task = task;
    }

    public NMSAccesor getNmsAccesor() {
        return nmsAccesor;
    }

    public long getDays() {
        return getDays;
    }

    public static Main getInstance() {
        return instance;
    }

    public ArrayList<Player> getDoneEffectPlayers() {
        return doneEffectPlayers;
    }

    public void setupConsoleFilter() {
        try {
            Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
            setLog4JFilter();
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            Log4JFilter filter = new Log4JFilter();
            Filter f = (Filter) new Log4JFilter();
            Bukkit.getLogger().setFilter(f);
            Logger.getLogger("Minecraft").setFilter(f);
        }
    }

    private void setLog4JFilter() {
        org.apache.logging.log4j.core.Logger logger;
        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new Log4JFilter());
    }

    public Nightmare getNightmareEvent() {
        return nightmare;
    }

    public static boolean isRunningPaperSpigot() {
        return runningPaperSpigot;
    }

    public boolean isRunningCraftBukkit() {
        return runningCraftBukkit;
    }

    public InfernalDungeon getDungeons() {
        return this.dungeons;
    }

    public ArrayList<InfernalDungeon> getGeneratedDungeons() {

        return this.dungeons.getByList();
    }

    public ObsidianSet getObsidianSet() {
        return obsidianSet;
    }

    public InfernalDungeon getClosestDungeon(Location l) {

        if (!this.dungeons.hasDungeons()) {

            return null;
        }

        InfernalDungeon choosen = getGeneratedDungeons().get(0);

        for (InfernalDungeon d : getGeneratedDungeons()) {

            if (d.getLocation().distance(l) < choosen.getLocation().distance(l)) {

                choosen = d;
            }
        }

        return choosen;
    }

    public void addGhastSpawn() {

        BiomeBase base = BiomeBase.a(Biomes.MUSHROOM_FIELDS);
        base.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.GHAST, 8, 4, 4));

        BiomeBase b = BiomeBase.a(Biomes.MUSHROOM_FIELD_SHORE);
        b.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.GHAST, 8, 4, 4));
    }

    public void startHour() {

        long stormTicks = 3600L * 5;

        boolean weather = world.hasStorm();
        int stormDuration = world.getWeatherDuration();
        int stormTicksToSeconds = stormDuration / 20;
        long stormIncrement = stormTicksToSeconds + stormTicks;
        int intsTicks = (int) stormTicks;
        int inc = (int) stormIncrement;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:weather thunder");

        if (weather) {
            world.setWeatherDuration(inc * 20);
        } else {
            world.setWeatherDuration(intsTicks * 20);
        }
    }
}