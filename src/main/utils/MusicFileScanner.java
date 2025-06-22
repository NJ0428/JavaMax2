package main.utils;

import main.game.Song;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 음악 파일을 스캔하고 Song 객체를 생성하는 유틸리티 클래스
 */
public class MusicFileScanner {

    /**
     * gameplay 폴더에서 MP3 파일들을 스캔하여 Song 목록을 반환합니다
     */
    public static List<Song> scanGameplayFolder() {
        List<Song> songs = new ArrayList<>();

        try {
            File gameplayDir = new File(Constants.GAMEPLAY_PATH);
            System.out.println("음악 파일 스캔 시작: " + gameplayDir.getAbsolutePath());

            if (!gameplayDir.exists() || !gameplayDir.isDirectory()) {
                System.err.println("Gameplay 폴더를 찾을 수 없습니다: " + gameplayDir.getAbsolutePath());
                return songs;
            }

            File[] files = gameplayDir.listFiles();
            if (files == null) {
                System.err.println("폴더 내용을 읽을 수 없습니다.");
                return songs;
            }

            System.out.println("발견된 파일 수: " + files.length);

            for (File file : files) {
                if (file.isFile() && isSupportedAudioFile(file.getName())) {
                    Song song = createSongFromFile(file);
                    if (song != null) {
                        songs.add(song);
                        System.out.println("곡 추가됨: " + song.getTitle() + " (" + file.getName() + ")");
                    }
                }
            }

            System.out.println("총 " + songs.size() + "개의 곡이 로드되었습니다.");

        } catch (Exception e) {
            System.err.println("음악 파일 스캔 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * 파일이 지원되는 오디오 형식인지 확인합니다
     */
    private static boolean isSupportedAudioFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".mp3") ||
                lowerName.endsWith(".wav") ||
                lowerName.endsWith(".ogg");
    }

    /**
     * 파일로부터 Song 객체를 생성합니다
     */
    private static Song createSongFromFile(File file) {
        try {
            String fileName = file.getName();
            String nameWithoutExt = removeFileExtension(fileName);

            // 파일명에서 정보 추출
            String title = extractTitle(nameWithoutExt);
            String artist = extractArtist(nameWithoutExt);
            String genre = determineGenre(nameWithoutExt);
            int bpm = estimateBPM(nameWithoutExt);

            Song song = new Song(title, artist, "Unknown Album", genre, bpm, fileName);

            // 썸네일 경로 설정 (커버 이미지가 있는지 확인)
            String coverPath = findCoverImage(nameWithoutExt);
            if (coverPath != null) {
                song.setThumbnailPath(coverPath);
            }

            return song;

        } catch (Exception e) {
            System.err.println("파일에서 Song 생성 실패: " + file.getName() + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 파일명에서 확장자를 제거합니다
     */
    private static String removeFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot > 0) ? fileName.substring(0, lastDot) : fileName;
    }

    /**
     * 파일명에서 제목을 추출합니다
     */
    private static String extractTitle(String nameWithoutExt) {
        // 언더스코어나 하이픈으로 구분된 경우 첫 번째 부분을 제목으로 사용
        String[] parts = nameWithoutExt.split("[_-]");
        if (parts.length > 0) {
            return beautifyTitle(parts[0]);
        }
        return beautifyTitle(nameWithoutExt);
    }

    /**
     * 파일명에서 아티스트를 추출합니다 (간단한 추정)
     */
    private static String extractArtist(String nameWithoutExt) {
        // 숫자로 끝나는 경우 제거 (예: BuriedStar_3 -> BuriedStar)
        String cleaned = nameWithoutExt.replaceAll("_\\d+$", "");

        // 카멜케이스나 언더스코어로 구분된 경우 분리
        String[] parts = cleaned.split("[_-]");
        if (parts.length > 1) {
            return beautifyTitle(parts[parts.length - 1]);
        }

        return "Unknown Artist";
    }

    /**
     * 제목을 보기 좋게 만듭니다
     */
    private static String beautifyTitle(String title) {
        // 카멜케이스를 공백으로 분리
        String result = title.replaceAll("([a-z])([A-Z])", "$1 $2");

        // 첫 글자를 대문자로
        if (result.length() > 0) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }

        return result;
    }

    /**
     * 파일명을 기반으로 장르를 추정합니다
     */
    private static String determineGenre(String nameWithoutExt) {
        String lower = nameWithoutExt.toLowerCase();

        if (lower.contains("electronic") || lower.contains("edm") || lower.contains("techno")) {
            return "Electronic";
        } else if (lower.contains("rock") || lower.contains("metal")) {
            return "Rock";
        } else if (lower.contains("pop")) {
            return "Pop";
        } else if (lower.contains("jazz")) {
            return "Jazz";
        } else if (lower.contains("classical") || lower.contains("piano")) {
            return "Classical";
        } else if (lower.contains("ambient") || lower.contains("chill")) {
            return "Ambient";
        } else {
            return "Unknown";
        }
    }

    /**
     * BPM을 추정합니다 (기본값 반환)
     */
    private static int estimateBPM(String nameWithoutExt) {
        String lower = nameWithoutExt.toLowerCase();

        if (lower.contains("fast") || lower.contains("speed")) {
            return 140;
        } else if (lower.contains("slow") || lower.contains("chill")) {
            return 80;
        } else {
            return 120; // 기본값
        }
    }

    /**
     * 커버 이미지를 찾습니다
     */
    private static String findCoverImage(String nameWithoutExt) {
        String[] extensions = { ".png", ".jpg", ".jpeg" };

        for (String ext : extensions) {
            File coverFile = new File(Constants.SONG_COVERS_PATH + nameWithoutExt + "_cover" + ext);
            if (coverFile.exists()) {
                return coverFile.getPath();
            }
        }

        return null;
    }
}