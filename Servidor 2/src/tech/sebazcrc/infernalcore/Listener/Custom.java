package tech.sebazcrc.infernalcore.Listener;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.infernalcore.Entity.MobFactory;
import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.Util.Item.CustomItems;
import tech.sebazcrc.infernalcore.Util.Item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class Custom implements Listener {

    private Map<Player, Integer> actualCrafts = new HashMap<>();
    private Main instance;
    private SplittableRandom random = new SplittableRandom();

    public Custom(Main instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPre(PrepareItemCraftEvent e) {

        ItemStack result = e.getInventory().getResult();

        if (result == null) return;

        if (result.isSimilar(CustomItems.createDungeonCompass())) {

            int correctItems = 0;

            for (ItemStack s : e.getInventory().getMatrix()) {

                if (s != null) {

                    if (s.getType() == Material.HEART_OF_THE_SEA && s.getAmount() >= 8) {
                        correctItems++;
                    }

                    if (s.getType() == Material.REDSTONE_BLOCK && s.getAmount() >= 16) {
                        correctItems++;
                    }

                    if (s.getType() == Material.MAP && s.getAmount() >= 32) {
                        correctItems++;
                    }

                    if (s.getType() == Material.VINE && s.getAmount() >= 32) {
                        correctItems++;
                    }
                }
            }

            if (correctItems < 4) {
                e.getInventory().setResult(null);
                return;
            }

            if (correctItems >= 4) {
                e.getInventory().setResult(e.getRecipe().getResult());
            }
        }

        if (result.isSimilar(CustomItems.createCryingObsidian())) {
            int correctItems = 0;

            for (ItemStack s : e.getInventory().getMatrix()) {

                if (s != null) {

                    if (s.getType() == Material.DIAMOND_AXE) {

                        if (s.isSimilar(CustomItems.createLegendaryAxe())) {
                            correctItems++;
                        }
                    }

                    if (s.getType() == Material.GHAST_TEAR) {
                        if (s.isSimilar(CustomItems.createLagrimaDeAlma())) {
                            correctItems++;
                        }
                    }

                    if (s.getType() == Material.BLAZE_POWDER) {
                        if (s.isSimilar(CustomItems.createCreeperPowerSource())) {
                            correctItems++;
                        }
                    }

                    if (s.getType() == Material.OBSIDIAN && s.getAmount() >= 2) {
                        correctItems++;
                    }
                }
            }

            if (correctItems < 9) {
                e.getInventory().setResult(null);
                return;
            }
            e.getInventory().setResult(e.getRecipe().getResult());
        }

        if (instance.getObsidianSet().isObsidianPiece(result)) {

            int niceObsidians = 0;

            for (ItemStack s : e.getInventory().getMatrix()) {
                if (s != null) {
                    if (s.getType() == Material.OBSIDIAN) {
                        if (s.isSimilar(CustomItems.createCryingObsidian())) {
                            niceObsidians++;
                        }
                    }
                }
            }

            if (niceObsidians < 4) {
                e.getInventory().setResult(null);
                return;
            }

            ItemStack t = null;

            if (result.getType() == Material.DIAMOND_HELMET) {
                t = instance.getObsidianSet().craftObsidianHelmet();
            } else if (result.getType() == Material.DIAMOND_CHESTPLATE) {
                t = instance.getObsidianSet().craftObsidianChest();
            } else if (result.getType() == Material.DIAMOND_LEGGINGS) {
                t = instance.getObsidianSet().craftObsidianLegs();
            } else if (result.getType() == Material.DIAMOND_BOOTS) {
                t = instance.getObsidianSet().craftObsidianBoots();
            }

            e.getInventory().setResult(t);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {

        Player p = e.getPlayer();

        if (e.getRightClicked() instanceof PigZombie) {

            PigZombie z = (PigZombie) e.getRightClicked();

            if (p.getInventory().getItemInMainHand() != null) {

                if (p.getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {

                    e.getPlayer().sendMessage(instance.format(instance.tag + "&e¡Tu mob no puede ser renombrado, pero no despawneará!"));
                    z.setRemoveWhenFarAway(false);
                    e.setCancelled(true);
                }
            }

            if (p.getInventory().getItemInOffHand() != null) {

                if (p.getInventory().getItemInOffHand().getType() == Material.NAME_TAG) {

                    e.getPlayer().sendMessage(instance.format(instance.tag + "&e¡Tu mob no puede ser renombrado, pero no despawneará!"));
                    z.setRemoveWhenFarAway(false);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {

        if (e.getRecipe().getResult().isSimilar(CustomItems.createDungeonCompass()) || e.getRecipe().getResult().isSimilar(CustomItems.createCryingObsidian())) {

            e.getInventory().setMatrix(clearMatrix());
            e.getWhoClicked().setItemOnCursor(e.getRecipe().getResult());
        }

        if (e.getRecipe().getResult().isSimilar(CustomItems.createCryingObsidian())) {

            if (!(e.getWhoClicked() instanceof Player)) return;

            Player p = (Player) e.getWhoClicked();
            int times = 0;
            if (this.actualCrafts.containsKey(p)) {
                times = this.actualCrafts.get(p);
            }
            times++;
            this.actualCrafts.replace(p, times);

            for (Player o : Bukkit.getOnlinePlayers()) {
                if (o.hasPermission("infernalcore.logs")) {

                    o.sendMessage(instance.format("&eEl jugador &b" + e.getWhoClicked().getName() + " &eha creado una Cying Obsidian &b#" + times));
                }
            }

            Bukkit.getConsoleSender().sendMessage(instance.format("&eEl jugador &b" + e.getWhoClicked().getName() + " &eha creado una Cying Obsidian &b#" + times));
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        if (e.getPlayer().getInventory().getItemInOffHand() != null) {
            if (e.getPlayer().getInventory().getItemInOffHand().isSimilar(CustomItems.createCryingObsidian())) {
                e.setCancelled(true);
            }
        }

        if (e.getPlayer().getInventory().getItemInMainHand() != null) {
            if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(CustomItems.createCryingObsidian())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {

            if (instance.getDays() >= 10 && hasItem(e.getPlayer())) {

                if (hasCompass(e.getPlayer())) {

                    Player p = e.getPlayer();

                    try {

                        if (isSameLoc(p.getCompassTarget(), instance.getClosestDungeon(p.getLocation()).getLocation())) {

                            p.sendMessage(instance.format(instance.tag + "&c¡Tu brújula ya apunta a esa dirección!"));
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            return;
                        }

                        p.setCompassTarget(instance.getClosestDungeon(p.getLocation()).getLocation());
                        p.sendMessage(instance.format(instance.tag + "&aRedireccionando tu brújula al Dungeon más cercano."));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100.0F, 100.0F);
                        e.setCancelled(false);

                    } catch (Exception x) {}
                }
            }
        }
    }

    private boolean isSameLoc(Location compassTarget, Location location) {

        return compassTarget.getX() == location.getX() && compassTarget.getY() == location.getY() && compassTarget.getZ() == location.getZ();
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        if (e.getItemDrop().getItemStack().isSimilar(CustomItems.createDungeonCompass())) {

            java.util.List list = new ArrayList();

            if (e.getItemDrop().getItemStack().getLore() != null) {
                list = e.getItemDrop().getItemStack().getLore();
            }

            list.add("Drop:" + e.getPlayer().getName());

            ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();
            meta.setLore(list);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {

        if (e.getEntity() instanceof Player) {

            Player p = (Player) e.getEntity();
            Item i = e.getItem();

            if (i.getItemStack().isSimilar(CustomItems.createDungeonCompass())) {

                boolean contains = true;
                String playerName = "";

                if (!i.getItemStack().getLore().isEmpty()) {

                    for (String s : i.getItemStack().getLore()) {

                        if (s.startsWith("Drop")) {

                            playerName = s.split(":")[1];
                        }
                    }
                }

                if (!playerName.equalsIgnoreCase(p.getName())) {

                    p.sendMessage(instance.format(instance.tag + "&e¡No puedes recibir &6Dungeon Trackers &ede otros jugadores! (de &b" + playerName + "&e)"));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent e) {

        if (e.isCancelled()) return;

        Raid raid = e.getRaid();
        Player p = e.getPlayer();

        if (instance.getDays() >= 15) {

            if (!instance.getNightmareEvent().isRunning()) {

                if (random.nextInt(100) + 1 <= 40) {
                    p.sendMessage(Main.format(Main.tag + "&e¡Has comenzado una &6Infernal Raid &emucha suerte!"));
                    p.getWorld().playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 10.0f, -5.0f);
                    raid.setInfernalRaid(true);
                }
            } else {
                p.sendMessage(Main.format(Main.tag + "&e¡Has comenzado una &6Infernal Raid &emucha suerte!"));
                p.getWorld().playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 10.0f, -5.0f);
                raid.setInfernalRaid(true);
            }
        }
    }

    @EventHandler
    public void onRaidWave(RaidSpawnWaveEvent e) {

        Raid raid = e.getRaid();

        if (instance.getDays() >= 15) {

            if (raid.isInfernalRaid()) {

                Raider leader = e.getPatrolLeader();
                Vindicator v = leader.getWorld().spawn(leader.getLocation(), Vindicator.class);

                if (v == null) return;

                v.getEquipment().setItemInMainHand(CustomItems.createLegendaryAxe());
                v.getEquipment().setItemInMainHandDropChance(0);
                v.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                v.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
                v.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
                MobFactory.addID(v, "legendary_vindicator");
                v.setCustomName(instance.format("&6Legendary Vindicator"));

                v.setCanJoinRaid(true);
                try {
                    raid.getRaiders().add(v);
                } catch (Exception x) {
                }

                for (Raider raider : e.getRaiders()) {

                    if (MobFactory.hasID(raider, "legendary_vindicator")) return;

                    if (raider.getType() == EntityType.PILLAGER) {

                        ItemStack crossbow = new ItemBuilder(Material.CROSSBOW).addEnchant(Enchantment.QUICK_CHARGE, 2).addEnchant(Enchantment.PIERCING, 3).build();
                        raider.getEquipment().setItemInMainHand(crossbow);
                    }

                    if (raider.getType() == EntityType.VINDICATOR) {
                        raider.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                    }
                }
            }
        }
    }



    private boolean hasCompass(Player p) {

        return p.getInventory().getItemInMainHand() != null && isCompass(p.getInventory().getItemInMainHand()) ? p.getInventory().getItemInMainHand().isSimilar(CustomItems.createDungeonCompass()) : p.getInventory().getItemInOffHand().isSimilar(CustomItems.createDungeonCompass());
    }

    private boolean isCompass(ItemStack itemInMainHand) {

        return itemInMainHand != null ? itemInMainHand.getType() == Material.COMPASS : false;
    }

    private boolean hasItem(Player p) {

        return p.getInventory().getItemInMainHand() != null || p.getInventory().getItemInOffHand() != null;
    }

    public ItemStack[] clearMatrix() {

        return new ItemStack[]{
                new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR)};
    }
}
