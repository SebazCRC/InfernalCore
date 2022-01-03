package com.permadeathcore;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.permadeathcore.End.Task.EndTask;
import com.permadeathcore.End.Util.EndManager;
import com.permadeathcore.End.Util.NMSAccesor;
import com.permadeathcore.Listener.*;
import com.permadeathcore.Manager.EndDataManager;
import com.permadeathcore.Manager.EndWorldManager;
import com.permadeathcore.NMS.EntityRegistry.PeaceToHostileManager;
import com.permadeathcore.NMS.NMSHandler;
import com.permadeathcore.Piglin.EntityManager;
import com.permadeathcore.Piglin.PiglinManager;
import com.permadeathcore.Task.RuletaMortal;
import com.permadeathcore.Util.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public final class Main extends JavaPlugin implements Listener {

    public String fecha;

    //instancia del plugin
    private Eventos eventos;
    private EntityManager manager;

    private NMSHandler nmsHandler;

    public static Main instance;
    // Day Counter
    public LocalDate fechaInicio;
    public LocalDate fechaActual = LocalDate.now();
    private long getDays;
    public String tag = ChatColor.GOLD + "" + ChatColor.BOLD + "Infernal" + ChatColor.WHITE + "" + ChatColor.BOLD + "Core" + ChatColor.GRAY + " >> " + ChatColor.RESET;
    public long stormTicks;
    public long stormHours;
    public World world = Bukkit.getWorld("world");
    public World endWorld = Bukkit.getWorld("world_the_end");

    private EndTask task = null;
    private EndManager endManager;
    private EndDataManager endData;

    private NMSAccesor nmsAccesor;
    private DiamondArmor diamondSet;

    private HashMap<String, Integer> maldiciones = new HashMap<>();
    private HashMap<Player, PotionEffect> efectos = new HashMap<>();

    private boolean loaded = false;

    BossBar bossBar;

    int timeForSkeletonPatrol = 60*10 + 15;
    int timeAviableForSuperZombie = 86400*2; //86 400
    int timeAviableForWitherEvent = 43200;

    int nextNetherCheck = 60;

    int currentColor = 1;
    private boolean canCompleteZombie;
    private boolean isWitherEvent;

    private boolean isReloaded = false;

    private ArrayList<SkeletonRaid> raids = new ArrayList<>();

    @SuppressWarnings("rawtypes")
    @Override
    public void onEnable() {

        this.loaded = false;
        setupConfig();

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

        this.canCompleteZombie = getConfig().getBoolean("Eventos.SuperZombie");

        if (!getConfig().contains("Eventos.WitherStars")) {

            getConfig().set("Eventos.WitherStars", true);
            saveConfig();
            reloadConfig();
        }

        this.isWitherEvent = getConfig().getBoolean("Eventos.WitherStars");

        if (getDays == 0) {

            this.getDays = 1;
        }

        if (getDays < 1) {
            System.out.println("[ERROR] 'Fecha' en el archivo config.yml tiene un valor negativo o incorrecto!");
            System.out.println("[ERROR] La fecha introducida genera un numero negativo!");
        }

        getServer().getPluginManager().registerEvents(this, this);
        instance = this;

        world = Bukkit.getWorld("world");
        endWorld = Bukkit.getWorld("world_the_end");

        this.bossBar = Bukkit.createBossBar("Tiempo", BarColor.RED, BarStyle.SEGMENTED_10);

        if (getDays >= 5 && getDays < 10) {

            this.bossBar = Bukkit.createBossBar(format("&6&lFiebre del Wither "), BarColor.RED, BarStyle.SEGMENTED_10);
        }
        nmsAccesor = new NMSAccesor(instance);
        this.diamondSet = new DiamondArmor(this);

        if (getDays >= 30) {

            endManager = new EndManager(instance);
            getServer().getPluginManager().registerEvents(endManager, instance);

            endData = new EndDataManager(instance);
            getServer().getPluginManager().registerEvents(new EndWorldManager(instance), instance);
        }


        try {
            nmsHandler = (NMSHandler) Class.forName("com.permadeathcore.NMS.Versions.NMSHandler_" + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].substring(1)).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        eventos = new Eventos(instance);
        getServer().getPluginManager().registerEvents(eventos, instance);
        getServer().getPluginManager().registerEvents(diamondSet, instance);
        getServer().getPluginManager().registerEvents(new RaidEvents(this), instance);
        getServer().getPluginManager().registerEvents(new PeaceToHostileManager(this), instance);
        getServer().getPluginManager().registerEvents(new EfectosMob(this), instance);
        getServer().getPluginManager().registerEvents(new VoidEvents(this), instance);
        getServer().getPluginManager().registerEvents(new SpecialSkeletons(this), instance);

        if (isReloaded) {

            System.out.println("[IC] Se ha detectado un reload.");
            this.manager = new EntityManager(instance);
            this.manager.registerEntities();
        }

        try {
            if (Class.forName("org.spigotmc.SpigotConfig") == null) {

                getLogger().info("[INFO] Este servidor utiliza CraftBukkit, recomendamos cambiar a Spigot/PaperMC");
            }
        } catch (ClassNotFoundException e) {

            getLogger().info("[INFO] Este servidor utiliza CraftBukkit, recomendamos cambiar a Spigot/PaperMC");
        }

        registerNR();
        registerDB();
        registerDC();
        registerDH();
        registerDL();
        registerSpecialBook();
        registerSpecialShield();
        registerTrident();

        Bukkit.getConsoleSender().sendMessage(format("&eSe han registrado las recetas especiales."));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {

                if (!loaded) {

                    if (manager != null) {

                        getServer().getPluginManager().registerEvents(manager, instance);
                        loaded = true;
                    }
                }

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

                for (Player player : Bukkit.getOnlinePlayers()) {

                    boolean f = false;
                    for (String k : getMaldiciones().keySet()) {

                        String name = k.split(";")[0];

                        if (name.contains(player.getName())) {

                            f = true;
                        }
                    }

                    if (!f) {
                        setupVoidSlots(player, true);
                    }
                }

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
                            te = " - &eTiempo de Bloqueo de Inventario: &b" + (hrs > 9 ? hrs : "0" + hrs) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);

                            if (!world.hasStorm()) {

                                te = "&eTiempo de Bloqueo de Inventario: &b" + (hrs > 9 ? hrs : "0" + hrs) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);
                            }
                        }
                    }

                    String Message = instance.getConfig().getString("Server-Messages.ActionBarMessage");

                    if (world.hasStorm()) {

                        on.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.replace("%tiempo%", time)) + format(te)));
                    }

                    if (!world.hasStorm() && contains) {

                        on.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(format(te)));
                    }
                }

                if (getDays >= 5) {

                    if (isWitherEvent) {

                        if (timeAviableForWitherEvent > 0) {

                            for (Player all : Bukkit.getOnlinePlayers()) {

                                if (!getBossBar().getPlayers().contains(all)) {

                                    getBossBar().addPlayer(all);
                                }
                            }

                            int res = timeAviableForWitherEvent;

                            int hrs = res / 3600;
                            int minAndSec = res % 3600;
                            int min = minAndSec / 60;
                            int sec = minAndSec % 60;

                            String tiempo = (hrs > 9 ? hrs : "0" + hrs) + "h " + (min > 9 ? min : "0" + min) + "min " + (sec > 9 ? sec : "0" + sec) + "seg";


                            if (currentColor == 1) {
                                bossBar.setTitle(format("&6&lFiebre del Wither &r&6&n" + tiempo));
                                currentColor = 2;
                            }

                            if (currentColor == 2) {
                                bossBar.setTitle(format("&6&lFiebre del Wither &e&l&n" + tiempo));
                                currentColor = 3;
                            }

                            if (currentColor == 3) {
                                bossBar.setTitle(format("&6&lFiebre del Wither &f&l&n" + tiempo));
                                currentColor = 4;
                            }

                            if (currentColor == 4) {
                                bossBar.setTitle(format("&6&lFiebre del Wither &b&l&n" + tiempo));
                                currentColor = 5;
                            }

                            if (currentColor == 5) {
                                bossBar.setTitle(format("&6&lFiebre del Wither &e&l&n" + tiempo));
                                currentColor = 6;
                            }

                            if (currentColor == 6) {
                                bossBar.setTitle(format("&6&lFiebre del Wither &e&l&n" + tiempo));
                                currentColor = 1;
                            }

                            timeAviableForWitherEvent = timeAviableForWitherEvent - 1;
                        }

                        if (timeAviableForWitherEvent == 0) {

                            for (Player player : bossBar.getPlayers()) {

                                bossBar.removePlayer(player);
                            }

                            Bukkit.broadcastMessage(format("&c&lHa acabado el evento de Fiebre del Wither"));

                            isWitherEvent = false;
                            getConfig().set("Eventos.WitherStars", false);
                            saveConfig();
                            reloadConfig();

                            for (Player on : Bukkit.getOnlinePlayers()) {
                                on.playSound(on.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                            }
                        }
                    }
                }

                if (getDays >= 8) {

                    if (nextNetherCheck > 0) {

                        nextNetherCheck--;
                    }

                    if (nextNetherCheck == 0) {

                        nextNetherCheck = 60;

                        if (Bukkit.getWorld("world_nether").getPlayers().size() >= 1) {

                            for (Player player : Bukkit.getWorld("world_nether").getPlayers()) {

                                int random = new Random().nextInt(9999) + 1;

                                if (random == 5) {

                                    player.setFireTicks(15 * 20);
                                }
                            }
                        }
                    }
                }

                if (getDays >= 8) {

                    if (canCompleteZombie) {

                        if (timeAviableForSuperZombie > 0) {

                            int res = timeAviableForSuperZombie;

                            int hrs = res / 3600;
                            int minAndSec = res % 3600;
                            int min = minAndSec / 60;
                            int sec = minAndSec % 60;

                            String tiempo = (hrs > 9 ? hrs : "0" + hrs) + "h " + (min > 9 ? min : "0" + min) + "min " + (sec > 9 ? sec : "0" + sec) + "seg";

                            for (Player all : Bukkit.getOnlinePlayers()) {

                                if (!getBossBar().getPlayers().contains(all)) {

                                    getBossBar().addPlayer(all);
                                }
                            }

                            bossBar.setTitle(format("&e&lEVENTO ESPECIAL: &6&n" + tiempo));

                            timeAviableForSuperZombie = timeAviableForSuperZombie - 1;
                        }

                        if (timeAviableForSuperZombie == 0) {

                            for (Player player : bossBar.getPlayers()) {

                                bossBar.removePlayer(player);
                            }

                            Bukkit.broadcastMessage(format("&c&lSe ha acabado el Tiempo para el &6&nOrbe Bendito"));


                            canCompleteZombie = false;
                            getConfig().set("Eventos.SuperZombie", false);
                            saveConfig();
                            reloadConfig();

                            for (Player on : Bukkit.getOnlinePlayers()) {
                                on.playSound(on.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                            }
                        }
                    }
                }

                if (getDays >= 9) {

                    ArrayList<Player> attackList = new ArrayList<>();

                    if (timeForSkeletonPatrol > 0) {

                        timeForSkeletonPatrol = timeForSkeletonPatrol - 1;
                    }

                    if (timeForSkeletonPatrol == 60 * 20) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &620 &eminutos."));
                    }

                    if (timeForSkeletonPatrol == 60 * 10) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &610 &eminutos."));
                    }

                    if (timeForSkeletonPatrol == 60 * 5) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &65 &eminutos."));
                    }

                    if (timeForSkeletonPatrol == 60) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &61 &eminuto."));
                    }

                    if (timeForSkeletonPatrol == 30) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &630 &esegundos."));

                        for (Player online : Bukkit.getOnlinePlayers()) {

                            online.playSound(online.getLocation(), Sound.UI_BUTTON_CLICK, 5.0F, 5.0F);
                        }
                    }

                    if (timeForSkeletonPatrol == 15) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &615 &esegundos."));

                        for (Player online : Bukkit.getOnlinePlayers()) {

                            online.playSound(online.getLocation(), Sound.UI_BUTTON_CLICK, 5.0F, 5.0F);
                        }
                    }

                    if (timeForSkeletonPatrol < 11 && timeForSkeletonPatrol > 1) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &6" + timeForSkeletonPatrol + " &esegundos."));

                        for (Player online : Bukkit.getOnlinePlayers()) {

                            online.playSound(online.getLocation(), Sound.UI_BUTTON_CLICK, 5.0F, 5.0F);
                        }
                    }

                    if (timeForSkeletonPatrol == 1) {

                        Bukkit.broadcastMessage(format("&c&lANUNCIO >> &eSe aproxima una Skeleton Raid en &6" + timeForSkeletonPatrol + " &esegundo."));

                        for (Player online : Bukkit.getOnlinePlayers()) {

                            online.playSound(online.getLocation(), Sound.UI_BUTTON_CLICK, 5.0F, 5.0F);
                        }
                    }


                    if (timeForSkeletonPatrol == 0) {

                        int randomTime = new Random().nextInt(3);
                        timeForSkeletonPatrol = 60 * 30;

                        if (randomTime == 0) {

                            timeForSkeletonPatrol = 60 * 20;
                            Bukkit.broadcastMessage(format("&eEl próximo ataque será en &620 &eminutos."));
                        }

                        if (randomTime == 1) {

                            timeForSkeletonPatrol = 60 * 30;
                            Bukkit.broadcastMessage(format("&eEl próximo ataque será en &630 &eminutos."));
                        }

                        if (randomTime == 2 || randomTime == 3) {

                            timeForSkeletonPatrol = 60 * 60;
                            Bukkit.broadcastMessage(format("&eEl próximo ataque será en &61 &ehora."));
                        }

                        for (Player on : Bukkit.getOnlinePlayers()) {

                            boolean contains = false;

                            if (on.getInventory().getItemInOffHand() != null) {

                                if (on.getInventory().getItemInOffHand().getType() == Material.LIGHT_BLUE_DYE && on.getInventory().getItemInOffHand().getItemMeta().isUnbreakable()) {

                                    contains = true;
                                }
                            }

                            for (ItemStack s : on.getInventory().getContents()) {

                                if (s != null) {

                                    if (s.getType() == Material.LIGHT_BLUE_DYE && s.getItemMeta().isUnbreakable()) {

                                        contains = true;
                                    }
                                }
                            }

                            if (contains) {

                                if (new Random().nextInt(4) == 1) {

                                    attackList.add(on);
                                }
                            } else {


                                attackList.add(on);
                            }
                        }

                        if (attackList.isEmpty()) {

                            Bukkit.broadcastMessage(format("&6SKELETON RAIDS >> &eNingún jugador será atacado"));
                            return;
                        }


                        int current = 0;
                        String jugadores = "&b";

                        for (Player player : attackList) {

                            if (current == 0) {

                                jugadores = player.getName();
                            }

                            if (current == attackList.size()) {

                                jugadores = jugadores + player.getName() + ".";
                            } else {

                                if (current > 0) {

                                    jugadores = jugadores + ", " + player.getName();
                                }
                            }

                            current++;
                        }

                        Bukkit.broadcastMessage(format("&6&lSKELETON RAIDS >> &eLos jugadores atacados serán: " + jugadores));

                        for (Player on : attackList) {


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

                            raids.add(new SkeletonRaid(instance, loc, on));
                        }

                        attackList.clear();
                    }
                }
            }
        }, 0, 20L);
    }

    private void registerNR() {

        ItemStack s = crearReliquiaN();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( " R ", "RSR", " R " );
        recipe.setIngredient('S', Material.BOOK);
        recipe.setIngredient( 'R', Material.BLAZE_ROD);
        getServer().addRecipe(recipe);
    }

    private void registerDH() {

        ItemStack s = diamondSet.craftHelmet();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( "DRD", "DAD", "DTD" );
        recipe.setIngredient('R', Material.ORANGE_DYE);
        recipe.setIngredient( 'D', Material.DIAMOND);
        recipe.setIngredient( 'T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient( 'A', Material.DIAMOND_HELMET);
        getServer().addRecipe(recipe);
    }

    private void registerDC() {

        ItemStack s = diamondSet.craftChestplate();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( "DRD", "DAD", "DTD" );
        recipe.setIngredient('R', Material.ORANGE_DYE);
        recipe.setIngredient( 'D', Material.DIAMOND);
        recipe.setIngredient( 'T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient( 'A', Material.DIAMOND_CHESTPLATE);
        getServer().addRecipe(recipe);
    }

    private void registerDL() {

        ItemStack s = diamondSet.craftLegs();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( "DRD", "DAD", "DTD" );
        recipe.setIngredient('R', Material.ORANGE_DYE);
        recipe.setIngredient( 'D', Material.DIAMOND);
        recipe.setIngredient( 'T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient( 'A', Material.DIAMOND_LEGGINGS);
        getServer().addRecipe(recipe);
    }

    private void registerDB() {

        ItemStack s = diamondSet.craftBoots();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( "DRD", "DAD", "DTD" );
        recipe.setIngredient('R', Material.ORANGE_DYE);
        recipe.setIngredient( 'D', Material.DIAMOND);
        recipe.setIngredient( 'T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient( 'A', Material.DIAMOND_BOOTS);
        getServer().addRecipe(recipe);
    }

    private void registerTrident() {

        ItemStack s = new ItemBuilder(Material.TRIDENT).setDisplayName(format("&6Tridente Mejorado")).addEnchant(Enchantment.RIPTIDE, 5).setUnbrekeable(true).build();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( " D ", " R ", "GGG" );
        recipe.setIngredient('R', Material.TRIDENT);
        recipe.setIngredient( 'D', Material.ORANGE_DYE);
        recipe.setIngredient( 'G', Material.GOLD_BLOCK);
        getServer().addRecipe(recipe);
    }

    private void registerSpecialBook() {

        ItemStack s = new ItemBuilder(Material.BOOK).setDisplayName(format("&6Libro del Conocimiento")).setUnbrekeable(true).build();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( "BBB", "B B", "BBB" );
        recipe.setIngredient('B', Material.BOOKSHELF);
        getServer().addRecipe(recipe);
    }

    private void registerSpecialShield() {

        ItemStack s = new ItemBuilder(Material.SHIELD).setDisplayName(format("&6&lEscudo Reforzado")).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbrekeable(true).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();

        ShapedRecipe recipe = new ShapedRecipe(s);
        recipe.shape( "ISI", "IRI", " I " );
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('S', Material.SHIELD);
        recipe.setIngredient('R', Material.ORANGE_DYE);
        getServer().addRecipe(recipe);
    }

    public DiamondArmor getDiamondSet() {
        return diamondSet;
    }

    @Override
    public void onLoad() {

        setupConfig();
        if (this.manager == null) {

            this.manager = new EntityManager(this);
        }
        this.manager.registerEntities();
    }

    @Override
    public void onDisable() {

        saveConfig();
        reloadConfig();

        manager.getPiglin().unregister();

        this.isReloaded = true;
    }

    public EntityManager getManager() {
        return manager;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public int getTimeForSkeletonPatrol() {
        return timeForSkeletonPatrol;
    }

    public int getTimeAviableForSuperZombie() {
        return timeAviableForSuperZombie;
    }

    public boolean isCanCompleteZombie() {
        return canCompleteZombie;
    }

    public ArrayList<SkeletonRaid> getRaids() {
        return raids;
    }

    private void setupConfig() {

        File f = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

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

        FileAPI.UtilFile c = FileAPI.select(this, f, config);
        c.create("config.yml", true);

        if (config.getString("Fecha").isEmpty()) {

            config.set("Fecha", s);
            reloadConfig();
        }

        c.set("Fecha", s);

        c.set("ban-enabled", true);
        c.set("Server-Messages.coords-msg-enable", true);
        c.set("Server-Messages.OnJoin", "&e%player% se ha unido al servidor.");
        c.set("Server-Messages.OnLeave", "&e%player% ha abandonado el servidor.");
        c.set("Server-Messages.StormEnd", "&cLa tormenta ha llegado a su fin.");
        c.set("Server-Messages.Sleep", "&7%player% &efue a dormir.");
        c.set("Server-Messages.DeathMessageTitle", "&c¡Permadeath!");
        c.set("Server-Messages.DeathMessageSubtitle", "%player% ha muerto");
        c.set("Server-Messages.DeathMessageChat", "&c&lEl comienzo del sufrimiento eterno de &4&l%player% &c&lha comenzado ¡HA SIDO PERMABANEADO!");
        c.set("Server-Messages.DeathTrainMessage", "&c¡Comienza el Death Train con duración de %tiempo% horas!");
        c.set("Server-Messages.ActionBarMessage", "&7Quedan %tiempo% de tormenta");
        c.set("Server-Messages.DefaultDeathMessage", "&7%player% ha muerto, y ahora descanza en paz.");
        c.set("Server-Messages.CustomDeathMessages.SebazCRC", "&7Tranquilos no murió, solamente está probando");
        c.set("Server-Messages.CustomDeathMessages.vo1d_dev", "&7Tranquilos no murió, solamente está probando");
        c.set("Server-Messages.CustomDeathMessages.ElRichMC", "&7Eso no ha sido muy eyeyey de tu parte.");
        c.set("Eventos.SuperZombie", true);
        c.set("Eventos.WitherStars", true);
        c.set("TotemFail.Enable", true);
        c.set("TotemFail.ChatMessage", "&7¡El tótem de &c%player% &7ha fallado!");
        c.set("TotemFail.ChatMessageTotems", "&7¡Los tótems de &c%player% &7han fallado!");
        c.set("TotemFail.NotEnoughTotems", "&7¡%player% no tenía suficientes tótems en el inventario!");
        c.set("TotemFail.PlayerUsedTotemMessage", "&7El jugador %player% ha consumido un tótem (Probabilidad: %totem_fail% %porcent% %number%)");
        c.set("TotemFail.PlayerUsedTotemsMessage", "&7El jugador %player% ha usado {ammount} tótems (Probabilidad: %totem_fail% %porcent% %number%)");
        c.set("Worlds.MainWorld", "world");
        c.set("Worlds.EndWorld", "world_the_end");
        c.set("Helmet", 15);
        c.set("Chestplate", 15);
        c.set("Leggings", 15);
        c.set("Boots", 15);

        c.save();
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

    public void setNmsAccesor(NMSAccesor nmsAccesor) {
        this.nmsAccesor = nmsAccesor;
    }

    public HashMap<String, Integer> getMaldiciones() {
        return maldiciones;
    }

    public HashMap<Player, PotionEffect> getEfectos() {
        return efectos;
    }

    public boolean isWitherEvent() {
        return isWitherEvent;
    }

    public static int dias;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        //Comando por argumentos
        if (command.getName().equalsIgnoreCase("infernalcore") || command.getName().equalsIgnoreCase("ic")) {
            if (sender instanceof Player) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("awake")) {
                        int timeAwake = player.getStatistic(Statistic.TIME_SINCE_REST) / 20;
                        player.sendMessage(tag + ChatColor.RED + "Tiempo despierto: " + ChatColor.GRAY + timeAwake + "s");

                    } else if (args[0].equalsIgnoreCase("girarruleta")) {

                        if (player.isOp()) {

                            new RuletaMortal(this, player).runTaskTimer(this, 20L, 20L);
                        }

                    } else if (args[0].equalsIgnoreCase("mensaje")) {

                        if (args.length == 1) {

                            player.sendMessage(format("&cDebes escribir un mensaje, ejemplo: /ic mensaje He muerto"));

                            if (getConfig().contains("Server-Messages.CustomDeathMessages." + player.getName())) {

                                player.sendMessage(format("&eTu mensaje de muerte actual es: &7" + getConfig().getString("Server-Messages.CustomDeathMessages." + player.getName())));
                            } else {

                                player.sendMessage(format("&eTu mensaje de muerte actual es: &7" + getConfig().getString("Server-Messages.DefaultDeathMessage")));
                            }

                            return false;
                        }

                        String msg = "";

                        for (int i = 0; i < args.length; i++) {
                            if (!args[i].equalsIgnoreCase(args[0])) {

                                String s = args[i];
                                msg = msg + " " + s;
                            }
                        }

                        if (msg.contains("&")) {

                            player.sendMessage(ChatColor.RED + "No se admite el uso de " + ChatColor.GOLD + "&");
                            return false;
                        }

                        getConfig().set("Server-Messages.CustomDeathMessages." + player.getName(), "&7" + msg);
                        saveConfig();
                        reloadConfig();

                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, -5);
                        player.sendMessage(format("&eHas cambiado tu mensaje de muerte a: &7" + msg));

                    } else if (args[0].equalsIgnoreCase("summonraid")) {

                        if (player.isOp()) {

                            Player on = player;

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

                                        found = true;
                                        altura = i;
                                    }
                                }
                            }

                            loc.setY(altura + 2);

                            raids.add(new SkeletonRaid(instance, loc, on));

                        }
                    } else if (args[0].equalsIgnoreCase("tiempoWither")) {

                        if (player.isOp()) {

                            int time = Integer.parseInt(args[1]);
                            this.timeAviableForWitherEvent = time;
                        }
                    } else if (args[0].equalsIgnoreCase("tiempoRaids")) {

                        if (player.isOp()) {

                            int time = Integer.parseInt(args[1]);
                            this.timeForSkeletonPatrol = time;
                        }
                    } else if (args[0].equalsIgnoreCase("tiempoZombie")) {

                        if (player.isOp()) {

                            int time = Integer.parseInt(args[1]);
                            this.timeAviableForSuperZombie = time;
                        }

                    } else if (args[0].equalsIgnoreCase("raid")) {

                        ArrayList<SkeletonRaid> sraid = new ArrayList<>();

                        int raidInt = 0;

                        for (SkeletonRaid raids : getRaids()) {

                            if (raids.getPlayers().contains(player)) {

                                sraid.add(raids);
                                raidInt++;
                            }
                        }

                        if (raidInt == 0) {

                            player.sendMessage(format("&cNo te encuentras en ninguna Skeleton Raid."));
                            return false;
                        }

                        SkeletonRaid raid = sraid.get(0);

                        if (raidInt > 1) {

                            if (args.length == 1) {

                                player.sendMessage(format("&cTe encuentras en varias Raids, introduce un número"));
                                player.sendMessage(format("&eEjemplo: &7/ic raid &b&l<1-" + raidInt + ">"));

                                return false;
                            }

                            try {

                                int number = Integer.parseInt(args[1]);
                                int choosen = 0;

                                if (number == 0) {

                                    raid = sraid.get(number);

                                    choosen = 0;
                                    return false;
                                }

                                choosen = number - 1;
                                raid = sraid.get(choosen);

                            } catch (NumberFormatException x) {

                                player.sendMessage(format("&cDebes escribir un número válido."));
                            }
                        }

                        player.sendMessage(format("&f&m-------------------------------------"));
                        player.sendMessage(format("&eRaid ID: &d&l" + raid.getRaidID()));
                        player.sendMessage(format("&eEstado: &7" + (raid.getCurrentState() == RaidState.FIGHTING ? "Luchando" : "Esperando...")));
                        player.sendMessage(format("&eJugadores: &7" + raid.getPlayers().size()));
                        player.sendMessage(format("&eRestantes: &7" + raid.getRaiders().size()));
                        player.sendMessage(format("&eOleada: &7" + raid.getCurrentPhase()));
                        player.sendMessage(format("&f&m-------------------------------------"));

                    }else if (args[0].equalsIgnoreCase("condena")) {

                        if (args.length == 1) {

                            player.sendMessage(format("&cEl uso correcto para el comando es: &7/condena <jugador>"));
                            return false;
                        }

                        if (args.length >= 2) {

                            String name = args[1];

                            if (Bukkit.getPlayer(name) == null) {

                                player.sendMessage(format("&cNo hemos encontrado el jugador &e&n" + name));
                                return false;
                            }

                            Player j = Bukkit.getPlayer(name);

                            if (!j.isOnline() || j.isDead()) {

                                player.sendMessage(format("&cEse jugador no está en línea o está muerto."));
                                return false;
                            }

                            boolean found = false;

                            String cooldown = "";
                            ArrayList<String> msg = new ArrayList<>();

                            for (String k : getMaldiciones().keySet()) {

                                String pn = k.split(";")[0];
                                Maldicion maldicion = Maldicion.valueOf(k.split(";")[1]);

                                if (pn.contains(name)) {

                                    found = true;

                                    int seconds = getMaldiciones().get(k);
                                    int hours = seconds / 3600;
                                    int minAndSec = seconds % 3600;
                                    int min = minAndSec / 60;
                                    int sec = minAndSec % 60;

                                    cooldown = (hours > 9 ? hours : "0" + hours) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);

                                    if (maldicion == Maldicion.INVENTORY_LOCK) {

                                        msg.add(format("&7Bloqueo de inventario &f&l- &e" + cooldown));
                                    }

                                    if (maldicion == Maldicion.HP_LOST) {

                                        msg.add(format("&7Pérdida de vida &f&l- &e" + cooldown));
                                    }
                                }
                            }

                            if (!found) {

                                player.sendMessage(format("&cEse jugador no cumple ninguna condena especial, como Bloqueo del inventario o Pérdida de vida."));
                                return false;
                            }

                            player.sendMessage(format("&6Condenas de " + name));

                            for (String s : msg) {

                                player.sendMessage(s);
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("duracion")) {
                        boolean weather = world.hasStorm(); // Variable que detecta si hay tormenta
                        int stormDurationTick = world.getWeatherDuration();
                        int stormTicksToSeconds = stormDurationTick / 20;

                        int seconds = stormTicksToSeconds;

                        if (weather) {
                            if (seconds < 86400) {

                                LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                                String time = timeOfDay.toString();

                                player.sendMessage(tag + ChatColor.RED + "Quedan " + ChatColor.GRAY + time);

                            } else {
                                dias = 0;
                                // EN CASO DE SUPERAR LAS 24H se contara con dias + horas (ej. 1d 01:58:02)
                                while (seconds > 86400) {
                                    seconds -= 86400;
                                    dias += 1;
                                }

                                LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                                String time = timeOfDay.toString();

                                player.sendMessage(tag + ChatColor.RED + "Quedan " + ChatColor.GRAY + dias + "d " + time);
                            }
                        } else {
                            player.sendMessage(tag + ChatColor.RED + "¡No hay ninguna tormenta en marcha!");
                        }
                    } else if (args[0].equalsIgnoreCase("banner")) {

                        if (player.isOp()) {

                            ItemStack b = player.getInventory().getItemInMainHand();
                            BannerMeta meta = (BannerMeta) b.getItemMeta();

                            int current = 0;
                            for (Pattern pattern : meta.getPatterns()) {
                                current++;
                                player.sendMessage(format("&b#" + current + " &ePatter Type: &7" + pattern.getPattern().toString() + " &eColor: &7" + pattern.getColor().toString()));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("reload")) {

                        if (player.hasPermission("permadeathcore.reload")) {

                            long old = getDays;

                            reloadConfig();

                            player.sendMessage(format("&aSe ha recargado el archivo de configuración."));

                            setupConfig();

                            this.fecha = getConfig().getString("Fecha");
                            this.fechaInicio = LocalDate.parse(getConfig().getString("Fecha"));
                            this.fechaActual = LocalDate.now();
                            this.getDays = this.fechaInicio.until(this.fechaActual, ChronoUnit.DAYS);

                            if (getDays != old) {

                                player.sendMessage(format("&eSe han actualizado los días a: &7" + getDays));
                            }

                            if (Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.MainWorld"))) == null) {

                                System.out.println("[ERROR] Error al cargar el mundo principal, esto hará que los Death Train no se presenten.");
                                System.out.println("[ERROR] Tan solo ve a config.yml y establece el mundo principal en la opción: MainWorld");

                                world = Bukkit.getWorlds().get(0);
                                System.out.println("[INFO] El plugin utilizará el mundo " + world.getName() + " como mundo principal.");
                                System.out.println("[INFO] Si deseas utilizar otro mundo, configura en el archivo config.yml.");

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
                            }

                        } else {

                            player.sendMessage(format("&cNo tienes permiso para utilizar este comando."));
                        }
                    } else if (args[0].equalsIgnoreCase("dias")) {
                        if (getDays < 1) {
                            player.sendMessage(tag + ChatColor.DARK_RED + "[ERROR!] Se ha producido un error al cargar el dia, config.yml mal configurado.");
                        } else {
                            player.sendMessage(tag + ChatColor.RED + "Estamos en el dia: " + ChatColor.GRAY + getDays);
                        }
                    } else if (args[0].equalsIgnoreCase("info")) {
                        player.sendMessage(tag + ChatColor.RED + "Version Info:");
                        player.sendMessage(ChatColor.GRAY + "- Nombre: " + ChatColor.GREEN + "PermaDeathCore.jar");
                        player.sendMessage(ChatColor.GRAY + "- Versión: " + ChatColor.GREEN + "PermaDeathCore 1.0.3 Spigot");
                        player.sendMessage(ChatColor.GRAY + "- Dificultades: " + ChatColor.GREEN + "Soportado de dia 1 a día 25");
                        player.sendMessage(ChatColor.GRAY + "- Creadores: " + ChatColor.GREEN + "vo1d_dev & SebazCRC");
                        player.sendMessage(ChatColor.GRAY + "- Página web: " + ChatColor.GREEN + "http://permadeathcore.com/");
                    } else if (args[0].equalsIgnoreCase("discord")) {
                        player.sendMessage(tag + ChatColor.BLUE + "https://discord.gg/EPtg96t");
                    }
                } else {
                    sender.sendMessage(tag + ChatColor.RED + "Comandos disponibles:");
                    sender.sendMessage(ChatColor.RED + "/infernalcore dias " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra el dia en el que esta el plugin)");
                    sender.sendMessage(ChatColor.RED + "/infernalcore reload " + ChatColor.GRAY + ChatColor.ITALIC + "(Recarga el archivo config.yml)");
                    sender.sendMessage(ChatColor.RED + "/infernalcore cambios <día> " + ChatColor.GRAY + ChatColor.ITALIC + "(Te otorga la lista de cambios para cierto día.)");
                    sender.sendMessage(ChatColor.RED + "/infernalcore awake " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra el tiempo despierto)");
                    sender.sendMessage(ChatColor.RED + "/infernalcore duracion " + ChatColor.GRAY + ChatColor.ITALIC + "(Muestra la duracion de la tormenta)");
                    sender.sendMessage(ChatColor.RED + "/infernalcore info " + ChatColor.GRAY + ChatColor.ITALIC + "(Información general)");
                    sender.sendMessage(ChatColor.RED + "/infernalcore discord " + ChatColor.GRAY + ChatColor.ITALIC + "(Discord oficial del plugin)");
                }
            }
        }
        return false;
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

    public boolean esReliquiaN(ItemStack stack) {

        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        if (stack.getType() == Material.LIGHT_BLUE_DYE && stack.getItemMeta().getDisplayName().endsWith(format("&6Reliquia Del Fin"))) {
            return true;
        }
        return false;
    }

    public boolean esOrbeBendito(ItemStack stack) {

        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        if (stack.getType() == Material.ORANGE_DYE && stack.getItemMeta().getDisplayName().contains(format("&6Orbe Bendito"))) {
            return true;
        }

        return false;
    }

    public ItemStack crearReliquiaN() {

        ItemStack s = new ItemBuilder(Material.ORANGE_DYE).setDisplayName(format("&6Reliquia del Nether")).build();

        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}")));
        s.setItemMeta(meta);

        return s;
    }

    public ItemStack crearOrbeBendito() {

        ItemStack s = new ItemBuilder(Material.LIGHT_BLUE_DYE).setDisplayName(format("&6Orbe Bendito")).build();

        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}")));
        s.setItemMeta(meta);

        return s;
    }

    public static String format(String texto) {

        return ChatColor.translateAlternateColorCodes('&', texto);
    }

    public long getDays() {
        return getDays;
    }

    public static Main getInstance() {
        return instance;
    }

    public ItemStack createNetheriteSword() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(format("&6Espada de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public ItemStack createNetheritePickaxe() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayName(format("&6Pico de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public ItemStack createNetheriteHoe() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_HOE).setDisplayName(format("&6Azada de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public ItemStack createNetheriteAxe() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_AXE).setDisplayName(format("&6Hacha de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public ItemStack createNetheriteShovel() {

        ItemStack s = new ItemBuilder(Material.DIAMOND_SHOVEL).setDisplayName(format("&6Pala de Netherite")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        return s;
    }

    public ItemStack crearInfernalNetherite() {

        ItemStack s = new ItemBuilder(Material.DIAMOND).setDisplayName(format("&6Infernal Netherite Block")).build();
        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(HiddenStringUtils.encodeString("{" + UUID.randomUUID().toString() + ": 0}")));
        s.setItemMeta(meta);

        return s;
    }
}