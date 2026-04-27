package game;

import util.Constants;
import util.LevelConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LevelSelectPanel extends JPanel {
    private Game game;
    private JButton backButton;
    private JButton[] levelButtons;

    public LevelSelectPanel(Game game) {
        this.game = game;
        this.setLayout(null);
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setPreferredSize(new Dimension(Constants.MENU_WIDTH, Constants.MENU_HEIGHT));

        initializeComponents();
    }

    private void initializeComponents() {
        JLabel titleLabel = new JLabel("选择关卡", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(Constants.MENU_TITLE_COLOR);
        titleLabel.setBounds(0, 30, Constants.MENU_WIDTH, 50);
        add(titleLabel);

        levelButtons = new JButton[20];
        int startX = 80;
        int startY = 120;
        int buttonWidth = 80;
        int buttonHeight = 60;
        int gap = 20;

        for (int i = 0; i < 20; i++) {
            int row = i / 5;
            int col = i % 5;
            int x = startX + col * (buttonWidth + gap);
            int y = startY + row * (buttonHeight + gap);

            final int level = i + 1;
            LevelConfig.LevelData data = LevelConfig.getLevelData(level);

            JButton btn = new JButton("第" + level + "关");
            btn.setFont(new Font("微软雅黑", Font.BOLD, 14));
            btn.setBackground(Constants.MENU_BUTTON_COLOR);
            btn.setForeground(Color.BLACK);
            btn.setBounds(x, y, buttonWidth, buttonHeight);
            btn.setFocusPainted(false);

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.startLevel(level);
                }
            });

            levelButtons[i] = btn;
            add(btn);
        }

        backButton = new JButton("返回主菜单");
        backButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(Constants.MENU_WIDTH / 2 - 100, Constants.MENU_HEIGHT - 100, 200, 50);
        backButton.setFocusPainted(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.showPanel("MENU");
            }
        });
        add(backButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        int startX = 80;
        int startY = 120;
        int buttonWidth = 80;
        int buttonHeight = 60;
        int gap = 20;

        for (int i = 0; i < 20; i++) {
            int row = i / 5;
            int col = i % 5;
            int x = startX + col * (buttonWidth + gap);
            int y = startY + row * (buttonHeight + gap) + buttonHeight + 15;

            LevelConfig.LevelData data = LevelConfig.getLevelData(i + 1);
            String info = data.fruitName + " " + data.fruitScore;

            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (buttonWidth - fm.stringWidth(info)) / 2;
            g2d.drawString(info, textX, y);
        }
    }
}
