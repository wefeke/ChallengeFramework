package wefeke.modFramework.GlobalCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wefeke.modFramework.Framework.ChallengeManager;
import wefeke.modFramework.modFramework;


public class PauseChallengeCommand implements CommandExecutor {

    private final ChallengeManager challengeManager;

    public PauseChallengeCommand(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }

        challengeManager.pauseChallenge(challengeManager.getActiveChallenge().getName());
        return true;
    }
}
