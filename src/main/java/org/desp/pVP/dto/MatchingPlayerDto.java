package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MatchingPlayerDto {
    private String uuid;
    private int point;
    private String tier;
    private String joinTime;
}