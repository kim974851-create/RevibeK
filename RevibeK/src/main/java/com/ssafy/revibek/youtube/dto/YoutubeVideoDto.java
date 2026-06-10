// com.ssafy.revibek.youtube.dto.YoutubeVideoDto.java
package com.ssafy.revibek.youtube.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeVideoDto {
    private Long id;
    private Long youtubeChannelId;
    private String channelId;
    private String videoId;
    private String videoUrl;
    private String videoTitle;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private String publishedAt;
    private String collectStatus;
    private String createdAt;
    private String updatedAt;
}