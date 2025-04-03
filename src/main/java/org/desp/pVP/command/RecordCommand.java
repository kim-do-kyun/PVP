package org.desp.pVP.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.PlayerDataDto;
import org.jetbrains.annotations.NotNull;

public class RecordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) return false;

        PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
        int wins = playerData.getWins();
        int losses = playerData.getLosses();

        String tier = playerData.getTier();

        player.sendMessage("§f 플레이어님의 티어 : " + tier + " / 승: " + wins + " / 패: " + losses);
        return false;
    }
}
