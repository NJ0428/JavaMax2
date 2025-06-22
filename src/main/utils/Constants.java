package main.utils;

import java.awt.Color;

/**
 * 게임에서 사용되는 상수들을 정의
 */
public class Constants {
    // 화면 크기
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;

    // 게임 플레이 영역
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    // 노트 관련
    public static final int NOTE_WIDTH = 50;
    public static final int NOTE_HEIGHT = 20;
    public static final int NOTE_LANES = 6; // 6키로 변경
    public static final int LANE_WIDTH = GAME_WIDTH / NOTE_LANES;

    // 노트 속도 (픽셀/프레임)
    public static final int NOTE_SPEED = 5;

    // 판정선 위치
    public static final int JUDGMENT_LINE_Y = GAME_HEIGHT - 100;

    // 색상
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    public static final Color LANE_COLOR = new Color(50, 50, 70);
    public static final Color NOTE_COLOR = new Color(255, 100, 100);
    public static final Color JUDGMENT_LINE_COLOR = new Color(255, 255, 0);

    // 판정 범위 (프레임)
    public static final int PERFECT_RANGE = 10;
    public static final int GOOD_RANGE = 20;
    public static final int MISS_RANGE = 30;

    // 점수
    public static final int PERFECT_SCORE = 300;
    public static final int GOOD_SCORE = 100;
    public static final int MISS_SCORE = 0;

    // FPS
    public static final int TARGET_FPS = 60;
    public static final int FRAME_TIME = 1000 / TARGET_FPS;

    // 키 매핑 (6키)
    public static final int[] LANE_KEYS = { 83, 68, 70, 74, 75, 76 }; // S, D, F, J, K, L
    public static final String[] LANE_KEY_NAMES = { "S", "D", "F", "J", "K", "L" };

    // 리소스 경로
    public static final String IMAGE_PATH = "resources/images/";
    public static final String AUDIO_PATH = "resources/audio/";
    public static final String MUSIC_PATH = AUDIO_PATH + "music/";
    public static final String EFFECT_PATH = AUDIO_PATH + "effects/";

    // 새로운 리소스 경로
    public static final String BGM_PATH = AUDIO_PATH + "bgm/"; // 배경음악
    public static final String GAMEPLAY_PATH = AUDIO_PATH + "gameplay/"; // 게임 플레이 음악
    public static final String SONG_COVERS_PATH = IMAGE_PATH + "songs/covers/"; // 앨범 커버
    public static final String SONG_VIDEOS_PATH = IMAGE_PATH + "songs/videos/"; // 배경 영상
    public static final String EFFECTS_IMAGES_PATH = IMAGE_PATH + "effects/"; // 이펙트 이미지
}