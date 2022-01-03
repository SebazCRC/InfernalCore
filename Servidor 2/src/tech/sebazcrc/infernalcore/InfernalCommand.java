package tech.sebazcrc.infernalcore;

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.infernalcore.Configurations.Language;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.sebazcrc.infernalcore.Custom.InfernalDungeon;
import tech.sebazcrc.infernalcore.Manager.Data.PlayerDataManager;
import tech.sebazcrc.infernalcore.Task.Ruleta;
import tech.sebazcrc.infernalcore.Util.Item.CustomItems;
import tech.sebazcrc.infernalcore.Util.Item.ItemBuilder;
import tech.sebazcrc.infernalcore.Util.Maldicion;
import tech.sebazcrc.infernalcore.Util.Messages.MessageUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.SplittableRandom;

import static tech.sebazcrc.infernalcore.Main.instance;

public class InfernalCommand implements CommandExecutor {
    
    private Main instance;

    public InfernalCommand(Main instance) {
        this.instance = instance;
    }

    public static int dias;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Comando por argumentos
        if (command.getName().equalsIgnoreCase("ic")) {

            World world = instance.world;
            World endWorld = instance.endWorld;

            CommandSender player = sender;

            if (args.length == 0) {

                sendHelp(player);
                return false;
            }

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("awake")) {

                    if (player instanceof Player) {

                        int timeAwake = ((Player) player).getStatistic(Statistic.TIME_SINCE_REST) / 20;
                        player.sendMessage(instance.tag + ChatColor.RED + "Tiempo despierto: " + ChatColor.GRAY + MessageUtil.convertSeconds(timeAwake));
                    }

                } else if (args[0].equalsIgnoreCase("duracion")) {
                    boolean weather = world.hasStorm();
                    int stormDurationTick = world.getWeatherDuration();
                    int stormTicksToSeconds = stormDurationTick / 20;

                    int seconds = stormTicksToSeconds;

                    if (weather) {
                        if (seconds < 86400) {

                            LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                            String time = timeOfDay.toString();

                            player.sendMessage(instance.tag + ChatColor.RED + "Quedan " + ChatColor.GRAY + time);

                        } else {
                            dias = 0;
                            while (seconds > 86400) {
                                seconds -= 86400;
                                dias += 1;
                            }

                            LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                            String time = timeOfDay.toString();

                            player.sendMessage(instance.tag + ChatColor.RED + "Quedan " + ChatColor.GRAY + dias + "d " + time);
                        }
                    } else {
                        player.sendMessage(instance.tag + ChatColor.RED + "¡No hay ninguna tormenta en marcha!");
                    }

                } else if (args[0].equalsIgnoreCase("addLoc")) {

                    if (player.hasPermission("infernalcore.addLoc")) {

                        instance.getDungeons().addToFile(((Player)player).getLocation());
                    }

                } else if (args[0].equalsIgnoreCase("dungeonList")) {

                    if (player instanceof Player) {

                        Player p = (Player) player;

                        if (p.isOp()) {

                            for (int i = 0; i < instance.getGeneratedDungeons().size(); i++) {

                                int s = i + 1;

                                int x = (int) instance.getGeneratedDungeons().get(i).getLocation().getX();
                                int y = (int) instance.getGeneratedDungeons().get(i).getLocation().getY();
                                int z = (int) instance.getGeneratedDungeons().get(i).getLocation().getZ();

                                p.sendMessage(instance.format("&eDungeon #" + s + "&7: &bx:" + x + " y:" + y + " z:" + z));
                            }
                        }

                    }
                } else if (args[0].equalsIgnoreCase("condena")) {

                    if (args.length == 1) {

                        player.sendMessage(instance.format("&cEl uso correcto para el comando es: &7/condena <jugador>"));
                        return false;
                    }

                    if (args.length >= 2) {

                        String name = args[1];

                        if (Bukkit.getPlayer(name) == null) {

                            player.sendMessage(instance.format("&cNo hemos encontrado el jugador &e&n" + name));
                            return false;
                        }

                        Player j = Bukkit.getPlayer(name);

                        if (!j.isOnline() || j.isDead()) {

                            player.sendMessage(instance.format("&cEse jugador no está en línea o está muerto."));
                            return false;
                        }

                        boolean found = false;

                        String cooldown = "";
                        ArrayList<String> msg = new ArrayList<>();

                        for (String k : instance.getMaldiciones().keySet()) {

                            String pn = k.split(";")[0];
                            Maldicion maldicion = Maldicion.valueOf(k.split(";")[1]);

                            if (pn.contains(name)) {

                                found = true;

                                int seconds = instance.getMaldiciones().get(k);
                                int hours = seconds / 3600;
                                int minAndSec = seconds % 3600;
                                int min = minAndSec / 60;
                                int sec = minAndSec % 60;

                                cooldown = (hours > 9 ? hours : "0" + hours) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);

                                if (maldicion == Maldicion.INVENTORY_LOCK) {

                                    msg.add(instance.format("&7Bloqueo de inventario &f&l- &e" + cooldown));
                                }

                                if (maldicion == Maldicion.HP_LOST) {

                                    msg.add(instance.format("&7Pérdida de vida &f&l- &e" + cooldown));
                                }
                            }
                        }

                        if (!found) {

                            player.sendMessage(instance.format("&cEse jugador no cumple ninguna condena especial, como Bloqueo del inventario o Pérdida de vida."));
                            return false;
                        }

                        player.sendMessage(instance.format("&6Condenas de " + name));

                        for (String s : msg) {

                            player.sendMessage(s);
                        }
                    }

                } else if (args[0].equalsIgnoreCase("checkechest")) {

                    if (player.hasPermission("infernalcore.nightmare")) {

                        Player p = Bukkit.getPlayer(args[1]);

                        ((Player)player).openInventory(p.getEnderChest());
                    }
                } else if (args[0].equalsIgnoreCase("checkVillagers")) {

                    if (player.hasPermission("infernalcore.villagers")) {
                        instance.doVillagerCheck();
                    }
                } else if (args[0].equalsIgnoreCase("startNightmare")) {

                    if (player instanceof Player) {

                        Player p = (Player) player;

                        if (p.hasPermission("infernalcore.nightmare")) {
                            instance.getNightmareEvent().setTimeLeft(60 * 60 * 5);
                            instance.getNightmareEvent().setRunning(true);
                            instance.world.setMonsterSpawnLimit(80);
                            instance.startHour();

                            Bukkit.broadcastMessage(instance.format(instance.tag + "&e¡Comienza el Nightmare Event con duración de 5 horas!"));
                            for (Player on : Bukkit.getOnlinePlayers()) {
                                on.playSound(on.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 10.0f, -5.0f);
                            }

                            instance.addGhastSpawn();
                        }
                    }
                } else if (args[0].equalsIgnoreCase("giveObsidian")) {

                    if (player.hasPermission("infernalcore.summon")) {
                        ((Player) player).getInventory().addItem(CustomItems.createLegendaryAxe());
                    }
                } else if (args[0].equalsIgnoreCase("summonBoss")) {

                    if (player.hasPermission("infernalcore.summon")) {

                        Player p = (Player) player;

                        //summon minecraft:zombie -6360.19 13.00 259.30 {CustomName:""Smukler"",CustomNameVisible:1,PersistenceRequired:1b,Health:100,Attributes:[{Name:"generic.maxHealth",Base:100},{Name:"generic.knockbackResistance",Base:0.16f},{Name:"generic.movementSpeed",Base:0.4f},{Name:"generic.attackDamage",Base:14},{Name:"zombie.spawnReinforcements",Base:1f}],HandItems:[{id:"minecraft:iron_sword",tag:{Enchantments:[{id:sharpness,lvl:5},{id:fire_aspect,lvl:1}]},Count:1},{id:"minecraft:enchanted_golden_apple",Count:1}],HandDropChances:[0F,2F],ArmorItems:[{tag:{Enchantments:[{id:protection,lvl:4}]},id:"minecraft:diamond_boots",Count:1},{tag:{Enchantments:[{id:protection,lvl:4}]},id:"minecraft:diamond_leggings",Count:1},{tag:{Enchantments:[{id:protection,lvl:4}]},id:"minecraft:diamond_chestplate",Count:1},{id:"minecraft:player_head",tag:{SkullOwner:MHF_Herobrine},Count:1}],ActiveEffects:[{Id:1,Amplifier:1,Duration:2147483647},{Id:5,Amplifier:0,Duration:2147483647}]}

                        Zombie z = p.getLocation().getWorld().spawn(p.getLocation(), Zombie.class);
                        z.setRemoveWhenFarAway(false);

                        z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2));

                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) skull.getItemMeta();
                        meta.setOwner("MHF_Herobrine");
                        skull.setItemMeta(meta);

                        z.getEquipment().setHelmet(skull);
                        z.getEquipment().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                        z.getEquipment().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());
                        z.getEquipment().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build());

                        z.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0D);
                        z.setHealth(100.0D);
                        z.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.16);
                        z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(14);

                        z.getEquipment().setItemInMainHand(new ItemBuilder(Material.IRON_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 5).addEnchant(Enchantment.FIRE_ASPECT, 1).build());
                        z.getEquipment().setItemInOffHand(new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).build());
                        z.getEquipment().setItemInOffHandDropChance(100.0f);

                        z.setCustomName("Smukler");
                        z.getPersistentDataContainer().set(new NamespacedKey(instance, "dungeon_boss"), PersistentDataType.BYTE, (byte) 1);

                    }
                } else if (args[0].equalsIgnoreCase("generateDungeons")) {

                    if (player.hasPermission("infernalcore.generatedungeons")) {

                        SplittableRandom random = new SplittableRandom();
                        int x = random.nextInt(10000) + 1;
                        int z = random.nextInt(10000) + 1;

                        if (random.nextBoolean()) {
                            x = x * -1;
                        }

                        if (random.nextBoolean()) {
                            z = z * -1;
                        }

                         player.sendMessage(instance.format("&b" + x + " 50" + " " + z));
                    }

                } else if (args[0].equalsIgnoreCase("idioma")) {

                    if (!(player instanceof Player)) return false;

                    Player p = (Player) player;

                    if (args.length == 1) {

                        sender.sendMessage(instance.format("&ePor favor ingresa un idioma."));
                        p.sendMessage(instance.format("&7Ejemplo: &b/pdc idioma es"));
                        p.sendMessage(instance.format("&eArgumentos válidos: &b<es, en>"));
                        return false;
                    }

                    String lang = args[1];
                    PlayerDataManager data = new PlayerDataManager(p.getName(), instance);

                    if (lang.equalsIgnoreCase("es")) {

                        if (data.getLanguage() == Language.SPANISH) {

                            p.sendMessage(instance.format("&c¡Ya estás usando el idioma español!"));
                            return false;
                        }

                        data.setLanguage(Language.SPANISH);
                        p.sendMessage(instance.format("&eHas cambiado tu idioma a: &bEspañol"));

                    } else if (lang.equalsIgnoreCase("en")) {

                        if (data.getLanguage() == Language.ENGLISH) {

                            p.sendMessage(instance.format("&cYou're already using the English Language"));
                            return false;
                        }

                        data.setLanguage(Language.ENGLISH);
                        p.sendMessage(instance.format("&eYou've changed your language sucessfully to: &bEnglish"));

                    } else {

                        p.sendMessage(instance.format("&cNo has ingresado un idioma válido."));
                    }

                } else if (args[0].equalsIgnoreCase("girarruleta")) {

                    if (player.hasPermission("infernalcore.girarruleta")) {

                        new Ruleta(instance, (Player) player).runTaskTimer(instance, 20L, 20L);
                    }

                } else if (args[0].equalsIgnoreCase("mobcap")) {

                    if (player.hasPermission("infernalcore.mobcap")) {

                        instance.world.setMonsterSpawnLimit(65);
                    }

                } else if (args[0].equalsIgnoreCase("cambiarDia")) {

                    if (!player.hasPermission("permadeathcore.cambiardia")) {
                        player.sendMessage(instance.format("&cNo tienes permiso para hacer esto"));
                        return false;
                    }

                    if (args.length <= 1) {

                        player.sendMessage(instance.format("&cNecesitas agregar un día"));
                        player.sendMessage(instance.format("&eEjemplo: &7/pdc cambiarDia <día>"));
                        return false;
                    }

                    instance.addDays(player, args[1]);

                } else if (args[0].equalsIgnoreCase("reload")) {

                    if (player.hasPermission("permadeathcore.reload")) {

                        instance.reload(player);

                    } else {

                        player.sendMessage(instance.format("&cNo tienes permiso para utilizar este comando."));
                    }
                } else if (args[0].equalsIgnoreCase("mensaje")) {

                    if (!(player instanceof Player)) return false;

                    if (args.length == 1) {

                        player.sendMessage(instance.format("&cDebes escribir un mensaje, ejemplo: /ic mensaje He muerto"));

                        if (instance.getConfig().contains("Server-Messages.CustomDeathMessages." + player.getName())) {

                            player.sendMessage(instance.format("&eTu mensaje de muerte actual es: &7" + instance.getConfig().getString("Server-Messages.CustomDeathMessages." + player.getName())));
                        } else {

                            player.sendMessage(instance.format("&eTu mensaje de muerte actual es: &7" + instance.getConfig().getString("Server-Messages.DefaultDeathMessage")));
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

                    instance.getConfig().set("Server-Messages.CustomDeathMessages." + player.getName(), "&7" + msg);
                    instance.saveConfig();
                    instance.reloadConfig();

                    if (player instanceof Player) {
                        ((Player) player).playSound(((Player) player).getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, -5);
                    }
                    player.sendMessage(instance.format("&eHas cambiado tu mensaje de muerte a: &7" + msg));

                } else if (args[0].equalsIgnoreCase("dias")) {
                    if (instance.getDays() < 1) {
                        player.sendMessage(instance.tag + ChatColor.DARK_RED + "[ERROR!] Se ha producido un error al cargar el dia, config.yml mal configurado.");
                    } else {
                        player.sendMessage(instance.tag + ChatColor.YELLOW + "Estamos en el día: " + ChatColor.GRAY + instance.getDays());
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    player.sendMessage(instance.tag + ChatColor.RED + "Version Info:");
                    player.sendMessage(ChatColor.GRAY + "- Nombre: " + ChatColor.GREEN + "InfernalCore.jar");
                    player.sendMessage(ChatColor.GRAY + "- Versión: " + ChatColor.GREEN + "InfernalCore v" + instance.getDescription().getVersion());
                    player.sendMessage(ChatColor.GRAY + "- Autor: " + ChatColor.GREEN + "SebazCRC");
                    player.sendMessage(ChatColor.GRAY + "- Página web: " + ChatColor.GREEN + "http://infernalcore.net/");

                } else {
                    sendHelp(player);
                }
            }
        }

        return false;
    }

    private void sendHelp(CommandSender sender) {

        sender.sendMessage(instance.tag + ChatColor.RED + "InfernalCore - " + ChatColor.BLUE + "Made by SebazCRC");
        sender.sendMessage(ChatColor.WHITE + "/ic idioma <es, en>" + ChatColor.GOLD + ChatColor.ITALIC + "(Cambia tu idioma)");
        sender.sendMessage(ChatColor.WHITE + "/ic dias " + ChatColor.GOLD + ChatColor.ITALIC + "(Muestra el día en el que está el plugin)");
        sender.sendMessage(ChatColor.WHITE + "/ic duracion " + ChatColor.GOLD + ChatColor.ITALIC + "(Muestra la duración de la tormenta)");

        if (sender instanceof Player) {

            sender.sendMessage(ChatColor.WHITE + "/ic mensaje <mensaje> " + ChatColor.GOLD + ChatColor.ITALIC + "(Cambia tu mensaje de muerte)");
            sender.sendMessage(ChatColor.WHITE + "/ic awake " + ChatColor.GOLD + ChatColor.ITALIC + "(Muestra el tiempo despierto)");
        }
        sender.sendMessage(ChatColor.WHITE + "/ic info " + ChatColor.GOLD + ChatColor.ITALIC + "(Información general)");

        if (sender.hasPermission("permadeathcore.admin")) {

            sender.sendMessage("");
            sender.sendMessage(instance.tag + ChatColor.RED + "Comandos de administrador:");
            sender.sendMessage(ChatColor.RED + "/ic cambiarDia <dia> " + ChatColor.GRAY + ChatColor.ITALIC + "(Cambia el día actual, pd: puede que requiera un reinicio)");
            sender.sendMessage(ChatColor.RED + "/ic reload " + ChatColor.GRAY + ChatColor.ITALIC + "(Recarga el archivo config.yml)");
        }
    }

    private void sendChanges(String[] args, CommandSender player) {

        try {

            if (args.length == 1) {
                player.sendMessage(instance.format(instance.tag + "&eEl uso correcto de este comando es: &7/pdc cambios <día>"));
                return;
            }

            if (args.length >= 2) {
                int dia = Integer.parseInt(args[1]);

                String check = "\u2713";
                String equis = "\u274C";

                if (dia < 1 || dia > 60) {

                    player.sendMessage(instance.format("&cNo hemos encontrado información para este día."));
                    return;
                }

                player.sendMessage(instance.format(instance.tag + "&eMostrando cambios para el día: &7" + dia));

                if (dia >= 1 && dia <= 9) {

                    player.sendMessage(instance.format(instance.tag + "&c&lNo hay cambios disponibles para este día (Minecraft Vanilla)"));
                }

                if (dia >= 10 && dia <= 19) {

                    player.sendMessage(instance.format("&a&l" + check + " Ahora todas las arañas tienen efectos de poción."));
                    player.sendMessage(instance.format("&a&l" + check + " Se necesitan mínimo &74 &a&ljugadores para pasar la noche."));
                    player.sendMessage(instance.format("&c&l" + equis + " Doble de mobs (se necesita configurarlo en bukkit.yml)."));
                }

                if (dia >= 20 && dia <= 29) {

                    player.sendMessage(instance.format("&a&l" + check + " Drops de Mobs eliminados (los de la lista oficial)."));
                    player.sendMessage(instance.format("&a&l" + check + " No se puede saltar la noche."));
                    player.sendMessage(instance.format("&a&l" + check + " Pigmans enojados por defecto."));
                    player.sendMessage(instance.format("&a&l" + check + " Ahora las arañas tienen 3 - 5 efectos."));
                    player.sendMessage(instance.format("&a&l" + check + " 1 de cada 100 Ravagers otorga un Tótem de la Inmortalidad."));
                    player.sendMessage(instance.format("&a&l" + check + " Ahora los Phantoms son de tamaño 9 y tienen el doble de vida."));
                    player.sendMessage(instance.format("&a&l" + check + " Arañas con un esqueleto de clase."));
                    player.sendMessage(instance.format("&a&l" + check + " Entidades pacíficas agresivas."));
                }

                if (dia >= 25 && dia <= 29) {

                    player.sendMessage(instance.format("&e&lCAMBIOS EXTRAS PARA EL DÍA 25"));
                    player.sendMessage(instance.format("&a&l" + check + " Death Train Reset (y este otorga efectos a los mobs)"));
                    player.sendMessage(instance.format("&a&l" + check + " Ahora todas las arañas tienen 5 efectos."));
                    player.sendMessage(instance.format("&a&l" + check + " Ahora los Ravagers tienen Fuerza II y Velocidad I, con un 20% de Drop de Tótem."));
                    player.sendMessage(instance.format("&a&l" + check + " GigaSlimes, GigaMagmaCubes y Ghasts Demoníacos."));
                    player.sendMessage(instance.format("&a&l" + check + " Armadura de Netherite."));
                }

                if (dia >= 30 && dia <= 39) {

                    player.sendMessage(instance.format("&a&l" + check + " Ahora todos los esqueletos tienen clases y llevan una flecha de daño II."));
                    player.sendMessage(instance.format("&a&l" + check + " Ya no se puede obtener la armadura de Netherite."));
                    player.sendMessage(instance.format("&a&l" + check + " Los calamares son guardianes con Speed II."));
                    player.sendMessage(instance.format("&a&l" + check + " Los murciélagos son blazes con resistencia II"));
                    player.sendMessage(instance.format("&a&l" + check + " Ahora los Creepers son eléctricos"));
                    player.sendMessage(instance.format("&a&l" + check + " Los Pillagers ahora son invisibles."));
                    player.sendMessage(instance.format("&a&l" + check + " Los Pigmans ahora tienen armadura de Diamante."));
                    player.sendMessage(instance.format("&a&l" + check + " Los Gólems ahora tienen Velocidad IV."));
                    player.sendMessage(instance.format("&a&l" + check + " Los Silverfish y Endermites tienen 5 efectos, de la misma lista que las arañas"));
                    player.sendMessage(instance.format("&a&l" + check + " Los Shulkers ahora son Shulkers Explosivos y poseen un 20% de dropear un Shulker Shell."));
                    player.sendMessage(instance.format("&a&l" + check + " Aparecen Ender Creepers y Ender Ghasts en el End"));
                    player.sendMessage(instance.format("&a&l" + check + " La batalla contra el Ender Dragon está completamente modificada."));
                    player.sendMessage(instance.format("&a&l" + check + " Ahora los Tótems tienen 1% de fallar (puedes configurarlo en la configuración a tu gusto)"));
                }

                if (dia >= 40 && dia <= 49) {

                    if (args.length == 2) {

                        player.sendMessage(instance.format("&a&l" + check + " Todos los jugadores pierden 5 slots de su inventario."));
                        player.sendMessage(instance.format("&a&l" + check + " Se habilita el fuego amigo."));
                        player.sendMessage(instance.format("&a&l" + check + " Ya no se pueden craftear Antorchas / Antorchas de Redstone."));
                        player.sendMessage(instance.format("&a&l" + check + " Todos los jugadores pierden 4 contenedores de vida."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Tótems tienen 3% de fallar y al activarse consumen 2 en lugar de 1."));
                        player.sendMessage(instance.format("&a&l" + check + " Las Elytras en los Barcos ahora aparecen rotas."));
                        player.sendMessage(instance.format("&a&l" + check + " Ahora los Shulkers dropean Shells con un 2%."));
                        player.sendMessage(instance.format("&a&l" + check + " Ahora los Phantoms llevan encima un esqueleto de clase."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Creepers ahora tienen Velocidad II y Resistencia II."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Guardianes ahora tienen Velocidad II y Resistencia III y su rayo es el doble de rápido."));
                        player.sendMessage(instance.format("&a&l" + check + " Las arañas ahora son arañas de cueva."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Zombies son Vindicators con Fuerza I y doble de vida."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Perros ahora son Gatos."));
                        player.sendMessage(instance.format("&a&l" + check + " Las vacas, ovejas, cerdos, pollos y vacasetas ahora son Ravagers"));
                        player.sendMessage(instance.format("&a&l" + check + " Los Endermans del Nether son ahora Ender Creepers."));
                        player.sendMessage(instance.format("&2&lPÁGINA 1/2 &f&l- &7Si deseas ver la página 2 de los cambios escribe: /pdc cambios" + dia + " 2"));

                    } else {

                        player.sendMessage(instance.format("&a&l" + check + " La Tormenta puede dar ceguera."));
                        player.sendMessage(instance.format("&a&l" + check + " Super Golden Apple+ & Hyper Golden Apple+"));
                        player.sendMessage(instance.format("&a&l" + check + " Los gatos ahora son Gatos Supernova."));
                        player.sendMessage(instance.format("&a&l" + check + " Reliquia del Fin (elimina la maldición de la pérdida de slots)."));
                        player.sendMessage(instance.format("&a&l" + check + " Se pueden descraftear los shulkers."));
                        player.sendMessage(instance.format("&e&l" + check + " El Portal a The Beginning será generado el día 50"));
                        player.sendMessage(instance.format("&c&l" + check + " El 75% de los Ghasts Demoníacos son Demonios Flotantes,"));
                        player.sendMessage(instance.format("&a&l" + check + " Pigman Jockeys."));
                        player.sendMessage(instance.format("&a&l" + check + " Jess la Emperatriz."));
                        player.sendMessage(instance.format("&a&l" + check + " El 1% de los Endermans spawnea hostil."));
                        player.sendMessage(instance.format("&a&l" + check + " Las Brujas son ahora Brujas Imposibles."));
                        player.sendMessage(instance.format("&2&lPÁGINA 2/2 &f&l- &7Si deseas ver la página 1 de los cambios escribe: /pdc cambios" + dia + " 1"));
                    }
                }

                if (dia >= 50 && dia <= 59) {

                    if (args.length == 2) {

                        player.sendMessage(instance.format("&a&l" + check + " The Beginning (y sus respectivos mobs)."));
                        player.sendMessage(instance.format("&a&l" + check + " Bacalao de la Muerte."));
                        player.sendMessage(instance.format("&a&l" + check + " Gatos Galácticos."));
                        player.sendMessage(instance.format("&a&l" + check + " Ya no se pueden obtener cubos de agua / lava."));
                        player.sendMessage(instance.format("&e&l" + "-" + " Los tótems tienen un 5% de fallar y estos consumen 3."));
                        player.sendMessage(instance.format("&a&l" + check + " Las Raid dan Hero of The Village durante 5min."));
                        player.sendMessage(instance.format("&a&l" + check + " Picar bloques quitan medio corazón cada uno."));
                        player.sendMessage(instance.format("&a&l" + check + " Fundir Hierro y Oro da Pepitas."));
                        player.sendMessage(instance.format("&a&l" + check + " Te ahogas 5 veces más rápido."));
                        player.sendMessage(instance.format("&a&l" + check + " Estar en Soulsand da Lentitud II."));
                        player.sendMessage(instance.format("&a&l" + check + " Al estar en el overworld puedes obtener Levitación de 3 a 20 segundos por las noches."));
                        player.sendMessage(instance.format("&a&l" + check + " En el Nether ahora llueven mobs."));
                        player.sendMessage(instance.format("&a&l" + check + " Hay más probabilidad de recibir ceguera por la lluvia."));
                        player.sendMessage(instance.format("&a&l" + check + " Ahora los mobs hostiles tienen resistencia contra el fuego."));
                        player.sendMessage(instance.format("&a&l" + check + " Las Arañas de Cueva ya no pueden tener Glowing."));
                        player.sendMessage(instance.format("&2&lPÁGINA 1/2 &f&l- &7Si deseas ver la página 2 de los cambios escribe: /pdc cambios" + dia + " 2"));
                    } else {
                        player.sendMessage(instance.format("&a&l" + check + " Los Creepers Eléctricos son ahora Quantum Creepers y el 20% son Ender Creepers."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Phantoms son GigaPhantoms y tienen 1/100 de que aparezcan 4 Ender Ghasts."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Pollos ahora son Silverfishs."));
                        player.sendMessage(instance.format("&a&l" + check + " El 20% de los Pigmans son ahora Pigmans Jockeys, los drops están al 33%."));
                        player.sendMessage(instance.format("&a&l" + check + " El 1% de los Pillagers son Evokers."));
                        player.sendMessage(instance.format("&a&l" + check + " Los Gólems de Hierro tienen ahora Resistencia II."));
                        player.sendMessage(instance.format("&a&l" + check + " Ahora los Ahogados Siempre llevan un Tridente."));
                        player.sendMessage(instance.format("&a&l" + check + " Las abejas hacen 15 de daño base."));
                        player.sendMessage(instance.format("&a&l" + check + " El Efecto Fatiga Minera no se puede quitar con Leche y dura el doble."));
                        player.sendMessage(instance.format("&a&l" + check + " Dormir tiene un 10% de reiniciar el contador de Phatnoms."));
                        player.sendMessage(instance.format("&a&l" + check + " Armadura de Netherite Infernal"));
                        player.sendMessage(instance.format("&a&l" + check + " Reset del Death Train (de nuevo)"));
                        player.sendMessage(instance.format("&a&l" + check + " Gigantes"));
                        player.sendMessage(instance.format("&a&l" + check + " Wither Skeleton Emperador"));
                        player.sendMessage(instance.format("&2&lPÁGINA 2/2 &f&l- &7Si deseas ver la página 1 de los cambios escribe: /pdc cambios" + dia + " 1"));
                    }
                }

                if (dia == 60) {

                    player.sendMessage(instance.format("&c&l" + equis + " PRONTO"));
                }
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            player.sendMessage(instance.format(instance.tag + "&c¡Vaya!, parece que &7" + args[2] + " &cno es un número, prueba con: &e1, 5, 10"));
        }
    }
}
