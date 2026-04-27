package entity;

import maze.Maze;
import util.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ghost extends Entity {
    private String name;
    private Color originalColor;
    private Constants.GhostMode mode;
    private boolean inHouse;
    private int dotsEatenForRelease;
    private int frightenedTimer;
    private boolean eaten;
    private int scatterTargetX;
    private int scatterTargetY;
    private boolean isExitingHouse;
    private int baseSpeed;
    private int normalSpeed;

    public Ghost(float x, float y, String name, Color color) {
        super(x, y, Constants.GHOST_SPEED);
        this.name = name;
        this.originalColor = color;
        this.mode = Constants.GhostMode.SCATTER;
        this.inHouse = false;
        this.dotsEatenForRelease = 0;
        this.frightenedTimer = 0;
        this.eaten = false;
        this.isExitingHouse = false;
        this.baseSpeed = Constants.GHOST_SPEED;
        this.normalSpeed = Constants.GHOST_SPEED;

        setScatterTarget();
    }

    private void setScatterTarget() {
        switch (name) {
            case "Blinky":
                scatterTargetX = Constants.MAZE_WIDTH - 2;
                scatterTargetY = 0;
                break;
            case "Pinky":
                scatterTargetX = 1;
                scatterTargetY = 0;
                break;
            case "Inky":
                scatterTargetX = Constants.MAZE_WIDTH - 2;
                scatterTargetY = Constants.MAZE_HEIGHT - 1;
                break;
            case "Clyde":
                scatterTargetX = 0;
                scatterTargetY = Constants.MAZE_HEIGHT - 1;
                break;
        }
    }

    @Override
    public void update() {
        if (mode == Constants.GhostMode.FRIGHTENED) {
            frightenedTimer -= 16;
            if (frightenedTimer <= 0) {
                mode = Constants.GhostMode.CHASE;
                speed = normalSpeed;
            }
        }

        if (eaten && !isInGhostHouse()) {
            int distToHouse = Math.abs(tileX - 14) + Math.abs(tileY - 14);
            if (distToHouse <= 1) {
                eaten = false;
                mode = Constants.GhostMode.CHASE;
                speed = normalSpeed;
                inHouse = true;
                x = 14 * Constants.TILE_SIZE;
                y = 14 * Constants.TILE_SIZE;
                updateTilePosition();
                currentDirection = Constants.Direction.NONE;
                nextDirection = Constants.Direction.NONE;
                isExitingHouse = false;
            }
        }

        if (inHouse && !isExitingHouse) {
            return;
        }

        if (name.equals("Blinky") && !eaten && mode != Constants.GhostMode.FRIGHTENED) {
            updateBlinkySpeed();
        }

        updateSpeedForTunnel();
        moveGhost();
    }

    private void updateSpeedForTunnel() {
        if (eaten) {
            speed = Constants.EATEN_GHOST_SPEED;
            return;
        }

        if (isInTunnel()) {
            if (mode == Constants.GhostMode.FRIGHTENED) {
                speed = Constants.FRIGHTENED_GHOST_SPEED / 2;
            } else {
                speed = normalSpeed / 2;
            }
        } else {
            if (mode == Constants.GhostMode.FRIGHTENED) {
                speed = Constants.FRIGHTENED_GHOST_SPEED;
            } else if (mode == Constants.GhostMode.EATEN) {
                speed = Constants.EATEN_GHOST_SPEED;
            } else {
                speed = normalSpeed;
            }
        }
    }

    private void updateBlinkySpeed() {
        Maze maze = null;
        try {
            java.lang.reflect.Field field = getClass().getDeclaredField("maze");
            field.setAccessible(true);
        } catch (Exception e) {
        }
    }


    public void updateBlinkySpeed(int dotsRemaining, int totalDots) {
        if (!name.equals("Blinky") || eaten || mode == Constants.GhostMode.FRIGHTENED) {
            return;
        }

        float remainingRatio = (float)dotsRemaining / totalDots;
        float baseSpeed = Constants.GHOST_SPEED;

        if (remainingRatio < 0.2f) {
            normalSpeed = (int)(baseSpeed * 1.4f);
        } else if (remainingRatio < 0.4f) {
            normalSpeed = (int)(baseSpeed * 1.2f);
        } else if (remainingRatio < 0.6f) {
            normalSpeed = (int)(baseSpeed * 1.1f);
        } else {
            normalSpeed = (int)baseSpeed;
        }

        speed = normalSpeed;
    }


    private void moveGhost() {
        if (isCenteredOnTile()) {
            centerOnTile();

            if (nextDirection != Constants.Direction.NONE) {
                currentDirection = nextDirection;
            }
        }

        if (currentDirection != Constants.Direction.NONE) {
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

    public void updateAI(int[][] mazeData, Pacman pacman, Ghost blinky, int totalDotsEaten) {
        if (mode == Constants.GhostMode.EATEN) {
            moveToTarget(mazeData, 14, 14);
            return;
        }

        if (inHouse && !isExitingHouse) {
            if (shouldRelease(totalDotsEaten)) {
                exitGhostHouse(mazeData);
            }
            return;
        }

        if (mode == Constants.GhostMode.FRIGHTENED) {
            moveRandomly(mazeData);
            return;
        }

        int targetX, targetY;

        if (mode == Constants.GhostMode.SCATTER) {
            targetX = scatterTargetX;
            targetY = scatterTargetY;
        } else {
            targetX = getChaseTargetX(pacman, blinky);
            targetY = getChaseTargetY(pacman, blinky);
        }

        moveToTarget(mazeData, targetX, targetY);
    }

    private boolean shouldRelease(int totalDotsEaten) {
        switch (name) {
            case "Blinky":
                return true;
            case "Pinky":
                return totalDotsEaten >= 48;
            case "Inky":
                return totalDotsEaten >= 30;
            case "Clyde":
                return totalDotsEaten >= 80;
            default:
                return false;
        }
    }

    private int getChaseTargetX(Pacman pacman, Ghost blinky) {
        int pacmanTileX = pacman.getTileX();
        Constants.Direction pacmanDir = pacman.getCurrentDirection();

        switch (name) {
            case "Blinky":
                return pacmanTileX;

            case "Pinky":
                int offset = 4;
                if (pacmanDir == Constants.Direction.UP) {
                    return pacmanTileX - offset;
                } else if (pacmanDir == Constants.Direction.DOWN) {
                    return pacmanTileX;
                } else if (pacmanDir == Constants.Direction.LEFT) {
                    return pacmanTileX - offset;
                } else if (pacmanDir == Constants.Direction.RIGHT) {
                    return pacmanTileX + offset;
                }
                return pacmanTileX;

            case "Inky":
                int offset2 = 2;
                int pivotX;

                if (pacmanDir == Constants.Direction.UP) {
                    pivotX = pacmanTileX - offset2;
                } else if (pacmanDir == Constants.Direction.DOWN) {
                    pivotX = pacmanTileX;
                } else if (pacmanDir == Constants.Direction.LEFT) {
                    pivotX = pacmanTileX - offset2;
                } else if (pacmanDir == Constants.Direction.RIGHT) {
                    pivotX = pacmanTileX + offset2;
                } else {
                    pivotX = pacmanTileX;
                }

                int blinkyX = blinky != null ? blinky.getTileX() : 14;

                int targetX = blinkyX + (pivotX - blinkyX) * 2;
                targetX = Math.max(0, Math.min(targetX, Constants.MAZE_WIDTH - 1));
                return targetX;

            case "Clyde":
                int distance = Math.abs(tileX - pacmanTileX) + Math.abs(tileY - pacman.getTileY());
                if (distance > 8) {
                    return pacmanTileX;
                } else {
                    return scatterTargetX;
                }

            default:
                return pacmanTileX;
        }
    }

    private int getChaseTargetY(Pacman pacman, Ghost blinky) {
        int pacmanTileY = pacman.getTileY();
        Constants.Direction pacmanDir = pacman.getCurrentDirection();

        switch (name) {
            case "Blinky":
                return pacmanTileY;

            case "Pinky":
                int offset = 4;
                if (pacmanDir == Constants.Direction.UP) {
                    return pacmanTileY - offset * 2;
                } else if (pacmanDir == Constants.Direction.DOWN) {
                    return pacmanTileY + offset;
                } else if (pacmanDir == Constants.Direction.LEFT) {
                    return pacmanTileY;
                } else if (pacmanDir == Constants.Direction.RIGHT) {
                    return pacmanTileY;
                }
                return pacmanTileY;

            case "Inky":
                int offset2 = 2;
                int pivotY;

                if (pacmanDir == Constants.Direction.UP) {
                    pivotY = pacmanTileY - offset2;
                } else if (pacmanDir == Constants.Direction.DOWN) {
                    pivotY = pacmanTileY + offset2;
                } else if (pacmanDir == Constants.Direction.LEFT) {
                    pivotY = pacmanTileY;
                } else if (pacmanDir == Constants.Direction.RIGHT) {
                    pivotY = pacmanTileY;
                } else {
                    pivotY = pacmanTileY;
                }

                int blinkyY = blinky != null ? blinky.getTileY() : 11;

                int targetY = blinkyY + (pivotY - blinkyY) * 2;
                targetY = Math.max(0, Math.min(targetY, Constants.MAZE_HEIGHT - 1));
                return targetY;

            case "Clyde":
                int distance = Math.abs(tileX - pacman.getTileX()) + Math.abs(tileY - pacmanTileY);
                if (distance > 8) {
                    return pacmanTileY;
                } else {
                    return scatterTargetY;
                }

            default:
                return pacmanTileY;
        }
    }

    private void moveRandomly(int[][] mazeData) {
        if (!isCenteredOnTile()) {
            return;
        }

        centerOnTile();

        List<Constants.Direction> validDirs = new ArrayList<>();

        for (Constants.Direction dir : Constants.Direction.values()) {
            if (dir == Constants.Direction.NONE) continue;

            if (isOppositeDirection(dir, currentDirection)) {
                continue;
            }

            int newX = tileX;
            int newY = tileY;

            switch (dir) {
                case UP: newY--; break;
                case DOWN: newY++; break;
                case LEFT: newX--; break;
                case RIGHT: newX++; break;
            }

            if (newX < 0) newX = Constants.MAZE_WIDTH - 1;
            if (newX >= Constants.MAZE_WIDTH) newX = 0;

            if (isValidMove(mazeData, newX, newY)) {
                validDirs.add(dir);
            }
        }

        if (!validDirs.isEmpty()) {
            int randomIndex = (int)(Math.random() * validDirs.size());
            nextDirection = validDirs.get(randomIndex);
        }
    }

    private void moveToTarget(int[][] mazeData, int targetX, int targetY) {
        if (!isCenteredOnTile()) {
            return;
        }

        centerOnTile();

        Constants.Direction bestDir = currentDirection;
        int minDistance = Integer.MAX_VALUE;
        boolean foundValid = false;

        for (Constants.Direction dir : Constants.Direction.values()) {
            if (dir == Constants.Direction.NONE) continue;

            if (isOppositeDirection(dir, currentDirection) && currentDirection != Constants.Direction.NONE) {
                continue;
            }

            int newX = tileX;
            int newY = tileY;

            switch (dir) {
                case UP: newY--; break;
                case DOWN: newY++; break;
                case LEFT: newX--; break;
                case RIGHT: newX++; break;
            }

            if (newX < 0) newX = Constants.MAZE_WIDTH - 1;
            if (newX >= Constants.MAZE_WIDTH) newX = 0;

            if (isValidMove(mazeData, newX, newY)) {
                int distance = Math.abs(newX - targetX) + Math.abs(newY - targetY);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestDir = dir;
                    foundValid = true;
                }
            }
        }

        if (foundValid) {
            nextDirection = bestDir;
        }
    }

    private void exitGhostHouse(int[][] mazeData) {
        if (!isCenteredOnTile()) {
            return;
        }

        centerOnTile();

        isExitingHouse = true;

        if (tileY > 11) {
            nextDirection = Constants.Direction.UP;
        } else if (tileY == 11 && tileX == 14) {
            nextDirection = Constants.Direction.LEFT;
        } else if (tileX == 13 && tileY == 11) {
            inHouse = false;
            isExitingHouse = false;
        }
    }

    private boolean isValidMove(int[][] mazeData, int x, int y) {
        if (x < 0 || x >= Constants.MAZE_WIDTH || y < 0 || y >= Constants.MAZE_HEIGHT) {
            return true;
        }

        if (eaten) {
            return mazeData[y][x] != 1;
        }

        return mazeData[y][x] != 1 && mazeData[y][x] != 4;
    }

    private boolean isOppositeDirection(Constants.Direction dir1, Constants.Direction dir2) {
        return (dir1 == Constants.Direction.UP && dir2 == Constants.Direction.DOWN) ||
               (dir1 == Constants.Direction.DOWN && dir2 == Constants.Direction.UP) ||
               (dir1 == Constants.Direction.LEFT && dir2 == Constants.Direction.RIGHT) ||
               (dir1 == Constants.Direction.RIGHT && dir2 == Constants.Direction.LEFT);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (eaten) {
            drawEyes(g2d);
            return;
        }

        Color ghostColor;
        if (mode == Constants.GhostMode.FRIGHTENED) {
            if (frightenedTimer < 2000 &&
                (System.currentTimeMillis() / 200) % 2 == 0) {
                ghostColor = Color.WHITE;
            } else {
                ghostColor = new Color(0, 0, 139);
            }
        } else {
            ghostColor = originalColor;
        }

        int centerX = (int)x + Constants.TILE_SIZE / 2;
        int centerY = (int)y + Constants.TILE_SIZE / 2;
        int radius = Constants.TILE_SIZE / 2 - 2;

        g2d.setColor(ghostColor);

        g2d.fillArc(centerX - radius, centerY - radius,
                   radius * 2, radius * 2, 0, 180);

        g2d.fillRect(centerX - radius, centerY - 2, radius * 2, radius + 2);

        int waveWidth = radius * 2 / 3;
        for (int i = 0; i < 3; i++) {
            int wx = centerX - radius + i * waveWidth;
            g2d.fillArc(wx, centerY + radius - 8, waveWidth, 10, 180, 180);
        }

        if (mode != Constants.GhostMode.FRIGHTENED) {
            drawEyes(g2d);
        } else {
            drawFrightenedFace(g2d, centerX, centerY);
        }
    }

    private void drawEyes(Graphics2D g2d) {
        int eyeOffsetX = 5;
        int eyeOffsetY = -3;
        int eyeWidth = 6;
        int eyeHeight = 7;
        int pupilSize = 3;

        int leftEyeX = (int)x + Constants.TILE_SIZE / 2 - eyeOffsetX;
        int rightEyeX = (int)x + Constants.TILE_SIZE / 2 + eyeOffsetX - eyeWidth;
        int eyeY = (int)y + Constants.TILE_SIZE / 2 + eyeOffsetY;

        g2d.setColor(Color.WHITE);
        g2d.fillOval(leftEyeX, eyeY, eyeWidth, eyeHeight);
        g2d.fillOval(rightEyeX, eyeY, eyeWidth, eyeHeight);

        g2d.setColor(new Color(0, 0, 139));
        int pupilOffsetX = 0;
        int pupilOffsetY = 0;

        switch (currentDirection) {
            case UP: pupilOffsetY = -2; break;
            case DOWN: pupilOffsetY = 2; break;
            case LEFT: pupilOffsetX = -2; break;
            case RIGHT: pupilOffsetX = 2; break;
        }

        g2d.fillOval(leftEyeX + 2 + pupilOffsetX, eyeY + 2 + pupilOffsetY, pupilSize, pupilSize);
        g2d.fillOval(rightEyeX + 2 + pupilOffsetX, eyeY + 2 + pupilOffsetY, pupilSize, pupilSize);
    }

    private void drawFrightenedFace(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(new Color(255, 255, 255));

        int eyeY = centerY - 3;
        g2d.fillOval(centerX - 6, eyeY, 4, 4);
        g2d.fillOval(centerX + 2, eyeY, 4, 4);

        int mouthY = centerY + 4;
        g2d.setStroke(new BasicStroke(2));
        for (int i = -6; i <= 4; i += 3) {
            g2d.drawLine(centerX + i, mouthY, centerX + i + 2, mouthY + 3);
            g2d.drawLine(centerX + i + 2, mouthY + 3, centerX + i + 4, mouthY);
        }
        g2d.setStroke(new BasicStroke(1));
    }

    public void setFrightened(int duration) {
        this.mode = Constants.GhostMode.FRIGHTENED;
        this.frightenedTimer = duration;
        this.normalSpeed = Constants.FRIGHTENED_GHOST_SPEED;
        this.speed = Constants.FRIGHTENED_GHOST_SPEED;
        this.eaten = false;
    }

    public void setEaten() {
        this.eaten = true;
        this.mode = Constants.GhostMode.EATEN;
        this.speed = Constants.EATEN_GHOST_SPEED;
        this.frightenedTimer = 0;
    }

    public void resetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.currentDirection = Constants.Direction.NONE;
        this.nextDirection = Constants.Direction.NONE;
        updateTilePosition();
        this.eaten = false;
        this.mode = Constants.GhostMode.SCATTER;
        this.inHouse = false;
        this.isExitingHouse = false;
        this.normalSpeed = baseSpeed;
        this.speed = baseSpeed;
    }

    public String getName() { return name; }
    public Constants.GhostMode getMode() { return mode; }
    public boolean isEaten() { return eaten; }
    public boolean isInGhostHouse() { return inHouse && !isExitingHouse; }

    public void setMode(Constants.GhostMode mode) {
        if (!eaten && this.mode != Constants.GhostMode.FRIGHTENED) {
            this.mode = mode;
        }
    }

    public void setInHouse(boolean inHouse) {
        this.inHouse = inHouse;
    }

    public void setNormalSpeed(int speed) {
        this.normalSpeed = speed;
        this.baseSpeed = speed;
    }
}
