package org.desp.pVP.utils;

import static org.desp.pVP.utils.MatchUtils.getTierFromPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.desp.pVP.PVP;
import org.desp.pVP.database.ArenaRepository;
import org.desp.pVP.database.MatchLogDataRepository;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.database.RewardDataRepository;
import org.desp.pVP.database.RewardLogDataRepository;
import org.desp.pVP.dto.MatchLogDto;
import org.desp.pVP.dto.MatchingPlayerDto;
import org.desp.pVP.dto.PlayerDataDto;
import org.desp.pVP.dto.RespawnMessageDto;
import org.desp.pVP.dto.RewardDataDto;
import org.desp.pVP.dto.RewardLogDto;
import org.desp.pVP.dto.RoomDto;

public class MatchManager {
    private static MatchManager instance;
    private final Map<String, MatchSession> activeSessions = new HashMap<>();
    private final List<MatchingPlayerDto> rankQueue = new ArrayList<>();
    private final List<MatchingPlayerDto> friendlyQueue = new ArrayList<>();
    private final Map<String, Thread> waitingThreads = new ConcurrentHashMap<>();
    public final Map<String, String> recentOpponentMap = new HashMap<>();
    private final Map<String, RespawnMessageDto> postRespawnMessageMap = new ConcurrentHashMap<>();

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

