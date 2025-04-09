package org.desp.pVP.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RespawnMessageDto {
    private final String title;
    private final String subtitle;
    private final String actionBar;
}
