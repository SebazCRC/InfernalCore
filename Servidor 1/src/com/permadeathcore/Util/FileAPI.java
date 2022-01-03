package com.permadeathcore.Util;

import java.io.File;
import java.io.IOException;

import com.permadeathcore.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileAPI {

    private interface InterfaceFile {
        void create(String filename, boolean saveResource);

        void load();

        void save();

        void set(String path, Object s);
    }

    public static class UtilFile implements InterfaceFile {
        private Main plugin;
        private File f;
        private FileConfiguration fc;

        public UtilFile(Main plugin, File f, FileConfiguration fc) {
            this.plugin = plugin;
            this.f = f;
            this.fc = fc;
        }

        @Override
        public void create(String filename, boolean saveResource) {
            f = new File(plugin.getDataFolder(), filename);
            fc = new YamlConfiguration();

            if (!f.exists()) {
                f.getParentFile().mkdirs();

                if (saveResource == false) {
                    try {
                        f.createNewFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (plugin.getResource(filename) == null) {
                        plugin.saveResource(filename, true);
                    } else {
                        plugin.saveResource(filename, false);
                    }
                }

                load();
            }
        }

        @Override
        public void load() {
            try {
                fc.load(f);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void save() {
            try {
                fc.save(f);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public File getF() {
            return f;
        }

        public FileConfiguration getFc() {
            return fc;
        }

        @Override
        public void set(String path, Object s) {

            if (!fc.contains(path)) {
                fc.set(path, s);
                load();
            }
        }
    }

    private static UtilFile UF;

    public static UtilFile select(Main plugin, File f, FileConfiguration fc) {
        UF = new UtilFile(plugin, f, fc);
        return UF;
    }
}