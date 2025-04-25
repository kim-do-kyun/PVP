package org.desp.pVP.threadUtil;

import java.util.Calendar;
import org.bukkit.Bukkit;
import org.desp.pVP.PVP;
import org.desp.pVP.database.PlayerDataRepository;

public class PlayerRankScheduler {

    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PVP.getInstance(), () -> {
            try {
                PlayerDataRepository.getInstance().sortAllPlayerRank();
                System.out.println("플레이어 랭크 정렬 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
            }, 0L, 3600L);
    }

    public void challengerUpdater() {
        // 현재 시간에서 자정까지의 밀리초 계산
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);  // 자정 12시로 설정
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 자정까지의 남은 시간
        long delay = calendar.getTimeInMillis() - currentTimeMillis;
        if (delay <= 0) {
            // 만약 현재 시간이 자정 이후라면, 다음 날 자정으로 설정
            delay = 24 * 60 * 60 * 1000;  // 24시간 후
        }

        // 자정 12시가 되면 updateChallengerPlayers 실행
        Bukkit.getScheduler().runTaskLater(PVP.getInstance(), this::updateChallengerPlayers, delay / 50L);

        // 그 이후, 매일 24시간 마다 실행되도록 스케줄링
        Bukkit.getScheduler().runTaskTimer(PVP.getInstance(), this::updateChallengerPlayers, delay / 50L, 24 * 60 * 60 * 20L);
        // 24 * 60 * 60 * 20L => 24시간 후에 반복 실행
    }

    private void updateChallengerPlayers() {
        PlayerDataRepository.getInstance().updateChallengerPlayers();
        System.out.println("챌린저 인원 업데이트 완료!");
    }
}