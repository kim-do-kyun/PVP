package org.desp.pVP.database;

import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.desp.pVP.dto.MatchLogDto;

public class MatchLogDataRepository {

    private static MatchLogDataRepository instance;
    private final MongoCollection<Document> matchLogDB;
    @Getter
    private final Map<String, MatchLogDto> matchLogDataCache = new HashMap<>();

    public MatchLogDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.matchLogDB = database.getDatabase().getCollection("MatchLog");
    }

    public static MatchLogDataRepository getInstance() {
        if (instance == null) {
            instance = new MatchLogDataRepository();
        }
        return instance;
    }

    public void saveMatchLog(MatchLogDto matchLog) {
        Document document = new Document()
                .append("playerA", matchLog.getPlayerA())
                .append("playerB", matchLog.getPlayerB())
                .append("winner", matchLog.getWinner())
                .append("startTime", matchLog.getStartTime())
                .append("endTime", matchLog.getEndTime())
                .append("type", matchLog.getType())
                .append("pointChange", matchLog.getPointChange());

        matchLogDB.insertOne(document);
    }

    public void resetMatchLog() {
        try {
            matchLogDataCache.clear();
            matchLogDB.deleteMany(new Document());
            System.out.println("PVP MatchLog DB 초기화 완료");
        } catch (Exception e) {
            System.out.println("PVP MatchLog DB 데이터 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}
