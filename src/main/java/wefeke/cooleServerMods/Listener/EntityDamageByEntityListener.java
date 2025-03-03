package wefeke.cooleServerMods.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.ItemMeta;
import wefeke.cooleServerMods.CooleServerMods;

public class EntityDamageByEntityListener implements Listener {


    private final CooleServerMods plugin;

    public EntityDamageByEntityListener(CooleServerMods plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Trident trident) {
            ItemMeta meta = trident.getItem().getItemMeta();
            if (meta != null && "Zeus".equals(meta.getDisplayName())) {
                Entity hitEntity = event.getEntity();
                if (hitEntity instanceof Player) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 0);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 20);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 40);
                }
                else {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 0);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 2);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 4);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 6);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 8);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 10);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 20);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 40);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> hitEntity.getWorld().strikeLightning(hitEntity.getLocation()), 60);

                }


            }
        }
    }
}
