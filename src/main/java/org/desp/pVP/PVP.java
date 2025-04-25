package org.desp.pVP;

import java.util.Collection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.pVP.command.MatchingCommand;
import org.desp.pVP.database.ArenaRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.database.reward.RewardDataRepository;
import org.desp.pVP.database.reward.RewardLogRepository;
import org.desp.pVP.database.reward.SeasonRewardDataRepository;
import org.desp.pVP.listener.AugmentConfirmListener;
import org.desp.pVP.listener.PVPEndListener;
import org.desp.pVP.listener.PlayerDuringMatchListener;
import org.desp.pVP.listener.PlayerJoinAndQuitListener;
import org.desp.pVP.listener.PlayerRespawnListener;
import org.desp.pVP.listener.PlayerStopListener;
import org.desp.pVP.threadUtil.PlayerRankScheduler;
import org.desp.pVP.utils.MatchManager;

public final class PVP extends JavaPlugin {

    @Getter
    private static PVP instance;

    @Override
    public void onEnable() {
        instance = this;

        PlayerRankScheduler scheduler = new PlayerRankScheduler();
        scheduler.start();
        scheduler.challengerUpdater();

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerDataRepository.getInstance().loadPlayerData(player);
            RewardLogRepository.getInstance().loadRewardLogData(player);

        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PVPEndListener(), this);
        Bukkit.getPluginManager().registerEvents(new AugmentConfirmListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerStopListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDuringMatchListener(), this);
        getCommand("대전").setExecutor(new MatchingCommand());

        ArenaRepository.getInstance().loadAllRooms();
        RewardDataRepository.getInstance().loadRewardData();
        SeasonRewardDataRepository.getInstance().loadRewardData();
    }

    @Override
    public void onDisable() {

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerDataRepository.getInstance().savePlayerData(player);
            MatchManager.getInstance().stopWaitingThread(player.getUniqueId().toString());
            RewardLogRepository.getInstance().saveRewardLog(player);
        }

    }
}
