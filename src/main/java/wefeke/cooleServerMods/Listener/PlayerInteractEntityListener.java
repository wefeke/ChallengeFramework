package wefeke.cooleServerMods.Listener;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import wefeke.cooleServerMods.CooleServerMods;

public class PlayerInteractEntityListener implements Listener {

    private final CooleServerMods plugin;

    public PlayerInteractEntityListener(CooleServerMods plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Item item) {
            Player player = event.getPlayer();
            String woolName = item.getCustomName();
            if (woolName != null && woolName.startsWith("Team")) {
                player.sendMessage("You clicked on " + woolName);
                plugin.getPlayerInteractListener().removeWoolEntities();
            }
        }
    }

}
