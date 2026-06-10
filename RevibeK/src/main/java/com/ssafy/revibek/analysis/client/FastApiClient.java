package com.ssafy.revibek.analysis.client;

import com.ssafy.revibek.analysis.dto.AnalyzeRequestDto;
import com.ssafy.revibek.analysis.dto.AnalyzeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class FastApiClient {

    @Value("${fastapi.host:http://localhost:8000}")
    private String fastApiHost;

    @Value("${app.fastapi.enabled:false}")
    private boolean fastApiEnabled;

    @Value("${app.fastapi.fallback:mock}")
    private String fallbackMode;

    private final RestTemplate restTemplate = new RestTemplate();

    public AnalyzeResponseDto analyze(AnalyzeRequestDto request) {
        if (!fastApiEnabled) {
            log.info("[FastAPI] disabled. Returning mock analysis result.");
            return mockResponse(request, "FastAPI server is disabled. Mock analysis result returned.");
        }

        String url = fastApiHost + "/api/ai/analyze";
        try {
            ResponseEntity<AnalyzeResponseDto> response =
                restTemplate.postForEntity(url, request, AnalyzeResponseDto.class);
            AnalyzeResponseDto body = response.getBody();
            if (body == null) {
                return mockResponse(request, "FastAPI returned empty response. Mock analysis result returned.");
            }
            return body;
        } catch (Exception e) {
            log.warn("[FastAPI] call failed: {}. fallback={}", e.getMessage(), fallbackMode);
            return mockResponse(request, "FastAPI server is not available. Mock analysis result returned.");
        }
    }

    private AnalyzeResponseDto mockResponse(AnalyzeRequestDto request, String message) {
        AnalyzeResponseDto mock = new AnalyzeResponseDto();
        mock.setYoutubeVideoId(request.getYoutubeVideoId());
        mock.setTitle(request.getTitle());
        mock.setStatus("MOCK");
        mock.setMessage(message);
        mock.setDurationSeconds(request.getDurationSeconds());
        mock.setBpm(120.0);
        mock.setEnergy(0.75);
        mock.setDanceability(0.70);
        mock.setLoudness(-8.5);
        mock.setMusicalKey("C");
        mock.setMusicalScale("major");
        return mock;
    }
}