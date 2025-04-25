//package org.desp.pVP.utils;
//
//import java.util.List;
//import java.util.Map;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.desp.pVP.database.PlayerDataRepository;
//import org.desp.pVP.database.reward.SeasonRewardDataRepository;
//import org.desp.pVP.database.reward.SeasonRewardLogRepository;
//import org.desp.pVP.dto.PlayerDataDto;
//import org.desp.pVP.dto.RewardDataDto;
//import org.desp.pVP.dto.SeasonRewardLogDto;
//
//public class SeasonResetUtils {
//
//    public static void resetSeason(Player player) {
//
//        String user_id = player.getName();
//        String uuid = player.getUniqueId().toString();
//
//        Map<String, PlayerDataDto> playerDataCache = PlayerDataRepository.getInstance().getPlayerDataCache();
//        Map<String, List<RewardDataDto>> seasonRewardDataDtoCache = SeasonRewardDataRepository.getInstance()
//                .getSeasonRewardDataDtoCache();
//        Map<String, SeasonRewardLogDto> seasonRewardLogDataCache = SeasonRewardLogRepository.getInstance()
//                .getSeasonRewardLogDataCache();
//
//        PlayerDataDto playerDataDto = playerDataCache.get(uuid);
//        String playerTier = playerDataDto.getTier();
//
//        List<RewardDataDto> seasonRewardDataDtos = seasonRewardDataDtoCache.get(playerTier);
//
//        MatchUtils.sendReward(MatchUtils.getReward(seasonRewardDataDtos), player);
//
//        SeasonRewardLogDto seasonRewardLogDto = SeasonRewardLogDto.builder()
//                .user_id(user_id)
//                .uuid(uuid)
//                .tier(playerTier)
//                .rewardItem()
//
//        // 3. 플레이어 랭킹 데이터 초기화
//        for (PlayerDataDto data : playerDataCache.values()) {
//            data.setRankPoint(0);
//            data.setTier("Bronze");
//            PlayerDataRepository.getInstance().save(data);
//        }
//
//        // 4. 보상 데이터 및 로그 초기화
//        SeasonRewardDataRepository.getInstance().clearAll();
//        SeasonRewardLogRepository.getInstance().clearAll();
//
//        Bukkit.broadcastMessage("§b[시스템] 시즌이 초기화되었습니다. 새로운 시즌을 시작하세요!");
//    }
//
//}
