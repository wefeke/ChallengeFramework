package wefeke.modFramework.GlobalListeners;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;



public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent playerJoinEvent) {

        Player player = playerJoinEvent.getPlayer();

        if (player.hasPlayedBefore()) {
            player.sendMessage("Willkommen zurück " + player.getDisplayName());
        } else {
            player.sendMessage("Moin " + player.getDisplayName());
        }
    }

}
