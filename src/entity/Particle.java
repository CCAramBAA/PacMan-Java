package entity;

import java.awt.*;

public class Particle {
    private float x, y;
    private float vx, vy;
    private Color color;
    private int lifetime;
    private int age;
    private float size;

    public Particle(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.vx = (float)(Math.random() * 4 - 2);
        this.vy = (float)(Math.random() * 4 - 2);
        this.lifetime = 30 + (int)(Math.random() * 20);
        this.age = 0;
        this.size = 2 + (float)(Math.random() * 3);
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.1f;
        age++;
    }

    public void draw(Graphics2D g2d) {
        if (isDead()) return;

        float alpha = 1.0f - (float)age / lifetime;
        g2d.setColor(new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int)(255 * alpha)
        ));

        g2d.fillOval((int)x, (int)y, (int)size, (int)size);
    }

    public boolean isDead() {
        return age >= lifetime;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getAge() { return age; }
}
