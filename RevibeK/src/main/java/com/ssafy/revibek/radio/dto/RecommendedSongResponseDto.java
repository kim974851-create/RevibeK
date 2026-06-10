package com.ssafy.revibek.radio.dto;

import com.ssafy.revibek.song.dto.SongDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendedSongResponseDto {
    private String songId;
    private String title;
    private String artist;
    private String genre;
    private String era;
    private String youtubeUrl;
    private String youtubeId;
    private float score;
    private String reason;

    public static RecommendedSongResponseDto from(SongDto song, String reason) {
        return new RecommendedSongResponseDto(
            song.getId(),
            song.getTitle(),
            song.getArtist(),
            song.getGenre(),
            song.getEra(),
            song.getYoutubeUrl(),
            song.getYoutubeId(),
            song.getScore(),
            reason
        );
    }
}
