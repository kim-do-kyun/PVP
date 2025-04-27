package org.desp.pVP.listener;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.desp.pVP.PVP;
import org.desp.pVP.utils.MatchManager;
import org.desp.pVP.utils.MatchSession;

public class PlayerStopListener implements Listener {

    @EventHandler
    public void onPlayerStop5Sec(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        MatchSession session = MatchManager.getInstance().getSession(player.getUniqueId().toString());
        if (session == null) {
            return;
        }
        boolean stop5min = session.getStop5sec();
        if (stop5min) {
            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSkillSkip(PlayerCastSkillEvent event) {
        Player player = event.getPlayer();

        MatchSession session = MatchManager.getInstance().getSession(player.getUniqueId().toString());
        if (session == null) {
            return;
        }
        boolean stop5min = session.getStop5sec();
        if (stop5min) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPotionUse(ConsumableConsumedEvent event) {
        Player player = event.getPlayer();

        MatchSession session = MatchManager.getInstance().getSession(player.getUniqueId().toString());
        if (session == null) {
            return;
        }
        boolean stop5min = session.getStop5sec();
        if (stop5min) {
            event.setCancelled(true);
            player.sendMessage("§c PVP 도중에는 포션을 사용할 수 없습니다.");
        }
    }
    @EventHandler
    public void onHeal(EntityRegainHealthEvent e){
        if(e.getEntity() instanceof Player player){
            MatchSession session = MatchManager.getInstance().getSession(player.getUniqueId().toString());
            if (session == null) {
                return;
            }
            boolean stop5min = session.getStop5sec();
            if(!stop5min){
                return;
            }
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(PVP.getInstance());
            String name = mmoCoreAPI.getPlayerData(player).getProfess().getName();
            if(name.equals("크루세이더")){
                double amount = e.getAmount();
                amount -= amount*50/100;
                e.setAmount(amount);
            }
        }
    }
}

