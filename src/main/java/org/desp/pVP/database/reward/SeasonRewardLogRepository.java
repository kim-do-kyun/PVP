package org.desp.pVP.database.reward;

import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.pVP.database.DatabaseRegister;
import org.desp.pVP.database.PlayerDataRepository;
import org.desp.pVP.dto.PlayerDataDto;
import org.desp.pVP.dto.SeasonRewardLogDto;

public class SeasonRewardLogRepository {

    private static SeasonRewardLogRepository instance;
    private final MongoCollection<Document> rewardLogDB;
    @Getter
    private final Map<String, SeasonRewardLogDto> seasonRewardLogDataCache = new HashMap<>();

    public SeasonRewardLogRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.rewardLogDB = database.getDatabase().getCollection("SeasonRewardLog");
    }

    public static SeasonRewardLogRepository getInstance() {
        if (instance == null) {
            instance = new SeasonRewardLogRepository();
        }
        return instance;
    }

    public void saveRewardLog(Player player) {
        String uuid = player.getUniqueId().toString();
        SeasonRewardLogDto rewardLog = seasonRewardLogDataCache.get(uuid);

        Map<String, PlayerDataDto> playerDataCache = PlayerDataRepository.getInstance().getPlayerDataCache();
        PlayerDataDto playerDataDto = playerDataCache.get(uuid);

        Document document = new Document()
                .append("user_id", rewardLog.getUser_id())
                .append("uuid", rewardLog.getUuid())
                .append("tier", playerDataDto.getTier())
                .append("rewardItem", rewardLog.getRewardItem());

        rewardLogDB.insertOne(document);
    }
}
