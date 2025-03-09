package wefeke.challengeFramework;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wefeke.challengeFramework.Framework.ChallengeInventoryListener;
import wefeke.challengeFramework.Framework.TimerManager;
import wefeke.challengeFramework.GlobalCommands.*;
import wefeke.challengeFramework.Framework.ChallengeManager;
import wefeke.challengeFramework.Challenges.DamageChallenge.DamageChallenge;
import wefeke.challengeFramework.GlobalListeners.*;

import java.util.ArrayList;
import java.util.Objects;



public final class challengeFramework extends JavaPlugin {

    private ArrayList<Player> playerNotHungry;
    private ChallengeManager challengeManager;
    private TimerManager timerManager;

    public challengeFramework() {
        playerNotHungry = new ArrayList<>();
        timerManager = new TimerManager(this);
        challengeManager = new ChallengeManager(this, timerManager);
        timerManager.setChallengeManager(challengeManager);
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
        Objects.requireNonNull(getCommand("cancelchallenge")).setExecutor(new CancelChallengeCommand(challengeManager, timerManager));
        Objects.requireNonNull(getCommand("resetchallenge")).setExecutor(new ResetChallengeCommand(challengeManager));
        Objects.requireNonNull(getCommand("pausechallenge")).setExecutor(new PauseChallengeCommand(challengeManager, timerManager));
        Objects.requireNonNull(getCommand("resumechallenge")).setExecutor(new ResumeChallengeCommand(challengeManager));

        Objects.requireNonNull(getCommand("toggleHunger")).setExecutor(new HungerToggleCommand(this));
    }

    public void registerChallenges() {
        challengeManager.registerChallenge("damage", new DamageChallenge(this));
    }
}
