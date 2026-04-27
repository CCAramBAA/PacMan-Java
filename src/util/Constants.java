package util;

import java.awt.*;

public class Constants {
    // 游戏窗口设置
    public static final int TILE_SIZE = 24;
    public static final int MAZE_WIDTH = 28;
    public static final int MAZE_HEIGHT = 31;
    public static final int PANEL_WIDTH = MAZE_WIDTH * TILE_SIZE;
    public static final int PANEL_HEIGHT = MAZE_HEIGHT * TILE_SIZE + 50;

    // 菜单设置
    public static final int MENU_WIDTH = MAZE_WIDTH * TILE_SIZE;
    public static final int MENU_HEIGHT = MAZE_HEIGHT * TILE_SIZE + 50;

    // 颜色定义
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color WALL_COLOR = new Color(33, 33, 222);
    public static final Color DOT_COLOR = Color.WHITE;
    public static final Color PACMAN_COLOR = Color.YELLOW;

    // 幽灵颜色（原版色值）
    public static final Color BLINKY_COLOR = new Color(255, 0, 0);
    public static final Color PINKY_COLOR = new Color(211, 147, 151);
    public static final Color INKY_COLOR = new Color(1, 184, 192);
    public static final Color CLYDE_COLOR = new Color(211, 132, 8);
    public static final Color FRIGHTENED_COLOR = new Color(0, 0, 139);
    public static final Color FRIGHTENED_FLASH_COLOR = Color.WHITE;

    // 菜单颜色
    public static final Color MENU_TITLE_COLOR = Color.YELLOW;
    public static final Color MENU_TEXT_COLOR = Color.WHITE;
    public static final Color MENU_BUTTON_COLOR = new Color(255, 255, 0);
    public static final Color MENU_BUTTON_HOVER = new Color(255, 255, 100);

    // 速度设置（基础值，实际由DifficultyManager调整）
    public static final int PACMAN_SPEED = 10;
    public static final int GHOST_SPEED = 3;
    public static final int FRIGHTENED_GHOST_SPEED = 2;
    public static final int EATEN_GHOST_SPEED = 6;

    // 分数设置
    public static final int DOT_SCORE = 100000;
    public static final int POWER_PELLET_SCORE = 50;
    public static final int[] GHOST_EAT_SCORES = {200, 400, 800, 1600};

    // 生命设置
    public static final int INITIAL_LIVES = 1;

    // 游戏状态
    public enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER, LEVEL_COMPLETE
    }

    // 方向枚举
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    // 幽灵模式枚举
    public enum GhostMode {
        CHASE,
        SCATTER,
        FRIGHTENED,
        EATEN
    }

    // 时间设置（毫秒）- 第1关基准值
    public static final long SCATTER_MODE_TIME = 7000;
    public static final long CHASE_MODE_TIME = 20000;
    public static final long FRIGHTENED_TIME = 7000;
    public static final long FRIGHTENED_FLASH_TIME = 2000;

    // FPS设置
    public static final int FPS = 60;

    // 吃豆人无敌时间（毫秒）
    public static final long PACMAN_INVINCIBLE_TIME = 2000;
}
