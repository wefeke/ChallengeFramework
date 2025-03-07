package wefeke.modFramework.Framework;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import wefeke.modFramework.modFramework;

public class ChallengeInventoryListener implements Listener {
    private final modFramework plugin;

    public ChallengeInventoryListener(modFramework plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();

        // Handle main challenge selection inventory
        if (title.equals("Challenge Selection")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }

            String challengeName = clickedItem.getItemMeta().getDisplayName().substring(2); // Remove color code

            for (String key : plugin.getChallengeManager().getChallenges().keySet()) {
                Challenge challenge = plugin.getChallengeManager().getChallenge(key);
                if (challenge.getName().equals(challengeName)) {
                    player.openInventory(challenge.getSettingsInventory(player));
                    break;
                }
            }
            return;
        }

        // Handle settings inventories
        if (title.endsWith(" Settings")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }

            String challengeName = title.substring(0, title.indexOf(" Settings"));
            String itemName = clickedItem.getItemMeta().getDisplayName();

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

                            // Refresh inventory
                            player.openInventory(challenge.getSettingsInventory(player));
                        }
                    }

                    // Start button
                    if (itemName.equals(ChatColor.GREEN + "Start Challenge")) {
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

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();

            // Team selection
            if (itemName.endsWith(" Team")) {
                String teamColor = itemName.substring(0, itemName.indexOf(" Team"));
                plugin.getChallengeManager().setPlayerTeam(player, teamColor);
                player.sendMessage(ChatColor.GREEN + "You selected the " + teamColor + " team. Click confirm when ready.");
                return;
            }

            // Confirm button
            if (itemName.equals(ChatColor.GREEN + "Confirm Selection")) {
                if (plugin.getChallengeManager().getPlayerTeam(player).isEmpty()) {
                    player.sendMessage(ChatColor.RED + "You must select a team first!");
                    return;
                }

                plugin.getChallengeManager().confirmTeamSelection(player);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Team selection confirmed!");
                return;
            }
        }
    }

    private void openTeamSelectionForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.openInventory(plugin.getChallengeManager().getTeamSelectionInventory(player));
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "Select your team to start the challenge!");
    }
}