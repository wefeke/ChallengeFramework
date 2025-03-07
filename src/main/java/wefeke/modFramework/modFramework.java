package wefeke.modFramework;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wefeke.modFramework.Framework.ChallengeInventoryListener;
import wefeke.modFramework.GlobalCommands.*;
import wefeke.modFramework.Framework.ChallengeManager;
import wefeke.modFramework.Challenges.DamageChallenge.DamageChallenge;
import wefeke.modFramework.GlobalListeners.*;

import java.util.ArrayList;
import java.util.Objects;



public final class modFramework extends JavaPlugin {

    private ArrayList<Player> playerNotHungry;
    private ChallengeManager challengeManager;

    public modFramework() {
        playerNotHungry = new ArrayList<>();
        challengeManager = new ChallengeManager(this);
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

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    @Override
    public void onEnable() {
        try {
            registerGlobalListeners();
            registerCommands();
            registerChallenges();
            getLogger().info("Plugin enabled successfully!");
        } catch (Exception e) {
            getLogger().severe("Error while enabling plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Shutdown");
    }

    public void registerGlobalListeners() {

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
            getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
            getServer().getPluginManager().registerEvents(new ChallengeInventoryListener(this), this);
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("startchallenge")).setExecutor(new StartChallengeCommand(challengeManager));
        Objects.requireNonNull(getCommand("startchallenge")).setExecutor(new CancelChallengeCommand(challengeManager));
        Objects.requireNonNull(getCommand("startchallenge")).setExecutor(new ResetChallengeCommand(challengeManager));
        Objects.requireNonNull(getCommand("startchallenge")).setExecutor(new PauseChallengeCommand(challengeManager));
        Objects.requireNonNull(getCommand("startchallenge")).setExecutor(new ResumeChallengeCommand(challengeManager));

        Objects.requireNonNull(getCommand("toggleHunger")).setExecutor(new HungerToggleCommand(this));
    }

    public void registerChallenges() {
        challengeManager.registerChallenge("damage", new DamageChallenge(this));
    }
}
