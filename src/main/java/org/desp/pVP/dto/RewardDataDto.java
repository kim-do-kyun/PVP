package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RewardDataDto {
    private String item_info;
    private String item_id;
    private int amount;
}
