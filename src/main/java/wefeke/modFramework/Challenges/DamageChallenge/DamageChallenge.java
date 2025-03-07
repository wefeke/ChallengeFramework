package wefeke.modFramework.Challenges.DamageChallenge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import wefeke.modFramework.Framework.BaseChallenge;

import java.util.Map;

public class DamageChallenge extends BaseChallenge {
    private PlayerDamageListener playerDamageListener;

    private int damageMultiplier = 1;

    public DamageChallenge(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void initSettings() {
        settings.put("duration", 60);
        settings.put("damageMultiplier", 1);
    }

    @Override
    public void execute() {
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new RunListener(), plugin);
    }

    @Override
    public void pause() {
        if (!running) return;
        running = false;
        Bukkit.getScheduler().cancelTask(taskId);

        // Notify players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.YELLOW + getName() + " challenge has been paused!");
        }
    }

    @Override
    public void cancel() {
        if (!running) return;
        EntityDamageEvent.getHandlerList().unregister(playerDamageListener);

        super.cancel();
    }

    @Override
    public String getName() {
        return "Damage Share";
    }

    @Override
    public String getDescription() {
        return "When a player takes damage, all players take damage";
    }

    @Override
    public Inventory getSettingsInventory(Player player) {
        Inventory inv = super.getSettingsInventory(player);
        addSettingControls(inv, 16, "damageMultiplier", "Damage Multiplier", Material.DIAMOND_SWORD, 2, 1);
        return inv;
    }

    public int getDamageMultiplier() {
        return settings.getOrDefault("damageMultiplier", 1);
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public Map<String, Integer> getSettings() {
        return settings;
    }

}