package wefeke.cooleServerMods.Listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;

import java.util.Arrays;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent playerJoinEvent) {

        Player player = playerJoinEvent.getPlayer();

        if (player.hasPlayedBefore()) {
            player.sendMessage("Willkommen zurück");
        } else {
            player.sendMessage("Moin Alda");
            player.setDisplayName("Hurensohn");
            ItemStack dias = new ItemStack(Material.DIAMOND, 1);
            ItemMeta diaMeta = dias.getItemMeta();
            diaMeta.setDisplayName("Eisen");
            diaMeta.setEnchantable(1);
            diaMeta.addEnchant(Enchantment.SHARPNESS, 5, true);
            diaMeta.setLore(Arrays.asList("Das ist ein geiler Diamant", "Der ist sehr selten"));
            dias.setItemMeta(diaMeta);
            player.getInventory().addItem(dias);
        }

        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("Team Joiner");
        bookMeta.setLore(Arrays.asList("Mit diesem Buch kannst du einem Team beitreten"));
        bookMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        book.setItemMeta(bookMeta);
        player.getInventory().addItem(book);

        ItemStack trident = new ItemStack(Material.TRIDENT, 1);
        ItemMeta tridentMeta = trident.getItemMeta();
        if (tridentMeta != null) {
            tridentMeta.setDisplayName("Zeus");
            tridentMeta.addEnchant(Enchantment.LOYALTY, 3, true);
            tridentMeta.addEnchant(Enchantment.IMPALING, 5, true);
            tridentMeta.addEnchant(Enchantment.UNBREAKING, 3, true);
            trident.setItemMeta(tridentMeta);
        }
        player.getInventory().addItem(trident);


        ItemStack stick = new ItemStack(Material.STICK, 1);
        ItemMeta stickMeta = stick.getItemMeta();
        if (stickMeta != null) {
            stickMeta.setDisplayName("Magic Stick");
            stick.setItemMeta(stickMeta);
        }
        player.getInventory().addItem(stick);

        // Add the Klopfer item
        ItemStack klopfer = new ItemStack(Material.RABBIT_FOOT, 1);
        ItemMeta klopferMeta = klopfer.getItemMeta();
        if (klopferMeta != null) {
            klopferMeta.setDisplayName("Klopfer");
            klopferMeta.setLore(Arrays.asList("Rechtsklick, um 60 Blöcke hoch zu springen"));
            klopfer.setItemMeta(klopferMeta);
        }
        player.getInventory().addItem(klopfer);
    }

}
