package com.pointing.poker.service;

import com.pointing.poker.dto.RoomDto;
import com.pointing.poker.model.Player;
import com.pointing.poker.model.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RoomService {

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom() {
        String code;
        do {
            code = generateCode();
        } while (rooms.containsKey(code));

        Room room = new Room();
        room.setCode(code);
        room.setRevealed(false);
        rooms.put(code, room);
        return room;
    }

    public Room getOrThrow(String code) {
        Room room = rooms.get(code);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + code);
        }
        return room;
    }

    private String generateCode() {
        // 6-character uppercase code, like “AB12CD”
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setCode(room.getCode());
        dto.setStoryTitle(room.getStoryTitle());
        dto.setJiraKey(room.getJiraKey());
        dto.setRevealed(room.isRevealed());
        dto.setRoomOwnerId(room.getRoomOwnerId());
        dto.setRoomOwnerName(room.getRoomOwnerName());

        List<Player> players = room.getPlayers().values().stream()
                .map(p -> {
                    Player pd = new Player();
                    pd.setId(p.getId());
                    pd.setName(p.getName());
                    pd.setVote(p.getVote());
                    return pd;
                })
                .toList();

        dto.setPlayers(players);
        return dto;
    }

    public void addOrUpdatePlayer(String roomCode, String playerId, String name) {
//        Room room = getOrThrow(roomCode);
//        Player player = room.getPlayers().getOrDefault(playerId, new Player());
//        player.setId(playerId);
//        player.setName(name);
//        room.getPlayers().put(playerId, player);
        rooms.compute(roomCode, (key, existingRoom) -> {
            if (existingRoom != null) {
                // Assign owner if room has none
                if (existingRoom.getRoomOwnerId() == null) {
                    existingRoom.setRoomOwnerId(playerId);
                    existingRoom.setRoomOwnerName(name);
                    log.info("Assigned {} as owner for room {}", name, roomCode);
                }
                Player player = existingRoom.getPlayers().getOrDefault(playerId, new Player());
                player.setId(playerId);
                player.setName(name);
                existingRoom.getPlayers().put(playerId, player);
                return existingRoom; // Return the updated object
            } else {
                return null; // Return null to remove the key, or a new Room object
            }
        });
    }

    public void removePlayer(String roomCode, String playerId) {
        Room room = getOrThrow(roomCode);
        room.getPlayers().remove(playerId);
    }

    public void setVote(String roomCode, String playerId, String value) {
        Room room = getOrThrow(roomCode);
        Player player = room.getPlayers().get(playerId);
        if (player != null) {
            player.setVote(value);
        }
    }

    public void clearVotes(String roomCode) {
        Room room = getOrThrow(roomCode);
        room.getPlayers().values().forEach(p -> p.setVote(null));
        room.setRevealed(false);
    }

    public void setRevealed(String roomCode, boolean revealed) {
        Room room = getOrThrow(roomCode);
        room.setRevealed(revealed);
    }

    public void updateStory(String roomCode, String title, String jiraKey) {
        Room room = getOrThrow(roomCode);
        room.setStoryTitle(title);
        room.setJiraKey(jiraKey);
    }
}
