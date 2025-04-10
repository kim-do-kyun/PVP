package org.desp.pVP.database;

import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.desp.pVP.dto.RoomDto;

public class ArenaRepository {

    private static ArenaRepository instance;
    private final MongoCollection<Document> arenaDB;
    @Getter
    public Map<String, RoomDto> arenaMap = new HashMap<>();

    private ArenaRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.arenaDB = database.getDatabase().getCollection("MapData");
    }

    public static ArenaRepository getInstance() {
        if (instance == null) instance = new ArenaRepository();
        return instance;
    }

    // 서버 시작 시 전체 로딩
    public void loadAllRooms() {
        for (Document doc : arenaDB.find()) {
            RoomDto roomDto = RoomDto.builder()
                    .roomName(doc.getString("roomName"))
                    .isPlaying(doc.getBoolean("isPlaying", false))
                    .playerAWarpLocation(doc.getString("playerAWarpLocation"))
                    .playerBWarpLocation(doc.getString("playerBWarpLocation"))
                    .build();

            arenaMap.put(roomDto.getRoomName(), roomDto);
        }
    }

//    public void updateRoomStatus(String roomId, boolean isPlaying) {
//        arenaDB.updateOne(
//                Filters.eq("roomName", new ObjectId(roomId)),
//                Updates.set("isPlaying", isPlaying)
//        );
//    }

    public RoomDto getAvailableRoom() {
        for (RoomDto room : arenaMap.values()) {
            if (!room.isPlaying()) {
                room.setPlaying(true);
//                ArenaRepository.getInstance().updateRoomStatus(room.getRoomName(), true);
                return room;
            }
        }
        return null;
    }
}
