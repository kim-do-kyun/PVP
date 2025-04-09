package org.desp.pVP.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.desp.pVP.PVP;
import org.desp.pVP.dto.RespawnMessageDto;
import org.desp.pVP.utils.MatchManager;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        MatchManager matchManager = MatchManager.getInstance();
        RespawnMessageDto message = matchManager.getPostRespawnMessage(uuid);

        if (message != null) {
            Bukkit.getScheduler().runTaskLater(PVP.getInstance(), () -> {
                player.sendTitle(message.getTitle(), message.getSubtitle(), 10, 60, 10);
                player.sendActionBar(message.getActionBar());
            }, 20L);

            matchManager.removePostRespawnMessage(uuid);
        }
    }
}
