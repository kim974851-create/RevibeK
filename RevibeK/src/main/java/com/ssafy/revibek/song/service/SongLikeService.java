package com.ssafy.revibek.song.service;

import com.ssafy.revibek.song.dto.SongLikeStatusDto;
import com.ssafy.revibek.song.mapper.SongLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SongLikeService {

    private final SongLikeMapper songLikeMapper;

    @Transactional
    public SongLikeStatusDto like(String userId, String songId) {
        if (songLikeMapper.countLike(userId, songId) == 0) {
            songLikeMapper.insertLike(UUID.randomUUID().toString(), userId, songId);
            songLikeMapper.increaseLikeCount(songId);
        }
        return status(userId, songId);
    }

    @Transactional
    public SongLikeStatusDto unlike(String userId, String songId) {
        int deleted = songLikeMapper.deleteLike(userId, songId);
        if (deleted > 0) {
            songLikeMapper.decreaseLikeCount(songId);
        }
        return status(userId, songId);
    }

    @Transactional(readOnly = true)
    public SongLikeStatusDto status(String userId, String songId) {
        boolean liked = songLikeMapper.countLike(userId, songId) > 0;
        int likeCount = songLikeMapper.selectLikeCount(songId);
        return new SongLikeStatusDto(songId, liked, likeCount);
    }
}
