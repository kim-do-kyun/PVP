package org.desp.pVP.listener;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
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
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String uuid = player.getUniqueId().toString();

        // 대전 중이고 OP가 아닌 경우
        if (MatchManager.getInstance().isInCombat(uuid)) {

            if (event.getSlot() == 40) {
                ItemStack cursorItem = event.getCursor();

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

        if (MatchManager.getInstance().isInCombat(uuid)) {
            event.getOffHandItem();
            if (event.getOffHandItem().getType() == Material.FISHING_ROD) {
                player.sendMessage("§c대전 중에는 왼손에 낚싯대를 장착할 수 없습니다.");
                event.setCancelled(true);
            }
        }
    }
}
