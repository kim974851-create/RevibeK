package com.ssafy.revibek.qdrant;

import com.ssafy.revibek.song.dto.SongDto;
import com.ssafy.revibek.song.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/qdrant")
@Tag(name = "Qdrant", description = "벡터 DB 관리 API")
@RequiredArgsConstructor
public class QdrantController {

    private final QdrantService qdrantService;
    private final SongService songService;

    @Value("${app.qdrant.enabled:false}")
    private boolean qdrantEnabled;

    @PostMapping("/embed")
    @Operation(summary = "전체 곡 벡터 저장", description = "songs 테이블 전체를 Qdrant에 upsert")
    public ResponseEntity<String> embedAll() {
        if (!qdrantEnabled) {
            return ResponseEntity.ok("Qdrant disabled. DB score fallback mode is active.");
        }
        qdrantService.createCollectionIfNotExists();
        List<SongDto> songs = songService.getAllSongs();
        qdrantService.upsertSongs(songs);
        return ResponseEntity.ok(songs.size() + "곡 Qdrant 저장 완료");
    }

    @GetMapping("/similar/{songId}")
    @Operation(summary = "유사곡 조회", description = "특정 곡과 유사한 곡 N개 반환")
    public ResponseEntity<QdrantSimilarResponseDto> getSimilar(
            @PathVariable String songId,
            @RequestParam(defaultValue = "10") int limit) {

        int safeLimit = Math.max(1, Math.min(limit, 10));
        if (!qdrantEnabled) {
            return ResponseEntity.ok(new QdrantSimilarResponseDto(
                "DB_SCORE_FALLBACK",
                songService.getTopScoreSongs(safeLimit)
            ));
        }

        try {
            List<String> similarIds = qdrantService.searchSimilar(songId, safeLimit);
            List<SongDto> songs = similarIds.stream()
                .map(songService::getSongById)
                .filter(song -> song != null)
                .toList();
            if (songs.isEmpty()) {
                return ResponseEntity.ok(new QdrantSimilarResponseDto(
                    "DB_SCORE_FALLBACK",
                    songService.getTopScoreSongs(safeLimit)
                ));
            }
            return ResponseEntity.ok(new QdrantSimilarResponseDto("QDRANT", songs));
        } catch (Exception e) {
            log.warn("[Qdrant] search failed. Using DB score fallback: {}", e.getMessage());
            return ResponseEntity.ok(new QdrantSimilarResponseDto(
                "DB_SCORE_FALLBACK",
                songService.getTopScoreSongs(safeLimit)
            ));
        }
    }
}
