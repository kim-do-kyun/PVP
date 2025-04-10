package org.desp.pVP.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.desp.pVP.database.ArenaRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.RoomDto;
import org.desp.pVP.utils.MatchManager;
import org.desp.pVP.utils.MatchSession;

public class PlayerJoinAndQuitListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataRepository.getInstance().loadPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        MatchSession session = MatchManager.getInstance().getSession(player.getUniqueId().toString());
        if (session != null) {
            if (session.isFightStarted()) {
                RoomDto room = session.getRoom();
                room.setPlaying(false);
                ArenaRepository.getInstance().arenaMap.put(room.getRoomName(), room);
                // 상대방 플레이어 구하기
                Player opponent = Bukkit.getPlayer(session.getOpponent(player.getUniqueId().toString()));
                if (opponent != null) {
                    MatchManager.getInstance().handleMatchResult(opponent, player);
                }
            }
        }

        PlayerDataRepository.getInstance().savePlayerData(player);
    }
}
