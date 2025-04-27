package org.desp.pVP.database;

import static org.desp.pVP.utils.MatchUtils.getAnyPlayer;
import static org.desp.pVP.utils.MatchUtils.getTierFromPoint;
import static org.desp.pVP.utils.MatchUtils.giveRankUpReward;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.pVP.dto.PlayerDataDto;
import org.desp.pVP.dto.PlayerRankInfoDto;

public class PlayerDataRepository {

    private static PlayerDataRepository instance;
    private final MongoCollection<Document> playerList;
    public static Map<String, PlayerDataDto> playerDataCache = new HashMap<>();
    public static List<PlayerRankInfoDto> playerRankInfoCache = new ArrayList<>();

    public PlayerDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerList = database.getDatabase().getCollection("PlayerData");
    }

    public static PlayerDataRepository getInstance() {
        if (instance == null) {
            instance = new PlayerDataRepository();
        }
        return instance;
    }

    public void loadAllPlayerData() {
        for (Document document : playerList.find()) {
            String user_id = document.getString("user_id");
            String uuid = document.getString("uuid");
            String tier = document.getString("tier");
            int point = document.getInteger("point");
            int wins = document.getInteger("wins");
            int losses = document.getInteger("losses");

            PlayerDataDto playerDto = PlayerDataDto.builder()
                    .user_id(user_id)
                    .uuid(uuid)
                    .tier(tier)
                    .point(point)
                    .wins(wins)
                    .losses(losses)
                    .build();

            playerDataCache.put(uuid, playerDto);
        }
    }

    public void loadPlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Document document = new Document("uuid", uuid);
        if (playerList.find(Filters.eq("uuid", uuid)).first() == null) {
            Document newUserDocument = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("tier", "브론즈")
                    .append("point", 0)
                    .append("wins", 0)
                    .append("losses", 0);
            playerList.insertOne(newUserDocument);
        }

        String tier = playerList.find(document).first().getString("tier");
        int point = playerList.find(document).first().getInteger("point");
        int wins = playerList.find(document).first().getInteger("wins");
        int losses = playerList.find(document).first().getInteger("losses");

        PlayerDataDto playerDto = PlayerDataDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .tier(tier)
                .point(point)
                .wins(wins)
                .losses(losses)
                .build();

        playerDataCache.put(uuid, playerDto);
    }

    public PlayerDataDto getPlayerData(Player player) {
        return playerDataCache.get(player.getUniqueId().toString());
    }

    public void savePlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        PlayerDataDto playerDataDto = playerDataCache.get(uuid);
        Document document = new Document()
                .append("user_id", playerDataDto.getUser_id())
                .append("uuid", uuid)
                .append("tier", playerDataDto.getTier())
                .append("point", playerDataDto.getPoint())
                .append("wins", playerDataDto.getWins())
                .append("losses", playerDataDto.getLosses());

        playerList.replaceOne(
                Filters.eq("uuid", uuid),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public String getPlayerNameToUUID(String user_id) {
        Document document = playerList.find(Filters.eq("user_id", user_id)).first();
        if (document != null) {
            return document.getString("uuid");
        }
        return null;
    }

    public void sortAllPlayerRank() {
        playerRankInfoCache.clear();

        for (Entry<String, PlayerDataDto> playerData : playerDataCache.entrySet()) {
            PlayerDataDto playerInfo = playerData.getValue();
            String userId = playerInfo.getUser_id();
            int point = playerInfo.getPoint();
            String tier = getTierFromPoint(point);

            PlayerRankInfoDto playerInfoDto = PlayerRankInfoDto.builder()
                    .playerName(userId)
                    .rank(tier)
                    .points(point)
                    .build();

            playerRankInfoCache.add(playerInfoDto);
        }

        playerRankInfoCache.sort((a, b) -> Integer.compare(b.getPoints(), a.getPoints()));
    }

    public int getPlayerRank(String userName) {
        for (int i = 0; i < playerRankInfoCache.size(); i++) {
            PlayerRankInfoDto player = playerRankInfoCache.get(i);
            if (player.getPlayerName().equals(userName)) {
                return i + 1;
            }
        }
        return -1;
    }

    public List<PlayerRankInfoDto> getTop10Players() {
        int limit = Math.min(10, playerRankInfoCache.size());
        System.out.println("======================");
        System.out.println("playerDataCache = " + playerDataCache.size());
        System.out.println("playerRankInfoCache = " + playerRankInfoCache.size());
        System.out.println("limit = " + limit);
        System.out.println("======================");
        return playerRankInfoCache.subList(0, limit);
    }

    public Map<String, PlayerRankInfoDto> getChallengerPlayers() {
        List<PlayerRankInfoDto> top10Players = getTop10Players();

        Map<String, PlayerRankInfoDto> challengerPlayers = new HashMap<>();

        for (PlayerRankInfoDto top10Player : top10Players) {
            if (top10Player.getPoints() >= 120) {
                challengerPlayers.put(top10Player.getPlayerName(), top10Player);
                if (challengerPlayers.size() == 5) {
                    break;
                }
            }
        }

        return challengerPlayers;
    }

    public void updateChallengerPlayers() {
        sortAllPlayerRank();

        Map<String, PlayerRankInfoDto> challengerPlayers = getChallengerPlayers();
        if (challengerPlayers.isEmpty()) {
            return;
        }

        for (Map.Entry<String, PlayerRankInfoDto> entry : challengerPlayers.entrySet()) {
            String playerName = entry.getKey();
            PlayerRankInfoDto playerRankInfoDto = entry.getValue();

            playerRankInfoDto.setRank("챌린저");

            PlayerDataDto playerDataDto = playerDataCache.get(playerName);

            if (playerDataDto != null) {
                giveRankUpReward(getAnyPlayer(playerDataDto.getUser_id()), playerDataDto.getTier(), "챌린저");
                playerDataDto.setTier("챌린저");
            }
        }
    }

    public Map<String, PlayerDataDto> getPlayerDataCache() {
        return playerDataCache;
    }

    public List<PlayerRankInfoDto> getPlayerRankInfoCache() {
        return playerRankInfoCache;
    }

    public void resetPlayerDataDB() {
        try {
            playerDataCache.clear();
            playerRankInfoCache.clear();
            playerList.deleteMany(new Document());
            System.out.println("PVP Player DB 초기화 완료");
        } catch (Exception e) {
            System.out.println("PVP PlayerDB 데이터 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}
