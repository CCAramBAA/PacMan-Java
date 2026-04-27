package game;

import animation.DeathAnimation;
import entity.Fruit;
import entity.Ghost;
import entity.Pacman;
import entity.Particle;
import maze.Maze;
import maze.Tile;
import util.Constants;
import util.DifficultyManager;
import util.ScoreManager;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    private Timer gameTimer;
    private Maze maze;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private DifficultyManager difficultyManager;
    private Constants.GameState gameState;

    private long lastModeSwitch;
    private boolean scatterMode;

    private Fruit currentFruit;
    private List<Particle> particles;
    private DeathAnimation deathAnimation;
    private List<FloatingText> floatingTexts;

    private int dotsEatenThisLevel;
    private int totalDotsEaten;
    private int ghostsEatenThisCombo;
    private int fruitSpawnCount;

    private long gameTime;
    private int fruitsEaten;

    private Game game;

    private static class FloatingText {
        int x, y;
        String text;
        Color color;
        int lifetime;
        int age;

        FloatingText(int x, int y, String text, Color color) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
            this.lifetime = 60;
            this.age = 0;
        }

        void update() {
            y -= 1;
            age++;
        }

        boolean isDead() {
            return age >= lifetime;
        }

        void draw(Graphics2D g2d) {
            float alpha = 1.0f - (float)age / lifetime;
            g2d.setColor(new Color(color.getRed(), color.getGreen(),
                                  color.getBlue(), (int)(255 * alpha)));
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int x = this.x - fm.stringWidth(text) / 2;
            g2d.drawString(text, x, this.y);
        }
    }

    public GamePanel(Game game) {
        this.game = game;
        this.setPreferredSize(new Dimension(Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT));
        this.setBackground(Constants.BACKGROUND_COLOR);
        this.setFocusable(true);

        initializeGame();
        setupKeyListener();

        gameTimer = new Timer(1000 / Constants.FPS, this);
        gameTimer.start();
    }

    private void initializeGame() {
        maze = new Maze();

        if (difficultyManager == null) {
            difficultyManager = new DifficultyManager();
        }

        int pacmanSpeed = difficultyManager.getPacmanSpeed();
        pacman = new Pacman(14 * Constants.TILE_SIZE, 23 * Constants.TILE_SIZE);
        pacman.setSpeed(pacmanSpeed);

        if (scoreManager == null) {
            scoreManager = new ScoreManager();
        }
        if (soundManager == null) {
            soundManager = new SoundManager();
        }

        particles = new ArrayList<>();
        floatingTexts = new ArrayList<>();

        ghosts = new ArrayList<>();

        initializeGhosts();

        gameState = Constants.GameState.PLAYING;
        lastModeSwitch = System.currentTimeMillis();
        scatterMode = true;
        dotsEatenThisLevel = 0;
        totalDotsEaten = 0;
        ghostsEatenThisCombo = 0;
        fruitSpawnCount = 0;
        gameTime = 0;
        fruitsEaten = 0;
        currentFruit = null;
        deathAnimation = null;

        soundManager.playBackgroundMusic();
    }

    private void initializeGhosts() {
        int currentLevel = difficultyManager.getCurrentLevel();
        int ghostSpeed = difficultyManager.getGhostSpeed();

        ghosts.clear();

        Ghost blinky = new Ghost(14 * Constants.TILE_SIZE, 11 * Constants.TILE_SIZE, "Blinky", Constants.BLINKY_COLOR);
        blinky.setSpeed(ghostSpeed);
        blinky.setInHouse(false);
        blinky.resetPosition(14 * Constants.TILE_SIZE, 11 * Constants.TILE_SIZE);
        ghosts.add(blinky);

        Ghost pinky = new Ghost(14 * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE, "Pinky", Constants.PINKY_COLOR);
        pinky.setSpeed(ghostSpeed);
        pinky.setInHouse(true);
        ghosts.add(pinky);

        Ghost inky = new Ghost(12 * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE, "Inky", Constants.INKY_COLOR);
        inky.setSpeed(ghostSpeed);
        inky.setInHouse(true);
        ghosts.add(inky);

        Ghost clyde = new Ghost(16 * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE, "Clyde", Constants.CLYDE_COLOR);
        clyde.setSpeed(ghostSpeed);
        clyde.setInHouse(true);
        ghosts.add(clyde);
    }


    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                pacman.setNextDirection(Constants.Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                pacman.setNextDirection(Constants.Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                pacman.setNextDirection(Constants.Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                pacman.setNextDirection(Constants.Direction.RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                if (gameState == Constants.GameState.GAME_OVER) {
                    game.showPanel("MENU");
                } else if (gameState == Constants.GameState.LEVEL_COMPLETE) {
                    nextLevel();
                } else if (gameState == Constants.GameState.PLAYING) {
                    gameState = Constants.GameState.PAUSED;
                } else if (gameState == Constants.GameState.PAUSED) {
                    gameState = Constants.GameState.PLAYING;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (gameState == Constants.GameState.PLAYING || gameState == Constants.GameState.PAUSED) {
                    game.showPanel("MENU");
                }
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == Constants.GameState.PLAYING) {
            update();
        }
        repaint();
    }

    private void update() {
        if (deathAnimation != null && deathAnimation.isPlaying()) {
            deathAnimation.update();
            if (deathAnimation.isFinished()) {
                int currentScore = scoreManager.getScore();
                if (scoreManager.isNewHighScore(currentScore)) {
                    game.showNameInput(currentScore);
                }
            }
            return;
        }

        gameTime++;
        updateModeSwitching();
        updateFruit();
        updateParticles();
        updateFloatingTexts();

        pacman.update();
        int[][] mazeData = convertMazeToArray();
        pacman.updateMovement(mazeData);

        checkDotCollision();

        Ghost blinky = getGhostByName("Blinky");
        for (Ghost ghost : ghosts) {
            ghost.update();
            ghost.updateAI(mazeData, pacman, blinky, totalDotsEaten);
        }

        checkGhostCollision();

        if (maze.areAllDotsEaten()) {
            gameState = Constants.GameState.LEVEL_COMPLETE;
            soundManager.playSound("level_complete");
        }

        if (pacman.getLives() <= 0) {
            gameState = Constants.GameState.GAME_OVER;
            deathAnimation = new DeathAnimation(
                    (int)pacman.getX() + Constants.TILE_SIZE/2,
                    (int)pacman.getY() + Constants.TILE_SIZE/2
            );
            deathAnimation.start();
            soundManager.playSound("death");
        }
    }

    private void updateFruit() {
        if (currentFruit != null) {
            currentFruit.update();

            if (currentFruit.isVisible() &&
                    pacman.getBounds().intersects(currentFruit.getBounds())) {
                scoreManager.addScore(currentFruit.getPoints());
                soundManager.playSound("eat_fruit");
                fruitsEaten++;

                addFloatingText((int)currentFruit.getX(), (int)currentFruit.getY(),
                              "+" + currentFruit.getPoints(), currentFruit.getType().getColor());

                for (int i = 0; i < 15; i++) {
                    particles.add(new Particle(
                            currentFruit.getX(),
                            currentFruit.getY(),
                            currentFruit.getType().getColor()
                    ));
                }

                currentFruit = null;
            }
        }
    }

    private void updateParticles() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update();
            if (p.isDead()) {
                iterator.remove();
            }
        }
    }

    private void updateFloatingTexts() {
        Iterator<FloatingText> iterator = floatingTexts.iterator();
        while (iterator.hasNext()) {
            FloatingText ft = iterator.next();
            ft.update();
            if (ft.isDead()) {
                iterator.remove();
            }
        }
    }

    private void addFloatingText(int x, int y, String text, Color color) {
        floatingTexts.add(new FloatingText(x, y, text, color));
    }

    private void spawnFruit() {
        if (fruitSpawnCount >= 2) {
            return;
        }

        if (currentFruit == null || !currentFruit.isVisible()) {
            Fruit.FruitType type = Fruit.getFruitForLevel(difficultyManager.getCurrentLevel());
            currentFruit = new Fruit(14, 17, type);
            currentFruit.spawn();
            fruitSpawnCount++;
        }
    }

    private void updateModeSwitching() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastModeSwitch;

        long scatterTime = difficultyManager.getScatterTime();
        long chaseTime = difficultyManager.getChaseTime();
        long modeTime = scatterMode ? scatterTime : chaseTime;

        if (elapsed >= modeTime) {
            scatterMode = !scatterMode;
            lastModeSwitch = currentTime;

            Constants.GhostMode newMode = scatterMode ?
                Constants.GhostMode.SCATTER : Constants.GhostMode.CHASE;

            for (Ghost ghost : ghosts) {
                if (ghost.getMode() != Constants.GhostMode.FRIGHTENED && !ghost.isEaten()) {
                    ghost.setMode(newMode);
                }
            }
        }
    }

    private void checkDotCollision() {
        int tileX = pacman.getTileX();
        int tileY = pacman.getTileY();

        if (maze.getTile(tileY, tileX) == Tile.DOT) {
            maze.eatDot(tileY, tileX);
            scoreManager.addDotScore();
            soundManager.playSound("eat_dot");
            dotsEatenThisLevel++;
            totalDotsEaten++;

            Ghost blinky = getGhostByName("Blinky");
            if (blinky != null) {
                blinky.updateBlinkySpeed(maze.getDotsRemaining(), maze.getTotalDots());
            }

            if (scoreManager.shouldAwardExtraLife()) {
                pacman.gainLife();
                scoreManager.resetExtraLifeFlag();
                addFloatingText((int)pacman.getX(), (int)pacman.getY(), "奖励生命!", Color.GREEN);
            }

            if (dotsEatenThisLevel == 70 || dotsEatenThisLevel == 170) {
                spawnFruit();
            }

            for (int i = 0; i < 3; i++) {
                particles.add(new Particle(
                        pacman.getX() + Constants.TILE_SIZE/2,
                        pacman.getY() + Constants.TILE_SIZE/2,
                        Color.WHITE
                ));
            }
        } else if (maze.getTile(tileY, tileX) == Tile.POWER_PELLET) {
            maze.eatDot(tileY, tileX);
            scoreManager.addPowerPelletScore();

            if (scoreManager.shouldAwardExtraLife()) {
                pacman.gainLife();
                scoreManager.resetExtraLifeFlag();
                addFloatingText((int)pacman.getX(), (int)pacman.getY(), "奖励生命!", Color.GREEN);
            }

            activateFrightenedMode();
            soundManager.playSound("eat_power");
            dotsEatenThisLevel++;
            totalDotsEaten++;
        }
    }



    private void activateFrightenedMode() {
        int frightenedDuration = difficultyManager.getFrightenedDuration();
        ghostsEatenThisCombo = 0;

        for (Ghost ghost : ghosts) {
            if (!ghost.isEaten()) {
                ghost.setFrightened(frightenedDuration);
            }
        }
        scoreManager.resetGhostEatIndex();
    }

    private void checkGhostCollision() {
        if (deathAnimation != null && deathAnimation.isPlaying()) {
            return;
        }

        if (pacman.isInvincible()) {
            return;
        }

        Rectangle pacmanBounds = pacman.getBounds();

        Ghost collisionGhost = null;

        for (Ghost ghost : ghosts) {
            Rectangle ghostBounds = ghost.getBounds();

            if (pacmanBounds.intersects(ghostBounds)) {
                collisionGhost = ghost;
                break;
            }
        }

        if (collisionGhost != null) {
            if (collisionGhost.getMode() == Constants.GhostMode.FRIGHTENED) {
                collisionGhost.setEaten();
                ghostsEatenThisCombo++;
                int points = scoreManager.addGhostScore();
                soundManager.playSound("eat_ghost");

                addFloatingText((int)collisionGhost.getX(), (int)collisionGhost.getY(),
                        "+" + points, Color.CYAN);

                for (int i = 0; i < 10; i++) {
                    particles.add(new Particle(
                            collisionGhost.getX() + Constants.TILE_SIZE/2,
                            collisionGhost.getY() + Constants.TILE_SIZE/2,
                            Constants.FRIGHTENED_COLOR
                    ));
                }
            } else if (!collisionGhost.isEaten()) {
                pacman.loseLife();
                if (pacman.getLives() > 0) {
                    pacman.resetPosition(14 * Constants.TILE_SIZE, 23 * Constants.TILE_SIZE);
                    pacman.setInvincible(true);
                    gameState = Constants.GameState.PLAYING;
                } else {
                    gameState = Constants.GameState.GAME_OVER;
                }
            }
        }
    }


    private void resetPositions() {
        pacman.resetPosition(14 * Constants.TILE_SIZE, 23 * Constants.TILE_SIZE);

        for (Ghost ghost : ghosts) {
            switch (ghost.getName()) {
                case "Blinky":
                    ghost.resetPosition(14 * Constants.TILE_SIZE, 11 * Constants.TILE_SIZE);
                    break;
                case "Pinky":
                    ghost.resetPosition(14 * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE);
                    break;
                case "Inky":
                    ghost.resetPosition(12 * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE);
                    break;
                case "Clyde":
                    ghost.resetPosition(16 * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE);
                    break;
            }
        }
    }

    private int[][] convertMazeToArray() {
        int[][] mazeArray = new int[Constants.MAZE_HEIGHT][Constants.MAZE_WIDTH];
        for (int row = 0; row < Constants.MAZE_HEIGHT; row++) {
            for (int col = 0; col < Constants.MAZE_WIDTH; col++) {
                Tile tile = maze.getTile(row, col);
                switch (tile) {
                    case WALL:
                        mazeArray[row][col] = 1;
                        break;
                    case DOOR:
                        mazeArray[row][col] = 4;
                        break;
                    default:
                        mazeArray[row][col] = 0;
                        break;
                }
            }
        }
        return mazeArray;
    }

    private Ghost getGhostByName(String name) {
        for (Ghost ghost : ghosts) {
            if (ghost.getName().equals(name)) {
                return ghost;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        maze.draw(g2d);

        if (currentFruit != null) {
            currentFruit.draw(g2d);
        }

        for (Particle p : particles) {
            p.draw(g2d);
        }

        for (FloatingText ft : floatingTexts) {
            ft.draw(g2d);
        }

        pacman.draw(g2d);

        for (Ghost ghost : ghosts) {
            ghost.draw(g2d);
        }

        if (deathAnimation != null && deathAnimation.isPlaying()) {
            deathAnimation.draw(g2d, 0, 0);
        }

        drawUI(g2d);
        drawOverlay(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(loadChineseFont(Font.BOLD, 16));

        int bottomY = Constants.PANEL_HEIGHT - 30;
        int secondRowY = Constants.PANEL_HEIGHT - 10;
        int spacing = Constants.PANEL_WIDTH / 4;

        g2d.drawString("分数: " + scoreManager.getScore(), 10, bottomY);
        g2d.drawString("最高: " + scoreManager.getHighScore(),
                spacing * 1, bottomY);
        g2d.drawString("关卡: " + difficultyManager.getCurrentLevel(),
                spacing * 2, bottomY);

        String livesText = "生命: ";
        g2d.drawString(livesText, 10, secondRowY);

        FontMetrics fm = g2d.getFontMetrics();
        int livesTextWidth = fm.stringWidth(livesText);
        int iconSize = 15;
        int iconSpacing = 5;
        int startX = 10 + livesTextWidth;
        int startY = secondRowY - iconSize + 5;

        int lives = pacman.getLives();
        for (int i = 0; i < lives && i < 10; i++) {
            int iconX = startX + i * (iconSize + iconSpacing);

            g2d.setColor(Constants.PACMAN_COLOR);
            int mouthAngle = 45;
            g2d.fillArc(iconX, startY, iconSize, iconSize,
                    mouthAngle, 360 - mouthAngle * 2);
        }

        g2d.drawString("难度: " + difficultyManager.getDifficultyName(),
                spacing * 3, bottomY);
    }

    private void drawOverlay(Graphics2D g2d) {
        if (gameState == Constants.GameState.PLAYING && dotsEatenThisLevel == 0) {
            drawCenteredText(g2d, "准备！", Color.YELLOW);
        } else if (gameState == Constants.GameState.GAME_OVER) {
            drawCenteredText(g2d, "游戏结束", Color.RED);
            drawSubText(g2d, "按空格键返回主菜单", Color.WHITE);
        } else if (gameState == Constants.GameState.LEVEL_COMPLETE) {
            drawCenteredText(g2d, "关卡完成！", Color.GREEN);
            drawSubText(g2d, "按空格键进入下一关", Color.WHITE);
        } else if (gameState == Constants.GameState.PAUSED) {
            drawCenteredText(g2d, "暂停", Color.YELLOW);
            drawSubText(g2d, "按ESC返回主菜单", Color.WHITE);
        }
    }

    private void drawCenteredText(Graphics2D g2d, String text, Color color) {
        g2d.setColor(color);
        g2d.setFont(loadChineseFont(Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (Constants.PANEL_WIDTH - fm.stringWidth(text)) / 2;
        int y = Constants.PANEL_HEIGHT / 2;
        g2d.drawString(text, x, y);
    }

    private void drawSubText(Graphics2D g2d, String text, Color color) {
        g2d.setColor(color);
        g2d.setFont(loadChineseFont(Font.PLAIN, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (Constants.PANEL_WIDTH - fm.stringWidth(text)) / 2;
        int y = Constants.PANEL_HEIGHT / 2 + 50;
        g2d.drawString(text, x, y);
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

    public int getCurrentScore() {
        return scoreManager.getScore();
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    private void nextLevel() {
        soundManager.stopBackgroundMusic();
        difficultyManager.nextLevel();

        maze = new Maze();
        pacman.resetPosition(14 * Constants.TILE_SIZE, 23 * Constants.TILE_SIZE);

        ghosts.clear();
        initializeGhosts();

        dotsEatenThisLevel = 0;
        totalDotsEaten = 0;
        ghostsEatenThisCombo = 0;
        fruitSpawnCount = 0;
        gameTime = 0;
        fruitsEaten = 0;
        currentFruit = null;
        deathAnimation = null;
        particles.clear();
        floatingTexts.clear();

        gameState = Constants.GameState.PLAYING;
        lastModeSwitch = System.currentTimeMillis();
        scatterMode = true;

        soundManager.playBackgroundMusic();
    }

    public void startNewGame() {
        scoreManager.resetScore();
        difficultyManager.reset();
        initializeGame();
    }

    public void startLevel(int level) {
        scoreManager.resetScore();
        difficultyManager.setLevel(level);
        initializeGame();
    }
}
