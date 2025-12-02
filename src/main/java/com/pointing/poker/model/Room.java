package com.pointing.poker.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Room {
    private String code;
    private String storyTitle;
    private String jiraKey;
    private boolean revealed;
    private String roomOwnerId;
    private String roomOwnerName;

    // playerId -> Player
    private Map<String, Player> players = new LinkedHashMap<>();
}
