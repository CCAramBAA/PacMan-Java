package util;

public class DifficultyManager {
    private int currentLevel;
    private LevelConfig.LevelData currentLevelData;

    public DifficultyManager() {
        this.currentLevel = 1;
        this.currentLevelData = LevelConfig.getLevelData(1);
    }

    public void nextLevel() {
        if (currentLevel < 20) {
            currentLevel++;
        }
        this.currentLevelData = LevelConfig.getLevelData(currentLevel);
    }

    public void reset() {
        currentLevel = 1;
        this.currentLevelData = LevelConfig.getLevelData(1);
    }

    public void setLevel(int level) {
        this.currentLevel = Math.max(1, Math.min(level, 20));
        this.currentLevelData = LevelConfig.getLevelData(currentLevel);
    }

    public int getGhostSpeed() {
        float baseSpeed = Constants.GHOST_SPEED;
        return (int)(baseSpeed * currentLevelData.ghostSpeedMultiplier);
    }

    public int getPacmanSpeed() {
        float baseSpeed = Constants.PACMAN_SPEED;
        return (int)(baseSpeed * currentLevelData.pacmanSpeedMultiplier);
    }

    public int getFrightenedDuration() {
        return (int)(currentLevelData.frightenedDuration * 1000);
    }

    public int getFrightenedFlashTime() {
        return (int)(currentLevelData.frightenedFlashTime * 1000);
    }

    public int getScatterTime() {
        if (currentLevel <= 3) return (int)Constants.SCATTER_MODE_TIME;
        if (currentLevel <= 6) return (int)(Constants.SCATTER_MODE_TIME - 2000);
        return (int)(Constants.SCATTER_MODE_TIME - 4000);
    }

    public int getChaseTime() {
        if (currentLevel <= 3) return (int)Constants.CHASE_MODE_TIME;
        if (currentLevel <= 6) return (int)(Constants.CHASE_MODE_TIME + 5000);
        return (int)(Constants.CHASE_MODE_TIME + 10000);
    }

    public float getGhostHouseTime() {
        float baseTime = 4000f;
        float reduction = (currentLevel - 1) * 300f;
        return Math.max(1000f, baseTime - reduction);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public String getDifficultyName() {
        if (currentLevel <= 5) return "简单";
        if (currentLevel <= 10) return "中等";
        if (currentLevel <= 15) return "困难";
        return "地狱";
    }

    public String getFruitName() {
        return currentLevelData.fruitName;
    }

    public int getFruitScore() {
        return currentLevelData.fruitScore;
    }

    public float getBlinkySpeedMultiplier(int dotsRemaining, int totalDots) {
        float remainingRatio = (float)dotsRemaining / totalDots;

        if (remainingRatio < 0.2f) {
            return 1.4f;
        } else if (remainingRatio < 0.4f) {
            return 1.2f;
        } else if (remainingRatio < 0.6f) {
            return 1.1f;
        }

        return 1.0f;
    }
}
