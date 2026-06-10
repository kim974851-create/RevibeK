package com.ssafy.revibek.song.controller;

import com.ssafy.revibek.song.dto.SongLikeStatusDto;
import com.ssafy.revibek.song.service.SongLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs/{songId}")
@RequiredArgsConstructor
public class SongLikeController {

    private final SongLikeService songLikeService;

    @PostMapping("/like")
    public ResponseEntity<SongLikeStatusDto> like(Authentication authentication, @PathVariable String songId) {
        return ResponseEntity.ok(songLikeService.like(authentication.getName(), songId));
    }

    @DeleteMapping("/like")
    public ResponseEntity<SongLikeStatusDto> unlike(Authentication authentication, @PathVariable String songId) {
        return ResponseEntity.ok(songLikeService.unlike(authentication.getName(), songId));
    }

    @GetMapping("/like-status")
    public ResponseEntity<SongLikeStatusDto> likeStatus(Authentication authentication, @PathVariable String songId) {
        return ResponseEntity.ok(songLikeService.status(authentication.getName(), songId));
    }
}
