package org.desp.pVP;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.pVP.command.CancelMatchCommand;
import org.desp.pVP.command.MatchingCommand;
import org.desp.pVP.command.RecordCommand;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.listener.AugmentConfirmListener;
import org.desp.pVP.listener.PVPEndListener;
import org.desp.pVP.listener.PlayerJoinAndQuitListener;

public final class PVP extends JavaPlugin {

    @Getter
    private static PVP instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getOnlinePlayers().forEach(player ->
                PlayerDataRepository.getInstance().loadPlayerData(player)
        );

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PVPEndListener(), this);
        Bukkit.getPluginManager().registerEvents(new AugmentConfirmListener(), this);
        getCommand("대전매칭").setExecutor(new MatchingCommand());
        getCommand("매칭취소").setExecutor(new CancelMatchCommand());
        getCommand("전적확인").setExecutor(new RecordCommand());
    }

    @Override
    public void onDisable() {

        Bukkit.getOnlinePlayers().forEach(player ->
                PlayerDataRepository.getInstance().savePlayerData(player)
        );
    }
}
