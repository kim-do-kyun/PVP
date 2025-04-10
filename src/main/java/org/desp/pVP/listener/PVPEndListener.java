package org.desp.pVP.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.desp.pVP.database.ArenaRepository;
import org.desp.pVP.dto.RoomDto;
import org.desp.pVP.utils.MatchManager;
import org.desp.pVP.utils.MatchSession;

public class PVPEndListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player loser = event.getEntity();
        Player winner = loser.getKiller();
        if (winner == null) return;

        MatchSession session = MatchManager.getInstance().getSession(loser.getUniqueId().toString());
        if (session == null) return;

        RoomDto room = session.getRoom();
        room.setPlaying(false);
        ArenaRepository.getInstance().arenaMap.put(room.getRoomName(), room);

        MatchManager.getInstance().handleMatchResult(winner, loser);
    }

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
}
