package game;

import util.Constants;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SettingsPanel extends JPanel {
    private Game game;
    private MenuButton backButton;

    private JSlider volumeSlider;
    private JCheckBox soundEnabledCheck;
    private JCheckBox musicEnabledCheck;

    private JLabel volumeLabel;
    private JLabel soundLabel;
    private JLabel musicLabel;
    private JLabel titleLabel;
    private JLabel volumeValueLabel;
    private boolean isLoading = true;

    public SettingsPanel(Game game) {
        this.game = game;
        this.setLayout(null);
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setPreferredSize(new Dimension(Constants.MENU_WIDTH, Constants.MENU_HEIGHT));
        this.setFocusable(true);

        initializeComponents();
        setupMouseListeners();
        loadSettings();
        isLoading = false;
    }

    private void initializeComponents() {
        titleLabel = new JLabel("游戏设置", SwingConstants.CENTER);
        titleLabel.setFont(loadChineseFont(Font.BOLD, 48));
        titleLabel.setForeground(Constants.MENU_TITLE_COLOR);
        titleLabel.setBounds(0, 30, Constants.MENU_WIDTH, 60);
        add(titleLabel);

        int centerX = Constants.MENU_WIDTH / 2;
        int startY = 150;
        int gap = 100;

        volumeLabel = new JLabel("主音量:", SwingConstants.RIGHT);
        volumeLabel.setFont(loadChineseFont(Font.PLAIN, 24));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setBounds(centerX - 250, startY, 150, 40);
        add(volumeLabel);

        volumeValueLabel = new JLabel("70%", SwingConstants.LEFT);
        volumeValueLabel.setFont(loadChineseFont(Font.PLAIN, 24));
        volumeValueLabel.setForeground(Color.YELLOW);
        volumeValueLabel.setBounds(centerX + 230, startY, 80, 40);
        add(volumeValueLabel);

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setBounds(centerX - 80, startY + 5, 300, 30);
        volumeSlider.setBackground(Constants.BACKGROUND_COLOR);
        volumeSlider.setForeground(Color.YELLOW);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintLabels(true);

        volumeSlider.addChangeListener(e -> {
            if (isLoading) return;
            int value = volumeSlider.getValue();
            volumeValueLabel.setText(value + "%");
            applySettings();
        });

        add(volumeSlider);

        soundLabel = new JLabel("音效开关:", SwingConstants.RIGHT);
        soundLabel.setFont(loadChineseFont(Font.PLAIN, 24));
        soundLabel.setForeground(Color.WHITE);
        soundLabel.setBounds(centerX - 250, startY + gap, 150, 40);
        add(soundLabel);

        soundEnabledCheck = new JCheckBox("启用音效");
        soundEnabledCheck.setFont(loadChineseFont(Font.PLAIN, 20));
        soundEnabledCheck.setForeground(Color.WHITE);
        soundEnabledCheck.setBackground(Constants.BACKGROUND_COLOR);
        soundEnabledCheck.setBounds(centerX - 80, startY + gap + 5, 150, 30);
        soundEnabledCheck.setSelected(true);

        soundEnabledCheck.addActionListener(e -> {
            if (isLoading) return;
            applySettings();
        });

        add(soundEnabledCheck);

        musicLabel = new JLabel("背景音乐:", SwingConstants.RIGHT);
        musicLabel.setFont(loadChineseFont(Font.PLAIN, 24));
        musicLabel.setForeground(Color.WHITE);
        musicLabel.setBounds(centerX - 250, startY + gap * 2, 150, 40);
        add(musicLabel);

        musicEnabledCheck = new JCheckBox("启用音乐");
        musicEnabledCheck.setFont(loadChineseFont(Font.PLAIN, 20));
        musicEnabledCheck.setForeground(Color.WHITE);
        musicEnabledCheck.setBackground(Constants.BACKGROUND_COLOR);
        musicEnabledCheck.setBounds(centerX - 80, startY + gap * 2 + 5, 150, 30);
        musicEnabledCheck.setSelected(true);

        musicEnabledCheck.addActionListener(e -> {
            if (isLoading) return;
            applySettings();
        });

        add(musicEnabledCheck);

        int buttonWidth = 150;
        int buttonHeight = 50;
        int bottomY = Constants.MENU_HEIGHT - 100;

        backButton = new MenuButton("返回", centerX - buttonWidth/2, bottomY, buttonWidth, buttonHeight, () -> {
            saveSettings();
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

    private void loadSettings() {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.FileInputStream fis = new java.io.FileInputStream("settings.properties");
            props.load(fis);
            fis.close();

            int volume = Integer.parseInt(props.getProperty("volume", "70"));
            boolean soundEnabled = Boolean.parseBoolean(props.getProperty("soundEnabled", "true"));
            boolean musicEnabled = Boolean.parseBoolean(props.getProperty("musicEnabled", "true"));

            volumeSlider.setValue(volume);
            volumeValueLabel.setText(volume + "%");
            soundEnabledCheck.setSelected(soundEnabled);
            musicEnabledCheck.setSelected(musicEnabled);
        } catch (Exception e) {
            volumeSlider.setValue(70);
            volumeValueLabel.setText("70%");
            soundEnabledCheck.setSelected(true);
            musicEnabledCheck.setSelected(true);
        }
    }

    private void applySettings() {
        SoundManager soundManager = game.getSoundManager();
        if (soundManager != null) {
            float volume = volumeSlider.getValue() / 100.0f;
            soundManager.setMasterVolume(volume);
            soundManager.setSoundEnabled(soundEnabledCheck.isSelected());
            soundManager.setMusicEnabled(musicEnabledCheck.isSelected());
        }
    }

    private void saveSettings() {
        try {
            java.util.Properties props = new java.util.Properties();
            props.setProperty("volume", String.valueOf(volumeSlider.getValue()));
            props.setProperty("soundEnabled", String.valueOf(soundEnabledCheck.isSelected()));
            props.setProperty("musicEnabled", String.valueOf(musicEnabledCheck.isSelected()));

            java.io.FileOutputStream fos = new java.io.FileOutputStream("settings.properties");
            props.store(fos, "Game Settings");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawButtons(g2d);
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
