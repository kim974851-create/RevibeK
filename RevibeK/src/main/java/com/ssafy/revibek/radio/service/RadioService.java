package com.ssafy.revibek.radio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.revibek.radio.ai.AiDjMentService;
import com.ssafy.revibek.radio.dto.RecommendedSongResponseDto;
import com.ssafy.revibek.radio.mapper.RadioMapper;
import com.ssafy.revibek.song.dto.SongDto;
import com.ssafy.revibek.song.service.SongService;
import com.ssafy.revibek.radio.dto.RadioCreateResponseDto;
import com.ssafy.revibek.radio.dto.RadioRequestDto;
import com.ssafy.revibek.radio.dto.RadioResponseDto;
import com.ssafy.revibek.tts.TtsResponseDto;
import com.ssafy.revibek.tts.TtsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RadioService {

	private final RadioMapper radioMapper;
	private final SongService songService;
	private final AiDjMentService aiDjMentService;
	private final TtsService ttsService;
	
	//라디오 세션 생성
	@Transactional
	public RadioCreateResponseDto createSession(String userId, RadioRequestDto dto) {
		String emotion = defaultText(dto.getEmotion(), "오늘의 감정");
		String situation = defaultText(dto.getSituation(), dto.getStory());
		String generation = firstNonBlank("K-POP", dto.getGeneration(), dto.getEra());
		String mood = firstNonBlank("감성", dto.getMood(), dto.getGenre());
		String recommendationEra = firstNonBlank(generation, dto.getEra(), dto.getGeneration());
		String recommendationGenre = firstNonBlank(mood, dto.getGenre(), dto.getMood());
		String story = defaultText(dto.getStory(), situation);
		String title = buildTitle(situation, mood, generation);
		String sessionId = UUID.randomUUID().toString();

		radioMapper.insertRadioSession(
			sessionId,
			userId,
			title,
			emotion,
			situation,
			generation,
			mood,
			story,
			null
		);

		List<SongDto> recommendedSongs = songService.getRadioRecommendedSongs(recommendationEra, recommendationGenre, 5);
		String djMent = aiDjMentService.generateDjMent(dto, recommendedSongs);
		radioMapper.updateDjMent(sessionId, djMent);

		List<RecommendedSongResponseDto> responseSongs = new ArrayList<>();
		for (int i = 0; i < recommendedSongs.size(); i++) {
			SongDto song = recommendedSongs.get(i);
			String reason = buildRecommendationReason(song, mood, recommendationEra);
			radioMapper.insertRecommendation(
				sessionId,
				song.getId(),
				i + 1,
				reason
			);
			responseSongs.add(RecommendedSongResponseDto.from(song, reason));
		}

		TtsResponseDto tts = ttsService.generateSpeech(djMent);
		return new RadioCreateResponseDto(
			sessionId,
			title,
			mood,
			story,
			djMent,
			tts.getMode(),
			tts.getAudioUrl(),
			tts.getAudioContent(),
			responseSongs
		);
	}
	
	
	//세션 단건 조회
	public RadioResponseDto getSession(String id, String userId) {
		// TODO: radioMapper.selectRadioSessionByIdAndUserId()
        // TODO: radioMapper.selectRecommendationBySessionId()
		RadioResponseDto session = radioMapper.selectRadioSessionByIdAndUserId(id, userId);
		if(session == null) {
			throw new RuntimeException("존재하지 않는 세션이거나 접근 권한이 없습니다.");
		}
		List<RadioResponseDto.RadioSongDto> songs = 
				radioMapper.selectRecommendationBySessionId(id);
		session.setSongs(songs);
		return session; 
	}
	
	//유저 세션 목록 조회
	public List<RadioResponseDto> getSessionByUser(String userId){
		List<RadioResponseDto> sessions = radioMapper.selectRadioSessionByUserId(userId);
		for (RadioResponseDto session : sessions) {
			List<RadioResponseDto.RadioSongDto> songs =
					radioMapper.selectRecommendationBySessionId(session.getId());
			session.setSongs(songs);
		}
		return sessions;
	}

	private String buildTitle(String situation, String mood, String generation) {
		return situation + " " + mood + "을 위한 " + generation + " K-POP 라디오";
	}

	private String buildRecommendationReason(SongDto song, String mood, String generation) {
		String songMood = defaultText(song.getMood(), song.getGenre());
		if (containsAny(mood, "지친", "지침", "피곤", "불안")) {
			return "지친 마음을 환기해줄 에너지 있는 곡입니다.";
		}
		if (generation != null && !generation.isBlank() && generation.equals(song.getEra())) {
			return generation + " 감성과 잘 맞는 곡이라 추천했습니다.";
		}
		return songMood + " 분위기가 " + mood + " 라디오 흐름과 잘 어울립니다.";
	}

	private boolean containsAny(String value, String... keywords) {
		if (value == null) {
			return false;
		}
		for (String keyword : keywords) {
			if (value.contains(keyword)) {
				return true;
			}
		}
		return false;
	}

	private String defaultText(String value, String fallback) {
		if (value != null && !value.isBlank()) {
			return value.trim();
		}
		if (fallback != null && !fallback.isBlank()) {
			return fallback.trim();
		}
		return "오늘";
	}

	private String firstNonBlank(String fallback, String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value.trim();
			}
		}
		return fallback;
	}
}
