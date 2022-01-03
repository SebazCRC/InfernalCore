package tech.sebazcrc.infernalcore.Util.Messages;

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

        String s = MessageUtil.getDateForDayOne();

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        if (config.getString("Fecha").isEmpty()) {

            config.set("Fecha", s);
            instance.reloadConfig();
        }

        c.set("Fecha", s);

        c.set("ban-enabled", true);
        c.set("anti-afk-enabled", true);
        c.set("AntiAFK.DaysForBan", 7);
        c.set("AntiAFK.Bypass", Arrays.asList("SebazCRC", "vo1d_dev"));
        c.set("Server-Messages.coords-msg-enable", true);
        c.set("Server-Messages.CustomDeathMessages.SebazCRC", "&7Tranquilos no murió, solamente está probando");
        c.set("Server-Messages.CustomDeathMessages.ElRichMC", "&7ElRichMC, Eso no ha sido muy eyeyey de tu parte.");
        c.set("TotemFail.Enable", true);
        c.set("TotemFail.Medalla", "&7¡El jugador %player% ha usado su medalla de superviviente!");
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

        player.sendMessage(Main.format("&9&l---------------------------------------------"));
        player.sendMessage(Main.format("                  &4&lPERMADEATH CORE"));
        player.sendMessage(Main.format("&3  Creado por &f&l@SebazCRC#0001&3, Autor original (1-20): &f&l@vo1d#0001"));
        player.sendMessage(Main.format("&3  Enlace a nuestro discord: &f&lhttps://discord.gg/wWZkjRt"));
        player.sendMessage(Main.format("&3  Utiliza /pdc para más información"));
        player.sendMessage(Main.format("&9&l---------------------------------------------"));
    }
}
