package wefeke.challengeFramework.Framework;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseChallenge implements Challenge {
    protected final JavaPlugin plugin;
    protected boolean running;
    protected int taskId;
    protected int duration = 10; // Default to 60 seconds
    protected Map<String, Integer> settings = new HashMap<>();

    public BaseChallenge(JavaPlugin plugin) {
        this.plugin = plugin;
        this.running = false;
        initSettings();
    }

    protected abstract void initSettings();

    @Override
    public void start() {
        if (running) return;
        running = true;
        execute();


        // Notify players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + getName() + " challenge has started! Duration: " + duration + " seconds");
        }
    }

    @Override
    public void cancel() {
        if (!running) return;
        running = false;

        // Notify players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.RED + getName() + " challenge has ended!");
        }
    }

    @Override
    public void reset() {
        cancel();
        start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
        settings.put("duration", duration);
    }

    @Override
    public Inventory getSettingsInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, getName() + " Settings");

        // Timer settings (center row)
        addSettingControls(inv, 10, "duration", "Timer", Material.CLOCK, 10, 1);


        // Add start button
        ItemStack startButton = new ItemStack(Material.GREEN_WOOL);
        ItemMeta startMeta = startButton.getItemMeta();
        startMeta.setDisplayName(ChatColor.GREEN + "Start Challenge");
        startMeta.setLore(Arrays.asList("Click to proceed to team selection"));
        startButton.setItemMeta(startMeta);
        inv.setItem(22, startButton);

        return inv;
    }

    // Get the icon for this challenge in the selection menu
    public Material getIcon() {
        return Material.BOOK; // Default icon, override in subclasses
    }

    protected void addSettingControls(Inventory inv, int slot, String key, String displayName, Material material, int bigIncrement, int smallIncrement) {
        int currentValue = settings.getOrDefault(key, 0);

        // Current value indicator
        ItemStack currentItem = new ItemStack(material);
        ItemMeta meta = currentItem.getItemMeta();
        meta.setDisplayName(displayName + ": " + currentValue);
        currentItem.setItemMeta(meta);
        inv.setItem(slot, currentItem);

        // Add increment buttons
        createButton(inv, slot - 9, "+" + bigIncrement, Material.GREEN_CONCRETE, key, bigIncrement);

        // Add decrement buttons
        createButton(inv, slot + 9, "-" + bigIncrement, Material.RED_CONCRETE, key, -bigIncrement);
    }

    private void createButton(Inventory inv, int slot, String name, Material material, String key, int change) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("Change " + key + " by " + change));
        button.setItemMeta(meta);
        inv.setItem(slot, button);
    }

    @Override
    public void applySetting(String key, int value) {
        settings.put(key, Math.max(0, settings.getOrDefault(key, 0) + value));
        if (key.equals("duration")) {
            duration = settings.get(key);
        }
    }

}