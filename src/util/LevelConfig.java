package util;

public class LevelConfig {

    public static class LevelData {
        public int level;
        public float ghostSpeedMultiplier;
        public float pacmanSpeedMultiplier;
        public float frightenedDuration;
        public float frightenedFlashTime;
        public String fruitName;
        public int fruitScore;

        public LevelData(int level, float ghostSpeed, float pacmanSpeed,
                        float frightenedDur, float flashTime,
                        String fruit, int score) {
            this.level = level;
            this.ghostSpeedMultiplier = ghostSpeed;
            this.pacmanSpeedMultiplier = pacmanSpeed;
            this.frightenedDuration = frightenedDur;
            this.frightenedFlashTime = flashTime;
            this.fruitName = fruit;
            this.fruitScore = score;
        }
    }

    private static final LevelData[] LEVELS = {
        new LevelData(1, 0.75f, 1.00f, 7.0f, 2.0f, "樱桃", 100),
        new LevelData(2, 0.75f, 1.00f, 7.0f, 2.0f, "草莓", 300),
        new LevelData(3, 0.75f, 1.00f, 6.5f, 2.0f, "橙子", 500),
        new LevelData(4, 0.80f, 1.00f, 6.5f, 2.0f, "橙子", 500),
        new LevelData(5, 0.80f, 1.00f, 6.0f, 1.8f, "苹果", 700),
        new LevelData(6, 0.85f, 1.00f, 5.5f, 1.8f, "苹果", 700),
        new LevelData(7, 0.85f, 1.00f, 5.0f, 1.8f, "哈密瓜", 1000),
        new LevelData(8, 0.90f, 1.00f, 4.5f, 1.5f, "哈密瓜", 1000),
        new LevelData(9, 0.90f, 1.00f, 4.0f, 1.5f, "飞船", 2000),
        new LevelData(10, 0.95f, 1.00f, 3.5f, 1.5f, "飞船", 2000),
        new LevelData(11, 0.95f, 0.95f, 3.0f, 1.2f, "铃铛", 3000),
        new LevelData(12, 1.00f, 0.95f, 2.8f, 1.2f, "铃铛", 3000),
        new LevelData(13, 1.00f, 0.95f, 2.5f, 1.2f, "金钥匙", 5000),
        new LevelData(14, 1.05f, 0.95f, 2.2f, 1.0f, "金钥匙", 5000),
        new LevelData(15, 1.05f, 0.90f, 2.0f, 1.0f, "金钥匙", 5000),
        new LevelData(16, 1.10f, 0.90f, 1.8f, 1.0f, "金钥匙", 5000),
        new LevelData(17, 1.10f, 0.90f, 1.5f, 0.8f, "金钥匙", 5000),
        new LevelData(18, 1.15f, 0.90f, 1.2f, 0.8f, "金钥匙", 5000),
        new LevelData(19, 1.15f, 0.85f, 1.0f, 0.8f, "金钥匙", 5000),
        new LevelData(20, 1.20f, 0.85f, 0.8f, 0.5f, "金钥匙", 5000)
    };

    public static LevelData getLevelData(int level) {
        if (level < 1 || level > 20) {
            return LEVELS[19];
        }
        return LEVELS[level - 1];
    }

    public static int getMaxLevel() {
        return 20;
    }
}
