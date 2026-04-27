package game;

import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NameInputPanel extends JPanel {
    private MenuButton submitButton;
    private MenuButton backButton;
    private JTextField nameField;
    private JLabel instructionLabel;
    private JLabel scoreLabel;

    private Game game;
    private int currentScore;

    public NameInputPanel(Game game) {
        this.game = game;
        this.currentScore = 0;

        initializeComponents();
        setupMouseListeners();

        this.setPreferredSize(new Dimension(Constants.MENU_WIDTH, Constants.MENU_HEIGHT));
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setFocusable(true);
    }

    private void initializeComponents() {
        instructionLabel = new JLabel("恭喜！你创造了新纪录！", SwingConstants.CENTER);
        instructionLabel.setFont(loadChineseFont(Font.BOLD, 28));
        instructionLabel.setForeground(Constants.MENU_TITLE_COLOR);
        instructionLabel.setBounds(0, 80, Constants.MENU_WIDTH, 40);
        add(instructionLabel);

        scoreLabel = new JLabel("分数: 0", SwingConstants.CENTER);
        scoreLabel.setFont(loadChineseFont(Font.PLAIN, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(0, 130, Constants.MENU_WIDTH, 40);
        add(scoreLabel);

        JLabel nameLabel = new JLabel("输入你的名字:", SwingConstants.CENTER);
        nameLabel.setFont(loadChineseFont(Font.PLAIN, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(0, 200, Constants.MENU_WIDTH, 40);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(loadChineseFont(Font.PLAIN, 24));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBounds(Constants.MENU_WIDTH / 2 - 150, 260, 300, 50);
        nameField.setBackground(new Color(50, 50, 50));
        nameField.setForeground(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        add(nameField);

        int buttonWidth = 150;
        int buttonHeight = 50;
        int bottomY = Constants.MENU_HEIGHT - 120;

        submitButton = new MenuButton("提交", Constants.MENU_WIDTH / 2 - 170, bottomY, buttonWidth, buttonHeight, () -> {
            submitScore();
        });

        backButton = new MenuButton("返回", Constants.MENU_WIDTH / 2 + 20, bottomY, buttonWidth, buttonHeight, () -> {
            game.showPanel("MENU");
        });
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (submitButton.containsPoint(e.getX(), e.getY())) {
                    submitButton.click();
                } else if (backButton.containsPoint(e.getX(), e.getY())) {
                    backButton.click();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                submitButton.setHovered(submitButton.containsPoint(e.getX(), e.getY()));
                backButton.setHovered(backButton.containsPoint(e.getX(), e.getY()));
                repaint();
            }
        });
    }

    public void setScore(int score) {
        this.currentScore = score;
        scoreLabel.setText("分数: " + score);
        nameField.setText("");
        nameField.requestFocusInWindow();
    }

    private void submitScore() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "匿名玩家";
        }

        HighScorePanel highScorePanel = (HighScorePanel) ((JPanel) game.getContentPane()).getComponent(2);
        highScorePanel.addHighScore(name, currentScore);

        game.showPanel("HIGH_SCORE");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawButtons(g2d);
    }

    private void drawButtons(Graphics2D g2d) {
        submitButton.draw(g2d);
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
