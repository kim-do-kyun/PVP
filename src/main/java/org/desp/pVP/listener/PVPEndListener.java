package org.desp.pVP.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.desp.pVP.utils.MatchManager;

public class PVPEndListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player loser = event.getEntity();
        Player winner = loser.getKiller();
        if (winner == null) return;

        MatchManager.getInstance().handleMatchResult(winner, loser);
    }
}
