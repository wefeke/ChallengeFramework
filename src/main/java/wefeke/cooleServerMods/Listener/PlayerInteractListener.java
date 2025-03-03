package wefeke.cooleServerMods.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import wefeke.cooleServerMods.CooleServerMods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInteractListener implements Listener {

    private final CooleServerMods plugin;
    private final List<Item> woolEntities = new ArrayList<>();
    private final Map<Player, Integer> playerJumpState = new HashMap<>();
    private final double[] jumpHeights = {1.0, 3.0, 5.0};

    public PlayerInteractListener(CooleServerMods plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.BOOK) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && "Team Joiner".equals(meta.getDisplayName())) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.0f);
                //spawnWoolBlocks(player);
                //openTeamSelectionMenu(player);
                event.setCancelled(true);
            }
        }

        if (item != null && item.getType() == Material.STICK) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && "Magic Stick".equals(meta.getDisplayName())) {
                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.setCustomName("MagicProjectile");
                event.setCancelled(true);
            }
        }
        // Make the player jump 60 blocks high when right-clicking with "Klopfer"
        if (item != null && item.getType() == Material.RABBIT_FOOT) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && "Klopfer".equals(meta.getDisplayName())) {
                if (event.getAction().toString().contains("LEFT_CLICK")) {
                    // Toggle jump state
                    int currentState = playerJumpState.getOrDefault(player, 0);
                    currentState = (currentState + 1) % jumpHeights.length;
                    playerJumpState.put(player, currentState);

                    // Update item lore
                    updateKlopferLore(item, currentState);
                } else if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    // Make the player jump
                    int currentState = playerJumpState.getOrDefault(player, 0);
                    player.setVelocity(new Vector(0, jumpHeights[currentState], 0));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
                event.setCancelled(true);
            }
        }
    }

    private void updateKlopferLore(ItemStack item, int currentState) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            String[] states = {"1 Block", "3 Blöcke", "5 Blöcke"};
            for (int i = 0; i < states.length; i++) {
                if (i == currentState) {
                    lore.add(ChatColor.GREEN + states[i]);
                } else {
                    lore.add(ChatColor.GRAY + states[i]);
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private void spawnWoolBlocks(Player player) {
        // Clear existing wool entities
        removeWoolEntities();

        Vector direction = player.getLocation().getDirection().normalize();
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector startPosition = player.getLocation().add(direction.clone().multiply(1)).add(0, 1.5, 0).toVector();

        Material[] woolColors = {Material.RED_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL, Material.GREEN_WOOL};

        for (int i = 0; i < 4; i++) {
            ItemStack wool = new ItemStack(woolColors[i], 1);
            Item woolEntity = player.getWorld().dropItem(startPosition.clone().add(right.clone().multiply(i * 2)).toLocation(player.getWorld()), wool);
            woolEntity.setGravity(false);
            woolEntity.setVelocity(new Vector(0, 0, 0));
            woolEntity.setCustomName("Team " + (i + 1));
            woolEntity.setCustomNameVisible(true);
            woolEntity.setPickupDelay(Integer.MAX_VALUE);
            woolEntities.add(woolEntity);
        }
    }

    public void removeWoolEntities() {
        for (Item woolEntity : woolEntities) {
            woolEntity.remove();
        }
        woolEntities.clear();
    }

    private void openTeamSelectionMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 18, "Select Your Team");

        Material[] woolColors = {Material.RED_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL, Material.GREEN_WOOL};
        String[] teamNames = {"Team 1", "Team 2", "Team 3", "Team 4"};

        for (int i = 0; i < 4; i++) {
            ItemStack wool = new ItemStack(woolColors[i], 1);
            ItemMeta meta = wool.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(teamNames[i]);
                wool.setItemMeta(meta);
            }
            menu.setItem(i, wool);
        }

        // Add player head beneath the first wool block
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        if (headMeta != null) {
            headMeta.setOwningPlayer(player);
            headMeta.setDisplayName(player.getName());
            playerHead.setItemMeta(headMeta);
        }
        menu.setItem(9, playerHead);

        player.openInventory(menu);
    }
}
