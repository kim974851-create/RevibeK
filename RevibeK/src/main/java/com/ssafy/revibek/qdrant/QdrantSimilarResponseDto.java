package com.ssafy.revibek.qdrant;

import com.ssafy.revibek.song.dto.SongDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QdrantSimilarResponseDto {
    private String source;
    private List<SongDto> songs;
}
