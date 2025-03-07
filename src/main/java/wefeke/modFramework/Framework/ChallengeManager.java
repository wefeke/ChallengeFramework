package wefeke.modFramework.Framework;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
    private final JavaPlugin plugin;
    private Challenge activeChallenge = null;
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<String, ChatColor> availableTeams = new HashMap<>();
    private final Set<UUID> confirmedPlayers = new HashSet<>();
    private String selectedChallenge;

    // Timer fields (integrated from TimerManager)
    private int taskIdStart = -1;
    private int taskIdPause = -1;
    private int secondsLeft;
    private boolean isPaused = false;
    private String challengeName;
    private String currentTimer;

    public ChallengeManager(JavaPlugin plugin) {
        this.plugin = plugin;

        // Initialize available teams
        availableTeams.put("Red", ChatColor.RED);
        availableTeams.put("Blue", ChatColor.BLUE);
        availableTeams.put("Green", ChatColor.GREEN);
        availableTeams.put("Yellow", ChatColor.YELLOW);
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

    public Challenge getActiveChallenge() {
        return activeChallenge;
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
            startTimer(duration, challenge.getName());
            this.challengeName = name;
            this.secondsLeft = duration;
            this.isPaused = false;

            if (taskIdStart > 0) {
                Bukkit.getScheduler().cancelTask(taskIdStart);
            }

            // Debug log
            plugin.getLogger().info("Starting timer: " + duration + " seconds for " + name);

            taskIdStart = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                int timeLeft = duration;

                @Override
                public void run() {
                    if (timeLeft > 0) {
                        secondsLeft = timeLeft;
                        StringBuilder message = new StringBuilder();
                        message.append(ChatColor.GOLD).append(ChatColor.BOLD);

                        int hoursRemaining = timeLeft / 3600;
                        int minutesRemaining = (timeLeft % 3600) / 60;
                        int secondsRemaining = timeLeft % 60;

                        // Format hours
                        if (hoursRemaining > 0) {
                            if (hoursRemaining < 10) {
                                message.append("0");
                            }
                            message.append(hoursRemaining);
                        } else {
                            message.append("00");
                        }
                        message.append(":");

                        // Format minutes
                        if (minutesRemaining > 0) {
                            if (minutesRemaining < 10) {
                                message.append("0");
                            }
                            message.append(minutesRemaining);
                        } else {
                            message.append("00");
                        }
                        message.append(":");

                        // Format seconds
                        if (secondsRemaining > 0) {
                            if (secondsRemaining < 10) {
                                message.append("0");
                            }
                            message.append(secondsRemaining);
                        } else {
                            message.append("00");
                        }

                        currentTimer = message.toString();

                        // Display to all players
                        Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player ->
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(currentTimer))
                        ));

                        timeLeft--;
                    } else {
                        // CRITICAL: Make sure challenge is properly ended
                        stopTimer();
                        finishActiveChallenge();

                        plugin.getLogger().info("Timer expired - ending challenge");
                    }
                }
            }, 0L, 20L); // 20 ticks = 1 second


            // Notify players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + challenge.getName() + " challenge has started! Duration: " + duration + " seconds");
            }
        }
    }

    public void resetChallenge(String name) {
        Challenge challenge = challenges.get(name);
        if (challenge != null) {
            challenge.reset();
        }
    }

    public void cancelChallenge(String name) {
        Challenge challenge = challenges.get(name);
        if (challenge != null) {
            challenge.cancel();
            if (activeChallenge == challenge) {
                activeChallenge = null;
                stopTimer();
            }
        }
    }

    public void pauseChallenge(String name) {
        Challenge challenge = challenges.get(name);
        if (challenge != null) {
            challenge.pause();
            pauseTimer();
        }
    }

    public void resumeChallenge(String name) {
        Challenge challenge = challenges.get(name);
        if (challenge != null && !challenge.isRunning()) {
            challenge.start();
            resumeTimer();

            // Notify players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + challenge.getName() + " challenge has been resumed!");
            }
        }
    }

    public void finishActiveChallenge() {
        if (activeChallenge != null) {
            activeChallenge.cancel();
            activeChallenge = null;
        }
    }

    // Selected Challenge management

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
        Challenge challenge = challenges.get(challengeName);
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

    // Timer methods (integrated from TimerManager)

    public void startTimer(int seconds, String name) {
        this.challengeName = name;
        this.secondsLeft = seconds;
        this.isPaused = false;

        if (taskIdStart > 0) {
            Bukkit.getScheduler().cancelTask(taskIdStart);
        }

        // Debug log
        plugin.getLogger().info("Starting timer: " + seconds + " seconds for " + name);

        taskIdStart = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int timeLeft = seconds;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    secondsLeft = timeLeft;
                    StringBuilder message = new StringBuilder();
                    message.append(ChatColor.GOLD).append(ChatColor.BOLD);

                    int hoursRemaining = timeLeft / 3600;
                    int minutesRemaining = (timeLeft % 3600) / 60;
                    int secondsRemaining = timeLeft % 60;

                    // Format hours
                    if (hoursRemaining > 0) {
                        if (hoursRemaining < 10) {
                            message.append("0");
                        }
                        message.append(hoursRemaining);
                    } else {
                        message.append("00");
                    }
                    message.append(":");

                    // Format minutes
                    if (minutesRemaining > 0) {
                        if (minutesRemaining < 10) {
                            message.append("0");
                        }
                        message.append(minutesRemaining);
                    } else {
                        message.append("00");
                    }
                    message.append(":");

                    // Format seconds
                    if (secondsRemaining > 0) {
                        if (secondsRemaining < 10) {
                            message.append("0");
                        }
                        message.append(secondsRemaining);
                    } else {
                        message.append("00");
                    }

                    currentTimer = message.toString();

                    // Display to all players
                    Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player ->
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(currentTimer))
                    ));

                    timeLeft--;
                } else {
                    // CRITICAL: Make sure challenge is properly ended
                    stopTimer();
                    finishActiveChallenge();

                    plugin.getLogger().info("Timer expired - ending challenge");
                }
            }
        }, 0L, 20L); // 20 ticks = 1 second
    }

    public void pauseTimer() {
        if (secondsLeft > 0) {
            Bukkit.getScheduler().cancelTask(taskIdStart);
            taskIdPause = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player ->
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(currentTimer + " §cPAUSED"))
                    ));
                }
            }, 0L, 20L);
            isPaused = true;
        }
    }

    public void resumeTimer() {
        if (isPaused && secondsLeft > 0) {
            Bukkit.getScheduler().cancelTask(taskIdPause);
            startTimer(secondsLeft, challengeName);
        }
    }

    public void stopTimer() {
        if (taskIdStart > 0) {
            Bukkit.getScheduler().cancelTask(taskIdStart);
            taskIdStart = -1;
        }
        if (taskIdPause > 0) {
            Bukkit.getScheduler().cancelTask(taskIdPause);
            taskIdPause = -1;
        }

        // Clear action bar
        Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player ->
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""))
        ));
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public String getCurrentTimer() {
        return currentTimer;
    }
}