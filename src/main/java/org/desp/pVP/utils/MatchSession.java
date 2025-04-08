package org.desp.pVP.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.desp.pVP.PVP;
import org.desp.pVP.dto.RoomDto;

@Getter @Setter
public class MatchSession {
    private RoomDto room;
    private boolean fightStarted = false;
    private final String type;
    private final String playerA;
    private final String playerB;
    private final String startTime;
    private String endTime;
    private final Map<String, String> augmentSelections = new HashMap<>();

    public MatchSession(String playerA, String playerB, String type) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.type = type;
        this.startTime = DateUtils.getCurrentTime();
    }

    public void selectAugment(String player, String augment) {
        augmentSelections.put(player, augment);
        //checkReadyToStart();
    }

    public void checkReadyToStart() {
        if (augmentSelections.size() == 2) {
            beginFight();
        }
    }

    public void beginFight() {
        //applyAugments();
        this.fightStarted = true;
        Player playerA = Bukkit.getPlayer(UUID.fromString(this.playerA));
        Player playerB = Bukkit.getPlayer(UUID.fromString(this.playerB));

        if (playerA != null) playerA.teleport(LocationUtils.parseLocation(room.getPlayerAWarpLocation()));
        if (playerB != null) playerB.teleport(LocationUtils.parseLocation(room.getPlayerBWarpLocation()));

        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 100, 10, false, false, false);
        PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 100, 128, false, false, false);
        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255, false, false, false);

        if (playerA != null) {
            playerA.addPotionEffect(slow);
            playerA.addPotionEffect(jump);
            playerA.addPotionEffect(resistance);
        }

        if (playerB != null) {
            playerB.addPotionEffect(slow);
            playerB.addPotionEffect(jump);
            playerB.addPotionEffect(resistance);
        }
        // 5초후에 경기가 시작된다는 메시지와 5초동안 아무동작 못하도록 설정
        // 카운트다운 타이틀
        for (int i = 5; i >= 1; i--) {
            final int count = i;
            Bukkit.getScheduler().runTaskLater(PVP.getInstance(), () -> {
                if (playerA != null) playerA.sendTitle("§e" + count + "초 후 전투 시작!", "", 0, 20, 0);
                if (playerB != null) playerB.sendTitle("§e" + count + "초 후 전투 시작!", "", 0, 20, 0);
            }, (6 - i) * 20L).getTaskId();

            //scheduledTaskIds.add(taskId);
        }

        // 전투 시작 후 효과 제거
        Bukkit.getScheduler().runTaskLater(PVP.getInstance(), () -> {
            if (playerA != null) {
                playerA.sendTitle("§c전투 시작!", "", 10, 40, 10);
                playerA.removePotionEffect(PotionEffectType.SLOW);
                playerA.removePotionEffect(PotionEffectType.JUMP);
                playerA.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }

            if (playerB != null) {
                playerB.sendTitle("§c전투 시작!", "", 10, 40, 10);
                playerB.removePotionEffect(PotionEffectType.SLOW);
                playerB.removePotionEffect(PotionEffectType.JUMP);
                playerB.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }

        }, 120L).getTaskId();
        //scheduledTaskIds.add(startId);
    }

    public void teleportSpawn() {
        Player playerA = Bukkit.getPlayer(UUID.fromString(this.playerA));
        Player playerB = Bukkit.getPlayer(UUID.fromString(this.playerB));

        if (playerA != null) playerA.teleport(new Location(Bukkit.getWorld("world"), -21.475, 37.000, -737.459));
        if (playerB != null) playerB.teleport(new Location(Bukkit.getWorld("world"), -21.475, 37.000, -737.459));
    }


    public List<String> getPlayers() {
        return Arrays.asList(playerA, playerB);
    }

    public String getOpponent(String uuid) {
        return uuid.equals(playerA) ? playerB : playerA;
    }

    public String getWinner(String loser) {
        return getOpponent(loser);
    }

    public void  endMatch() {
        this.endTime = DateUtils.getCurrentTime();
        this.fightStarted = false;
    }
}