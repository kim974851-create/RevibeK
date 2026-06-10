package com.ssafy.revibek.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaylistSongRequestDto {
    @NotBlank(message = "songId는 필수입니다.")
    private String songId;
}
