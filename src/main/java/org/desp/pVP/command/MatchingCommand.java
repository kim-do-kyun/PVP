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
import org.desp.pVP.dto.PlayerRankInfoDto;
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

        if (strings.length == 0) {
            player.sendMessage("§f /대전 매칭 랭크 - §c랭크전 §7매칭 대기열에 진입합니다.");
            player.sendMessage("§f /대전 매칭 친선 - §9친선전 §7매칭 대기열에 진입합니다.");
            player.sendMessage("§f /대전 매칭취소 - §7매칭 대기열에서 퇴장합니다.");
            player.sendMessage("§f /대전 전적확인 - §7랭크 티어와 승, 패 전적을 확인할 수 있습니다.");
            player.sendMessage("§f /대전 순위표 - §7상위 10명의 순위와 자신의 순위를 확인할 수 있습니다.");
            return true;
        } else if ("매칭".equals(strings[0]) && strings.length == 1) {
            player.sendMessage("§f /대전 매칭 랭크 - 랭크전 매칭 대기열에 진입합니다.");
            player.sendMessage("§f /대전 매칭 친선 - 친선전 매칭 대기열에 진입합니다.");
            return true;
        }

        if ("매칭".equals(strings[0]) && strings.length == 2) {
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
            if (strings.length == 1) {
                player.sendMessage("§f 플레이어님의 티어 : " + tier + "("+ point + "점) / 승: " + wins + " / 패: " + losses);
            } else if (strings.length == 2) {
                String user_id = strings[1];
                String playerNameToUUID = PlayerDataRepository.getInstance().getPlayerNameToUUID(user_id);
                PlayerDataDto playerDataDto = PlayerDataRepository.getInstance().getPlayerDataCache()
                        .get(playerNameToUUID);

                if (playerDataDto == null) {
                    player.sendMessage("§c 존재하지 않는 플레이어입니다");
                    return true;
                }

                player.sendMessage("§f " + user_id +  "님의 티어 : " + playerDataDto.getTier() + " / 승: " + playerDataDto.getWins() + " / 패: " + playerDataDto.getLosses());
            }
        } else if ("순위표".equals(strings[0])) {
            int playerRank = PlayerDataRepository.getInstance().getPlayerRank(player.getName());
            List<PlayerRankInfoDto> top10Players = PlayerDataRepository.getInstance().getTop10Players();

            player.sendMessage("§6====== §e📊 순위표 TOP 10 §6======");

            for (int i = 0; i < top10Players.size(); i++) {
                PlayerRankInfoDto topPlayer = top10Players.get(i);
                String rankLine = String.format("§f%d위 - §b%s§f | §e%s§f | %d점",
                        i + 1,
                        topPlayer.getPlayerName(),
                        topPlayer.getRank(),
                        topPlayer.getPoints()
                );
                player.sendMessage(rankLine);
            }

            player.sendMessage("§6==========================");

            if (playerRank != -1) {
                player.sendMessage("§a당신의 현재 순위는 §b" + playerRank + "위§a입니다!");
            } else {
                player.sendMessage("§c당신은 현재 랭킹에 없습니다.");
            }
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
            completions.add("순위표");
        } else if (strings.length == 2 && "매칭".equalsIgnoreCase(strings[0])) {
            completions.add("랭크");
            completions.add("친선");
        }

        return completions;
    }
}
