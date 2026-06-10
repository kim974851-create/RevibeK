package com.ssafy.revibek.radio.ai;

import com.ssafy.revibek.radio.dto.RadioRequestDto;
import com.ssafy.revibek.song.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiDjMentService {

    private final AiDjPromptBuilder promptBuilder;
    private final GmsClient gmsClient;

    public String generateDjMent(RadioRequestDto request, List<SongDto> recommendedSongs) {
        String prompt = promptBuilder.build(request, recommendedSongs);
        return gmsClient.generate(prompt)
            .filter(text -> !text.isBlank())
            .orElseGet(() -> fallbackMent(request, recommendedSongs));
    }

    private String fallbackMent(RadioRequestDto request, List<SongDto> recommendedSongs) {
        String mood = safe(request.getMood(), "오늘의 마음");
        String story = safe(request.getStory(), "당신의 하루");
        String era = safe(request.getEra(), safe(request.getGeneration(), "K-POP"));
        String genre = safe(request.getGenre(), "감성");

        StringBuilder message = new StringBuilder();
        message.append("오늘은 ").append(mood)
            .append(" 마음을 조금 가볍게 만들어줄 ")
            .append(era).append(" ").append(genre)
            .append(" 라디오를 준비했습니다. ");
        message.append(story).append("라는 사연에 어울리는 곡들을 골라봤어요. ");

        if (recommendedSongs != null && !recommendedSongs.isEmpty()) {
            SongDto first = recommendedSongs.get(0);
            message.append("첫 곡은 ").append(first.getArtist())
                .append("의 '").append(first.getTitle())
                .append("'입니다. 이 곡의 분위기가 지금의 마음을 조금 가볍게 만들어줄 거예요. ");
        }

        message.append("잠시 쉬어가듯 음악에 기대어보세요.");
        return message.toString();
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
