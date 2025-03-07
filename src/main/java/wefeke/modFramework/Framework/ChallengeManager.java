package wefeke.modFramework.Framework;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class ChallengeManager {
    private final Map<String, Challenge> challenges = new HashMap<>();
    private Challenge activeChallenge = null;
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<String, ChatColor> availableTeams = new HashMap<>();
    private final Set<UUID> confirmedPlayers = new HashSet<>();
    private String selectedChallenge;

    // Passed objects
    private final TimerManager timerManager;
    private final JavaPlugin plugin;

    public ChallengeManager(JavaPlugin plugin, TimerManager timerManager) {
        this.plugin = plugin;
        this.timerManager = timerManager;

        // Initialize available teams
        availableTeams.put("Red", ChatColor.RED);
        availableTeams.put("Blue", ChatColor.BLUE);
        availableTeams.put("Green", ChatColor.GREEN);
        availableTeams.put("Yellow", ChatColor.YELLOW);
        availableTeams.put("Pink", ChatColor.LIGHT_PURPLE);
        availableTeams.put("Aqua", ChatColor.AQUA);
        availableTeams.put("White", ChatColor.WHITE);
        availableTeams.put("Purple", ChatColor.DARK_PURPLE);

    }

    // General Challenge management
    public void registerChallenge(String name, Challenge challenge) {
        challenges.put(name, challenge);
    }

    public Map<String, Challenge> getChallenges() {
        return challenges;
    }

    public Challenge getChallenge(String name) {
        return challenges.get(name);
    }

    public void startChallenge(String name, int duration) {
        if (activeChallenge != null && activeChallenge.isRunning()) {
            activeChallenge.cancel();
        }

        Challenge challenge = challenges.get(name);

        if (challenge != null) {
            challenge.setDuration(duration);
            challenge.execute();
            activeChallenge = challenge;

            // Start the timer display
            timerManager.startTimer(duration);

            // Notify players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + challenge.getName() + " challenge has started! Duration: " + duration + " seconds");
                player.sendMessage(ChatColor.GREEN + activeChallenge.getName() + " active challenge has started! Duration: " + duration + " seconds");
            }
        }
    }

    public void resetChallenge(Challenge challenge) {
        if (challenge != null) {
            challenge.reset();
        }
    }

    public void cancelChallenge(Challenge challenge) {

        if (challenge != null) {
            challenge.cancel();
            activeChallenge = null;
            timerManager.cancelTimer();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.YELLOW + "Challenge canceled:" + challenge.getName());
            }
        }
    }

    public void pauseChallenge(Challenge challenge) {
        if (challenge != null) {
            challenge.pause();
            timerManager.pauseTimer();
        }
    }

    public void resumeChallenge(Challenge challenge) {
        if (challenge != null && !challenge.isRunning()) {
            challenge.start();
            timerManager.resumeTimer();

            // Notify players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + challenge.getName() + " challenge has been resumed!");
            }
        }
    }

    //Active Challenge
    public Challenge getActiveChallenge() {
        return activeChallenge;
    }

    public void setSelectedChallenge(String challengeName) {
        this.selectedChallenge = challengeName;
    }

    private void startSelectedChallenge() {
        if (selectedChallenge != null && challenges.containsKey(selectedChallenge)) {
            Challenge challenge = challenges.get(selectedChallenge);
            startChallenge(selectedChallenge, challenge.getSettings().get("duration"));

            // Reset for next time
            confirmedPlayers.clear();
            selectedChallenge = null;
        }
    }

    public void applySetting(String challengeName, String key, int value) {
        Challenge challenge = getChallenge(challengeName);
        if (challenge != null) {
            challenge.applySetting(key, value);
        }
    }

    public Inventory getTeamSelectionInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Team Selection");

        int slot = 10;
        for (Map.Entry<String, ChatColor> team : availableTeams.entrySet()) {
            ItemStack item = new ItemStack(getTeamMaterial(team.getKey()));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(team.getValue() + team.getKey() + " Team");
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        // Add confirm button
        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Selection");
        confirm.setItemMeta(confirmMeta);
        inv.setItem(22, confirm);

        return inv;
    }

    private Material getTeamMaterial(String team) {
        return switch (team) {
            case "Red" -> Material.RED_WOOL;
            case "Blue" -> Material.BLUE_WOOL;
            case "Green" -> Material.LIME_WOOL;
            case "Yellow" -> Material.YELLOW_WOOL;
            case "Pink" -> Material.PINK_WOOL;
            case "Aqua" -> Material.CYAN_WOOL;
            default -> Material.WHITE_WOOL;
        };
    }

    public void setPlayerTeam(Player player, String team) {
        playerTeams.put(player.getUniqueId(), team);
    }

    public String getPlayerTeam(Player player) {
        return playerTeams.getOrDefault(player.getUniqueId(), "");
    }

    public void confirmTeamSelection(Player player) {
        confirmedPlayers.add(player.getUniqueId());

        // Check if all online players confirmed
        if (confirmedPlayers.size() >= Bukkit.getOnlinePlayers().size()) {
            // Everyone confirmed, start the challenge
            startSelectedChallenge();
        } else {
            // Notify how many players still need to confirm
            int remaining = Bukkit.getOnlinePlayers().size() - confirmedPlayers.size();
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Waiting for " + remaining + " more player(s) to select teams...");
        }
    }

    public Inventory getChallengeSelectionInventory(Player player) {
        int size = 9 * Math.max(1, 1 + (challenges.size() / 7));
        Inventory inv = Bukkit.createInventory(null, size, "Challenge Selection");

        int slot = 0;
        for (Map.Entry<String, Challenge> entry : challenges.entrySet()) {
            Material icon = Material.BOOK; // Default Icon
            Challenge challenge = entry.getValue();

            // If the challenge specifies an icon, use it
            if (challenge instanceof BaseChallenge baseChallenge) {
                icon = baseChallenge.getIcon();
            }

            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + challenge.getName());
            meta.setLore(Arrays.asList(
                    challenge.getDescription(),
                    "",
                    ChatColor.YELLOW + "Click to configure settings"
            ));
            item.setItemMeta(meta);

            inv.setItem(slot++, item);

            // Add a gap after every 7 items
            if ((slot - 10) % 7 == 0) {
                slot += 2;
            }
        }
        return inv;
    }




}