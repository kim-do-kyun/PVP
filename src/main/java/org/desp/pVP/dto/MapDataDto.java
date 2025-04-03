package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MapDataDto {
    private String type;
    private boolean isPlaying;
}
