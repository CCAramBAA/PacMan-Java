package util;

import java.io.*;

public class ScoreManager {
    private int score;
    private int highScore;
    private int ghostEatIndex;
    private int lastExtraLifeThreshold;

    public ScoreManager() {
        this.score = 0;
        this.ghostEatIndex = 0;
        this.highScore = loadHighScore();
        this.lastExtraLifeThreshold = 0;
    }

    public void addDotScore() {
        checkExtraLife();
        score += Constants.DOT_SCORE;
        checkHighScore();
    }

    public void addPowerPelletScore() {
        checkExtraLife();
        score += Constants.POWER_PELLET_SCORE;
        checkHighScore();
    }

    public void addScore(int points) {
        checkExtraLife();
        score += points;
        checkHighScore();
    }

    public int addGhostScore() {
        if (ghostEatIndex >= Constants.GHOST_EAT_SCORES.length) {
            ghostEatIndex = 0;
        }
        int points = Constants.GHOST_EAT_SCORES[ghostEatIndex];
        checkExtraLife();
        score += points;
        ghostEatIndex++;
        checkHighScore();
        return points;
    }

    private void checkExtraLife() {
        int currentThreshold = score / 10000;
        int lastThreshold = lastExtraLifeThreshold / 10000;

        if (currentThreshold > lastThreshold) {
            lastExtraLifeThreshold = currentThreshold * 10000;
        }
    }

    public boolean shouldAwardExtraLife() {
        int currentThreshold = score / 10000;
        int lastThreshold = lastExtraLifeThreshold / 10000;

        if (currentThreshold > lastThreshold) {
            lastExtraLifeThreshold = currentThreshold * 10000;
            return true;
        }
        return false;
    }

    public void resetExtraLifeFlag() {
        // 不需要重置，因为使用阈值比较
    }

    public void resetGhostEatIndex() {
        ghostEatIndex = 0;
    }

    private void checkHighScore() {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }

    private void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("highscore.txt"))) {
            writer.println(highScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadHighScore() {
        File file = new File("highscore.txt");
        if (!file.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    public int getScore() { return score; }
    public int getHighScore() { return highScore; }

    public boolean isNewHighScore(int currentScore) {
        return currentScore > 0 && currentScore >= highScore;
    }

    public void resetScore() {
        score = 0;
        ghostEatIndex = 0;
        lastExtraLifeThreshold = 0;
    }
}
