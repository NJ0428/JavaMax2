package main.ui;

import main.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * 게임 오버 화면을 표시하는 패널
 */
public class GameOverPanel extends JPanel {
    public GameOverPanel() {
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 72));
        String title = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        int y = getHeight() / 2 - fm.getHeight();
        g2d.drawString(title, x, y);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        String info = "ENTER 키를 눌러 메뉴로";
        fm = g2d.getFontMetrics();
        int infoX = (getWidth() - fm.stringWidth(info)) / 2;
        g2d.drawString(info, infoX, y + 80);
    }
}
