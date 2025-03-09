package wefeke.challengeFramework.GlobalListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import wefeke.challengeFramework.challengeFramework;


public class FoodLevelChangeListener implements Listener {

    private final challengeFramework plugin;

    public FoodLevelChangeListener(challengeFramework plugin) {
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
