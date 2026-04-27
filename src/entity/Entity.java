package entity;

import util.Constants;
import java.awt.*;

public abstract class Entity {
    protected float x, y;
    protected int speed;
    protected Constants.Direction currentDirection;
    protected Constants.Direction nextDirection;
    protected int tileX, tileY;

    public Entity(float x, float y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.currentDirection = Constants.Direction.NONE;
        this.nextDirection = Constants.Direction.NONE;
        updateTilePosition();
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2d);

    protected void updateTilePosition() {
        this.tileX = (int) ((x + Constants.TILE_SIZE / 2) / Constants.TILE_SIZE);
        this.tileY = (int) ((y + Constants.TILE_SIZE / 2) / Constants.TILE_SIZE);
    }

    protected boolean canMove(Constants.Direction direction, int[][] maze) {
        int nextTileX = tileX;
        int nextTileY = tileY;

        switch (direction) {
            case UP:
                nextTileY--;
                break;
            case DOWN:
                nextTileY++;
                break;
            case LEFT:
                nextTileX--;
                break;
            case RIGHT:
                nextTileX++;
                break;
        }

        if (nextTileY < 0 || nextTileY >= maze.length) {
            return false;
        }

        if (nextTileX < 0 || nextTileX >= maze[0].length) {
            if (tileY == 14) {
                return true;
            }
            return false;
        }

        return maze[nextTileY][nextTileX] != 1 && maze[nextTileY][nextTileX] != 4;
    }

    protected void move() {
        switch (currentDirection) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
        }

        handleTunnel();
        updateTilePosition();
    }

    private void handleTunnel() {
        if (x < -Constants.TILE_SIZE / 2) {
            x = (Constants.MAZE_WIDTH - 1) * Constants.TILE_SIZE;
            updateTilePosition();
        } else if (x > (Constants.MAZE_WIDTH - 0.5) * Constants.TILE_SIZE) {
            x = 0;
            updateTilePosition();
        }
    }

    protected boolean isCenteredOnTile() {
        float centerX = tileX * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
        float centerY = tileY * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;

        return Math.abs(x + Constants.TILE_SIZE / 2 - centerX) < speed &&
               Math.abs(y + Constants.TILE_SIZE / 2 - centerY) < speed;
    }

    protected void centerOnTile() {
        x = tileX * Constants.TILE_SIZE;
        y = tileY * Constants.TILE_SIZE;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x + 2, (int)y + 2,
                           Constants.TILE_SIZE - 4, Constants.TILE_SIZE - 4);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
    public Constants.Direction getCurrentDirection() { return currentDirection; }
    public void setCurrentDirection(Constants.Direction direction) {
        this.currentDirection = direction;
    }
    public Constants.Direction getNextDirection() { return nextDirection; }
    public void setNextDirection(Constants.Direction direction) {
        this.nextDirection = direction;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getSpeed() {
        return speed;
    }

    protected boolean isInTunnel() {
        return tileY == 14 && (tileX <= 0 || tileX >= Constants.MAZE_WIDTH - 1);
    }
}
