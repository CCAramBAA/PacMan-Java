package animation;

import util.Constants;

import java.awt.*;

public class DeathAnimation extends Animation {
    private int centerX, centerY;
    private int maxRadius;

    public DeathAnimation(int x, int y) {
        super(30, 50);
        this.centerX = x;
        this.centerY = y;
        this.maxRadius = Constants.TILE_SIZE / 2;
    }

    @Override
    public void draw(Graphics2D g2d, int x, int y) {
        if (finished) return;

        float progress = getProgress();

        g2d.setColor(Constants.PACMAN_COLOR);

        int radius = (int)(maxRadius * (1 - progress * 0.3));

        int startAngle = (int)(progress * 360);
        int arcAngle = 360 - (int)(progress * 360);

        if (arcAngle > 0) {
            g2d.fillArc(
                centerX - radius,
                centerY - radius,
                radius * 2,
                radius * 2,
                startAngle,
                arcAngle
            );
        }

        if (progress > 0.7) {
            float alpha = (progress - 0.7f) / 0.3f;
            g2d.setColor(new Color(255, 255, 255, (int)(255 * alpha)));
            g2d.fillOval(centerX - 5, centerY - 5, 10, 10);
        }
    }

    @Override
    protected void onAnimationComplete() {
    }
}
