package com.ssafy.revibek.tts;

import com.ssafy.revibek.radio.dto.RadioResponseDto;
import com.ssafy.revibek.radio.mapper.RadioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
public class TtsController {

    private final RadioMapper radioMapper;
    private final TtsService ttsService;

    @PostMapping
    public ResponseEntity<TtsResponseDto> generate(@Valid @RequestBody TtsRequestDto request) {
        return ResponseEntity.ok(ttsService.generateSpeech(request.getText(), request.getVoice()));
    }

    @GetMapping("/radio/{radioSessionId}")
    public ResponseEntity<TtsResponseDto> getRadioTts(@PathVariable String radioSessionId) {
        RadioResponseDto session = radioMapper.selectRadioSessionById(radioSessionId);
        String text = session != null && session.getDjMent() != null && !session.getDjMent().isBlank()
            ? session.getDjMent()
            : "오늘의 RevibeK 라디오 멘트를 준비했습니다.";

        return ResponseEntity.ok(ttsService.generateSpeech(text));
    }
}
