package com.ssafy.revibek.playlist.controller;

import com.ssafy.revibek.playlist.dto.PlaylistCreateRequestDto;
import com.ssafy.revibek.playlist.dto.PlaylistResponseDto;
import com.ssafy.revibek.playlist.dto.PlaylistSongRequestDto;
import com.ssafy.revibek.playlist.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createPlaylist(Authentication authentication,
                                                             @Valid @RequestBody PlaylistCreateRequestDto dto) {
        String playlistId = playlistService.createPlaylist(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("playlistId", playlistId, "message", "플레이리스트 생성 완료"));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponseDto>> getMyPlaylists(Authentication authentication) {
        return ResponseEntity.ok(playlistService.getMyPlaylists(authentication.getName()));
    }

    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, String>> addSong(Authentication authentication,
                                                       @PathVariable String playlistId,
                                                       @Valid @RequestBody PlaylistSongRequestDto dto) {
        playlistService.addSong(authentication.getName(), playlistId, dto.getSongId());
        return ResponseEntity.ok(Map.of("message", "플레이리스트에 곡 추가 완료"));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, String>> removeSong(Authentication authentication,
                                                          @PathVariable String playlistId,
                                                          @PathVariable String songId) {
        playlistService.removeSong(authentication.getName(), playlistId, songId);
        return ResponseEntity.ok(Map.of("message", "플레이리스트에서 곡 삭제 완료"));
    }
}
