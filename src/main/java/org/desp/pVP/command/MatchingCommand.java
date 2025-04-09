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
import org.desp.pVP.dto.PlayerDataDto;
import org.desp.pVP.utils.DateUtils;
import org.desp.pVP.utils.MatchManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatchingCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        String uuid = player.getUniqueId().toString();
        PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
        int point = playerData.getPoint();
        String tier = playerData.getTier();
        int wins = playerData.getWins();
        int losses = playerData.getLosses();

        MatchingPlayerDto matchingPlayer = MatchingPlayerDto.builder()
                .uuid(player.getUniqueId().toString())
                .point(point)
                .tier(tier)
                .joinTime(DateUtils.getCurrentTime())
                .build();

        // 대전 매칭 랭크/친선
        // 대전 매칭취소
        // 대전 전적확인
        if ("매칭".equals(strings[0])) {
            switch (strings[1]){
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
        } else if ("매칭취소".equals(strings[0])) {
            boolean removed = MatchManager.getInstance().cancelQueue(uuid);
            if (removed) {
                MatchManager.getInstance().stopWaitingThread(uuid);
                player.sendMessage("§c매칭 대기열에서 나갔습니다.");
            } else {
                player.sendMessage("§e당신은 현재 매칭 대기 중이 아닙니다.");
            }
        } else if ("전적확인".equals(strings[0])) {
            player.sendMessage("§f 플레이어님의 티어 : " + tier + " / 승: " + wins + " / 패: " + losses);
        } else if ("전적삭제".equals(strings[0])) {
            String playerAName = strings[1];
            String playerBName = strings[2];

            String playerAUUID = PlayerDataRepository.getInstance().getPlayerNameToUUID(playerAName);
            String playerBUUID = PlayerDataRepository.getInstance().getPlayerNameToUUID(playerBName);
            MatchManager.getInstance().removeOpponentCache(playerAUUID, playerBUUID);

            player.sendMessage("§e " + playerAName + "님 과 " + playerBName + "님의 대전 기록이 삭제되었습니다");
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            completions.add("매칭");
            completions.add("매칭취소");
            completions.add("전적확인");
            completions.add("전적삭제");
        } else if (strings.length == 2 && "매칭".equalsIgnoreCase(strings[0])) {
            completions.add("랭크");
            completions.add("친선");
        }

        return completions;
    }
}
