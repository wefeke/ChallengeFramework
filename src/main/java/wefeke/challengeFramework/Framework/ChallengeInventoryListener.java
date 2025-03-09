package wefeke.challengeFramework.Framework;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wefeke.challengeFramework.challengeFramework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeInventoryListener implements Listener {
    private final challengeFramework plugin;
    // Cache for challenge settings to display current values
    private final Map<String, Map<String, Integer>> cachedSettings = new HashMap<>();

    public ChallengeInventoryListener(challengeFramework plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();

        // Check if clicked item is valid
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        // Play button sound for all inventory interactions
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        // Display animation effect to the player
        String itemName = clickedItem.getItemMeta().getDisplayName();
        player.sendTitle("", ChatColor.YELLOW + "» " + itemName + " «", 5, 15, 5);

        // Handle main challenge selection inventory
        if (title.equals("Challenge Selection")) {
            event.setCancelled(true);

            String challengeName = clickedItem.getItemMeta().getDisplayName().substring(2); // Remove color code

            for (String key : plugin.getChallengeManager().getChallenges().keySet()) {
                Challenge challenge = plugin.getChallengeManager().getChallenge(key);
                if (challenge.getName().equals(challengeName)) {
                    // Cache settings for this challenge
                    cachedSettings.put(key, new HashMap<>(challenge.getSettings()));

                    // Update inventory with values
                    Inventory settingsInv = challenge.getSettingsInventory(player);
                    updateSettingsDisplay(settingsInv, challenge);

                    player.openInventory(settingsInv);
                    break;
                }
            }
            return;
        }

        // Handle settings inventories
        if (title.endsWith(" Settings")) {
            event.setCancelled(true);

            String challengeName = title.substring(0, title.indexOf(" Settings"));
            itemName = clickedItem.getItemMeta().getDisplayName();

            // Find the challenge
            for (String key : plugin.getChallengeManager().getChallenges().keySet()) {
                Challenge challenge = plugin.getChallengeManager().getChallenge(key);
                if (challenge.getName().equals(challengeName)) {
                    // Process settings
                    if (clickedItem.getItemMeta().hasLore()) {
                        String lore = clickedItem.getItemMeta().getLore().get(0);
                        if (lore.startsWith("Change ")) {
                            String[] parts = lore.substring(7).split(" by ");
                            String settingKey = parts[0];
                            int value = Integer.parseInt(parts[1]);
                            challenge.applySetting(settingKey, value);

                            // Special sound for changing settings
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, value > 0 ? 1.5f : 0.8f);

                            // Cache the updated settings
                            cachedSettings.put(key, new HashMap<>(challenge.getSettings()));

                            // Create a new inventory with updated values
                            Inventory updatedInv = challenge.getSettingsInventory(player);
                            updateSettingsDisplay(updatedInv, challenge);

                            // Refresh inventory immediately
                            player.openInventory(updatedInv);
                            return;
                        }
                    }

                    // Start button
                    if (itemName.equals(ChatColor.GREEN + "Start Challenge")) {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.0f);
                        plugin.getChallengeManager().setSelectedChallenge(key);
                        openTeamSelectionForAllPlayers();
                        return;
                    }
                    break;
                }
            }
            return;
        }

        // Handle team selection inventory
        if (title.equals("Team Selection")) {
            event.setCancelled(true);

            itemName = clickedItem.getItemMeta().getDisplayName();

            // Team selection
            if (itemName.endsWith(" Team")) {
                String teamColor = itemName.substring(0, itemName.indexOf(" Team"));
                plugin.getChallengeManager().setPlayerTeam(player, teamColor);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.5f, 1.2f);
                player.sendMessage(ChatColor.GREEN + "You selected the " + teamColor + " team. Click confirm when ready.");
                return;
            }

            // Confirm button
            if (itemName.equals(ChatColor.GREEN + "Confirm Selection")) {
                if (plugin.getChallengeManager().getPlayerTeam(player).isEmpty()) {
                    player.sendMessage(ChatColor.RED + "You must select a team first!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 0.5f);
                    return;
                }

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.0f);
                plugin.getChallengeManager().confirmTeamSelection(player);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Team selection confirmed!");
                return;
            }
        }
    }

    /**
     * Updates the display of all setting controls in the inventory to show current values
     */
    private void updateSettingsDisplay(Inventory inv, Challenge challenge) {
        // Update increment/decrement button descriptions
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();

                // Only process setting control buttons
                if (!lore.isEmpty() && lore.get(0).startsWith("Change ")) {
                    String[] parts = lore.get(0).substring(7).split(" by ");
                    String settingKey = parts[0];
                    int changeValue = Integer.parseInt(parts[1]);

                    // Get current value of this setting
                    int currentValue = challenge.getSettings().getOrDefault(settingKey, 0);

                    // Create new lore with current value information
                    List<String> newLore = new ArrayList<>();
                    newLore.add(lore.get(0)); // Keep original "Change X by Y" line
                    newLore.add(ChatColor.GRAY + "Current value: " + ChatColor.YELLOW + currentValue);

                    meta.setLore(newLore);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    private void openTeamSelectionForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.openInventory(plugin.getChallengeManager().getTeamSelectionInventory(player));
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.0f);
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "Select your team to start the challenge!");
    }
}