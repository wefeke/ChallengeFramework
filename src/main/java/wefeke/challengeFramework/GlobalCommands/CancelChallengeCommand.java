package wefeke.challengeFramework.GlobalCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wefeke.challengeFramework.Framework.Challenge;
import wefeke.challengeFramework.Framework.ChallengeManager;
import wefeke.challengeFramework.Framework.TimerManager;


public class CancelChallengeCommand implements CommandExecutor {

    private final ChallengeManager challengeManager;
    private final TimerManager timerManager;

    public CancelChallengeCommand(ChallengeManager challengeManager, TimerManager timerManager) {
        this.challengeManager = challengeManager;
        this.timerManager = timerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }


        Challenge activeChallenge = challengeManager.getActiveChallenge();
        if (activeChallenge != null) {
                challengeManager.cancelChallenge(activeChallenge);
                timerManager.cancelTimer();
                player.sendMessage(ChatColor.GREEN + "Challenge canceled successfully.");
        } else {
            player.sendMessage(ChatColor.RED + "No active challenge to cancel.");
        }

        return true;
    }
}
