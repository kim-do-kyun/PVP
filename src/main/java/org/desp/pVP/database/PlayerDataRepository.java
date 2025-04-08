package org.desp.pVP.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.pVP.dto.PlayerDataDto;

public class PlayerDataRepository {

    private static PlayerDataRepository instance;
    private final MongoCollection<Document> playerList;
    public static Map<String, PlayerDataDto> playerDataCache = new HashMap<>();

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

    public Map<String, PlayerDataDto> getPlayerDataCache() {
        return playerDataCache;
    }
}
