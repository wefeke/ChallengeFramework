package wefeke.cooleServerMods.Commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class BegruessungCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player player) {
            player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 1,1);
        }

        return true;
    }
}
