package entity;

import util.Constants;
import java.awt.*;

public class Pacman extends Entity {
    private int mouthAngle;
    private int mouthSpeed;
    private boolean mouthOpening;
    private int lives;
    private boolean invincible;
    private long invincibleStartTime;
    private int baseSpeed;

    public Pacman(float x, float y) {
        super(x, y, Constants.PACMAN_SPEED);
        this.lives = Constants.INITIAL_LIVES;
        this.mouthAngle = 45;
        this.mouthSpeed = 8;
        this.mouthOpening = true;
        this.invincible = false;
        this.invincibleStartTime = 0;
        this.baseSpeed = Constants.PACMAN_SPEED;
    }

    @Override
    public void update() {
        animateMouth();
        updateSpeedForTunnel();
    }

    private void updateSpeedForTunnel() {
        if (isInTunnel()) {
            speed = baseSpeed;
        }
    }

    public void updateMovement(int[][] maze) {
        if (isCenteredOnTile()) {
            centerOnTile();

            if (nextDirection != Constants.Direction.NONE &&
                canMove(nextDirection, maze)) {
                currentDirection = nextDirection;
            }

            if (!canMove(currentDirection, maze)) {
                currentDirection = Constants.Direction.NONE;
            }
        }

        if (currentDirection != Constants.Direction.NONE) {
            move();
        }
    }

    private void animateMouth() {
        if (mouthOpening) {
            mouthAngle += mouthSpeed;
            if (mouthAngle >= 45) {
                mouthOpening = false;
            }
        } else {
            mouthAngle -= mouthSpeed;
            if (mouthAngle <= 0) {
                mouthOpening = true;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isInvincible()) {
            long elapsed = System.currentTimeMillis() - invincibleStartTime;
            if ((elapsed / 100) % 2 == 0) {
                return;
            }
        }

        g2d.setColor(Constants.PACMAN_COLOR);

        int centerX = (int)(x + Constants.TILE_SIZE / 2);
        int centerY = (int)(y + Constants.TILE_SIZE / 2);
        int radius = Constants.TILE_SIZE / 2 - 2;

        int startAngle = getStartAngle();

        g2d.fillArc(centerX - radius, centerY - radius,
                   radius * 2, radius * 2,
                   startAngle + mouthAngle, 360 - mouthAngle * 2);
    }

    private int getStartAngle() {
        switch (currentDirection) {
            case RIGHT:
                return 0;
            case DOWN:
                return 270;
            case LEFT:
                return 180;
            case UP:
                return 90;
            case NONE:
                return 0;
            default:
                return 0;
        }
    }

    public void loseLife() {
        lives--;
    }

    public void gainLife() {
        lives++;
    }

    public int getLives() {
        return lives;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
        if (invincible) {
            this.invincibleStartTime = System.currentTimeMillis();
        }
    }

    public boolean isInvincible() {
        if (invincible) {
            long elapsed = System.currentTimeMillis() - invincibleStartTime;
            if (elapsed >= Constants.PACMAN_INVINCIBLE_TIME) {
                invincible = false;
            }
        }
        return invincible;
    }

    public void resetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.currentDirection = Constants.Direction.NONE;
        this.nextDirection = Constants.Direction.NONE;
        updateTilePosition();
    }

    public void setBaseSpeed(int speed) {
        this.baseSpeed = speed;
        this.speed = speed;
    }
}
