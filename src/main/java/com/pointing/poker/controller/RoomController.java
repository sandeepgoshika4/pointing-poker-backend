package com.pointing.poker.controller;

import com.pointing.poker.dto.JoinRequest;
import com.pointing.poker.dto.RoomDto;
import com.pointing.poker.dto.StoryUpdateRequest;
import com.pointing.poker.dto.VoteRequest;
import com.pointing.poker.model.Room;
import com.pointing.poker.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    // Broadcast helper
    private void broadcast(String roomCode) {
        Room room = roomService.getOrThrow(roomCode);
        RoomDto dto = roomService.toDto(room);

        log.info("Broadcasting update for room {} to {} players",
                roomCode, room.getPlayers().size());

        messagingTemplate.convertAndSend("/topic/rooms/" + roomCode, dto);
    }

    // -------------------------------
    // REST ENDPOINTS
    // -------------------------------

    @PostMapping("/api/rooms")
    public ResponseEntity<RoomDto> createRoom() {
        Room room = roomService.createRoom();
        log.info("Room created: {}", room.getCode());
        return ResponseEntity.ok(roomService.toDto(room));
    }

    @GetMapping("/api/rooms/{code}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable String code) {
        log.info("Fetching room {} via REST", code);
        try {
            Room room = roomService.getOrThrow(code);
            return ResponseEntity.ok(roomService.toDto(room));
        } catch (IllegalArgumentException ex) {
            log.warn("Room {} not found", code);
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------
    // WEBSOCKET / STOMP ENDPOINTS
    // -------------------------------

    @MessageMapping("rooms/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, JoinRequest join) {
        log.info("Player joined room {} -> {} ({})",
                roomCode, join.getName(), join.getPlayerId());

        roomService.addOrUpdatePlayer(roomCode, join.getPlayerId(), join.getName());
        broadcast(roomCode);
    }

    @MessageMapping("rooms/{roomCode}/leave")
    public void leaveRoom(@DestinationVariable String roomCode, JoinRequest leave) {
        log.info("Player left room {} -> {} ({})",
                roomCode, leave.getName(), leave.getPlayerId());

        roomService.removePlayer(roomCode, leave.getPlayerId());
        broadcast(roomCode);
    }

    @MessageMapping("rooms/{roomCode}/vote")
    public void vote(@DestinationVariable String roomCode, VoteRequest vote) {
        log.info("Vote in room {} -> player {} voted '{}'",
                roomCode, vote.getPlayerId(), vote.getValue());

        roomService.setVote(roomCode, vote.getPlayerId(), vote.getValue());
        broadcast(roomCode);
    }

    @MessageMapping("rooms/{roomCode}/reveal")
    public void reveal(@DestinationVariable String roomCode, StoryUpdateRequest req) {

        Room room = roomService.getOrThrow(roomCode);

        if (!req.getPlayerId().equals(room.getRoomOwnerId())) {
            log.warn("Unauthorized reveal attempt by {}", req.getPlayerId());
            return;
        }

        log.info("Votes revealed for room {}", roomCode);

        roomService.setRevealed(roomCode, true);
        broadcast(roomCode);
    }

    @MessageMapping("rooms/{roomCode}/hide")
    public void hide(@DestinationVariable String roomCode) {
        log.info("Votes hidden for room {}", roomCode);

        roomService.setRevealed(roomCode, false);
        broadcast(roomCode);
    }

    @MessageMapping("rooms/{roomCode}/reset")
    public void reset(@DestinationVariable String roomCode) {
        log.info("Votes reset for room {}", roomCode);

        roomService.clearVotes(roomCode);
        broadcast(roomCode);
    }

    @MessageMapping("rooms/{roomCode}/story")
    public void updateStory(@DestinationVariable String roomCode, StoryUpdateRequest request) {
        Room room = roomService.getOrThrow(roomCode);

        // Only owner may update story
        if (!request.getPlayerId().equals(room.getRoomOwnerId())) {
            log.warn("Unauthorized story update attempt in room {} by {}", roomCode, request.getPlayerId());
            return; // skip processing
        }

        log.info("Story updated by owner {} in room {} -> Title='{}', Jira='{}'",
                request.getPlayerId(), roomCode, request.getStoryTitle(), request.getJiraKey());

        roomService.updateStory(roomCode, request.getStoryTitle(), request.getJiraKey());
        broadcast(roomCode);
    }
}
