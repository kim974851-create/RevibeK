package com.ssafy.revibek.tts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TtsService {

    private final TtsClient ttsClient;

    public TtsResponseDto generateSpeech(String text) {
        return generateSpeech(text, null);
    }

    public TtsResponseDto generateSpeech(String text, String voice) {
        String safeText = text == null || text.isBlank()
            ? "오늘의 RevibeK 라디오 멘트를 준비했습니다."
            : text.trim();

        return ttsClient.synthesize(safeText, voice)
            .map(audioContent -> new TtsResponseDto(
                "EXTERNAL_TTS",
                ttsClient.getLanguageCode(),
                null,
                audioContent,
                safeText,
                "External TTS synthesis succeeded."
            ))
            .orElseGet(() -> new TtsResponseDto(
                "BROWSER_TTS",
                ttsClient.getLanguageCode(),
                null,
                null,
                safeText,
                "External TTS failed or is not configured. Use browser Web Speech API."
            ));
    }
}
