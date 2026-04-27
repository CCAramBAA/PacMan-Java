package ai;

import entity.Ghost;
import entity.Pacman;
import util.Constants;
import java.util.*;

public class GhostAI {
    private Ghost ghost;
    private int targetX, targetY;
    private Constants.Direction nextMove;

    public GhostAI(Ghost ghost) {
        this.ghost = ghost;
        this.targetX = 0;
        this.targetY = 0;
        this.nextMove = Constants.Direction.NONE;
    }

    public void calculateTarget(Pacman pacman, Ghost blinky, Constants.GhostMode mode) {
        if (mode == Constants.GhostMode.SCATTER) {
            setScatterTarget();
        } else {
            setChaseTarget(pacman, blinky);
        }
    }

    private void setScatterTarget() {
        switch (ghost.getName()) {
            case "Blinky":
                targetX = 25;
                targetY = 0;
                break;
            case "Pinky":
                targetX = 2;
                targetY = 0;
                break;
            case "Inky":
                targetX = 27;
                targetY = 30;
                break;
            case "Clyde":
                targetX = 0;
                targetY = 30;
                break;
        }
    }

    private void setChaseTarget(Pacman pacman, Ghost blinky) {
        int pacmanTileX = pacman.getTileX();
        int pacmanTileY = pacman.getTileY();
        Constants.Direction pacmanDir = pacman.getCurrentDirection();

        switch (ghost.getName()) {
            case "Blinky":
                targetX = pacmanTileX;
                targetY = pacmanTileY;
                break;

            case "Pinky":
                targetX = pacmanTileX;
                targetY = pacmanTileY;
                for (int i = 0; i < 4; i++) {
                    switch (pacmanDir) {
                        case UP: targetY--; break;
                        case DOWN: targetY++; break;
                        case LEFT: targetX--; break;
                        case RIGHT: targetX++; break;
                        default: break;
                    }
                }
                break;

            case "Inky":
                int blinkyX = blinky.getTileX();
                int blinkyY = blinky.getTileY();
                int pivotX = pacmanTileX;
                int pivotY = pacmanTileY;

                for (int i = 0; i < 2; i++) {
                    switch (pacmanDir) {
                        case UP: pivotY--; break;
                        case DOWN: pivotY++; break;
                        case LEFT: pivotX--; break;
                        case RIGHT: pivotX++; break;
                        default: break;
                    }
                }

                targetX = blinkyX + (pivotX - blinkyX) * 2;
                targetY = blinkyY + (pivotY - blinkyY) * 2;

                targetX = Math.max(0, Math.min(targetX, Constants.MAZE_WIDTH - 1));
                targetY = Math.max(0, Math.min(targetY, Constants.MAZE_HEIGHT - 1));
                break;

            case "Clyde":
                int distance = Math.abs(ghost.getTileX() - pacmanTileX) +
                              Math.abs(ghost.getTileY() - pacmanTileY);
                if (distance > 8) {
                    targetX = pacmanTileX;
                    targetY = pacmanTileY;
                } else {
                    targetX = 0;
                    targetY = 30;
                }
                break;
        }
    }

    public void findPath(int[][] maze) {
        List<Constants.Direction> validDirections = getValidDirections(maze);

        if (validDirections.isEmpty()) {
            nextMove = Constants.Direction.NONE;
            return;
        }

        Constants.Direction bestDirection = validDirections.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (Constants.Direction dir : validDirections) {
            int newX = ghost.getTileX();
            int newY = ghost.getTileY();

            switch (dir) {
                case UP: newY--; break;
                case DOWN: newY++; break;
                case LEFT: newX--; break;
                case RIGHT: newX++; break;
            }

            int distance = Math.abs(newX - targetX) + Math.abs(newY - targetY);

            if (distance < minDistance) {
                minDistance = distance;
                bestDirection = dir;
            }
        }

        nextMove = bestDirection;
    }

    public void moveRandomly(int[][] maze) {
        List<Constants.Direction> validDirections = getValidDirections(maze);

        if (!validDirections.isEmpty()) {
            nextMove = validDirections.get(new Random().nextInt(validDirections.size()));
        }
    }

    public void moveToTarget(int[][] maze, int targetX, int targetY) {
        List<Constants.Direction> validDirections = getValidDirections(maze);

        if (validDirections.isEmpty()) {
            nextMove = Constants.Direction.NONE;
            return;
        }

        Constants.Direction bestDirection = validDirections.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (Constants.Direction dir : validDirections) {
            int newX = ghost.getTileX();
            int newY = ghost.getTileY();

            switch (dir) {
                case UP: newY--; break;
                case DOWN: newY++; break;
                case LEFT: newX--; break;
                case RIGHT: newX++; break;
            }

            int distance = Math.abs(newX - targetX) + Math.abs(newY - targetY);

            if (distance < minDistance) {
                minDistance = distance;
                bestDirection = dir;
            }
        }

        nextMove = bestDirection;
    }

    private List<Constants.Direction> getValidDirections(int[][] maze) {
        List<Constants.Direction> validDirections = new ArrayList<>();
        int currentX = ghost.getTileX();
        int currentY = ghost.getTileY();

        Constants.Direction[] allDirections = {
            Constants.Direction.UP, Constants.Direction.DOWN,
            Constants.Direction.LEFT, Constants.Direction.RIGHT
        };

        for (Constants.Direction dir : allDirections) {
            if (isValidDirection(dir, maze, currentX, currentY)) {
                validDirections.add(dir);
            }
        }

        return validDirections;
    }

    private boolean isValidDirection(Constants.Direction dir, int[][] maze, int x, int y) {
        Constants.Direction opposite = getOppositeDirection(ghost.getCurrentDirection());
        if (dir == opposite) {
            return false;
        }

        int newX = x;
        int newY = y;

        switch (dir) {
            case UP: newY--; break;
            case DOWN: newY++; break;
            case LEFT: newX--; break;
            case RIGHT: newX++; break;
        }

        if (newY < 0 || newY >= maze.length || newX < 0 || newX >= maze[0].length) {
            return false;
        }

        return maze[newY][newX] != 1;
    }

    public void executeMove() {
        if (nextMove != Constants.Direction.NONE) {
            ghost.setCurrentDirection(nextMove);
        }
    }

    private Constants.Direction getOppositeDirection(Constants.Direction dir) {
        switch (dir) {
            case UP: return Constants.Direction.DOWN;
            case DOWN: return Constants.Direction.UP;
            case LEFT: return Constants.Direction.RIGHT;
            case RIGHT: return Constants.Direction.LEFT;
            default: return dir;
        }
    }
}
