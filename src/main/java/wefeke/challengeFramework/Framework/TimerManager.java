package wefeke.challengeFramework.Framework;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import wefeke.challengeFramework.challengeFramework;

public class TimerManager {

    // Timer fields (integrated from TimerManager)
    private int taskIdStart = -1;
    private int taskIdPause = -1;
    private int secondsLeft;
    private boolean isPaused = false;
    private String currentTimer;

    private final challengeFramework plugin;
    private ChallengeManager challengeManager;

    // Timer methods (integrated from TimerManager)

    public TimerManager(challengeFramework plugin) {
        this.plugin = plugin;
    }

    public void setChallengeManager(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    public void startTimer(int seconds) {
        this.secondsLeft = seconds;
        this.isPaused = false;

        if (taskIdStart > 0) {
            Bukkit.getScheduler().cancelTask(taskIdStart);
        }

        // Debug log
        plugin.getLogger().info("Starting timer: " + seconds + " seconds");

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
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GRAY + "Timer running.");
                    }
                } else {
                    // CRITICAL: Make sure challenge is properly ended
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.AQUA + "Timer Canceled.");
                    }
                    cancelTimer();
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
            startTimer(secondsLeft);
        }
    }

    public void cancelTimer() {
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

        Challenge activeChallenge = challengeManager.getActiveChallenge();
        if (activeChallenge != null) {
            challengeManager.cancelChallenge(activeChallenge);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Challenge canceled successfully.");
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Challenge in timer is not active.");
            }
        }

    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public String getCurrentTimer() {
        return currentTimer;
    }

}
