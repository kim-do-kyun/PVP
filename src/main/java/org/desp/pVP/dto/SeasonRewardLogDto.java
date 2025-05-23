package org.desp.pVP.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class SeasonRewardLogDto {
    private String user_id;
    private String uuid;
    private String tier;
    private List<String> rewardItem;
}