        if (bukkitPlayer != null && "랭크".equals(type)) {
            switch (player.getTier()) {
                case "브론즈":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§e브론즈§f)로 매칭을 시작합니다...");
                    break;
                case "실버":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§7실버§f)로 매칭을 시작합니다...");
                    break;
                case "골드":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§6골드§f)로 매칭을 시작합니다...");
                    break;
                case "플레티넘":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§a플레티넘§f)로 매칭을 시작합니다...");
                    break;
                case "다이아":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§1다이아§f)로 매칭을 시작합니다...");
                    break;
                case "마스터":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§5마스터§f)로 매칭을 시작합니다...");
                    break;
                case "챌린저":
                    bukkitPlayer.sendMessage("§6[랭크] §f티어(§4챌린저§f)로 매칭을 시작합니다...");
                    break;
                default:
                    bukkitPlayer.sendMessage("§c[알림] 티어 정보가 올바르지 않습니다.");
                    break;
            }
        } else if (bukkitPlayer != null && "친선".equals(type)) {
            bukkitPlayer.sendMessage("§a[친선전] §f매칭을 시작합니다...");
        }

        startWaitingThread(player.getUuid());

        if ("친선".equals(type)) {
            for (MatchingPlayerDto other : friendlyQueue) {
                if (canMatch(player, other, type)) {
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
                if (canMatch(player, other, type)) {
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
            final int MAX_WAIT_SECONDS = 300; //5분동안 큐 안잡히면 자동 취소

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    seconds++;
                    if (seconds > MAX_WAIT_SECONDS) {
                        Bukkit.getScheduler().runTask(PVP.getInstance(), () -> {
                            if (cancelQueue(uuid)) {
                                player.sendMessage("§c매칭 대기 시간이 초과되어 자동으로 매칭이 취소되었습니다.");
                                player.sendActionBar("§c매칭 대기 시간이 초과되어 자동으로 매칭이 취소되었습니다.");
                                stopWaitingThread(uuid);
                            }
                        });
                        break;
                    }
                    player.sendTitle("§6[매칭] §f대기 중... " + seconds + "초 경과", "§f현재 매칭 대기 중인 인원: §c" + rankQueue.size() + "§f명");
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
        RoomDto room = ArenaRepository.getInstance().getAvailableRoom();
        if (room == null) {
            Player playerA = Bukkit.getPlayer(UUID.fromString(a.getUuid()));
            Player playerB = Bukkit.getPlayer(UUID.fromString(b.getUuid()));
            if (playerA != null) playerA.sendMessage("§c사용 가능한 경기장이 없어 매칭이 취소되었습니다.");
            if (playerB != null) playerB.sendMessage("§c사용 가능한 경기장이 없어 매칭이 취소되었습니다.");
            return;
        }

        MatchSession session = new MatchSession(a.getUuid(), b.getUuid(), type);
        session.setRoom(room);

        // 최근 매칭 기록 추가(패작 막는용)
        if ("랭크".equals(type)) {
            recentOpponentMap.put(a.getUuid(), b.getUuid());
            recentOpponentMap.put(b.getUuid(), a.getUuid());
        }

        for (String uuid : session.getPlayers()) {
            activeSessions.put(uuid, session);
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player != null) {
                String opponentUuid = session.getPlayers().stream()
                        .filter(u -> !u.equals(uuid))
                        .findFirst()
                        .orElse("unknown");
                Player opponent = Bukkit.getPlayer(UUID.fromString(opponentUuid));
                String opponentName = opponent != null ? opponent.getName() : "상대";
//                player.sendMessage("§e매칭이 성사되었습니다! 5초 안에 증강을 선택해주세요.");

                player.sendMessage("§5" + opponentName + " §f님과의 매칭이 성사되었습니다! §65초§f 뒤에 대전에 진입합니다.");
                // 증강 GUI
                //player.openInventory(new AugmentSelectGUI(session).getInventory());
            }

        }
        // 증강 선택에서 session 해결해야함
        Bukkit.getScheduler().runTaskLater(PVP.getInstance(), session::beginFight, 100L);
    }

    public MatchSession getSession(String playerUUID) {
        return activeSessions.get(playerUUID);
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

        session.endMatch();
        endMatch(session);

        Map<String, Integer> pointChange = new HashMap<>();
        if ("친선".equals(session.getType())) {
            winner.sendTitle("§l§a승리!", "§7포인트는 없지만 실력은 증명됐어요!", 10, 60, 10);
            winner.sendActionBar("§l§a[친선전 승리]");

            RespawnMessageDto message = RespawnMessageDto.builder()
                    .title("§l§c패배")
                    .subtitle("§7친선전에서는 패배해도 손해는 없습니다!")
                    .actionBar("§l§c[친선전 패배]")
                    .build();

            setPostRespawnMessage(loserId, message);

        } else if ("랭크".equals(session.getType())) {
            int point = 3;
            winner.sendTitle("§l§a승리!", "§7랭크 포인트 +" + point, 10, 60, 10);
            winner.sendActionBar("§l§a[랭크 승리] +" + point + " 포인트 획득!");

            RespawnMessageDto message = RespawnMessageDto.builder()
                    .title("§l§c패배")
                    .subtitle("§7랭크 포인트 -" + point)
                    .actionBar("§l§c[랭크 패배] -" + point + " 포인트 차감...")
                    .build();

            setPostRespawnMessage(loserId, message);

            // 승, 패 적립 및 포인트 지급
            // 승리시
            PlayerDataDto winnerDataDto = playerDataCache.get(winnerId);
            winnerDataDto.setWins(winnerDataDto.getWins()+1);
            winnerDataDto.setPoint(winnerDataDto.getPoint() + point);
            String winnerPrevTier = winnerDataDto.getTier();
            String winnerNewTier = getTierFromPoint(winnerDataDto.getPoint());
            winnerDataDto.setTier(winnerNewTier);

            Map<String, RewardLogDto> rewardLogDataCache = RewardLogDataRepository.getInstance()
                    .getRewardLogDataCache();
            RewardLogDto rewardLogDto = rewardLogDataCache.get(winner.getUniqueId().toString());


            if (!winnerPrevTier.equals(winnerNewTier) && rewardLogDto.getRewardedRank().stream().noneMatch(winnerNewTier::equals)) {
                winner.sendMessage("§b[승급] 티어가 " + winnerPrevTier + " → " + winnerNewTier + " 로 승급되었습니다!");

                Map<String, List<RewardDataDto>> rewardDataDtoCache = RewardDataRepository.getInstance()
                        .getRewardDataDtoCache();

                List<RewardDataDto> rewardDataDtos = rewardDataDtoCache.get(winnerNewTier);
                // 보상 지급
                MatchUtils.sendReward(MatchUtils.getReward(rewardDataDtos), winner);

                // 캐시에 저장
                rewardLogDto.getRewardedRank().add(winnerNewTier);
                rewardLogDataCache.put(winner.getUniqueId().toString(), rewardLogDto);
            }
            playerDataCache.put(winnerId, winnerDataDto);

            // 패배시
            PlayerDataDto loserDataDto = playerDataCache.get(loserId);
            loserDataDto.setLosses(loserDataDto.getLosses()+1);
            // 점수 0 이하로 안떨어지게
            int newPoint = Math.max(0, loserDataDto.getPoint() - point);
            loserDataDto.setPoint(newPoint);
            String loserPrevTier = loserDataDto.getTier();
            String loserNewTier = getTierFromPoint(loserDataDto.getPoint());
            loserDataDto.setTier(loserNewTier);
            if (!loserPrevTier.equals(loserNewTier)) {
                loser.sendMessage("§c[강등] 티어가 " + loserPrevTier + " → " + loserNewTier + " 로 강등되었습니다...");
            }
            playerDataCache.put(loserId, loserDataDto);

            // 점수 설정
            pointChange.put(winnerName, point);
            pointChange.put(loserName, -point);
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

        String message = "[§6" + matchLogDto.getType() + "] §f" + winnerName + "님과 " + loserName + "님의 대결에서 §9" + winnerName + "§f님이 승리하였습니다!!";

        Bukkit.broadcastMessage(message);
        session.setFightStarted(false);

        // 결과 로그 디비 저장
        MatchLogDataRepository.getInstance().saveMatchLog(matchLogDto);

        session.teleportSpawn();
    }

    public boolean isInCombat(String uuid) {
        MatchSession session = activeSessions.get(uuid);
        if (session == null) {
            return false;
        }
        return session.isFightStarted();
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

    private boolean canMatch(MatchingPlayerDto a, MatchingPlayerDto b, String type) {
        if ("랭크".equals(type) && !a.getTier().equals(b.getTier())) {
            return false;
        }

        String aRecent = recentOpponentMap.get(a.getUuid());
        String bRecent = recentOpponentMap.get(b.getUuid());

        if ((aRecent != null && aRecent.equals(b.getUuid())) ||
                (bRecent != null && bRecent.equals(a.getUuid()))) {
            return false;
        }

        return true;
    }

    public boolean cancelQueue(String uuid) {
        boolean removedFromRank = rankQueue.removeIf(player -> player.getUuid().equals(uuid));
        boolean removedFromFriendly = friendlyQueue.removeIf(player -> player.getUuid().equals(uuid));
        return removedFromRank || removedFromFriendly;
    }

    public void setPostRespawnMessage(String uuid, RespawnMessageDto message) {
        postRespawnMessageMap.put(uuid, message);
    }

    public RespawnMessageDto getPostRespawnMessage(String uuid) {
        return postRespawnMessageMap.get(uuid);
    }

    public void removePostRespawnMessage(String uuid) {
        postRespawnMessageMap.remove(uuid);
    }

}