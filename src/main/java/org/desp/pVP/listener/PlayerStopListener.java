package org.desp.pVP.listener;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
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
}

