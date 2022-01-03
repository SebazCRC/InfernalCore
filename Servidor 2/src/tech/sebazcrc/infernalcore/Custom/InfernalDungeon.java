package tech.sebazcrc.infernalcore.Custom;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.sebazcrc.infernalcore.Main;
import org.bukkit.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SplittableRandom;

public class InfernalDungeon {

    private Main instance;
    private Location location;

    private File f;
    private FileConfiguration c;

    private SplittableRandom random;

    private int HEIGHT = 50;

    public InfernalDungeon(Main instance) {
        this.instance = instance;
        initializeFile();
    }

    public boolean hasAviableDungeon() {

        return this.c.getStringList("DungeonsList").size() < 16;
    }

    public InfernalDungeon getByLocation(Location where) {
        location = where;
        return this;
    }

    public InfernalDungeon craftNewDungeon() {
        this.random = new SplittableRandom();
        generate();

        return this;
    }

    public ArrayList<InfernalDungeon> getByList() {

        ArrayList<InfernalDungeon> toAdd = new ArrayList<>();

        if (!hasDungeons()) {

            return toAdd;
        }

        for (String s : this.c.getStringList("DungeonsList")) {

            toAdd.add(new InfernalDungeon(instance).getByLocation(fromString(s)));
        }

        return toAdd;
    }

    private void generate() {

        //if (!this.exists()) {

        int x = random.nextInt(10000) + 1;
        int z = random.nextInt(10000) + 1;

        if (random.nextBoolean()) {
            x = x * -1;
        }

        if (random.nextBoolean()) {
            z = z * -1;
        }

        this.location = new Location(instance.world, x, HEIGHT, z);

        InfernalDungeon d = instance.getClosestDungeon(this.location);

        if (d != null) {

            if (d.getLocation().distance(this.location) <= 100) {

                x = random.nextInt(10000) + 1;
                z = random.nextInt(10000) + 1;

                if (random.nextBoolean()) {
                    x = x * -1;
                }

                if (random.nextBoolean()) {
                    z = z * -1;
                }
            }
        }

        instance.getLogger().info("[InfernalCore] Generando Infernal Dungeon N" + this.c.getStringList("DungeonsList").size() + "....");
        this.generateDungeon(instance.world, x, z);

        ArrayList<String> list = (ArrayList<String>) this.c.getStringList("DungeonsList");

        list.add(convertLocationToString(location));

        c.set("DungeonsList", list);
        saveDungeonFile();
        loadDungeonFile();
        //}
    }

    public boolean hasDungeons() {

        return !c.getStringList("DungeonsList").isEmpty();
    }

    private void generateDungeon(World world, int x, int z) {
        Clipboard clipboard;
        File file = new File(Main.getInstance().getDataFolder(), "schematics/dungeon.schem");

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        assert format != null;

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

            Operation operation = clipboardHolder
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, HEIGHT, z))
                    .ignoreAirBlocks(false)
                    .copyEntities(true)
                    .build();

            Operations.complete(operation);
            //editSession.replaceBlocks(
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private boolean exists() {

        if (location == null) return false;

        return this.c.getStringList("DungeonsList").contains(convertLocationToString(this.location));
    }

    public Location fromString(String where) {

        String[] s = where.split(";");

        int x = Integer.valueOf(s[0]);
        int y = Integer.valueOf(s[1]);
        int z = Integer.valueOf(s[2]);
        World w = Bukkit.getWorld(s[3]);

        return new Location(w, x, y, z);
    }

    public String convertLocationToString(Location loc) {

        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();

        return x + ";" + y + ";" + z + ";" + loc.getWorld().getName();
    }

    public Location getLocation() {
        return location;
    }

    private void initializeFile() {

        if (instance == null) {
            instance = Main.getInstance();
        }

        this.f = new File(instance.getDataFolder(), "dungeons.yml");
        this.c = YamlConfiguration.loadConfiguration(f);

        if (!f.exists()) {

            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            c.set("DungeonsList", new ArrayList<>());
            saveDungeonFile();
            loadDungeonFile();
        }
    }

    private void saveDungeonFile() {

        try {
            this.c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDungeonFile() {

        try {
            this.c.load(f);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ;
    }

    public void addToFile(Location location) {


        ArrayList<String> dungeons = (ArrayList<String>) this.c.getStringList("DungeonsList");

        dungeons.add(convertLocationToString(location));

        c.set("DungeonsList", dungeons);
        saveDungeonFile();
        loadDungeonFile();
    }
}
