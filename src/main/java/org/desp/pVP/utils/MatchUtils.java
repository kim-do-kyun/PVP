package org.desp.pVP.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
