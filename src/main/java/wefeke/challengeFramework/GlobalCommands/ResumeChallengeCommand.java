package wefeke.challengeFramework.GlobalCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wefeke.challengeFramework.Framework.Challenge;
import wefeke.challengeFramework.Framework.ChallengeManager;


public class ResumeChallengeCommand implements CommandExecutor {

    private final ChallengeManager challengeManager;

    public ResumeChallengeCommand(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }

        Challenge activeChallenge = challengeManager.getActiveChallenge();
        challengeManager.resumeChallenge(activeChallenge);
        return true;
    }
}
