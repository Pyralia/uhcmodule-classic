package fr.ariloxe.classic.module;

import com.pyralia.uhc.UHCApi;
import com.pyralia.uhc.game.common.DirectionnalArrow;
import com.pyralia.uhc.game.common.scoreboard.ScoreboardStage;
import com.pyralia.uhc.game.common.scoreboard.UHCScoreboard;
import com.pyralia.uhc.game.common.scoreboard.tools.ObjectiveSign;
import com.pyralia.uhc.game.rules.HostRuleDouble;
import com.pyralia.uhc.game.rules.HostRuleInteger;
import com.pyralia.uhc.game.task.TimerTask;
import com.pyralia.uhc.game.teams.UHCTeamManager;
import com.pyralia.uhc.manager.GameManager;
import com.pyralia.uhc.player.UHCPlayer;
import com.pyralia.uhc.player.UHCPlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ariloxe
 */
public class ClassicScoreboard implements UHCScoreboard {

    @Override
    public void updateScoreboard(ObjectiveSign objectiveSign, UUID uuid, String ip) {
        GameManager gameManager = UHCApi.getApi().getGameManager();

        if(gameManager.getScoreboardStage() == ScoreboardStage.INFOS){
            Player player = Bukkit.getPlayer(uuid);
            int a = UHCApi.getApi().getWorldManager().getUhcBorder().getSize();

            objectiveSign.setDisplayName("§6§lUHCHost");
            objectiveSign.setLine(0, "§f");
            objectiveSign.setLine(1, " §8┃ §7Host: §a" + gameManager.getHost());
            objectiveSign.setLine(2, " §8┃ §7Joueur(s): §e" + gameManager.getAliveUhcPlayers().size() + (UHCTeamManager.getInstance().isActivated() ? " §7(" + gameManager.getAliveUhcTeams().size() + ")" : ""));
            objectiveSign.setLine(3, "§d");
            objectiveSign.setLine(4, " §8┃ §7Temps: §f" + gameManager.getTimerTask().getTimer());
            objectiveSign.setLine(5, " §8┃ §7PvP: " + getTimeBeforePvP());
            objectiveSign.setLine(6, " §8┃ §7Bordure: " + getTimeBeforeBorder());
            objectiveSign.setLine(7, "§8");
            objectiveSign.setLine(8, " §8┃ §7Rayon: §c±" + a / 2 + "§8 (§f" + HostRuleDouble.BLOCKS_PER_SECONDS.getValue() + "b/s§8)");
            objectiveSign.setLine(9, " §8┃ §7Centre: §c" + DirectionnalArrow.distance(player.getLocation(), UHCApi.getApi().getWorldManager().getCenter()) + "m " + DirectionnalArrow.Fleche(DirectionnalArrow.Angle(player, UHCApi.getApi().getWorldManager().getCenter())));
            objectiveSign.setLine(10, "§e");
            objectiveSign.setLine(11, "§8§l❯ §6" + ip);
        } else if(gameManager.getScoreboardStage() == ScoreboardStage.KILLS){

            objectiveSign.setDisplayName("§6§lPyralia §8§l» §f§lKills");

            int lineNumber = 0;
            List<String> topKills = new LinkedList<>();
            List<Integer> topKillCounts = new LinkedList<>();

            for (UHCPlayer uhcPlayer : UHCApi.getUHCPlayersMap().values()) {
                if (uhcPlayer.getStats().getKills() != 0) {
                    topKills.add(uhcPlayer.getName());
                    topKillCounts.add(uhcPlayer.getStats().getKills());

                    for (int i = 0; i < topKillCounts.size(); i++) {
                        if (i == 10)
                            break;

                        if (uhcPlayer.getStats().getKills() > topKillCounts.get(i)) {
                            topKills.add(i, uhcPlayer.getState() == UHCPlayerState.ALIVE ? "§f" + uhcPlayer.getName() : "§7§o§m" + uhcPlayer.getName() + "§r");
                            topKillCounts.add(i, uhcPlayer.getStats().getKills());
                            break;
                        }
                    }
                }


                if (!topKills.isEmpty()) {
                    for (int i = 0; i < topKills.size(); i++) {
                        if (i == 10)
                            break;

                        objectiveSign.setLine(lineNumber, topKills.get(i) + " §c" + topKillCounts.get(i));
                        lineNumber++;
                    }
                }

                if(gameManager.getPveKills() > 0){
                    objectiveSign.setLine(lineNumber, "§1§lP§f§lv§4§lE §c" + gameManager.getPveKills());
                    lineNumber++;
                }

                objectiveSign.setLine(lineNumber, "§6" + ip);
            }
        }
    }

    private boolean pvp = false;
    private String getTimeBeforePvP() {
        TimerTask timerTask = UHCApi.getApi().getGameManager().getTimerTask();

        String ecartPvP;
        if (pvp)
            ecartPvP = "§a✔";
        else{
            ecartPvP = "§f" + timerTask.getEcart(HostRuleInteger.PVP.getValue(), 0);
            if(timerTask.isTime(HostRuleInteger.PVP.getValue(), 0))
                pvp = true;
        }

        if(timerTask.isTime(HostRuleInteger.PVP.getValue(), 0))
            pvp = true;

        return ecartPvP;
    }

    private boolean border = false;
    private String getTimeBeforeBorder() {
        TimerTask timerTask = UHCApi.getApi().getGameManager().getTimerTask();

        String ecartPvP;
        if (border)
            ecartPvP = "§a✔";
        else {
            ecartPvP = "§f" + timerTask.getEcart(HostRuleInteger.BORDURE.getValue(), 0);
            if(timerTask.isTime(HostRuleInteger.BORDURE.getValue(), 0))
                border = true;
        }

        return ecartPvP;
    }

}
