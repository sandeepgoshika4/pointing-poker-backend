package com.pointing.poker.dto;

import com.pointing.poker.model.Player;
import lombok.Data;

import java.util.List;

@Data
public class RoomDto {
    private String code;
    private String storyTitle;
    private String jiraKey;
    private boolean revealed;
    private String roomOwnerId;
    private String roomOwnerName;
    private List<Player> players;
}
