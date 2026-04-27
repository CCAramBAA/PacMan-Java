package game;

import util.Constants;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    private Game game;
    private MenuButton startButton;
    private MenuButton levelSelectButton;
    private MenuButton settingsButton;
    private MenuButton rulesButton;
    private MenuButton highScoreButton;
    private MenuButton exitButton;

    public MainMenu(Game game) {
        this.game = game;
        this.setLayout(null);
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setPreferredSize(new Dimension(Constants.MENU_WIDTH, Constants.MENU_HEIGHT));
        this.setFocusable(true);

        initializeComponents();
        setupMouseListeners();
    }

    private void initializeComponents() {
        JLabel titleLabel = new JLabel("PAC-MAN", SwingConstants.CENTER);  // 原来是 "吃豆人 PAC-MAN"

        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(Constants.MENU_TITLE_COLOR);
        titleLabel.setBounds(0, 30, Constants.MENU_WIDTH, 60);  // 原来是 y=50

        add(titleLabel);

        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = Constants.MENU_WIDTH / 2;

        int startY = 150;  // 原来是 180
        int gap = 20;      // 原来是 15

        startButton = new MenuButton("开始游戏", centerX - buttonWidth/2, startY, buttonWidth, buttonHeight, () -> {
            game.startNewGame();
            game.showPanel("GAME");
        });

        levelSelectButton = new MenuButton("关卡选择", centerX - buttonWidth/2, startY + buttonHeight + gap, buttonWidth, buttonHeight, () -> {
            game.showPanel("LEVEL_SELECT");
        });

        settingsButton = new MenuButton("游戏设置", centerX - buttonWidth/2, startY + (buttonHeight + gap) * 2, buttonWidth, buttonHeight, () -> {
            game.showPanel("SETTINGS");
        });

        rulesButton = new MenuButton("游戏规则", centerX - buttonWidth/2, startY + (buttonHeight + gap) * 3, buttonWidth, buttonHeight, () -> {
            game.showPanel("RULES");
        });

        highScoreButton = new MenuButton("最高分榜", centerX - buttonWidth/2, startY + (buttonHeight + gap) * 4, buttonWidth, buttonHeight, () -> {
            game.showPanel("HIGH_SCORE");
        });

        exitButton = new MenuButton("退出游戏", centerX - buttonWidth/2, startY + (buttonHeight + gap) * 5, buttonWidth, buttonHeight, () -> {
            System.exit(0);
        });
    }

    private void setupMouseListeners() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                checkButtonClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                updateButtonHover(e.getX(), e.getY());
            }
        });
    }

    private void checkButtonClick(int x, int y) {
        if (startButton.containsPoint(x, y)) startButton.click();
        else if (levelSelectButton.containsPoint(x, y)) levelSelectButton.click();
        else if (settingsButton.containsPoint(x, y)) settingsButton.click();
        else if (rulesButton.containsPoint(x, y)) rulesButton.click();
        else if (highScoreButton.containsPoint(x, y)) highScoreButton.click();
        else if (exitButton.containsPoint(x, y)) exitButton.click();
    }

    private void updateButtonHover(int x, int y) {
        startButton.setHovered(startButton.containsPoint(x, y));
        levelSelectButton.setHovered(levelSelectButton.containsPoint(x, y));
        settingsButton.setHovered(settingsButton.containsPoint(x, y));
        rulesButton.setHovered(rulesButton.containsPoint(x, y));
        highScoreButton.setHovered(highScoreButton.containsPoint(x, y));
        exitButton.setHovered(exitButton.containsPoint(x, y));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        startButton.draw(g2d);
        levelSelectButton.draw(g2d);
        settingsButton.draw(g2d);
        rulesButton.draw(g2d);
        highScoreButton.draw(g2d);
        exitButton.draw(g2d);
    }
}
