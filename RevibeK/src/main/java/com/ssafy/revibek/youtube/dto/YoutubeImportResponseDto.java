package com.ssafy.revibek.youtube.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeImportResponseDto {
    private String source;
    private String message;
    private List<YoutubeVideoResponseDto> videos;
}
