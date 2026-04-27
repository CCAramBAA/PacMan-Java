 package entity;

import util.Constants;
import util.LevelConfig;

import java.awt.*;

public class Fruit {
    private int x, y;
    private FruitType type;
    private boolean visible;
    private int displayTime;
    private long spawnTime;

    public enum FruitType {
        CHERRY(100, Color.RED, "樱桃"),
        STRAWBERRY(300, new Color(255, 100, 100), "草莓"),
        ORANGE(500, Color.ORANGE, "橙子"),
        APPLE(700, new Color(220, 20, 20), "苹果"),
        MELON(1000, new Color(100, 255, 100), "哈密瓜"),
        GALAXIAN(2000, Color.CYAN, "飞船"),
        BELL(3000, Color.YELLOW, "铃铛"),
        KEY(5000, new Color(255, 215, 0), "金钥匙");

        private final int points;
        private final Color color;
        private final String name;

        FruitType(int points, Color color, String name) {
            this.points = points;
            this.color = color;
            this.name = name;
        }

        public int getPoints() { return points; }
        public Color getColor() { return color; }
        public String getName() { return name; }
    }

    public Fruit(int tileX, int tileY, FruitType type) {
        this.x = tileX * Constants.TILE_SIZE + Constants.TILE_SIZE / 2;
        this.y = tileY * Constants.TILE_SIZE + Constants.TILE_SIZE / 2;
        this.type = type;
        this.visible = false;
        this.displayTime = 10000;
        this.spawnTime = 0;
    }

    public void spawn() {
        this.visible = true;
        this.spawnTime = System.currentTimeMillis();
    }

    public void update() {
        if (visible) {
            long elapsed = System.currentTimeMillis() - spawnTime;
            if (elapsed > displayTime) {
                visible = false;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        if (!visible) return;

        long elapsed = System.currentTimeMillis() - spawnTime;

        if (elapsed < 2000 || elapsed > displayTime - 2000) {
            if ((elapsed / 300) % 2 == 0) {
                return;
            }
        }

        int centerX = x;
        int centerY = y;
        int size = Constants.TILE_SIZE - 4;

        g2d.setColor(type.getColor());

        switch (type) {
            case CHERRY:
                drawCherry(g2d, centerX, centerY, size);
                break;
            case STRAWBERRY:
                drawStrawberry(g2d, centerX, centerY, size);
                break;
            case ORANGE:
                drawOrange(g2d, centerX, centerY, size);
                break;
            case APPLE:
                drawApple(g2d, centerX, centerY, size);
                break;
            case MELON:
                drawMelon(g2d, centerX, centerY, size);
                break;
            case GALAXIAN:
                drawGalaxian(g2d, centerX, centerY, size);
                break;
            case BELL:
                drawBell(g2d, centerX, centerY, size);
                break;
            case KEY:
                drawKey(g2d, centerX, centerY, size);
                break;
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String pointsText = String.valueOf(type.getPoints());
        int pointsX = centerX - fm.stringWidth(pointsText) / 2;
        int pointsY = centerY - size / 2 - 2;
        g2d.drawString(pointsText, pointsX, pointsY);
    }

    private void drawCherry(Graphics2D g2d, int x, int y, int size) {
        int radius = size / 4;
        g2d.fillOval(x - radius - 3, y - radius + 2, radius * 2, radius * 2);
        g2d.fillOval(x + radius - 3, y - radius + 2, radius * 2, radius * 2);
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.GREEN);
        g2d.drawLine(x - 3, y - radius + 2, x, y - size / 2);
        g2d.drawLine(x + 3, y - radius + 2, x, y - size / 2);
    }

    private void drawStrawberry(Graphics2D g2d, int x, int y, int size) {
        int[] xPoints = {x, x - size/3, x - size/4, x, x + size/4, x + size/3};
        int[] yPoints = {y + size/3, y, y - size/4, y - size/3, y - size/4, y};
        g2d.fillPolygon(xPoints, yPoints, 6);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x - 2, y - size/2, 4, 4);
    }

    private void drawOrange(Graphics2D g2d, int x, int y, int size) {
        g2d.fillOval(x - size/3, y - size/3, size * 2/3, size * 2/3);
        g2d.setColor(new Color(255, 200, 0));
        g2d.fillOval(x - 2, y - size/3 - 2, 4, 4);
    }

    private void drawApple(Graphics2D g2d, int x, int y, int size) {
        g2d.fillOval(x - size/3, y - size/4, size * 2/3, size * 2/3);
        g2d.setColor(Color.GREEN);
        g2d.fillOval(x - 1, y - size/2, 3, 3);
    }

    private void drawMelon(Graphics2D g2d, int x, int y, int size) {
        g2d.fillOval(x - size/3, y - size/4, size * 2/3, size * 2/3);
        g2d.setColor(Color.GREEN.darker());
        g2d.setStroke(new BasicStroke(2));
        for (int i = -1; i <= 1; i++) {
            g2d.drawLine(x + i * 4, y - size/4, x + i * 4, y + size/4);
        }
    }

    private void drawGalaxian(Graphics2D g2d, int x, int y, int size) {
        g2d.fillPolygon(
            new int[]{x, x - size/3, x - size/4, x, x + size/4, x + size/3},
            new int[]{y + size/3, y, y - size/3, y - size/4, y - size/3, y},
            6
        );
    }

    private void drawBell(Graphics2D g2d, int x, int y, int size) {
        int[] xPoints = {x - size/4, x + size/4, x + size/3, x, x - size/3};
        int[] yPoints = {y + size/4, y + size/4, y - size/4, y - size/3, y - size/4};
        g2d.fillPolygon(xPoints, yPoints, 5);
        g2d.fillOval(x - 2, y + size/4, 4, 4);
    }

    private void drawKey(Graphics2D g2d, int x, int y, int size) {
        g2d.fillOval(x - size/4, y - size/3, size/2, size/2);
        g2d.fillRect(x - 2, y, 4, size/3);
        g2d.fillRect(x, y + size/6, size/4, 2);
    }

    public Rectangle getBounds() {
        if (!visible) {
            return new Rectangle(0, 0, 0, 0);
        }
        return new Rectangle(x - Constants.TILE_SIZE/2, y - Constants.TILE_SIZE/2,
                           Constants.TILE_SIZE, Constants.TILE_SIZE);
    }

    public boolean isVisible() { return visible; }
    public int getX() { return x; }
    public int getY() { return y; }
    public FruitType getType() { return type; }
    public int getPoints() { return type.getPoints(); }

    public static FruitType getFruitForLevel(int level) {
        LevelConfig.LevelData data = LevelConfig.getLevelData(level);
        String fruitName = data.fruitName;

        switch (fruitName) {
            case "樱桃": return FruitType.CHERRY;
            case "草莓": return FruitType.STRAWBERRY;
            case "橙子": return FruitType.ORANGE;
            case "苹果": return FruitType.APPLE;
            case "哈密瓜": return FruitType.MELON;
            case "飞船": return FruitType.GALAXIAN;
            case "铃铛": return FruitType.BELL;
            case "金钥匙": return FruitType.KEY;
            default: return FruitType.CHERRY;
        }
    }
}
