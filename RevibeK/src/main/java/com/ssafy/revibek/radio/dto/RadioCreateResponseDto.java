package com.ssafy.revibek.radio.dto;

import com.ssafy.revibek.song.dto.SongDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RadioCreateResponseDto {
    private String radioSessionId;
    private String title;
    private String djMessage;
    private List<SongDto> recommendedSongs;
}
