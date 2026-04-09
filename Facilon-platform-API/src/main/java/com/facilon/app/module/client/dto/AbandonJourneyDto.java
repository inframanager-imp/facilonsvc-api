package com.facilon.app.module.client.dto;

import lombok.Data;

@Data
public class AbandonJourneyDto {
    private Long investorId;
    private Long brokerId;
    private Long productId;
    private String abandonReason;
}
