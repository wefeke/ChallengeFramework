package wefeke.modFramework.Framework;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public interface Challenge {
    void start();
    void reset();
    void cancel();
    void pause();



    boolean isRunning();
    void setDuration(int duration);
    String getName();
    Map<String, Integer> getSettings();
    String getDescription();
    Inventory getSettingsInventory(Player player);
    void applySetting(String key, int value);
    void execute();
    Material getIcon();
}