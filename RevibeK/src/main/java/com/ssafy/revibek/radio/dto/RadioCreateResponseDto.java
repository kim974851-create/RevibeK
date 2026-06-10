package com.ssafy.revibek.radio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RadioCreateResponseDto {
    private String radioSessionId;
    private String title;
    private String mood;
    private String story;
    private String djMent;
    private String ttsMode;
    private String ttsAudioUrl;
    private String ttsAudioContent;
    private List<RecommendedSongResponseDto> recommendedSongs;
}
