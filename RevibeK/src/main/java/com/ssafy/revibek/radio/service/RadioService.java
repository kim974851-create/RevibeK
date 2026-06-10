package com.ssafy.revibek.radio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.revibek.radio.mapper.RadioMapper;
import com.ssafy.revibek.song.dto.SongDto;
import com.ssafy.revibek.song.service.SongService;
import com.ssafy.revibek.radio.dto.RadioCreateResponseDto;
import com.ssafy.revibek.radio.dto.RadioRequestDto;
import com.ssafy.revibek.radio.dto.RadioResponseDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RadioService {

	private final RadioMapper radioMapper;
	private final SongService songService;
	
	//라디오 세션 생성
	@Transactional
	public RadioCreateResponseDto createSession(String userId, RadioRequestDto dto) {
		String emotion = defaultText(dto.getEmotion(), "오늘의 감정");
		String situation = defaultText(dto.getSituation(), dto.getStory());
		String generation = firstNonBlank("K-POP", dto.getGeneration(), dto.getEra());
		String mood = firstNonBlank("감성", dto.getMood(), dto.getGenre());
		String story = defaultText(dto.getStory(), situation);
		String title = buildTitle(situation, mood, generation);
		String djMessage = buildDjMessage(emotion, situation, generation, mood);
		List<SongDto> recommendedSongs = songService.getRadioRecommendedSongs(generation, mood, 5);
		djMessage = enrichDjMessageWithSongs(djMessage, recommendedSongs);

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
			djMessage
		);

		for (int i = 0; i < recommendedSongs.size(); i++) {
			SongDto song = recommendedSongs.get(i);
			radioMapper.insertRecommendation(
				sessionId,
				song.getId(),
				i + 1,
				buildRecommendationReason(song, mood)
			);
		}

		return new RadioCreateResponseDto(sessionId, title, djMessage, recommendedSongs);
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

	private String buildDjMessage(String emotion, String situation, String generation, String mood) {
		StringBuilder message = new StringBuilder();
		message.append("오늘은 ").append(situation)
			.append(" 속 ").append(emotion)
			.append(" 마음을 ").append(mood)
			.append(" 분위기로 바꿔줄 ").append(generation)
			.append(" K-POP 라디오를 준비했습니다. ");

		if (containsAny(emotion, "지침", "지친", "힘듦", "피곤", "불안")) {
			message.append("천천히 회복할 수 있도록 위로와 에너지를 함께 담았어요. ");
		} else if (containsAny(emotion, "설렘", "기대", "두근")) {
			message.append("청량한 기대감과 밝은 멜로디로 지금의 설렘을 더 키워볼게요. ");
		} else if (containsAny(emotion, "회상", "그리움", "추억")) {
			message.append("익숙한 멜로디와 추억의 감성으로 마음을 부드럽게 열어볼게요. ");
		}

		if (containsAny(situation, "공부", "작업", "프로젝트")) {
			message.append("집중을 깨지 않으면서도 자신감을 채워주는 곡들로 이어갑니다. ");
		} else if (containsAny(situation, "운동", "러닝", "헬스")) {
			message.append("몸을 움직이기 좋은 비트와 에너지 중심으로 선곡했어요. ");
		}

		if (containsAny(generation, "2세대", "2")) {
			message.append("후렴과 멜로디가 또렷한 2세대 감성을 느껴보세요.");
		} else if (containsAny(generation, "3세대", "3")) {
			message.append("감정선과 서사가 살아있는 3세대 곡들로 분위기를 만들어볼게요.");
		} else if (containsAny(generation, "5세대", "5")) {
			message.append("숏폼처럼 빠르게 꽂히는 비트와 도파민 있는 사운드를 준비했어요.");
		} else {
			message.append("지금 분위기에 어울리는 K-POP 흐름으로 함께 가볼게요.");
		}

		return message.toString();
	}

	private String buildRecommendationReason(SongDto song, String mood) {
		String songMood = defaultText(song.getMood(), song.getGenre());
		return songMood + " 분위기가 " + mood + " 라디오 흐름과 잘 어울립니다.";
	}

	private String enrichDjMessageWithSongs(String baseMessage, List<SongDto> recommendedSongs) {
		if (recommendedSongs == null || recommendedSongs.isEmpty()) {
			return baseMessage;
		}
		SongDto first = recommendedSongs.get(0);
		return baseMessage + " 첫 곡은 " + first.getArtist() + "의 '" + first.getTitle()
			+ "'입니다. 이 곡으로 지금의 분위기를 자연스럽게 열어볼게요.";
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
