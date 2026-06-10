package com.ssafy.revibek.playlist.service;

import com.ssafy.revibek.playlist.dto.PlaylistCreateRequestDto;
import com.ssafy.revibek.playlist.dto.PlaylistResponseDto;
import com.ssafy.revibek.playlist.mapper.PlaylistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistMapper playlistMapper;

    @Transactional
    public String createPlaylist(String userId, PlaylistCreateRequestDto dto) {
        String id = UUID.randomUUID().toString();
        playlistMapper.insertPlaylist(id, userId, dto.getName(), dto.getMoodTag(), dto.isPublic());
        return id;
    }

    @Transactional(readOnly = true)
    public List<PlaylistResponseDto> getMyPlaylists(String userId) {
        return playlistMapper.selectPlaylistsByUserId(userId);
    }

    @Transactional
    public void addSong(String userId, String playlistId, String songId) {
        validateOwner(userId, playlistId);
        if (playlistMapper.countPlaylistSong(playlistId, songId) > 0) {
            return;
        }
        int orderNum = playlistMapper.selectNextOrderNum(playlistId);
        playlistMapper.insertPlaylistSong(UUID.randomUUID().toString(), playlistId, songId, orderNum);
    }

    @Transactional
    public void removeSong(String userId, String playlistId, String songId) {
        validateOwner(userId, playlistId);
        playlistMapper.deletePlaylistSong(playlistId, songId);
    }

    private void validateOwner(String userId, String playlistId) {
        if (playlistMapper.countPlaylistByIdAndUserId(playlistId, userId) == 0) {
            throw new RuntimeException("존재하지 않는 플레이리스트이거나 접근 권한이 없습니다.");
        }
    }
}
