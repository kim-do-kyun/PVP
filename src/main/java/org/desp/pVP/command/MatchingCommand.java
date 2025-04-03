package org.desp.pVP.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.MatchingPlayerDto;
import org.desp.pVP.utils.DateUtils;
import org.desp.pVP.utils.MatchManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatchingCommand implements CommandExecutor, TabCompleter {
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

        switch (strings[0]){
            case "랭크":
                MatchManager.getInstance().joinQueue(matchingPlayer, "랭크");
                break;
            case "친선":
                MatchManager.getInstance().joinQueue(matchingPlayer, "친선");
                break;
            default:
                player.sendMessage("§e 잘못된 명령어입니다.");
                break;
        }
        return true;
    }

    private int getPlayerPoint(Player player) {
        return PlayerDataRepository.getInstance().getPlayerData(player).getPoint();
    }
    private String getPlayerTier(Player player) {
        return PlayerDataRepository.getInstance().getPlayerData(player).getTier();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {
        List<String> complition = new ArrayList<>();
        complition.add("친선");
        complition.add("랭크");
        return complition;
    }
}
