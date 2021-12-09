package fr.ariloxe.classic.module;

import com.pyralia.uhc.UHCApi;
import com.pyralia.uhc.game.GameState;
import com.pyralia.uhc.game.common.scoreboard.ScoreboardStage;
import com.pyralia.uhc.game.teams.UHCTeam;
import com.pyralia.uhc.game.teams.UHCTeamManager;
import com.pyralia.uhc.manager.GameManager;
import com.pyralia.uhc.module.Module;
import com.pyralia.uhc.player.UHCPlayer;
import com.pyralia.uhc.utils.PlayerUtils;
import fr.ariloxe.classic.Main;
import fr.ariloxe.classic.listener.ClassicListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Ariloxe
 */
public class Classic extends Module {

    public Classic(UHCApi instance) {
        super("§6§lUHCHost", Material.GOLDEN_APPLE, instance);
        super.setConfigurable(false);
        super.setHandler(new ClassicListener(this));
        super.setUhcScoreboard(new ClassicScoreboard());
    }

    @Override
    public void winTester() {

        GameManager gameManager = UHCApi.getApi().getGameManager();

        if(!UHCTeamManager.getInstance().isActivated()){
            if(gameManager.getAliveUhcPlayers().size() == 1){
                UHCPlayer uhcPlayer = gameManager.getAliveUhcPlayers().get(0);
                gameManager.setGameState(GameState.FINISH);

                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§6§lPyralia §8§l» §7Félicitations au joueur §a" + uhcPlayer.getName() + "§7 pour sa victoire en UHC avec §6" + uhcPlayer.getStats().getKills() + "§7 kills !");
                Bukkit.broadcastMessage("");

                restart();
            } else if(gameManager.getAliveUhcPlayers().size() == 1 && PlayerUtils.getPlayerWithoutSpec() == 1){
                UHCPlayer uhcPlayer = gameManager.getAliveUhcPlayers().stream().filter(uhcPlayer1 -> uhcPlayer1.getBukkitPlayer() != null && uhcPlayer1.getBukkitPlayer().getGameMode() != GameMode.SPECTATOR).findFirst().get();
                gameManager.setGameState(GameState.FINISH);

                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§6§lPyralia §8§l» §7Félicitations au joueur §a" + uhcPlayer.getName() + "§7 pour sa victoire en UHC avec §6" + uhcPlayer.getStats().getKills() + "§7 kills !");
                Bukkit.broadcastMessage("");

                restart();
            }
        } else {
            if(gameManager.getAliveUhcTeams().size() == 1){
                UHCTeam uhcTeam = gameManager.getAliveUhcTeams().get(0);
                gameManager.setGameState(GameState.FINISH);

                final int[] kills = new int[1];
                uhcTeam.getUhcPlayerList().forEach(uhcPlayer -> kills[0] = kills[0] + uhcPlayer.getStats().getKills());


                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§6§lPyralia §8§l» §7Félicitations à l'équipe " + uhcTeam.getDisplayName() + "§7 pour sa victoire en UHC avec §6" + kills[0] + "§7 kills !");
                Bukkit.broadcastMessage("");

                restart();
            }
        }
    }

    private void restart(){
        Bukkit.broadcastMessage("§6§lPyralia §8» (§cInfrastructure§8) §7: §cFermeture automatique du serveur dans 15 secondes !");

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> Bukkit.getOnlinePlayers().forEach(player -> UHCApi.getApi().getUhcInfraHandler().sendToLobby(player)), 20*15);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), Bukkit::shutdown, 20*17);
    }

    public void startScoreboardTask(){
        GameManager gameManager = UHCApi.getApi().getGameManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(gameManager.getScoreboardStage() == ScoreboardStage.INFOS){
                    if(gameManager.getPveKills() > 0)
                        gameManager.setScoreboardStage(ScoreboardStage.KILLS);
                    else if(UHCApi.getUHCPlayersMap().values().stream().anyMatch(uhcPlayer -> uhcPlayer.getStats().getKills() > 0))
                        gameManager.setScoreboardStage(ScoreboardStage.KILLS);
                } else
                    gameManager.setScoreboardStage(ScoreboardStage.INFOS);
            }
        }.runTaskTimer(Main.getInstance(), 8*20, 8*20);
    }

}
