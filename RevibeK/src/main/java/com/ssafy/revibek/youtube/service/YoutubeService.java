// com.ssafy.revibek.youtube.service.YoutubeService.java
package com.ssafy.revibek.youtube.service;

import com.ssafy.revibek.youtube.dto.YoutubeImportResponseDto;

public interface YoutubeService {
    YoutubeImportResponseDto processChannel(String channelUrl);
}