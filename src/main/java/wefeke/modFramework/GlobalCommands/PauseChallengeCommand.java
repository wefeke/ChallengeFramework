package wefeke.modFramework.GlobalCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wefeke.modFramework.Framework.Challenge;
import wefeke.modFramework.Framework.ChallengeManager;
import wefeke.modFramework.Framework.TimerManager;
import wefeke.modFramework.modFramework;


public class PauseChallengeCommand implements CommandExecutor {

    private final ChallengeManager challengeManager;
    private final TimerManager timerManager;

    public PauseChallengeCommand(ChallengeManager challengeManager, TimerManager timerManager) {
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
        challengeManager.pauseChallenge(activeChallenge);
        return true;
    }
}
