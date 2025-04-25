package org.desp.pVP.database.reward;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.pVP.database.DatabaseRegister;
import org.desp.pVP.dto.RewardLogDto;

public class RewardLogRepository {

    private static RewardLogRepository instance;
    private final MongoCollection<Document> rewardLogDB;
    @Getter
    private final Map<String, RewardLogDto> rewardLogDataCache = new HashMap<>();

    public RewardLogRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.rewardLogDB = database.getDatabase().getCollection("RewardLog");
    }

    public static RewardLogRepository getInstance() {
        if (instance == null) {
            instance = new RewardLogRepository();
        }
        return instance;
    }

    public void loadRewardLogData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Document document = new Document("uuid", uuid);
        if (rewardLogDB.find(Filters.eq("uuid", uuid)).first() == null) {
            List<String> rewardedRank = new ArrayList<>();
            Document newUserDocument = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("rewardedRank", rewardedRank);
            rewardLogDB.insertOne(newUserDocument);
        }

        List<String> rewardedRank = rewardLogDB.find(document).first().getList("rewardedRank", String.class);

        RewardLogDto rewardLogDto = RewardLogDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .rewardedRank(rewardedRank)
                .build();

        rewardLogDataCache.put(uuid, rewardLogDto);
    }

    public void saveRewardLog(Player player) {
        String uuid = player.getUniqueId().toString();
        RewardLogDto rewardLog = rewardLogDataCache.get(uuid);

        Document document = new Document()
                .append("user_id", rewardLog.getUser_id())
                .append("uuid", rewardLog.getUuid())
                .append("rewardedRank", rewardLog.getRewardedRank());

        Document filter = new Document("uuid", rewardLog.getUuid())
                .append("rewardedRank", rewardLog.getRewardedRank());

        rewardLogDB.replaceOne(filter, document, new ReplaceOptions().upsert(true));
    }
}
