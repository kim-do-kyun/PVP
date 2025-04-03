package org.desp.pVP.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MatchLogDto {
    private String playerA;
    private String playerB;
    private String winner;
    private String startTime;
    private String endTime;
    private String type;
    private Map<String, Integer> pointChange;
}
