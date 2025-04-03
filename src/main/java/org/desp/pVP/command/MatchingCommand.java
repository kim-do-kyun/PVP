package org.desp.pVP.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.MatchingPlayerDto;
import org.desp.pVP.utils.DateUtils;
import org.desp.pVP.utils.MatchManager;
import org.jetbrains.annotations.NotNull;

public class MatchingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        int point = getPlayerPoint(player);
        String tier = getPlayerTier(player);
        MatchingPlayerDto matchingPlayer = MatchingPlayerDto.builder()
                .uuid(player.getUniqueId().toString())
                .point(point)
                .tier(tier)
                .joinTime(DateUtils.getCurrentTime())
                .build();

        MatchManager.getInstance().joinQueue(matchingPlayer);
        player.sendMessage("§a[" + matchingPlayer.getTier() + "] 티어로 매칭을 시작합니다...");
        return true;
    }

    private int getPlayerPoint(Player player) {
        return PlayerDataRepository.getInstance().getPlayerData(player).getPoint();
    }
    private String getPlayerTier(Player player) {
        return PlayerDataRepository.getInstance().getPlayerData(player).getTier();
    }
}
