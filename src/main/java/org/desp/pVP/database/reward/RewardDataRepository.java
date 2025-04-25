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

public class RewardDataRepository {

    private static RewardDataRepository instance;
    private final MongoCollection<Document> rewardDB;
    @Getter
    private final Map<String, List<RewardDataDto>> rewardDataDtoCache = new HashMap<>();

    public RewardDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.rewardDB = database.getDatabase().getCollection("RewardData");
    }

    public static RewardDataRepository getInstance() {
        if (instance == null) {
            instance = new RewardDataRepository();
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
                            .item_id(split[0])
                            .amount(Integer.parseInt(split[1]))
                            .build();

                    rewardData.add(rewardDataDto);
                }
            }
            rewardDataDtoCache.put(rank, rewardData);
        }
    }
}
