package wefeke.cooleServerMods;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wefeke.cooleServerMods.Commands.BegruessungCommand;
import wefeke.cooleServerMods.Commands.HungerToggleCommand;
import wefeke.cooleServerMods.Entities.Team;
import wefeke.cooleServerMods.Listener.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;



public final class CooleServerMods extends JavaPlugin {

    private ArrayList<Player> playerNotHungry;

    private HashMap <Player, Team> teamHashMap;
    private PlayerInteractListener playerInteractListener;

    public CooleServerMods() {
        playerNotHungry = new ArrayList<>();
        teamHashMap = new HashMap<>();

    }

    public boolean isPlayerHungry(Player player){
        if (playerNotHungry.contains(player)){
            //player.sendMessage("Du bist in der Liste");
            return false;
        }
        else {
            //player.sendMessage("Du bist nicht in der Liste");
            return true;
        }
    }

    public void addPlayerNotHungry(Player player){
        this.playerNotHungry.add(player);
    }

    public void removePlayerNotHungry(Player player){
        this.playerNotHungry.remove(player);
    }

    @Override
    public void onEnable() {
        System.out.println("Startet alles");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
        playerInteractListener = new PlayerInteractListener(this);
        getServer().getPluginManager().registerEvents(playerInteractListener, this);
        Objects.requireNonNull(getCommand("begruessung")).setExecutor(new BegruessungCommand());
        Objects.requireNonNull(getCommand("toggleHunger")).setExecutor(new HungerToggleCommand(this));
    }

    @Override
    public void onDisable() {
        System.out.println("Shutdown");
    }

    public PlayerInteractListener getPlayerInteractListener() {
        return playerInteractListener;
    }
}
