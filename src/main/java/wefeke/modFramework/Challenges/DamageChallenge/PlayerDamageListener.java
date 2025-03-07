package wefeke.modFramework.Challenges.DamageChallenge;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

public class PlayerDamageListener implements Listener {
    private final DamageChallenge challenge;

    public PlayerDamageListener(DamageChallenge challenge) {
        this.challenge = challenge;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!Objects.requireNonNull(challenge).isRunning())
            return;

        if (event.getEntity() instanceof Player damagedPlayer) {
            double damage = event.getDamage() * challenge.getDamageMultiplier();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != damagedPlayer) {
                    player.damage(damage);
                }
            }
        }
    }
}