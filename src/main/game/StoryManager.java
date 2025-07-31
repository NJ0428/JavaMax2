package main.game;

import java.util.ArrayList;
import java.util.List;

/**
 * 스토리 모드를 관리하는 클래스
 */
public class StoryManager {
    private List<Story> stories;
    private int currentStoryIndex;
    private static StoryManager instance;

    private StoryManager() {
        stories = new ArrayList<>();
        currentStoryIndex = 0;
        initializeStories();
    }

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    /**
     * 스토리들을 초기화합니다
     */
    private void initializeStories() {
        // 기본 곡 생성 (실제로는 Song 데이터에서 가져와야 함)
        Song song1 = new Song("첫 번째 만남", "Unknown", "Easy", "Tutorial", 100, "game_bgm.wav");
        Song song2 = new Song("연습의 시간", "Unknown", "Normal", "Practice", 120, "game_bgm.wav");
        Song song3 = new Song("첫 번째 도전", "Unknown", "Hard", "Challenge", 140, "game_bgm.wav");
        Song song4 = new Song("라이벌의 등장", "Unknown", "Hard", "Rival", 160, "game_bgm.wav");
        Song song5 = new Song("최종 결전", "Unknown", "Expert", "Final", 180, "game_bgm.wav");

        // 스토리 1: 튜토리얼
        stories.add(new Story(1, "리듬의 시작",
                "음악의 세계에 첫 발을 내딛는 순간입니다.",
                "안녕하세요! 리듬 게임의 세계에 오신 것을 환영합니다.\n" +
                        "저는 여러분의 가이드가 될 리듬이에요.\n" +
                        "함께 음악의 마법을 배워볼까요?",
                "훌륭해요! 첫 번째 단계를 성공적으로 완료했습니다.\n" +
                        "이제 진짜 모험이 시작됩니다!",
                song1, Song.Difficulty.EASY, 500,
                "story_bg_1", "character_rhythm"));

        // 스토리 2: 기본 연습
        stories.add(new Story(2, "연습의 중요성",
                "기본기를 다지는 것이 성공의 열쇠입니다.",
                "좋은 시작이었어요! 하지만 아직 갈 길이 멀어요.\n" +
                        "더 복잡한 리듬에 도전해볼 준비가 되셨나요?\n" +
                        "꾸준한 연습만이 실력 향상의 비결입니다.",
                "점점 실력이 늘고 있어요!\n" +
                        "이제 더 어려운 도전을 준비해볼 시간입니다.",
                song2, Song.Difficulty.NORMAL, 800,
                "story_bg_2", "character_rhythm"));

        // 스토리 3: 첫 번째 시련
        stories.add(new Story(3, "첫 번째 시련",
                "진정한 실력을 시험받는 순간이 왔습니다.",
                "이제 진짜 도전이 시작됩니다.\n" +
                        "지금까지 배운 모든 것을 활용해야 해요.\n" +
                        "자신감을 가지고 도전해보세요!",
                "대단해요! 첫 번째 큰 시련을 극복했습니다.\n" +
                        "하지만 앞으로 더 큰 도전들이 기다리고 있어요.",
                song3, Song.Difficulty.HARD, 1200,
                "story_bg_3", "character_rhythm"));

        // 스토리 4: 라이벌의 등장
        stories.add(new Story(4, "라이벌의 도전",
                "강력한 라이벌이 나타났습니다. 실력을 증명할 시간입니다.",
                "음... 꽤 실력이 늘었군요.\n" +
                        "하지만 저를 이길 수 있을까요?\n" +
                        "진정한 리듬 마스터가 되려면 저를 넘어서야 합니다!",
                "인정합니다... 당신의 실력을 인정해요.\n" +
                        "하지만 아직 끝이 아닙니다. 더 큰 도전이 남아있어요!",
                song4, Song.Difficulty.HARD, 1500,
                "story_bg_4", "character_rival"));

        // 스토리 5: 최종 보스
        stories.add(new Story(5, "최종 결전",
                "모든 것을 건 마지막 대결의 시간입니다.",
                "드디어 여기까지 왔군요...\n" +
                        "리듬의 신전 최고층, 여기서 모든 것이 결정됩니다.\n" +
                        "지금까지의 모든 경험을 쏟아부으세요!",
                "축하합니다! 당신은 진정한 리듬 마스터입니다!\n" +
                        "이제 새로운 전설이 시작됩니다.\n" +
                        "앞으로도 음악과 함께하는 멋진 여행을 계속하세요!",
                song5, Song.Difficulty.EXPERT, 2000,
                "story_bg_5", "character_boss"));

        System.out.println("StoryManager: " + stories.size() + "개의 스토리가 로드되었습니다.");
    }

    /**
     * 모든 스토리 목록을 반환합니다
     */
    public List<Story> getAllStories() {
        return new ArrayList<>(stories);
    }

    /**
     * 해금된 스토리 목록을 반환합니다
     */
    public List<Story> getUnlockedStories() {
        List<Story> unlockedStories = new ArrayList<>();
        for (Story story : stories) {
            if (story.isUnlocked()) {
                unlockedStories.add(story);
            }
        }
        return unlockedStories;
    }

    /**
     * 특정 ID의 스토리를 반환합니다
     */
    public Story getStory(int storyId) {
        for (Story story : stories) {
            if (story.getStoryId() == storyId) {
                return story;
            }
        }
        return null;
    }

    /**
     * 현재 진행 중인 스토리를 반환합니다
     */
    public Story getCurrentStory() {
        if (currentStoryIndex < stories.size()) {
            return stories.get(currentStoryIndex);
        }
        return null;
    }

    /**
     * 스토리를 완료 처리하고 다음 스토리를 해금합니다
     */
    public void completeStory(int storyId, int score) {
        Story story = getStory(storyId);
        if (story != null && score >= story.getRequiredScore()) {
            story.setCompleted(true);

            // 다음 스토리 해금
            Story nextStory = getStory(storyId + 1);
            if (nextStory != null) {
                nextStory.setUnlocked(true);
                System.out.println("새로운 스토리가 해금되었습니다: " + nextStory.getTitle());
            }

            // 현재 스토리 인덱스 업데이트
            if (storyId > currentStoryIndex) {
                currentStoryIndex = storyId;
            }

            return;
        }

        System.out.println("스토리 완료 실패: 점수가 부족합니다. (필요: " +
                (story != null ? story.getRequiredScore() : "N/A") + ", 획득: " + score + ")");
    }

    /**
     * 전체 스토리 진행률을 반환합니다 (0.0 ~ 1.0)
     */
    public double getProgressRate() {
        int completedCount = 0;
        for (Story story : stories) {
            if (story.isCompleted()) {
                completedCount++;
            }
        }
        return stories.isEmpty() ? 0.0 : (double) completedCount / stories.size();
    }

    /**
     * 완료된 스토리 수를 반환합니다
     */
    public int getCompletedStoryCount() {
        int count = 0;
        for (Story story : stories) {
            if (story.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 전체 스토리 수를 반환합니다
     */
    public int getTotalStoryCount() {
        return stories.size();
    }

    /**
     * 스토리 모드가 모두 완료되었는지 확인합니다
     */
    public boolean isAllStoriesCompleted() {
        return getCompletedStoryCount() == getTotalStoryCount();
    }
}