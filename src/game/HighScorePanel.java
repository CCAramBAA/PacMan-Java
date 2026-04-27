package game;

import util.Constants;
import util.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import java.util.*;
import java.util.List;

public class HighScorePanel extends JPanel {
    private MenuButton backButton;
    private List<HighScoreEntry> highScores;

    private Game game;

    public static class HighScoreEntry {
        String name;
        int score;

        public HighScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public HighScorePanel(Game game) {
        this.game = game;
        this.highScores = new ArrayList<>();
        loadHighScores();

        initializeButtons();
        setupMouseListeners();

        this.setPreferredSize(new Dimension(Constants.MENU_WIDTH, Constants.MENU_HEIGHT));
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setFocusable(true);
    }

    private void initializeButtons() {
        int buttonWidth = 150;
        int buttonHeight = 50;
        int bottomY = Constants.MENU_HEIGHT - 80;

        backButton = new MenuButton("返回", (Constants.MENU_WIDTH - buttonWidth) / 2, bottomY, buttonWidth, buttonHeight, () -> {
            game.showPanel("MENU");
        });
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (backButton.containsPoint(e.getX(), e.getY())) {
                    backButton.click();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                backButton.setHovered(backButton.containsPoint(e.getX(), e.getY()));
                repaint();
            }
        });
    }

    private void loadHighScores() {
        File file = new File("highscores.dat");
        if (!file.exists()) {
            addDefaultScores();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    highScores.add(new HighScoreEntry(parts[0], Integer.parseInt(parts[1])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            addDefaultScores();
        }

        if (highScores.isEmpty()) {
            addDefaultScores();
        }
    }

    private void addDefaultScores() {
        highScores.add(new HighScoreEntry("玩家1", 10000));
        highScores.add(new HighScoreEntry("玩家2", 8000));
        highScores.add(new HighScoreEntry("玩家3", 6000));
        highScores.add(new HighScoreEntry("玩家4", 4000));
        highScores.add(new HighScoreEntry("玩家5", 2000));
    }

    public void addHighScore(String name, int score) {
        highScores.add(new HighScoreEntry(name, score));
        highScores.sort((a, b) -> b.score - a.score);

        if (highScores.size() > 10) {
            highScores = highScores.subList(0, 10);
        }

        saveHighScores();
        syncWithHighestScore();
    }

    private void syncWithHighestScore() {
        if (!highScores.isEmpty()) {
            int highestScore = highScores.get(0).score;
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("highscore.txt"))) {
                writer.println(highestScore);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveHighScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("highscores.dat"))) {
            for (HighScoreEntry entry : highScores) {
                writer.println(entry.name + "," + entry.score);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTitle(g2d);
        drawHighScores(g2d);
        drawButtons(g2d);
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(Constants.MENU_TITLE_COLOR);
        g2d.setFont(loadChineseFont(Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "最高分排行榜";
        int x = (Constants.MENU_WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, x, 60);
    }

    private void drawHighScores(Graphics2D g2d) {
        g2d.setFont(loadChineseFont(Font.PLAIN, 24));

        int startY = 130;
        int lineHeight = 45;

        for (int i = 0; i < highScores.size(); i++) {
            HighScoreEntry entry = highScores.get(i);

            if (i < 3) {
                g2d.setColor(new Color(255, 215, 0));
            } else {
                g2d.setColor(Constants.MENU_TEXT_COLOR);
            }

            String rankText = String.format("第%d名", i + 1);
            String scoreText = String.format("%s - %d分", entry.name, entry.score);

            FontMetrics fm = g2d.getFontMetrics();
            int rankWidth = fm.stringWidth(rankText);

            g2d.drawString(rankText, 150, startY + i * lineHeight);
            g2d.drawString(scoreText, 150 + rankWidth + 50, startY + i * lineHeight);
        }
    }

    private void drawButtons(Graphics2D g2d) {
        backButton.draw(g2d);
    }

    private Font loadChineseFont(int style, int size) {
        String[] fontNames = {"微软雅黑", "SimHei", "STSong", "KaiTi", "FangSong"};

        for (String fontName : fontNames) {
            Font font = new Font(fontName, style, size);
            if (font.canDisplay('中') && font.canDisplay('文')) {
                return font;
            }
        }

        return new Font(Font.SANS_SERIF, style, size);
    }
}
