package com.ssafy.revibek.song.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.revibek.song.dto.SongDto;

@Mapper
public interface SongDao {
    int insertSong(SongDto song);
    List<SongDto> selectAllSongs();
    SongDto selectSongById(String id);
    SongDto selectSongByYoutubeId(String youtubeId);
    SongDto selectSongByTitle(String title);
    List<SongDto> selectSongsByGenre(String genre);
    List<SongDto> selectRecommendSongs();
    List<SongDto> selectRecommendedSongsForRadio(@Param("generation") String generation,
                                                  @Param("mood") String mood,
                                                  @Param("limit") int limit);
    List<SongDto> selectSongsByGeneration(@Param("generation") String generation,
                                           @Param("limit") int limit);
    List<SongDto> selectSongsByMood(@Param("mood") String mood,
                                    @Param("limit") int limit);
    List<SongDto> selectTopScoreSongs(@Param("limit") int limit);
    int updateSong(SongDto song);
    int deleteSong(String id);
}