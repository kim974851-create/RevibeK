package com.ssafy.revibek.tts;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TtsRequestDto {
    @NotBlank(message = "text는 필수입니다.")
    private String text;
    private String voice;
}
