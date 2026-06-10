package com.ssafy.revibek.tts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TtsClient {

    private final RestTemplate restTemplate;

    @Value("${tts.enabled:${gcp.tts.enabled:false}}")
    private boolean enabled;

    @Value("${tts.api.url:${gcp.tts.base-url:https://texttospeech.googleapis.com/v1}}")
    private String baseUrl;

    @Value("${tts.api.key:${gcp.tts.api-key:}}")
    private String apiKey;

    @Value("${tts.voice:${gcp.tts.default-voice-name:ko-KR-Chirp3-HD-Vindemiatrix}}")
    private String voiceName;

    @Value("${tts.lang:${gcp.tts.default-language-code:ko-KR}}")
    private String languageCode;

    @Value("${tts.audio-encoding:${gcp.tts.default-audio-encoding:MP3}}")
    private String audioEncoding;

    @Value("${tts.speaking-rate:${gcp.tts.default-speaking-rate:1.0}}")
    private double speakingRate;

    @Value("${tts.pitch:${gcp.tts.default-pitch:0.0}}")
    private double pitch;

    public Optional<String> synthesize(String text, String requestedVoice) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            log.info("[TTS] disabled or API key missing. Using browser TTS fallback.");
            return Optional.empty();
        }

        try {
            String url = baseUrl + "/text:synthesize?key=" + apiKey;
            String voice = requestedVoice == null || requestedVoice.isBlank() ? voiceName : requestedVoice;
            Map<String, Object> body = Map.of(
                "input", Map.of("text", text),
                "voice", Map.of(
                    "languageCode", languageCode,
                    "name", voice
                ),
                "audioConfig", Map.of(
                    "audioEncoding", audioEncoding,
                    "speakingRate", speakingRate,
                    "pitch", pitch
                )
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);
            Object audioContent = response == null ? null : response.get("audioContent");
            if (audioContent == null || audioContent.toString().isBlank()) {
                return Optional.empty();
            }
            return Optional.of(audioContent.toString());
        } catch (Exception e) {
            log.warn("[TTS] external synthesis failed. Using browser TTS fallback. reason={}", e.getMessage());
            return Optional.empty();
        }
    }

    public String getLanguageCode() {
        return languageCode;
    }
}
