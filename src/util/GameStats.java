package util;

public class GameStats {
    private int totalScore;
    private int dotsEaten;
    private int powerPelletsEaten;
    private int ghostsEaten;
    private int fruitsEaten;
    private long gameTime;
    private int levelReached;

    public GameStats() {
        reset();
    }

    public void reset() {
        totalScore = 0;
        dotsEaten = 0;
        powerPelletsEaten = 0;
        ghostsEaten = 0;
        fruitsEaten = 0;
        gameTime = 0;
        levelReached = 1;
    }

    public void addDot() { dotsEaten++; }
    public void addPowerPellet() { powerPelletsEaten++; }
    public void addGhost() { ghostsEaten++; }
    public void addFruit() { fruitsEaten++; }
    public void updateScore(int score) { totalScore = score; }
    public void updateLevel(int level) { levelReached = level; }
    public void addTime(long milliseconds) { gameTime += milliseconds; }

    public int getTotalScore() { return totalScore; }
    public int getDotsEaten() { return dotsEaten; }
    public int getPowerPelletsEaten() { return powerPelletsEaten; }
    public int getGhostsEaten() { return ghostsEaten; }
    public int getFruitsEaten() { return fruitsEaten; }
    public long getGameTime() { return gameTime; }
    public int getLevelReached() { return levelReached; }

    public String getFormattedTime() {
        long seconds = gameTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}