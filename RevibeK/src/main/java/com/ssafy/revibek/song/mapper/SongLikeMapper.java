package com.ssafy.revibek.song.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SongLikeMapper {
    int countLike(@Param("userId") String userId, @Param("songId") String songId);
    int insertLike(@Param("id") String id, @Param("userId") String userId, @Param("songId") String songId);
    int deleteLike(@Param("userId") String userId, @Param("songId") String songId);
    int increaseLikeCount(@Param("songId") String songId);
    int decreaseLikeCount(@Param("songId") String songId);
    int selectLikeCount(@Param("songId") String songId);
}
