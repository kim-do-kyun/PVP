package org.desp.pVP.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.desp.pVP.gui.AugmentSelectGUI;
import org.desp.pVP.utils.MatchSession;

public class AugmentConfirmListener implements Listener {

    @EventHandler
    public void onConfirm(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AugmentSelectGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedDifficulty = event.getCurrentItem();

        if (clickedDifficulty == null || clickedDifficulty.getType() == Material.AIR) return;

        // 증강 리스트 구현( 증강 종류 디비 만들어서 처리할 효과 등등)
        MatchSession session = AugmentSelectGUI.getSession();

        String plusStat = clickedDifficulty.getItemMeta().getDisplayName();

        session.selectAugment(player.getName(), plusStat);


        player.closeInventory();
        // 선택한 증강 적용 후 게임 진입
    }
}
