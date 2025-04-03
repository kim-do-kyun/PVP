package org.desp.pVP.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
    private final List<MatchingPlayerDto> rankQueue = new ArrayList<>();
    private final List<MatchingPlayerDto> friendlyQueue = new ArrayList<>();
    private final Map<String, Thread> waitingThreads = new ConcurrentHashMap<>();

    public static MatchManager getInstance() {
        if (instance == null) instance = new MatchManager();
        return instance;
    }

    public void joinQueue(MatchingPlayerDto player, String type) {
        // 중복 큐 체크(랭크 큐 잡을때 친선 큐 못잡음)
        if (checkDuplicateQueue(player)) {
            return;
        }
        Player bukkitPlayer = Bukkit.getPlayer(UUID.fromString(player.getUuid()));
        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage("§a[" + player.getTier() + "] 티어(" +type+  ")로 매칭을 시작합니다...");
        }

        startWaitingThread(player.getUuid());

        if ("친선".equals(type)) {
            for (MatchingPlayerDto other : friendlyQueue) {
                if (canMatch(player, other)) {
                    friendlyQueue.remove(other);
                    stopWaitingThread(player.getUuid());
                    stopWaitingThread(other.getUuid());
                    startMatch(player, other, type);
                    return;
                }
            }
            friendlyQueue.add(player);
        } else if ("랭크".equals(type)) {
            for (MatchingPlayerDto other : rankQueue) {
                if (canMatch(player, other)) {
                    rankQueue.remove(other);
                    stopWaitingThread(player.getUuid());
                    stopWaitingThread(other.getUuid());
                    startMatch(player, other, type);
                    return;
                }
            }
            rankQueue.add(player);
        }
    }

    private void startWaitingThread(String uuid) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player == null) return;

        Thread thread = new Thread(() -> {
            int seconds = 0;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    seconds++;
                    player.sendActionBar("§e[매칭] 대기 중... " + seconds + "초 경과");
                }
            } catch (InterruptedException e) {
                // 정상 종료
            }
        });

        thread.start();
        waitingThreads.put(uuid, thread);
    }

    public void stopWaitingThread(String uuid) {
        Thread thread = waitingThreads.remove(uuid);
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void startMatch(MatchingPlayerDto a, MatchingPlayerDto b, String type) {
        MatchSession session = new MatchSession(a.getUuid(), b.getUuid(), type);
        for (String uuid : session.getPlayers()) {
            activeSessions.put(uuid, session);
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player != null) {
                player.sendMessage("§e매칭이 성사되었습니다! 5초 안에 증강을 선택해주세요.");
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

        String winnerName = winner.getName();
        String loserName = loser.getName();

        MatchSession session = getSession(loserId);
        if (session == null) return;
        if (!session.getPlayers().contains(winnerId)) return;

        winner.sendMessage("§a승리하였습니다!");
        loser.sendMessage("§c패배하였습니다.");

        session.endMatch();
        endMatch(session);

        Map<String, Integer> pointChange = new HashMap<>();
        if ("친선".equals(session.getType())) {
        } else if ("랭크".equals(session.getType())) {
            // 승, 패 적립 및 포인트 지급
            PlayerDataDto winnerDataDto = playerDataCache.get(winnerId);
            winnerDataDto.setWins(winnerDataDto.getWins()+1);
            winnerDataDto.setPoint(winnerDataDto.getPoint() + 3);
            playerDataCache.put(winnerId, winnerDataDto);

            PlayerDataDto loserDataDto = playerDataCache.get(loserId);
            loserDataDto.setLosses(loserDataDto.getLosses()+1);
            loserDataDto.setPoint(loserDataDto.getPoint() - 3);
            playerDataCache.put(loserId, loserDataDto);

            // 점수 설정
            pointChange.put(winnerName, 3);
            pointChange.put(loserName, -3);
        }
        MatchLogDto matchLogDto = MatchLogDto.builder()
                .playerA(winnerName)
                .playerB(loserName)
                .winner(winnerName)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .type(session.getType())
                .pointChange(pointChange)
                .build();

        // 결과 로그 디비 저장
        MatchLogDataRepository.getInstance().saveMatchLog(matchLogDto);

        session.teleportPlayers("world", -21.475, 37.000, -737.459, -21.475, 37.000, -737.459);
    }

    private boolean checkDuplicateQueue(MatchingPlayerDto player) {
        boolean isInRank = rankQueue.stream().anyMatch(p -> p.getUuid().equals(player.getUuid()));
        boolean isInFriendly = friendlyQueue.stream().anyMatch(p -> p.getUuid().equals(player.getUuid()));

        if (isInRank || isInFriendly) {
            Player bukkitPlayer = Bukkit.getPlayer(UUID.fromString(player.getUuid()));
            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage("§c이미 매칭 대기 중입니다. 다른 매칭에 참여할 수 없습니다.");
            }
            return true;
        }
        return false;
    }

    private boolean canMatch(MatchingPlayerDto a, MatchingPlayerDto b) {
        return a.getTier().equals(b.getTier());
    }

    public boolean cancelQueue(String uuid) {
        boolean removedFromRank = rankQueue.removeIf(player -> player.getUuid().equals(uuid));
        boolean removedFromFriendly = friendlyQueue.removeIf(player -> player.getUuid().equals(uuid));
        return removedFromRank || removedFromFriendly;
    }
}