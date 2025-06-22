package main.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 이미지 로딩을 담당하는 유틸리티 클래스
 */
public class ImageLoader {
    private static ImageLoader instance;
    private Map<String, BufferedImage> imageCache;

    private ImageLoader() {
        imageCache = new HashMap<>();
        loadDefaultImages();
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    /**
     * 기본 이미지들을 로드합니다
     */
    private void loadDefaultImages() {
        // 기본 이미지들 로드 (파일이 없어도 오류 방지)
        loadImage("note_default", Constants.IMAGE_PATH + "notes/note_default.png");
        loadImage("note_perfect", Constants.IMAGE_PATH + "notes/note_perfect.png.png");
        loadImage("note_good", Constants.IMAGE_PATH + "notes/note_good.png.png");
        loadImage("note_great", Constants.IMAGE_PATH + "notes/note_great.png");
        loadImage("note_miss", Constants.IMAGE_PATH + "notes/note_miss.png");
        loadImage("note_early", Constants.IMAGE_PATH + "notes/note_early.png");
        loadImage("note_late", Constants.IMAGE_PATH + "notes/note_late.png");

        loadImage("background_menu", Constants.IMAGE_PATH + "backgrounds/menu_bg.png");
        loadImage("background_game", Constants.IMAGE_PATH + "backgrounds/game_bg.png");
        loadImage("background_result", Constants.IMAGE_PATH + "backgrounds/result_bg.png");

        loadImage("button_normal", Constants.IMAGE_PATH + "ui/button_normal.png");
        loadImage("button_hover", Constants.IMAGE_PATH + "ui/button_hover.png");
        loadImage("button_pressed", Constants.IMAGE_PATH + "ui/button_pressed.png");
        loadImage("logo", Constants.IMAGE_PATH + "ui/logo.png");
        loadImage("icon_music", Constants.IMAGE_PATH + "ui/icon_music.png");
        loadImage("icon_sound", Constants.IMAGE_PATH + "ui/icon_sound.png");
    }

    /**
     * 이미지를 로드합니다
     * 
     * @param key      이미지 키
     * @param filePath 파일 경로
     * @return 로드 성공 여부
     */
    public boolean loadImage(String key, String filePath) {
        try {
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                imageCache.put(key, image);
                return true;
            } else {
                System.out.println("이미지 파일을 찾을 수 없습니다: " + filePath);
                return false;
            }
        } catch (IOException e) {
            System.err.println("이미지 로드 실패: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 이미지를 가져옵니다
     * 
     * @param key 이미지 키
     * @return BufferedImage 또는 null
     */
    public BufferedImage getImage(String key) {
        return imageCache.get(key);
    }

    /**
     * 이미지가 로드되어 있는지 확인합니다
     * 
     * @param key 이미지 키
     * @return 로드 여부
     */
    public boolean hasImage(String key) {
        return imageCache.containsKey(key);
    }

    /**
     * 이미지를 캐시에서 제거합니다
     * 
     * @param key 이미지 키
     */
    public void removeImage(String key) {
        imageCache.remove(key);
    }

    /**
     * 모든 이미지를 캐시에서 제거합니다
     */
    public void clearCache() {
        imageCache.clear();
    }

    /**
     * 현재 캐시된 이미지 개수를 반환합니다
     */
    public int getCacheSize() {
        return imageCache.size();
    }

    /**
     * 캐시된 모든 이미지 키를 반환합니다
     */
    public String[] getCachedImageKeys() {
        return imageCache.keySet().toArray(new String[0]);
    }
}