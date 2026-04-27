package game;

import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class RulesPanel extends JPanel implements ActionListener {
    private Timer animationTimer;
    private MenuButton nextButton;
    private MenuButton prevButton;
    private MenuButton backButton;

    private int currentPage;
    private int totalPages;
    private float animationProgress;

    private Game game;

    private static final String[] TITLES = {
        "欢迎来到PAC-MAN",
        "幽灵介绍",
        "吃豆机制",
        "能量豆",
        "计分系统",
        "游戏控制"
    };

    private static final String[] CONTENTS = {
        "PAC-MAN是一款经典街机游戏，你需要在迷宫中\n" +
        "吃掉所有豆子，同时躲避幽灵的追捕。\n\n" +
        "目标：吃掉所有豆子进入下一关！\n\n" +
        "小心 - 四个幽灵正在追你！",

        "BLINKY（红色）：追击者\n" +
        "总是直接追击PAC-MAN\n\n" +
        "PINKY（粉色）：伏击者\n" +
        "试图预判你的位置\n\n" +
        "INKY（青色）：不可预测\n" +
        "使用复杂模式围堵你\n\n" +
        "CLYDE（橙色）：害羞鬼\n" +
        "靠近时会逃跑",

        "普通豆子（共240个）：\n" +
        "• 每个10分\n" +
        "• 吃完所有豆子即可过关\n" +
        "• 会发出'哇卡哇卡'的声音\n\n" +
        "你必须吃掉每一个豆子才能胜利！",

        "能量豆（共4个）：\n" +
        "• 每个50分\n" +
        "• 位于迷宫四个角落\n" +
        "• 使幽灵变蓝并可被吃掉\n" +
        "• 吃掉幽灵获得额外分数！\n\n" +
        "效果持续8秒（每关递减）",

        "分数说明：\n" +
        "• 豆子：10分\n" +
        "• 能量豆：50分\n" +
        "• 幽灵：200 → 400 → 800 → 1600\n" +
        "• 水果：100 - 5000分\n\n" +
        "快速连续吃幽灵获得连击奖励！",

        "移动：方向键（↑↓←→）\n" +
        "暂停：空格键\n" +
        "重新开始：空格键（游戏结束后）\n\n" +
        "技巧：\n" +
        "• 提前规划路线\n" +
        "• 利用隧道逃脱\n" +
        "• 保留能量豆应急\n" +
        "• 学习幽灵模式！"
    };

    public RulesPanel(Game game) {
        this.game = game;
        this.currentPage = 0;
        this.totalPages = TITLES.length;
        this.animationProgress = 0;

        initializeButtons();
        setupMouseListeners();

        animationTimer = new Timer(50, this);
        animationTimer.start();

        this.setPreferredSize(new Dimension(Constants.MENU_WIDTH, Constants.MENU_HEIGHT));
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setFocusable(true);
    }

    private void initializeButtons() {
        int buttonWidth = 150;
        int buttonHeight = 50;
        int bottomY = Constants.MENU_HEIGHT - 100;

        prevButton = new MenuButton("上一页", 100, bottomY-70, buttonWidth, buttonHeight, () -> {
            if (currentPage > 0) {
                currentPage--;
                animationProgress = 0;
            }
        });

        nextButton = new MenuButton("下一页", Constants.MENU_WIDTH - 250, bottomY-70, buttonWidth, buttonHeight, () -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                animationProgress = 0;
            }
        });

        backButton = new MenuButton("返回", (Constants.MENU_WIDTH - buttonWidth) / 2, bottomY , buttonWidth, buttonHeight, () -> {
            game.showPanel("MENU");
        });
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkButtonClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateButtonHover(e.getX(), e.getY());
            }
        });
    }

    private void checkButtonClick(int x, int y) {
        if (prevButton.containsPoint(x, y)) prevButton.click();
        else if (nextButton.containsPoint(x, y)) nextButton.click();
        else if (backButton.containsPoint(x, y)) backButton.click();
    }

    private void updateButtonHover(int x, int y) {
        prevButton.setHovered(prevButton.containsPoint(x, y));
        nextButton.setHovered(nextButton.containsPoint(x, y));
        backButton.setHovered(backButton.containsPoint(x, y));
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (animationProgress < 1.0f) {
            animationProgress += 0.05f;
            if (animationProgress > 1.0f) animationProgress = 1.0f;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawPageIndicator(g2d);
        drawTitle(g2d);
        drawContent(g2d);
        drawAnimations(g2d);
        drawButtons(g2d);
    }

    private void drawPageIndicator(Graphics2D g2d) {
        g2d.setColor(Constants.MENU_TEXT_COLOR);
        g2d.setFont(loadChineseFont(Font.PLAIN, 18));
        String pageText = String.format("第 %d 页 / 共 %d 页", currentPage + 1, totalPages);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (Constants.MENU_WIDTH - fm.stringWidth(pageText)) / 2;
        g2d.drawString(pageText, x, Constants.MENU_HEIGHT - 20);
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(Constants.MENU_TITLE_COLOR);
        g2d.setFont(loadChineseFont(Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (Constants.MENU_WIDTH - fm.stringWidth(TITLES[currentPage])) / 2;

        int yOffset = (int)(100 * animationProgress);
        g2d.drawString(TITLES[currentPage], x, 10 + yOffset);
    }

    private void drawContent(Graphics2D g2d) {
        g2d.setColor(Constants.MENU_TEXT_COLOR);
        g2d.setFont(loadChineseFont(Font.PLAIN, 20));

        String content = CONTENTS[currentPage];
        String[] lines = content.split("\n");

        int startY = 160;
        int lineHeight = 28;

        for (int i = 0; i < lines.length; i++) {
            int alpha = (int)(255 * animationProgress);
            g2d.setColor(new Color(Constants.MENU_TEXT_COLOR.getRed(),
                                  Constants.MENU_TEXT_COLOR.getGreen(),
                                  Constants.MENU_TEXT_COLOR.getBlue(), alpha));

            FontMetrics fm = g2d.getFontMetrics();
            int x = (Constants.MENU_WIDTH - fm.stringWidth(lines[i])) / 2;
            g2d.drawString(lines[i], x, startY + i * lineHeight);
        }
    }

    private void drawAnimations(Graphics2D g2d) {
        if (currentPage == 0) {
            drawPacmanAnimation(g2d);
        } else if (currentPage == 1) {
            drawGhostAnimation(g2d);
        }
    }

    private void drawPacmanAnimation(Graphics2D g2d) {
        int centerX = Constants.MENU_WIDTH / 2;
        int centerY = 380;
        int radius = 30;

        g2d.setColor(Constants.PACMAN_COLOR);

        int mouthAngle = (int)(Math.abs(Math.sin(System.currentTimeMillis() / 200.0)) * 45);

        g2d.fillArc(centerX - radius, centerY - radius,
                   radius * 2, radius * 2,
                   30 + mouthAngle, 360 - mouthAngle * 2);
    }

    private void drawGhostAnimation(Graphics2D g2d) {
        int startX = Constants.MENU_WIDTH / 2 -170;
        int centerY = 500;

        Color[] colors = {Constants.BLINKY_COLOR, Constants.PINKY_COLOR,
                         Constants.INKY_COLOR, Constants.CLYDE_COLOR};
        String[] names = {"Blinky", "Pinky", "Inky", "Clyde"};

        for (int i = 0; i < 4; i++) {
            int x = startX + i * 100;

            g2d.setColor(colors[i]);
            g2d.fillOval(x, centerY - 30, 60, 50);
            g2d.fillRect(x, centerY - 10, 60, 30);

            g2d.setColor(Color.WHITE);
            g2d.fillOval(x + 15, centerY - 20, 12, 15);
            g2d.fillOval(x + 33, centerY - 20, 12, 15);

            g2d.setColor(Color.BLUE);
            g2d.fillOval(x + 18, centerY - 15, 6, 8);
            g2d.fillOval(x + 36, centerY - 15, 6, 8);

            g2d.setColor(Color.WHITE);
            g2d.setFont(loadChineseFont(Font.PLAIN, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (60 - fm.stringWidth(names[i])) / 2;
            g2d.drawString(names[i], textX, centerY + 50);
        }
    }

    private void drawButtons(Graphics2D g2d) {
        prevButton.draw(g2d);
        nextButton.draw(g2d);
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
