package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PlayerDataDto {
    private String user_id;
    private String uuid;
    private String tier;
    private int point;
    private int wins;
    private int losses;
}
