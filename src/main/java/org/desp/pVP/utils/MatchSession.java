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
    private final Map<String, AugmentType> augmentSelections = new HashMap<>();

    public MatchSession(String playerA, String playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.startTime = DateUtils.getCurrentTime();
    }

    public void selectAugment(String player, AugmentType augment) {
        augmentSelections.put(player, augment);
        checkReadyToStart();
    }

    private void checkReadyToStart() {
        if (augmentSelections.size() == 2) {
            beginFight();
        }
    }

    public void beginFight() {
        //applyAugments();
        teleportPlayers();
    }

    private void applyAugments() {
        for (Map.Entry<String, AugmentType> entry : augmentSelections.entrySet()) {
            Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));
            if (player == null) continue;

//            switch (entry.getValue()) {
//                case ATTACK -> player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0));
//                case HEALTH -> {
//                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0);
//                    player.setHealth(30.0);
//                }
//                case SPEED -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
//            }
        }
    }

    private void teleportPlayers() {
        Player playerA = Bukkit.getPlayer(UUID.fromString(this.playerA));
        Player playerB = Bukkit.getPlayer(UUID.fromString(this.playerB));

        if (playerA != null) playerA.teleport(new Location(Bukkit.getWorld("world"), 0.467, 18, 9.370));
        if (playerB != null) playerB.teleport(new Location(Bukkit.getWorld("world"), 0.467, 18, 9.370));
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