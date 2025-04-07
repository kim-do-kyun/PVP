package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RoomDto {
    private String roomName;
    private boolean isPlaying;
    private String playerAWarpLocation;
    private String playerBWarpLocation;
}

