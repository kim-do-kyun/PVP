package org.desp.pVP.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.desp.pVP.utils.MatchManager;

public class PlayerDuringMatchListener implements Listener {

//    @EventHandler
//    public void onCommand(PlayerCommandPreprocessEvent event) {
//        // 대전중이면서 op가 아니면
//        Player player = event.getPlayer();
//        String uuid = player.getUniqueId().toString();
//
//        if (MatchManager.getInstance().isInCombat(uuid) && !player.isOp()) {
//            event.setCancelled(true);
//        }
//    }

    @EventHandler
    public void onOffHandSlotClick(InventoryClickEvent event) {
        // 플레이어만 검사
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String uuid = player.getUniqueId().toString();

        // 대전 중이고 OP가 아닌 경우
        if (MatchManager.getInstance().isInCombat(uuid)) {

            // 왼손 슬롯 번호: 40
            if (event.getSlot() == 40) {
                ItemStack cursorItem = event.getCursor(); // 지금 마우스에 들고 있는 아이템

                // 커서 아이템이 낚싯대인 경우
                if (cursorItem != null && cursorItem.getType() == Material.FISHING_ROD) {
                    player.sendMessage("§c대전 중에는 왼손에 낚싯대를 장착할 수 없습니다.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSwapToOffHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        // 대전 중이고 OP가 아닌 경우
        if (MatchManager.getInstance().isInCombat(uuid)) {
            // 바꾸려는 아이템이 낚싯대인 경우
            if (event.getOffHandItem() != null && event.getOffHandItem().getType() == Material.FISHING_ROD) {
                player.sendMessage("§c대전 중에는 왼손에 낚싯대를 장착할 수 없습니다.");
                event.setCancelled(true);
            }
        }
    }
}
