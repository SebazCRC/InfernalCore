package tech.sebazcrc.infernalcore.Util.Recipe;

import org.bukkit.Bukkit;
import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Util.Item.CustomItems;
import tech.sebazcrc.infernalcore.Util.Item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeManager {

    private Main instance;

    public RecipeManager(Main instance) {
        this.instance = instance;
    }

    public void registerRecipes() {
        if (this.instance.getDays() >= 10) {
            registerDungeonCompass();
        }
    }

    public void registerC2() {
        registerObsidianH();
        registerObsidianC();
        registerObsidianL();
        registerObsidianB();
        registerCryingObsidian();

        Bukkit.getConsoleSender().sendMessage(instance.format("&eRegistrando recetas: 15"));
    }

    private void registerDungeonCompass() {
        ItemStack s = CustomItems.createDungeonCompass();

        NamespacedKey key = new NamespacedKey(instance, "INFERNALCORE_DC");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape( " M ", "HCR", " V " );
        recipe.setIngredient( 'C', Material.COMPASS);
        recipe.setIngredient( 'V', Material.VINE);
        recipe.setIngredient( 'R', Material.REDSTONE_BLOCK);
        recipe.setIngredient( 'H', Material.HEART_OF_THE_SEA);
        recipe.setIngredient( 'M', Material.MAP);
        instance.getServer().addRecipe(recipe);
    }

    private void registerCryingObsidian() {
        ItemStack s = CustomItems.createCryingObsidian();

        NamespacedKey key = new NamespacedKey(instance, "INFERNALCORE_COB");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape( "OGO", "OCO", "OAO" );
        recipe.setIngredient( 'O', Material.OBSIDIAN);
        recipe.setIngredient( 'G', Material.GHAST_TEAR);
        recipe.setIngredient( 'C', Material.BLAZE_POWDER);
        recipe.setIngredient( 'A', Material.DIAMOND_AXE);

        Bukkit.getConsoleSender().sendMessage("Registrando crying obsidian");
        instance.getServer().addRecipe(recipe);
    }

    private void registerObsidianH() {
        ItemStack s = Main.getInstance().getObsidianSet().craftObsidianHelmet();

        NamespacedKey key = new NamespacedKey(instance, "INFERNALCORE_OH");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape( " O ", "OAO", " O " );
        recipe.setIngredient( 'O', Material.OBSIDIAN);
        recipe.setIngredient( 'A', Material.DIAMOND_HELMET);

        instance.getServer().addRecipe(recipe);
    }

    private void registerObsidianC() {
        ItemStack s = Main.getInstance().getObsidianSet().craftObsidianChest();

        NamespacedKey key = new NamespacedKey(instance, "INFERNALCORE_OC");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape( " O ", "OAO", " O " );
        recipe.setIngredient( 'O', Material.OBSIDIAN);
        recipe.setIngredient( 'A', Material.DIAMOND_CHESTPLATE);

        instance.getServer().addRecipe(recipe);
    }

    private void registerObsidianL() {
        ItemStack s = Main.getInstance().getObsidianSet().craftObsidianLegs();

        NamespacedKey key = new NamespacedKey(instance, "INFERNALCORE_OL");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape( " O ", "OAO", " O " );
        recipe.setIngredient( 'O', Material.OBSIDIAN);
        recipe.setIngredient( 'A', Material.DIAMOND_LEGGINGS);

        instance.getServer().addRecipe(recipe);
    }

    private void registerObsidianB() {
        ItemStack s = Main.getInstance().getObsidianSet().craftObsidianBoots();

        NamespacedKey key = new NamespacedKey(instance, "INFERNALCORE_OB");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape( " O ", "OAO", " O " );
        recipe.setIngredient( 'O', Material.OBSIDIAN);
        recipe.setIngredient( 'A', Material.DIAMOND_BOOTS);

        instance.getServer().addRecipe(recipe);
    }
}
