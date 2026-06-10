// com.ssafy.revibek.youtube.controller.YoutubeController.java
package com.ssafy.revibek.youtube.controller;

import com.ssafy.revibek.youtube.service.YoutubeService;
import com.ssafy.revibek.youtube.dto.YoutubeImportResponseDto;
import com.ssafy.revibek.youtube.dto.YoutubeVideoResponseDto;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;

    @PostMapping("/channel")
    public ResponseEntity<YoutubeImportResponseDto> addChannel(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        return ResponseEntity.ok(youtubeService.processChannel(url));
    }

    @PostMapping("/channels")
    public ResponseEntity<YoutubeImportResponseDto> addChannels(@RequestBody Map<String, List<String>> request) {
        List<String> urls = request.get("urls");
        List<YoutubeVideoResponseDto> videos = urls.stream()
            .map(youtubeService::processChannel)
            .flatMap(response -> response.getVideos().stream())
            .toList();
        String source = videos.isEmpty() ? "DUMMY" : "YOUTUBE_OR_DUMMY";
        return ResponseEntity.ok(new YoutubeImportResponseDto(source, "채널 " + urls.size() + "개 처리 완료", videos));
    }
}