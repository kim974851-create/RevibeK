// com.ssafy.revibek.youtube.service.YoutubeServiceImpl.java
package com.ssafy.revibek.youtube.service;

import com.ssafy.revibek.youtube.dto.YoutubeChannelDto;
import com.ssafy.revibek.youtube.dto.YoutubeImportResponseDto;
import com.ssafy.revibek.youtube.dto.YoutubeVideoDto;
import com.ssafy.revibek.youtube.dto.YoutubeVideoResponseDto;
import com.ssafy.revibek.youtube.mapper.YoutubeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeServiceImpl implements YoutubeService {

    @Value("${youtube.api.key:}")
    private String apiKey;

    private final YoutubeMapper youtubeMapper;
    private final RestTemplate restTemplate;

    private static final String YOUTUBE_API = "https://www.googleapis.com/youtube/v3";

    @Override
    public YoutubeImportResponseDto processChannel(String channelUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[YouTube] API key 없음. 더미 영상으로 응답합니다.");
            return dummyResponse("YouTube API key가 없어 더미 영상을 반환합니다.");
        }

        try {
            String handle = parseHandle(channelUrl);
            String rawChannelId = parseChannelId(channelUrl);

            Map<String, Object> channelInfo = handle != null
                ? fetchChannelByHandle(handle)
                : fetchChannelById(rawChannelId);

            if (channelInfo == null) {
                log.warn("[SKIP] 채널 정보 없음: {}", channelUrl);
                return dummyResponse("채널 정보를 찾지 못해 더미 영상을 반환합니다.");
            }

            String channelId = (String) channelInfo.get("channelId");
            String channelName = (String) channelInfo.get("channelName");
            String uploadsPlaylistId = (String) channelInfo.get("uploadsPlaylistId");

            YoutubeChannelDto channelDto = new YoutubeChannelDto();
            channelDto.setChannelId(channelId);
            channelDto.setChannelName(channelName);
            channelDto.setChannelUrl(channelUrl);
            channelDto.setUploadsPlaylist(uploadsPlaylistId);
            youtubeMapper.insertChannel(channelDto);

            log.info("[채널] {} ({})", channelName, channelId);

            List<YoutubeVideoDto> videos = fetchAllVideos(uploadsPlaylistId, channelId);
            for (YoutubeVideoDto video : videos) {
                youtubeMapper.insertVideo(video);
            }
            log.info("  → {}개 영상 저장 완료", videos.size());
            return new YoutubeImportResponseDto(
                "YOUTUBE",
                "YouTube 채널 영상 수집 완료",
                videos.stream().map(video -> toResponse(video, channelName)).toList()
            );

        } catch (Exception e) {
            log.error("[SKIP] 채널 처리 실패: {} - {}", channelUrl, e.getMessage());
            return dummyResponse("YouTube API 호출 실패로 더미 영상을 반환합니다.");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchChannelByHandle(String handle) {
        String url = UriComponentsBuilder.fromUriString(YOUTUBE_API + "/channels")
            .queryParam("part", "snippet,contentDetails")
            .queryParam("forHandle", handle)
            .queryParam("key", apiKey)
            .toUriString();
        return parseChannelResponse(restTemplate.getForObject(url, Map.class));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchChannelById(String channelId) {
        String url = UriComponentsBuilder.fromUriString(YOUTUBE_API + "/channels")
            .queryParam("part", "snippet,contentDetails")
            .queryParam("id", channelId)
            .queryParam("key", apiKey)
            .toUriString();
        return parseChannelResponse(restTemplate.getForObject(url, Map.class));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseChannelResponse(Map<String, Object> response) {
        if (response == null) return null;
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items == null || items.isEmpty()) return null;

        Map<String, Object> item = items.get(0);
        Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
        Map<String, Object> contentDetails = (Map<String, Object>) item.get("contentDetails");
        Map<String, Object> relatedPlaylists = (Map<String, Object>) contentDetails.get("relatedPlaylists");

        return Map.of(
            "channelId", item.get("id"),
            "channelName", snippet.get("title"),
            "uploadsPlaylistId", relatedPlaylists.get("uploads")
        );
    }

    @SuppressWarnings("unchecked")
    private List<YoutubeVideoDto> fetchAllVideos(String playlistId, String channelId) {
        List<YoutubeVideoDto> videos = new ArrayList<>();
        String nextPageToken = null;

        do {
            String url = YOUTUBE_API + "/playlistItems"
                + "?part=snippet,contentDetails"
                + "&playlistId=" + playlistId
                + "&maxResults=50"
                + "&key=" + apiKey
                + (nextPageToken != null ? "&pageToken=" + nextPageToken : "");

            log.info("[playlistItems 조회] playlistId: {}", playlistId);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) break;

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            if (items != null) {
                List<String> videoIds = new ArrayList<>();
                List<YoutubeVideoDto> tempVideos = new ArrayList<>();

                for (Map<String, Object> item : items) {
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    Map<String, Object> contentDetails = (Map<String, Object>) item.get("contentDetails");

                    String videoId = (String) contentDetails.get("videoId");
                    String title = (String) snippet.get("title");
                    String publishedAt = (String) contentDetails.get("videoPublishedAt");
                    String thumbnailUrl = extractThumbnailUrl(snippet);

                    if (publishedAt != null) {
                        publishedAt = publishedAt.replace("T", " ").substring(0, 19);
                    }

                    YoutubeVideoDto dto = new YoutubeVideoDto();
                    dto.setChannelId(channelId);
                    dto.setVideoId(videoId);
                    dto.setVideoUrl("https://www.youtube.com/watch?v=" + videoId);
                    dto.setVideoTitle(title);
                    dto.setThumbnailUrl(thumbnailUrl);
                    dto.setPublishedAt(publishedAt);
                    tempVideos.add(dto);
                    videoIds.add(videoId);
                }

                log.info("[videoIds 크기] {}", videoIds.size());

                Map<String, Integer> durationMap = fetchDurationSeconds(videoIds);

                log.info("[durationMap 크기] {}", durationMap.size());

                for (YoutubeVideoDto dto : tempVideos) {
                    dto.setDurationSeconds(durationMap.getOrDefault(dto.getVideoId(), null));
                }

                videos.addAll(tempVideos);
            }

            nextPageToken = (String) response.get("nextPageToken");

        } while (nextPageToken != null);

        return videos;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> fetchDurationSeconds(List<String> videoIds) {
        Map<String, Integer> durationMap = new HashMap<>();

        String ids = String.join(",", videoIds);
        String url = YOUTUBE_API + "/videos"
            + "?part=contentDetails"
            + "&id=" + ids
            + "&key=" + apiKey;

        log.info("[Duration 조회] {}개 영상", videoIds.size());

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        log.info("[Duration 응답] {}", response);

        if (response == null) return durationMap;

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items == null) return durationMap;

        for (Map<String, Object> item : items) {
            String videoId = (String) item.get("id");
            Map<String, Object> contentDetails = (Map<String, Object>) item.get("contentDetails");
            String duration = (String) contentDetails.get("duration");
            log.info("[Duration 파싱] videoId: {}, duration: {}", videoId, duration);
            durationMap.put(videoId, parseDuration(duration));
        }

        return durationMap;
    }

    private Integer parseDuration(String duration) {
        try {
            java.time.Duration d = java.time.Duration.parse(duration);
            return (int) d.getSeconds();
        } catch (Exception e) {
            log.error("[Duration 파싱 실패] {}", duration);
            return null;
        }
    }

    private String parseHandle(String url) {
        if (url.contains("/@")) {
            String handle = url.replaceAll(".*/\\@([^/]+).*", "$1");
            try {
                return java.net.URLDecoder.decode(handle, "UTF-8");
            } catch (Exception e) {
                return handle;
            }
        }
        return null;
    }

    private String parseChannelId(String url) {
        if (url.contains("/channel/")) {
            return url.replaceAll(".*/channel/([^/]+).*", "$1");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractThumbnailUrl(Map<String, Object> snippet) {
        try {
            Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
            Map<String, Object> medium = (Map<String, Object>) thumbnails.get("medium");
            return (String) medium.get("url");
        } catch (Exception e) {
            return "";
        }
    }

    private YoutubeVideoResponseDto toResponse(YoutubeVideoDto video, String channelName) {
        return new YoutubeVideoResponseDto(
            video.getVideoTitle(),
            channelName,
            video.getVideoId(),
            video.getVideoUrl(),
            video.getThumbnailUrl()
        );
    }

    private YoutubeImportResponseDto dummyResponse(String message) {
        return new YoutubeImportResponseDto("DUMMY", message, dummyVideos());
    }

    private List<YoutubeVideoResponseDto> dummyVideos() {
        return List.of(
            new YoutubeVideoResponseDto(
                "2세대 K-POP 감성 라이브",
                "RevibeK Dummy",
                "sample12345A",
                "https://www.youtube.com/watch?v=sample12345A",
                "https://img.youtube.com/vi/sample12345A/hqdefault.jpg"
            ),
            new YoutubeVideoResponseDto(
                "3세대 K-POP 자신감 플레이리스트",
                "RevibeK Dummy",
                "sample12345B",
                "https://www.youtube.com/watch?v=sample12345B",
                "https://img.youtube.com/vi/sample12345B/hqdefault.jpg"
            ),
            new YoutubeVideoResponseDto(
                "5세대 K-POP 에너지 믹스",
                "RevibeK Dummy",
                "sample12345C",
                "https://www.youtube.com/watch?v=sample12345C",
                "https://img.youtube.com/vi/sample12345C/hqdefault.jpg"
            )
        );
    }
}