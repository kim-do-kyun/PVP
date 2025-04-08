package org.desp.pVP;

import java.util.Collection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.pVP.command.CancelMatchCommand;
import org.desp.pVP.command.MatchingCommand;
import org.desp.pVP.command.RecordCommand;
import org.desp.pVP.database.ArenaRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.listener.AugmentConfirmListener;
import org.desp.pVP.listener.PVPEndListener;
import org.desp.pVP.listener.PlayerJoinAndQuitListener;
import org.desp.pVP.utils.MatchManager;

public final class PVP extends JavaPlugin {

    @Getter
    private static PVP instance;

    @Override
    public void onEnable() {
        instance = this;

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerDataRepository.getInstance().loadPlayerData(player);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PVPEndListener(), this);
        Bukkit.getPluginManager().registerEvents(new AugmentConfirmListener(), this);
        getCommand("대전매칭").setExecutor(new MatchingCommand());
        getCommand("매칭취소").setExecutor(new CancelMatchCommand());
        getCommand("전적확인").setExecutor(new RecordCommand());

        ArenaRepository.getInstance().loadAllRooms();
    }

    @Override
    public void onDisable() {

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerDataRepository.getInstance().savePlayerData(player);
            MatchManager.getInstance().stopWaitingThread(player.getUniqueId().toString());
        }

    }
}
