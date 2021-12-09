package fr.ariloxe.classic.listener;

import com.pyralia.uhc.UHCApi;
import com.pyralia.uhc.events.custom.DeathEvent;
import com.pyralia.uhc.events.custom.StartEvent;
import com.pyralia.uhc.game.rules.HostRuleBoolean;
import com.pyralia.uhc.game.teams.UHCTeam;
import com.pyralia.uhc.game.teams.UHCTeamManager;
import com.pyralia.uhc.manager.GameManager;
import com.pyralia.uhc.player.UHCPlayer;
import com.pyralia.uhc.player.UHCPlayerState;
import com.pyralia.uhc.utils.SoundUtils;
import fr.ariloxe.classic.Main;
import fr.ariloxe.classic.module.Classic;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * @author Ariloxe
 */
public class ClassicListener implements Listener {

    private final Classic classic;
    public ClassicListener(Classic classic){
        this.classic = classic;
    }

    @EventHandler
    public void onDeath(DeathEvent deathEvent){
        GameManager gameManager = UHCApi.getApi().getGameManager();
        UHCPlayer victim = deathEvent.getVictim();

        if(!UHCTeamManager.getInstance().isActivated()){
            if(deathEvent.getKiller() != null)
                Bukkit.broadcastMessage("§6§lPyralia §8§l» §c" + victim.getName() + "§7 was slain by §a" + deathEvent.getKiller().getName());
            else
                Bukkit.broadcastMessage("§6§lPyralia §8§l» §c" + victim.getName() + "§7 est mort !");
        } else {
            UHCTeam uhcTeam = victim.getUhcTeam();
            if(deathEvent.getKiller() != null)
                Bukkit.broadcastMessage("§6§lPyralia §8§l» " + uhcTeam.getPrefix() + victim.getName() + "§7 was slain by " + deathEvent.getKiller().getUhcTeam().getPrefix() + deathEvent.getKiller().getName());
            else
                Bukkit.broadcastMessage("§6§lPyralia §8§l» " + uhcTeam.getPrefix() + victim.getName() + "§7 est mort !");

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()->{
                if(!victim.getUhcTeam().isAlive()){
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()->{
                        victim.getUhcTeam().destroy();
                    }, 2);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), classic::winTester, 5);
                }
            }, 15);
        }

        SoundUtils.playSoundToAll(Sound.WITHER_SPAWN);
        gameManager.getAliveUhcPlayers().remove(victim);
        gameManager.getDisconnectedList().remove(victim);
        victim.setState(UHCPlayerState.DEAD);

        classic.winTester();
        Player player = victim.getBukkitPlayer();
        if(player != null){
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> player.spigot().respawn(), 3);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()->{
                if(HostRuleBoolean.SPEC.get())
                    player.setGameMode(GameMode.SPECTATOR);
                else {
                    UHCApi.getApi().getUhcInfraHandler().sendToLobby(player);
                    player.sendMessage("§6§lPyralia §8§l» §7Merci d'avoir joué ! Malheureusement, les spectateurs étant désactivés vous ne pouvez pas regarder cette partie !");
                }
            }, 5);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent playerRespawnEvent){
        Player player = playerRespawnEvent.getPlayer();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()->{
            if(HostRuleBoolean.SPEC.get())
                player.setGameMode(GameMode.SPECTATOR);
            else {
                UHCApi.getApi().getUhcInfraHandler().sendToLobby(player);
                player.sendMessage("§6§lPyralia §8§l» §7Merci d'avoir joué ! Malheureusement, les spectateurs étant désactivés vous ne pouvez pas regarder cette partie !");
            }
        }, 5);
    }

    @EventHandler
    public void onStart(StartEvent startEvent){
        classic.startScoreboardTask();
    }

}
