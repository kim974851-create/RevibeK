package com.ssafy.revibek.song.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.revibek.song.dto.SongDto;
import com.ssafy.revibek.song.mapper.SongDao;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongDao songDao;

    @Override
    public int registerSong(SongDto song) {
        return songDao.insertSong(song);
    }

    @Override
    public List<SongDto> getAllSongs() {
        return songDao.selectAllSongs();
    }

    @Override
    public SongDto getSongById(String id) {
        return songDao.selectSongById(id);
    }

    @Override
    public SongDto getSongByYoutubeId(String youtubeId) {
        return songDao.selectSongByYoutubeId(youtubeId);
    }

    @Override
    public SongDto getSongByTitle(String title) {
        return songDao.selectSongByTitle(title);
    }

    @Override
    public List<SongDto> getSongsByGenre(String genre) {
        return songDao.selectSongsByGenre(genre);
    }

    @Override
    public List<SongDto> getRecommendSongs() {
        return songDao.selectRecommendSongs();
    }

    @Override
    public List<SongDto> getRadioRecommendedSongs(String generation, String mood, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 5));

        List<SongDto> songs = songDao.selectRecommendedSongsForRadio(generation, mood, safeLimit);
        if (!songs.isEmpty()) {
            return songs;
        }

        songs = songDao.selectSongsByGeneration(generation, safeLimit);
        if (!songs.isEmpty()) {
            return songs;
        }

        songs = songDao.selectSongsByMood(mood, safeLimit);
        if (!songs.isEmpty()) {
            return songs;
        }

        return songDao.selectTopScoreSongs(safeLimit);
    }

    @Override
    public List<SongDto> getSongsByGeneration(String generation, int limit) {
        return songDao.selectSongsByGeneration(generation, Math.max(1, Math.min(limit, 20)));
    }

    @Override
    public List<SongDto> getSongsByMood(String mood, int limit) {
        return songDao.selectSongsByMood(mood, Math.max(1, Math.min(limit, 20)));
    }

    @Override
    public List<SongDto> getTopScoreSongs(int limit) {
        return songDao.selectTopScoreSongs(Math.max(1, Math.min(limit, 20)));
    }

    @Override
    public int modifySong(SongDto song) {
        return songDao.updateSong(song);
    }

    @Override
    public int removeSong(String id) {
        return songDao.deleteSong(id);
    }
}