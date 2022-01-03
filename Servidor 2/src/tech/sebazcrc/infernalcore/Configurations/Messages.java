package tech.sebazcrc.infernalcore.Configurations;

import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Manager.Data.PlayerDataManager;
import tech.sebazcrc.infernalcore.Util.File.FileAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Messages {

    private Main instance;

    public Messages(Main instance) {
        this.instance = instance;

        saveDataES();
        saveDataEN();
    }

    public void saveDataES() {

        new FileAPI.FileOut(instance, "mensajes_ES", "mensajes/", false);

        File f = new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.set("Server-Messages.coords-msg-enable", true);
        c.set("Server-Messages.OnJoin", "&e%player% se ha unido al servidor.");
        c.set("Server-Messages.OnLeave", "&e%player% ha abandonado el servidor.");
        c.set("Server-Messages.StormEnd", "&cLa tormenta ha llegado a su fin.");
        c.set("Server-Messages.Sleep", "&7%player% &efue a dormir.");
        c.set("Server-Messages.DeathMessageTitle", "&c¡InfernalCore!");
        c.set("Server-Messages.DeathMessageSubtitle", "%player% ha muerto");
        c.set("Server-Messages.DeathMessageChat", "&c&lEste es el comienzo del sufrimiento eterno de &4&l%player%&c&l. ¡HA SIDO PERMABANEADO!");
        c.set("Server-Messages.DeathTrainMessage", "&c¡Comienza el Death Train con duración de %tiempo% horas!");
        c.set("Server-Messages.ActionBarMessage", "&7Quedan %tiempo% de tormenta");

        c.save();
        c.load();
    }

    public void saveDataEN() {

        new FileAPI.FileOut(instance, "mensajes_EN", "mensajes/", false);

        File f = new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.set("Server-Messages.OnJoin", "&e%player% joined the game.");
        c.set("Server-Messages.OnLeave", "&e%player% left the game.");
        c.set("Server-Messages.StormEnd", "&cThe storm has been ended.");
        c.set("Server-Messages.Sleep", "&7%player% &ewent to sleep, Sweet dreams!.");
        c.set("Server-Messages.DeathMessageTitle", "&cInfernalCore!");
        c.set("Server-Messages.DeathMessageSubtitle", "%player% died");
        c.set("Server-Messages.DeathMessageChat", "&c&lThis is the beginning of the eternal suffering of &4&l%player%&c&l. HAS BEEN INFERNA-BANNED!");
        c.set("Server-Messages.DeathTrainMessage", "&cStarting the Death Train with a duration of %tiempo% hours!");
        c.set("Server-Messages.ActionBarMessage", "&7%tiempo% storm left");

        c.save();
        c.load();
    }

    public void reloadFiles() {

        loadEs();
        loadEn();
    }

    private void loadEn() {

        new FileAPI.FileOut(instance, "mensajes_EN", "mensajes/", false);

        File f = new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);
    }

    private void loadEs() {
        new FileAPI.FileOut(instance, "mensajes_ES", "mensajes/", false);

        File f = new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.load();
    }

    public String getMessageByPlayer(String path, String playerName, java.util.List replaces) {

        HashMap<String, Object> r = new HashMap<>();
        //r.put("%player%", playerName);

        for (Object o : replaces) {

            String s = String.valueOf(o);

            String[] l = s.split(";;;");
            Object b = l[1];

            r.put(l[0], b);
        }

        PlayerDataManager data = new PlayerDataManager(playerName, instance);
        Language lang = data.getLanguage();

        File f = getByLang(lang);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        String returning = c.getString(path);

        for (String a : r.keySet()) {

            String value = String.valueOf(r.get(a));
            returning = returning.replace(a, (CharSequence) value);
        }

        return Main.format(returning);
    }

    public void sendMessageToConsole(String path) {

        Language lang = Language.SPANISH;

        File f = getByLang(lang);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        String returning = c.getString(path);

        Bukkit.getConsoleSender().sendMessage(Main.format(returning));
    }

    public String getMessageForConsole(String path) {

        Language lang = Language.SPANISH;

        File f = getByLang(lang);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        String returning = c.getString(path);

        return Main.format(returning);
    }

    public String getMessageByPlayer(String path, String playerName) {

        HashMap<String, Object> r = new HashMap<>();
        //r.put("%player%", playerName);

        PlayerDataManager data = new PlayerDataManager(playerName, instance);
        Language lang = data.getLanguage();

        File f = getByLang(lang);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        String returning = c.getString(path);

        for (String a : r.keySet()) {

            String value = String.valueOf(r.get(a));
            returning = returning.replace(a, (CharSequence) value);
        }

        return Main.format(returning);
    }

    private File getByLang(Language lang) {

        if (lang == Language.SPANISH) {

            return new File(instance.getDataFolder(), "mensajes/mensajes_ES.yml");
        } else if (lang == Language.ENGLISH) {

            return new File(instance.getDataFolder(), "mensajes/mensajes_EN.yml");
        }

        return null;
    }

    public ArrayList<String> formatReplace(String s, Object v, ArrayList list) {

        if (list == null) {
            list = new ArrayList();
        }

        list.add(s + ";;;" + String.valueOf(v));

        return list;
    }
}
