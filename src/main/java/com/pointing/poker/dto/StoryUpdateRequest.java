package com.pointing.poker.dto;

import lombok.Data;

@Data
public class StoryUpdateRequest {
    private String playerId;
    private String storyTitle;
    private String jiraKey;
}
