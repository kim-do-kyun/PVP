package org.desp.pVP.utils;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.desp.pVP.database.reward.RewardDataRepository;
import org.desp.pVP.database.reward.RewardLogRepository;
import org.desp.pVP.dto.RewardDataDto;
import org.desp.pVP.dto.RewardLogDto;

public class MatchUtils {

    public static String getTierFromPoint(int point) {
        if (point >= 100) return "마스터";
        else if (point >= 80) return "다이아";
        else if (point >= 60) return "플레티넘";
        else if (point >= 40) return "골드";
        else if (point >= 20) return "실버";
        else return "브론즈";

//        if (point >= 120) return "챌린저";
//        else
    }

    public static void giveRankUpReward(Player winner, String winnerPrevTier, String winnerNewTier) {
        Map<String, RewardLogDto> rewardLogDataCache = RewardLogRepository.getInstance()
                .getRewardLogDataCache();
        RewardLogDto rewardLogDto = rewardLogDataCache.get(winner.getUniqueId().toString());

        if (!winnerPrevTier.equals(winnerNewTier) && rewardLogDto.getRewardedRank().stream().noneMatch(winnerNewTier::equals)) {
            winner.sendMessage("§b[승급] 티어가 " + winnerPrevTier + " → " + winnerNewTier + " 로 승급되었습니다!");

            Map<String, List<RewardDataDto>> rewardDataDtoCache = RewardDataRepository.getInstance()
                    .getRewardDataDtoCache();

            List<RewardDataDto> rewardDataDtos = rewardDataDtoCache.get(winnerNewTier);
            // 보상 지급
            MatchUtils.sendReward(MatchUtils.getReward(rewardDataDtos), winner);

            // 캐시에 저장
            rewardLogDto.getRewardedRank().add(winnerNewTier);
            rewardLogDataCache.put(winner.getUniqueId().toString(), rewardLogDto);
        }
    }


    public static void sendReward(List<ItemStack> reward, Player player) {
        MMOMail mmoMail = MMOMail.getInstance();
        Mail rewardMail = mmoMail.getMailAPI().createMail(
                "시스템",
                "승급 보상입니다.",
                0,
                reward
        );
        mmoMail.getMailAPI().sendMail(player.getName(), rewardMail);
    }

    public static List<ItemStack> getReward(List<RewardDataDto> reward) {
        List<ItemStack> rewardItems = new ArrayList<>();

        for (RewardDataDto rewardDataDto : reward) {
            ItemStack mmoItemById = getMMOItemById(rewardDataDto.getItem_id());
            mmoItemById.setAmount(rewardDataDto.getAmount());
            rewardItems.add(mmoItemById);
        }
        return rewardItems;
    }

    public static ItemStack getMMOItemById(String itemId) {
        for (Type type : MMOItems.plugin.getTypes().getAll()) {
            MMOItem mmoItem = MMOItems.plugin.getMMOItem(type, itemId);
            if (mmoItem != null) {
                return mmoItem.newBuilder().build();
            }
        }
        return null;
    }

    public static void moveOffhandFishingRodToInventory(Player player) {
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (offhand != null && offhand.getType() == Material.FISHING_ROD) {
            int emptySlot = player.getInventory().firstEmpty();

            if (emptySlot != -1) {
                // 인벤토리에 옮김
                player.getInventory().setItem(emptySlot, offhand);
                player.sendMessage("§c전투 시작 전, 왼손의 낚싯대가 인벤토리로 옮겨졌습니다.");
            } else {
                // 드랍
                player.getWorld().dropItemNaturally(player.getLocation(), offhand);
                player.sendMessage("§c전투 시작 전, 왼손 낚싯대가 인벤토리에 공간이 없어 바닥에 버려졌습니다.");
            }

            // 왼손 초기화
            player.getInventory().setItemInOffHand(null);
        }
    }

    public static Player getAnyPlayer(String playerName) {
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            return onlinePlayer; // 온라인이면 바로 반환
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer.hasPlayedBefore()) {
            // 혹시 온라인 상태가 되면 다시 Player로 변환될 수 있음
            return Bukkit.getPlayer(offlinePlayer.getUniqueId());
        }

        return null; // 존재한 적도 없는 유저라면 null
    }


}
