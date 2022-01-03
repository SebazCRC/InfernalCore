package tech.sebazcrc.infernalcore.NMS;

import org.bukkit.Bukkit;

public class VersionManager {

    public static String getVersion() {

        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].substring(1);
        return version;
    }

    public static String getFormatedVersion() {

        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].substring(1);
        String format = "";

        if (version.equalsIgnoreCase("1_16_R1")) {

            format = "1.16.x";
        }

        if (version.equalsIgnoreCase("1_15_R1")) {

            format = "1.15.x";
        }

        if (version.equalsIgnoreCase("1_14_R1")) {

            format = "1.14.x";
        }

        return format;
    }

    public static boolean isRunning16_R1() {

        return getVersion().equalsIgnoreCase("1_16_R1");
    }

    public static boolean isRunning15() {

        return getVersion().equalsIgnoreCase("1_15_R1");
    }

    public static boolean isRunning14() {

        return getVersion().equalsIgnoreCase("1_14_R1");
    }
}
