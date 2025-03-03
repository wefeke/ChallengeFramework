package wefeke.cooleServerMods.Listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import wefeke.cooleServerMods.CooleServerMods;

public class ProjectileHitListener implements Listener {

    private final CooleServerMods plugin;

    public ProjectileHitListener(CooleServerMods plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if ("MagicProjectile".equals(projectile.getCustomName())) {
            Block hitBlock = event.getHitBlock();
            if (hitBlock != null) {
                hitBlock.setType(Material.RED_WOOL);
            }
        }
    }

}
