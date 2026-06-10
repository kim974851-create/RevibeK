package com.ssafy.revibek.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaylistCreateRequestDto {
    @NotBlank(message = "name은 필수입니다.")
    private String name;
    private String moodTag;
    private boolean isPublic;
}
