package game;

import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Game extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MainMenu mainMenu;
    private RulesPanel rulesPanel;
    private HighScorePanel highScorePanel;
    private NameInputPanel nameInputPanel;
    private LevelSelectPanel levelSelectPanel;
    private SettingsPanel settingsPanel;
    private GamePanel gamePanel;

    public Game() {
        setTitle("PAC-MAN");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initializePanels();

        setContentPane(mainPanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializePanels() {
        mainMenu = new MainMenu(this);
        rulesPanel = new RulesPanel(this);
        highScorePanel = new HighScorePanel(this);
        nameInputPanel = new NameInputPanel(this);
        levelSelectPanel = new LevelSelectPanel(this);
        settingsPanel = new SettingsPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(rulesPanel, "RULES");
        mainPanel.add(highScorePanel, "HIGH_SCORE");
        mainPanel.add(nameInputPanel, "NAME_INPUT");
        mainPanel.add(levelSelectPanel, "LEVEL_SELECT");
        mainPanel.add(settingsPanel, "SETTINGS");
        mainPanel.add(gamePanel, "GAME");
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        if (panelName.equals("GAME")) {
            gamePanel.requestFocusInWindow();
        } else if (panelName.equals("MENU")) {
            mainMenu.requestFocusInWindow();
        }
    }

    public void showNameInput(int score) {
        nameInputPanel.setScore(score);
        showPanel("NAME_INPUT");
    }

    public void startLevel(int level) {
        gamePanel.startLevel(level);
        showPanel("GAME");
    }

    public void startNewGame() {
        gamePanel.startNewGame();
    }

    public util.SoundManager getSoundManager() {
        if (gamePanel == null) {
            return null;
        }
        return gamePanel.getSoundManager();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Game();
            }
        });
    }
}
