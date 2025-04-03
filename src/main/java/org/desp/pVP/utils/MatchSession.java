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
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter @Setter
public class MatchSession {
    private final String playerA;
    private final String playerB;
    private final String startTime;
    private String endTime;
    private final Map<String, String> augmentSelections = new HashMap<>();
    private final String type;

    public MatchSession(String playerA, String playerB, String type) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.type = type;
        this.startTime = DateUtils.getCurrentTime();
    }

    public void selectAugment(String player, String augment) {
        augmentSelections.put(player, augment);
        System.out.println("======================");
        System.out.println("player = " + player);
        System.out.println("augment = " + augment);
        System.out.println("======================");
        //checkReadyToStart();
    }

    public void checkReadyToStart() {
        if (augmentSelections.size() == 2) {
            beginFight();
        }
    }

    public void beginFight() {
        //applyAugments();
        teleportPlayers("pvp",21.837, -17, 9.414,-19.615, -17, 9.383);
    }

//    private void applyAugments() {
//        for (Map.Entry<String, AugmentType> entry : augmentSelections.entrySet()) {
//            Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));
//            if (player == null) continue;
//
//        }
//    }

    public void teleportPlayers(String type, double ax, double ay, double az, double bx, double by, double bz) {
        Player playerA = Bukkit.getPlayer(UUID.fromString(this.playerA));
        Player playerB = Bukkit.getPlayer(UUID.fromString(this.playerB));

        if (playerA != null) playerA.teleport(new Location(Bukkit.getWorld(type), ax, ay, az));
        if (playerB != null) playerB.teleport(new Location(Bukkit.getWorld(type), bx, by, bz));
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

    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public void  endMatch() { this.endTime = DateUtils.getCurrentTime(); }
}