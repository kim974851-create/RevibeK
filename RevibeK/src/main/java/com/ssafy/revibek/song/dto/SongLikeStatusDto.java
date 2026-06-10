package com.ssafy.revibek.song.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SongLikeStatusDto {
    private String songId;
    private boolean liked;
    private int likeCount;
}
