package org.desp.pVP.database.reward;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.desp.pVP.database.DatabaseRegister;
import org.desp.pVP.dto.RewardDataDto;

public class SeasonRewardDataRepository {

    private static SeasonRewardDataRepository instance;
    private final MongoCollection<Document> rewardDB;
    @Getter
    private final Map<String, List<RewardDataDto>> seasonRewardDataDtoCache = new HashMap<>();

    public SeasonRewardDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.rewardDB = database.getDatabase().getCollection("SeasonRewardData");
    }

    public static SeasonRewardDataRepository getInstance() {
        if (instance == null) {
            instance = new SeasonRewardDataRepository();
        }
        return instance;
    }

    public void loadRewardData() {
        FindIterable<Document> documents = rewardDB.find();
        for (Document document : documents) {
            String rank = document.getString("rank");
            List<String> rewards = document.getList("rewards", String.class);

            List<RewardDataDto> rewardData = new ArrayList<>();
            for (String reward : rewards) {
                String[] split = reward.split(":");
                    if (split.length == 2) {
                    RewardDataDto rewardDataDto = RewardDataDto.builder()
                            .item_info(reward)
                            .item_id(split[0])
                            .amount(Integer.parseInt(split[1]))
                            .build();

                    rewardData.add(rewardDataDto);
                }
            }
            seasonRewardDataDtoCache.put(rank, rewardData);
        }
    }
}
