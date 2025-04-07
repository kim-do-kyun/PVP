package org.desp.pVP.listener;

import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.desp.pVP.database.ArenaRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.PlayerDataDto;
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



        List<String> players = session.getPlayers();

        for (String player : players) {
            String playerUUID = PlayerDataRepository.getInstance().getPlayerNameToUUID(player);

            Map<String, PlayerDataDto> playerDataCache = PlayerDataRepository.getInstance().getPlayerDataCache();
            PlayerDataDto playerDataDto = playerDataCache.get(playerUUID);
            String playerTier = playerDataDto.getTier();
            int playerPoint = playerDataDto.getPoint();

        }

        MatchManager.getInstance().handleMatchResult(winner, loser);
    }
}
