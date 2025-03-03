package wefeke.cooleServerMods.Listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;


public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (blockBreakEvent.getBlock().getType() == Material.JUNGLE_LOG){
            blockBreakEvent.getBlock().getWorld().dropItemNaturally(blockBreakEvent.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
            blockBreakEvent.getPlayer().playSound(blockBreakEvent.getPlayer().getLocation(), "minecraft:entity.player.levelup", 1,1);
        }
    }
}
