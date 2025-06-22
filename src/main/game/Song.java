package main.game;

/**
 * 노래 정보를 담는 클래스
 */
public class Song {
    private String title; // 곡 제목
    private String artist; // 아티스트
    private String album; // 앨범명
    private String genre; // 장르
    private int bpm; // BPM
    private String fileName; // 파일명
    private String thumbnailPath; // 썸네일 경로
    private Difficulty[] difficulties; // 난이도별 정보

    public Song(String title, String artist, String album, String genre, int bpm, String fileName) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.bpm = bpm;
        this.fileName = fileName;
        this.thumbnailPath = "resources/images/songs/" + fileName.replace(".wav", ".jpg");

        // 기본 난이도 설정
        this.difficulties = new Difficulty[4];
        this.difficulties[0] = new Difficulty("하", 1, 150);
        this.difficulties[1] = new Difficulty("중", 3, 250);
        this.difficulties[2] = new Difficulty("상", 5, 400);
        this.difficulties[3] = new Difficulty("최상", 8, 650);
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getBpm() {
        return bpm;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAudioPath() {
        // 파일명으로부터 실제 오디오 파일 경로 반환
        return "resources/audio/gameplay/" + fileName;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public Difficulty[] getDifficulties() {
        return difficulties;
    }

    public Difficulty getDifficulty(int index) {
        return (index >= 0 && index < difficulties.length) ? difficulties[index] : null;
    }

    /**
     * 곡의 전체 정보를 문자열로 반환
     */
    public String getFullInfo() {
        return String.format("%s - %s\n앨범: %s\n장르: %s\nBPM: %d",
                title, artist, album, genre, bpm);
    }

    /**
     * 난이도 정보를 담는 내부 클래스
     */
    public static class Difficulty {
        private String name; // 난이도 이름
        private int level; // 난이도 레벨
        private int noteCount; // 노트 개수
        private int bestScore; // 최고 점수
        private double bestAccuracy; // 최고 정확도

        public Difficulty(String name, int level, int noteCount) {
            this.name = name;
            this.level = level;
            this.noteCount = noteCount;
            this.bestScore = 0;
            this.bestAccuracy = 0.0;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public int getNoteCount() {
            return noteCount;
        }

        public int getBestScore() {
            return bestScore;
        }

        public double getBestAccuracy() {
            return bestAccuracy;
        }

        public void setBestScore(int bestScore) {
            this.bestScore = bestScore;
        }

        public void setBestAccuracy(double bestAccuracy) {
            this.bestAccuracy = bestAccuracy;
        }

        public String getDifficultyInfo() {
            return String.format("%s (Lv.%d)\n노트: %d개", name, level, noteCount);
        }
    }
}