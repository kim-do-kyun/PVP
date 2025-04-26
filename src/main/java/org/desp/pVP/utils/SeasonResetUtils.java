package org.desp.pVP.utils;

import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.desp.pVP.database.MatchLogDataRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.database.reward.RewardLogRepository;
import org.desp.pVP.database.reward.SeasonRewardDataRepository;
import org.desp.pVP.database.reward.SeasonRewardLogRepository;
import org.desp.pVP.dto.PlayerDataDto;
import org.desp.pVP.dto.RewardDataDto;
import org.desp.pVP.dto.SeasonRewardLogDto;

public class SeasonResetUtils {

    public static void resetSeason(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Map<String, PlayerDataDto> playerDataCache = PlayerDataRepository.getInstance().getPlayerDataCache();
        Map<String, List<RewardDataDto>> seasonRewardDataDtoCache = SeasonRewardDataRepository.getInstance()
                .getSeasonRewardDataDtoCache();
        Map<String, SeasonRewardLogDto> seasonRewardLogDataCache = SeasonRewardLogRepository.getInstance()
                .getSeasonRewardLogDataCache();

        PlayerDataDto playerDataDto = playerDataCache.get(uuid);
        String playerTier = playerDataDto.getTier();

        List<RewardDataDto> seasonRewardDataDtos = seasonRewardDataDtoCache.get(playerTier);

        MatchUtils.sendReward(MatchUtils.getReward(seasonRewardDataDtos), player);

        List<String> rewardItems = new ArrayList<>();
        for (RewardDataDto seasonRewardDataDto : seasonRewardDataDtos) {
            rewardItems.add(seasonRewardDataDto.getItem_info());
        }

        SeasonRewardLogDto seasonRewardLogDto = SeasonRewardLogDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .tier(playerTier)
                .rewardItem(rewardItems)
                .build();

        SeasonRewardLogRepository.getInstance().getSeasonRewardLogDataCache().put(uuid, seasonRewardLogDto);

        PlayerDataRepository.getInstance().resetPlayerDataDB();
        RewardLogRepository.getInstance().resetRewardLog();
        MatchLogDataRepository.getInstance().resetMatchLog();

        String message = "§b[시스템] 시즌이 초기화되었습니다. 새로운 시즌을 시작하세요!";
        Bukkit.broadcastMessage(message);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);

    }

}
