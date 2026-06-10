package com.ssafy.revibek.playlist.mapper;

import com.ssafy.revibek.playlist.dto.PlaylistResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistMapper {
    int insertPlaylist(@Param("id") String id,
                       @Param("userId") String userId,
                       @Param("name") String name,
                       @Param("moodTag") String moodTag,
                       @Param("isPublic") boolean isPublic);

    List<PlaylistResponseDto> selectPlaylistsByUserId(@Param("userId") String userId);

    int countPlaylistByIdAndUserId(@Param("playlistId") String playlistId,
                                   @Param("userId") String userId);

    int countPlaylistSong(@Param("playlistId") String playlistId,
                          @Param("songId") String songId);

    int insertPlaylistSong(@Param("id") String id,
                           @Param("playlistId") String playlistId,
                           @Param("songId") String songId,
                           @Param("orderNum") int orderNum);

    int selectNextOrderNum(@Param("playlistId") String playlistId);

    int deletePlaylistSong(@Param("playlistId") String playlistId,
                           @Param("songId") String songId);
}
