package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PlayerRankInfoDto {
    private String playerName;
    private String rank;
    private int points;
}
