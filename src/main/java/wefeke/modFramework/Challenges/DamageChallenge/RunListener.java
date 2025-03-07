package wefeke.modFramework.Challenges.DamageChallenge;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class RunListener implements Listener {

    private static final double FORWARD_THRESHOLD = 0.1; // Threshold to detect forward movement

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        player.sendMessage("You moved!");
    }
}