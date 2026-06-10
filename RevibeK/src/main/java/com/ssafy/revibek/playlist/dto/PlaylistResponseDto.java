package com.ssafy.revibek.playlist.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaylistResponseDto {
    private String id;
    private String userId;
    private String name;
    private String moodTag;
    private Boolean isPublic;
    private int songCount;
    private LocalDateTime createdAt;
}
