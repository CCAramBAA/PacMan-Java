package util;

import java.io.*;
import java.util.*;

public class AchievementManager {
    private Map<String, Achievement> achievements;
    private List<Achievement> unlockedAchievements;

    public AchievementManager() {
        achievements = new LinkedHashMap<>();
        unlockedAchievements = new ArrayList<>();
        initializeAchievements();
        loadProgress();
    }

    private void initializeAchievements() {
        addAchievement("first_blood", "第一滴血", "吃掉第一个幽灵");
        addAchievement("ghost_combo", "连击大师", "一回合吃掉4个幽灵");
        addAchievement("speed_runner", "速度跑者", "60秒内完成第一关");
        addAchievement("fruit_lover", "水果爱好者", "吃掉10个水果");
        addAchievement("pac_master", "吃豆大师", "总分达到10000分");
        addAchievement("survivor", "幸存者", "到达第5关");
        addAchievement("legend", "传奇", "到达第10关");
    }

    private void addAchievement(String id, String name, String description) {
        achievements.put(id, new Achievement(id, name, description));
    }

    public void checkAchievements(GameStats stats, int ghostsEatenCombo) {
        if (stats.getGhostsEaten() >= 1) {
            unlockAchievement("first_blood");
        }

        if (ghostsEatenCombo >= 4) {
            unlockAchievement("ghost_combo");
        }

        if (stats.getFruitsEaten() >= 10) {
            unlockAchievement("fruit_lover");
        }

        if (stats.getTotalScore() >= 10000) {
            unlockAchievement("pac_master");
        }

        if (stats.getLevelReached() >= 5) {
            unlockAchievement("survivor");
        }

        if (stats.getLevelReached() >= 10) {
            unlockAchievement("legend");
        }
    }

    public void unlockAchievement(String id) {
        Achievement achievement = achievements.get(id);
        if (achievement != null && !achievement.isUnlocked()) {
            achievement.unlock();
            unlockedAchievements.add(achievement);
            saveProgress();
            System.out.println("成就解锁: " + achievement.getName());
        }
    }

    private void saveProgress() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("achievements.dat"))) {
            for (Achievement achievement : achievements.values()) {
                if (achievement.isUnlocked()) {
                    writer.println(achievement.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProgress() {
        File file = new File("achievements.dat");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Achievement achievement = achievements.get(line.trim());
                if (achievement != null) {
                    achievement.unlock();
                    unlockedAchievements.add(achievement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }

    public List<Achievement> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public int getUnlockedCount() {
        return unlockedAchievements.size();
    }

    public int getTotalCount() {
        return achievements.size();
    }
}