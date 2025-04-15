package org.desp.pVP.utils;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.desp.pVP.dto.RewardDataDto;

public class MatchUtils {

    public static String getTierFromPoint(int point) {
        if (point >= 120) return "챌린저";
        else if (point >= 100) return "마스터";
        else if (point >= 80) return "다이아";
        else if (point >= 60) return "플레티넘";
        else if (point >= 40) return "골드";
        else if (point >= 20) return "실버";
        else return "브론즈";
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

}
