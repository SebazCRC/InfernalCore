package tech.sebazcrc.infernalcore.Util;

import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Util.File.FileAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

public class MessageUtil {

    public static void rl(CommandSender sender, Main instance) {

        sender.sendMessage(instance.format("&aSe ha recargado el archivo de configuración y los mensajes."));
        sender.sendMessage(instance.format("&eAlgunos cambios pueden requerir un reinicio para funcionar correctamente."));
    }

    public static void setupConfig(Main instance) {

        File f = new File(instance.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.set("ban-enabled", true);
        c.set("anti-afk-enabled", false);
        c.set("AntiAFK.DaysForBan", 7);
        c.set("Toggles.Netherite.Helmet", 10);
        c.set("Toggles.Netherite.Chestplate", 10);
        c.set("Toggles.Netherite.Leggings", 10);
        c.set("Toggles.Netherite.Boots", 10);
        c.set("Toggles.End.Mob-Spawn-Limit", 70);
        c.set("Toggles.End.Ender-Ghast-Count", 170);
        c.set("Toggles.End.Ender-Creeper-Count", 20);
        c.set("Toggles.End.Protect-End-Spawn", false);
        c.set("Toggles.End.Protect-Radius", 10);
        c.set("Toggles.End.PermadeathDemon.DisplayName", "&6&lPERMADEATH DEMON");
        c.set("Toggles.End.PermadeathDemon.DisplayNameEnraged", "&6&lENRAGED PERMADEATH DEMON");
        c.set("Toggles.End.PermadeathDemon.Health", 1350);
        c.set("Toggles.End.PermadeathDemon.EnragedHealth", 1350);
        c.set("Toggles.End.PermadeathDemon.Optimizar-TNT", false);
        c.set("Toggles.TheBeginning.YticGenerateChance", 100000);
        c.set("Toggles.Spider-Effect", true);
        c.set("Toggles.OP-Ban", true);
        c.set("Toggles.Mike-Creeper-Spawn", true);
        c.set("Toggles.Optimizar-Mob-Spawns", false);
        c.set("Toggles.Gatos-Supernova.Destruir-Bloques", true);
        c.set("Toggles.Gatos-Supernova.Fuego", true);
        c.set("Toggles.Gatos-Supernova.Explosion-Power", 200);
        c.set("Server-Messages.coords-msg-enable", true);
        c.set("TotemFail.Enable", true);
        c.set("TotemFail.Medalla", "&7¡El jugador %player% ha usado su medalla de superviviente!");
        c.set("TotemFail.ChatMessage", "&7¡El tótem de &c%player% &7ha fallado!");
        c.set("TotemFail.ChatMessageTotems", "&7¡Los tótems de &c%player% &7han fallado!");
        c.set("TotemFail.NotEnoughTotems", "&7¡%player% no tenía suficientes tótems en el inventario!");
        c.set("TotemFail.PlayerUsedTotemMessage", "&7El jugador %player% ha consumido un tótem (Probabilidad: %totem_fail% %porcent% %number%)");
        c.set("TotemFail.PlayerUsedTotemsMessage", "&7El jugador %player% ha usado {ammount} tótems (Probabilidad: %totem_fail% %porcent% %number%)");
        c.set("Worlds.MainWorld", "world");
        c.set("Worlds.EndWorld", "world_the_end");

        c.save();
        c.load();

    }

    public static String getDateForDayOne() {

        LocalDate act = LocalDate.now();
        LocalDate fechaActual = act.minusDays(1);

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

        return s;
    }

    public static String convertSeconds(int t) {

        long segundosbrutos = t * 20;

        long hours = segundosbrutos % 86400 / 3600;
        long minutes = (segundosbrutos % 3600) / 60;
        long seconds = segundosbrutos % 60;
        long days = segundosbrutos / 86400;

        String tiempo = "";

        tiempo = days >= 1 ? tiempo + days + "d " : tiempo;

        String fH = hours > 10 ? "" + hours : "0" + hours;
        String fM = minutes > 10 ? "" + minutes : "0" + minutes;
        String fS = seconds > 10 ? "" + seconds : "0" + seconds;

        tiempo = hours > 0 ? tiempo + fH + "h " : tiempo;
        tiempo = minutes > 0 ? tiempo + fM + "m " : tiempo;
        tiempo = seconds > 0 ? tiempo + fS + "s " : tiempo;

        return tiempo;
    }

    public static String convertTicks(int t) {

        return convertSeconds(t / 20);
    }

    public static void sendWelcomeMessage(Player player) {
    }
}
