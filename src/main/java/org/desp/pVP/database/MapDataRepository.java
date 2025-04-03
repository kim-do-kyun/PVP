//package org.desp.pVP.database;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.ReplaceOptions;
//import java.util.HashMap;
//import java.util.Map;
//import org.bson.Document;
//import org.bukkit.entity.Player;
//import org.desp.pVP.dto.PlayerDataDto;
//
//public class MapDataRepository {
//
//    private static MapDataRepository instance;
//    private final MongoCollection<Document> mapDB;
//    public static Map<String, PlayerDataDto> mapCache = new HashMap<>();
//
//    public MapDataRepository() {
//        DatabaseRegister database = new DatabaseRegister();
//        this.mapDB = database.getDatabase().getCollection("MapData");
//    }
//
//    public static MapDataRepository getInstance() {
//        if (instance == null) {
//            instance = new MapDataRepository();
//        }
//        return instance;
//    }
//
//    public void loadPlayerData(Player player) {
//        String user_id = player.getName();
//        String uuid = player.getUniqueId().toString();
//
//        String tier = mapDB.find(document).first().getString("tier");
//        int point = mapDB.find(document).first().getInteger("point");
//        int wins = mapDB.find(document).first().getInteger("wins");
//        int losses = mapDB.find(document).first().getInteger("losses");
//
//        PlayerDataDto playerDto = PlayerDataDto.builder()
//                .user_id(user_id)
//                .uuid(uuid)
//                .tier(tier)
//                .point(point)
//                .wins(wins)
//                .losses(losses)
//                .build();
//
//        mapCache.put(uuid, playerDto);
//    }
//
//    public Map<String, PlayerDataDto> getPlayerDataCache() {
//        return mapCache;
//    }
//}
