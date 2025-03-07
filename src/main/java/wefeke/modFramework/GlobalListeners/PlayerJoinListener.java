package wefeke.modFramework.GlobalListeners;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;



public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent playerJoinEvent) {

        Player player = playerJoinEvent.getPlayer();
        World world = player.getWorld();

        if (player.hasPlayedBefore()) {
            player.sendMessage("Willkommen zurück " + player.getDisplayName());
        } else {
            player.sendMessage("Moin " + player.getDisplayName());
        }

        // Set game mode to peaceful
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setDifficulty(org.bukkit.Difficulty.PEACEFUL);

        // Set time to day
        world.setTime(1000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    }

}
