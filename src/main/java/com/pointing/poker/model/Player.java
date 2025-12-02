package com.pointing.poker.model;

import lombok.Data;

@Data
public class Player {
    private String id;     // client-generated UUID
    private String name;
    private String vote;   // "0", "1", "?", "☕", etc.
}
