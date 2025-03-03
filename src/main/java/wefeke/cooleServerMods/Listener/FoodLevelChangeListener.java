package wefeke.cooleServerMods.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import wefeke.cooleServerMods.CooleServerMods;


public class FoodLevelChangeListener implements Listener {

    private final CooleServerMods plugin;

    public FoodLevelChangeListener(CooleServerMods plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent foodLevelChangeEvent) {

        if (foodLevelChangeEvent.getEntity() instanceof Player player) {
            if (plugin.isPlayerHungry(player)) {
                foodLevelChangeEvent.setCancelled(false);
            }
            else {
                foodLevelChangeEvent.setCancelled(true);
            }
        }
    }
}
