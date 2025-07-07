package main.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 프로그래밍적으로 간단한 사운드 효과를 생성하는 클래스
 * 실제 사운드 파일이 없어도 기본 효과음을 제공합니다
 */
public class SoundGenerator {
    private static final int SAMPLE_RATE = 44100;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    /**
     * 지정된 주파수와 지속시간의 사인파 톤을 생성합니다
     */
    public static Clip generateTone(double frequency, int durationMs) {
        return generateTone(frequency, durationMs, 0.5);
    }

    /**
     * 지정된 주파수, 지속시간, 볼륨의 사인파 톤을 생성합니다
     */
    public static Clip generateTone(double frequency, int durationMs, double volume) {
        try {
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS,
                    CHANNELS * SAMPLE_SIZE_IN_BITS / 8, SAMPLE_RATE, BIG_ENDIAN);

            int frameCount = (int) (durationMs * SAMPLE_RATE / 1000.0);
            byte[] samples = new byte[frameCount * 2]; // 16-bit samples

            for (int i = 0; i < frameCount; i++) {
                double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
                short sample = (short) (Math.sin(angle) * volume * Short.MAX_VALUE); // 최대 볼륨

                // 리틀 엔디안으로 저장
                samples[i * 2] = (byte) (sample & 0xFF);
                samples[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(samples);
            AudioInputStream ais = new AudioInputStream(bais, format, frameCount);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            System.out.println("사운드 생성됨: " + frequency + "Hz, " + durationMs + "ms, 볼륨:" + volume);
            return clip;

        } catch (LineUnavailableException | IOException e) {
            System.err.println("사운드 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 버튼 클릭 사운드 생성 (짧은 높은 톤)
     */
    public static Clip generateClickSound() {
        return generateTone(800, 100, 0.3);
    }

    /**
     * 호버 사운드 생성 (매우 짧은 부드러운 톤)
     */
    public static Clip generateHoverSound() {
        return generateTone(600, 50, 0.2);
    }

    /**
     * 일시정지 사운드 생성 (낮은 톤)
     */
    public static Clip generatePauseSound() {
        return generateTone(300, 200, 0.4);
    }

    /**
     * 재개 사운드 생성 (높은 톤)
     */
    public static Clip generateResumeSound() {
        return generateTone(500, 150, 0.4);
    }

    /**
     * 확인 사운드 생성 (2개 톤 조합)
     */
    public static Clip generateConfirmSound() {
        try {
            AudioFormat format = new AudioFormat(
                    SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

            int frameCount = (int) (300 * SAMPLE_RATE / 1000.0); // 300ms
            byte[] samples = new byte[frameCount * 2];

            for (int i = 0; i < frameCount; i++) {
                double progress = (double) i / frameCount;
                double frequency = 400 + (progress * 200); // 400Hz에서 600Hz로 상승
                double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
                short sample = (short) (Math.sin(angle) * 0.3 * Short.MAX_VALUE);

                samples[i * 2] = (byte) (sample & 0xFF);
                samples[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(samples);
            AudioInputStream ais = new AudioInputStream(bais, format, frameCount);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 취소 사운드 생성 (하강하는 톤)
     */
    public static Clip generateCancelSound() {
        try {
            AudioFormat format = new AudioFormat(
                    SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

            int frameCount = (int) (200 * SAMPLE_RATE / 1000.0); // 200ms
            byte[] samples = new byte[frameCount * 2];

            for (int i = 0; i < frameCount; i++) {
                double progress = (double) i / frameCount;
                double frequency = 600 - (progress * 200); // 600Hz에서 400Hz로 하강
                double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
                short sample = (short) (Math.sin(angle) * 0.3 * Short.MAX_VALUE);

                samples[i * 2] = (byte) (sample & 0xFF);
                samples[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(samples);
            AudioInputStream ais = new AudioInputStream(bais, format, frameCount);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 메뉴 선택 사운드 생성
     */
    public static Clip generateMenuSelectSound() {
        return generateTone(700, 120, 0.3);
    }

    /**
     * 메뉴 뒤로가기 사운드 생성
     */
    public static Clip generateMenuBackSound() {
        return generateTone(450, 150, 0.3);
    }

    /**
     * 성공 사운드 생성 (상승하는 3개 톤 조합)
     */
    public static Clip generateSuccessSound() {
        try {
            AudioFormat format = new AudioFormat(
                    SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

            int frameCount = (int) (600 * SAMPLE_RATE / 1000.0); // 600ms
            byte[] samples = new byte[frameCount * 2];

            for (int i = 0; i < frameCount; i++) {
                double progress = (double) i / frameCount;
                double frequency;

                // 3단계 상승 톤 (200ms씩)
                if (progress < 0.33) {
                    frequency = 400; // 첫 번째 톤
                } else if (progress < 0.66) {
                    frequency = 500; // 두 번째 톤
                } else {
                    frequency = 600; // 세 번째 톤
                }

                double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
                // 페이드 인/아웃 효과
                double envelope = Math.sin(Math.PI * progress);
                short sample = (short) (Math.sin(angle) * envelope * 0.4 * Short.MAX_VALUE);

                samples[i * 2] = (byte) (sample & 0xFF);
                samples[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(samples);
            AudioInputStream ais = new AudioInputStream(bais, format, frameCount);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}