package com.ssafy.revibek.song.service;

import java.util.List;

import com.ssafy.revibek.song.dto.SongDto;

public interface SongService {
    int registerSong(SongDto song);
    List<SongDto> getAllSongs();
    SongDto getSongById(String id);
    SongDto getSongByYoutubeId(String youtubeId);
    SongDto getSongByTitle(String title);
    List<SongDto> getSongsByGenre(String genre);
    List<SongDto> getRecommendSongs();
    List<SongDto> getRadioRecommendedSongs(String generation, String mood, int limit);
    List<SongDto> getSongsByGeneration(String generation, int limit);
    List<SongDto> getSongsByMood(String mood, int limit);
    List<SongDto> getTopScoreSongs(int limit);
    int modifySong(SongDto song);
    int removeSong(String id);
}