package com.pointing.poker.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private String playerId;
    private String value;
}
