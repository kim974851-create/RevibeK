package com.ssafy.revibek.tts;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TtsResponseDto {
    private String mode;
    private String lang;
    private String audioUrl;
    private String audioContent;
    private String text;
    private String message;
}
