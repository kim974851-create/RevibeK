package com.ssafy.revibek.radio.controller;

import com.ssafy.revibek.radio.dto.RadioRequestDto;
import com.ssafy.revibek.radio.dto.RadioCreateResponseDto;
import com.ssafy.revibek.radio.dto.RadioResponseDto;
import com.ssafy.revibek.radio.service.RadioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RadioController {

    private final RadioService radioService;

    // 라디오 세션 생성
    @PostMapping({"/api/radio", "/api/radios"})
    public ResponseEntity<RadioCreateResponseDto> createSession(Authentication authentication,
                                                                @Valid @RequestBody RadioRequestDto dto) {
        return ResponseEntity.ok(radioService.createSession(authentication.getName(), dto));
    }

    // 세션 단건 조회
    @GetMapping({"/api/radio/{id}", "/api/radios/{id}"})
    public ResponseEntity<RadioResponseDto> getSession(Authentication authentication,
                                                       @PathVariable String id) {
        return ResponseEntity.ok(radioService.getSession(id, authentication.getName()));
    }

    // 내 세션 목록 조회
    @GetMapping({"/api/radio/me", "/api/radios/me"})
    public ResponseEntity<List<RadioResponseDto>> getSessionByUser(Authentication authentication) {
        return ResponseEntity.ok(radioService.getSessionByUser(authentication.getName()));
    }
}