package tech.sebazcrc.infernalcore.Util.File;

import tech.sebazcrc.infernalcore.Main;
import com.sun.istack.internal.NotNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.logging.Level;

public class FileAPI {

    private interface InterfaceFile {
        void create(String filename, boolean saveResource);

        void load();

        void save();

        void set(String path, Object s);
    }

    public static class FileOut {

        public FileOut(Plugin plugin, @NotNull String path, String outPath, boolean replace) {

            this.saveFile(plugin, path, outPath, replace);
        }

        private void saveFile(Plugin plugin, @NotNull String resourcePath, String outPath, boolean replace) {

            if (resourcePath != null && !resourcePath.equals("")) {
                resourcePath = resourcePath.replace('\\', '/');
                InputStream in = plugin.getResource(resourcePath);
                if (in == null) {
                } else {
                    //File outFile = new File(plugin.getDataFolder(), "schematics/" + resourcePath);
                    File outFile = new File(plugin.getDataFolder(), outPath + resourcePath);
                    int lastIndex = resourcePath.lastIndexOf(47);
                    //File outDir = new File(plugin.getDataFolder(), "schematics/" + resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                    File outDir = new File(plugin.getDataFolder(), outPath + resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                    if (!outDir.exists()) {
                        outDir.mkdirs();
                    }

                    try {
                        if (outFile.exists() && !replace) {
                        } else {
                            OutputStream out = new FileOutputStream(outFile);
                            byte[] buf = new byte[1024];

                            int len;
                            while((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            out.close();
                            in.close();
                        }
                    } catch (IOException var10) {
                        plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
                    }

                }
            } else {
                throw new IllegalArgumentException("ResourcePath cannot be null or empty");
            }
        }
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
                save();
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