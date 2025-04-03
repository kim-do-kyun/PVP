package org.desp.pVP.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.desp.pVP.PVP;
import org.desp.pVP.database.MatchLogDataRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.MatchLogDto;
import org.desp.pVP.dto.MatchingPlayerDto;
import org.desp.pVP.dto.PlayerDataDto;
import org.desp.pVP.gui.AugmentSelectGUI;

public class MatchManager {
    private static MatchManager instance;
    private final Map<String, MatchSession> activeSessions = new HashMap<>();
    private final List<MatchingPlayerDto> queue = new ArrayList<>();

    public static MatchManager getInstance() {
        if (instance == null) instance = new MatchManager();
        return instance;
    }

    public void joinQueue(MatchingPlayerDto player) {
        for (MatchingPlayerDto other : queue) {
            if (canMatch(player, other)) {
                queue.remove(other);
                startMatch(player, other);
                return;
            }
        }
        queue.add(player);
    }

    private boolean canMatch(MatchingPlayerDto a, MatchingPlayerDto b) {
        return a.getTier().equals(b.getTier());
    }

    public boolean cancelQueue(String uuid) {
        return queue.removeIf(player -> player.getUuid().equals(uuid));
    }

    private void startMatch(MatchingPlayerDto a, MatchingPlayerDto b) {
        MatchSession session = new MatchSession(a.getUuid(), b.getUuid());
        for (String uuid : session.getPlayers()) {
            activeSessions.put(uuid, session);
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player != null) {
                player.sendMessage("§e매칭이 성사되었습니다! 증강을 선택해주세요.");
                // 증강 GUI
                AugmentSelectGUI gui = new AugmentSelectGUI(session);
                player.openInventory(gui.getInventory());
            }
        }
        // 증강 선택에서 session 해결해야함

        Bukkit.getScheduler().runTaskLater(PVP.getInstance(), session::beginFight, 100L);
    }

    public MatchSession getSession(String player) {
        return activeSessions.get(player);
    }

    public void endMatch(MatchSession session) {
        for (String uuid : session.getPlayers()) {
            activeSessions.remove(uuid);
        }
    }

    public void handleMatchResult(Player winner, Player loser) {
        Map<String, PlayerDataDto> playerDataCache = PlayerDataRepository.getInstance().getPlayerDataCache();
        String winnerId = winner.getUniqueId().toString();
        String loserId = loser.getUniqueId().toString();

        MatchSession session = getSession(loserId);
        if (session == null) return;
        if (!session.getPlayers().contains(winnerId)) return;

        winner.sendMessage("§a승리하였습니다!");
        loser.sendMessage("§c패배하였습니다.");

        session.endMatch();
        endMatch(session);

        // 승, 패 적립 및 포인트 지급
        PlayerDataDto winnerDataDto = playerDataCache.get(winnerId);
        winnerDataDto.setWins(winnerDataDto.getWins()+1);
        winnerDataDto.setPoint(winnerDataDto.getPoint() + 10);
        playerDataCache.put(winnerId, winnerDataDto);

        PlayerDataDto loserDataDto = playerDataCache.get(loserId);
        loserDataDto.setLosses(loserDataDto.getLosses()+1);
        loserDataDto.setPoint(loserDataDto.getPoint() - 10);
        playerDataCache.put(loserId, loserDataDto);

        // 점수 설정
        Map<String, Integer> pointChange = new HashMap<>();
        pointChange.put(winnerId, 10);
        pointChange.put(loserId, -10);

        MatchLogDto matchLogDto = MatchLogDto.builder()
                .playerA(winnerId)
                .playerB(loserId)
                .winner(winnerId)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .pointChange(pointChange)
                .build();

        // 결과 디비 저장
        MatchLogDataRepository.getInstance().saveMatchLog(matchLogDto);
    }
}