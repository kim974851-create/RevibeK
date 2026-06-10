package com.ssafy.revibek.youtube.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeVideoResponseDto {
    private String title;
    private String channelTitle;
    private String videoId;
    private String youtubeUrl;
    private String thumbnailUrl;
}
