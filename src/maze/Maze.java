package maze;

import util.Constants;
import java.awt.*;

public class Maze {
    private Tile[][] maze;
    private int dotsRemaining;
    private int totalDots;

    private static final int[][] MAZE_LAYOUT = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,3,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,3,1},
            {1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,0,1},
            {1,0,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,0,1,1,1,1,1,2,1,1,2,1,1,1,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,1,1,1,2,1,1,2,1,1,1,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,2,2,2,2,2,2,2,2,2,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,1,1,1,4,4,1,1,1,2,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,1,4,4,4,4,4,4,1,2,1,1,0,1,1,1,1,1,1},
            {2,2,2,2,2,2,0,2,2,2,1,4,4,4,4,4,4,1,2,2,2,0,2,2,2,2,2,2},
            {1,1,1,1,1,1,0,1,1,2,1,4,4,4,4,4,4,1,2,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,2,2,2,2,2,2,2,2,2,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,3,0,0,1,1,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,1,1,0,0,3,1},
            {1,1,1,0,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,0,1,1,1},
            {1,1,1,0,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,0,1,1,1},
            {1,0,0,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public Maze() {
        maze = new Tile[Constants.MAZE_HEIGHT][Constants.MAZE_WIDTH];
        initializeMaze();
    }

    private void initializeMaze() {
        dotsRemaining = 0;
        totalDots = 0;
        for (int row = 0; row < Constants.MAZE_HEIGHT; row++) {
            for (int col = 0; col < Constants.MAZE_WIDTH; col++) {
                switch (MAZE_LAYOUT[row][col]) {
                    case 0:
                        maze[row][col] = Tile.DOT;
                        dotsRemaining++;
                        totalDots++;
                        break;
                    case 1:
                        maze[row][col] = Tile.WALL;
                        break;
                    case 2:
                        maze[row][col] = Tile.EMPTY;
                        break;
                    case 3:
                        maze[row][col] = Tile.POWER_PELLET;
                        dotsRemaining++;
                        totalDots++;
                        break;
                    case 4:
                        maze[row][col] = Tile.DOOR;
                        break;
                }
            }
        }
    }

    public void draw(Graphics2D g2d) {
        for (int row = 0; row < Constants.MAZE_HEIGHT; row++) {
            for (int col = 0; col < Constants.MAZE_WIDTH; col++) {
                int x = col * Constants.TILE_SIZE;
                int y = row * Constants.TILE_SIZE;

                switch (maze[row][col]) {
                    case WALL:
                        g2d.setColor(Constants.WALL_COLOR);
                        g2d.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);
                        break;
                    case DOT:
                        g2d.setColor(Constants.DOT_COLOR);
                        g2d.fillOval(x + Constants.TILE_SIZE / 2 - 2,
                                    y + Constants.TILE_SIZE / 2 - 2, 4, 4);
                        break;
                    case POWER_PELLET:
                        g2d.setColor(Constants.DOT_COLOR);
                        g2d.fillOval(x + Constants.TILE_SIZE / 2 - 6,
                                    y + Constants.TILE_SIZE / 2 - 6, 12, 12);
                        break;
                    case DOOR:
                        g2d.setColor(Color.PINK);
                        g2d.fillRect(x, y + Constants.TILE_SIZE / 2 - 2,
                                   Constants.TILE_SIZE, 4);
                        break;
                }
            }
        }
    }

    public boolean isWall(int row, int col) {
        if (row < 0 || row >= Constants.MAZE_HEIGHT || col < 0 || col >= Constants.MAZE_WIDTH) {
            return true;
        }
        return maze[row][col] == Tile.WALL;
    }

    public boolean isDoor(int row, int col) {
        if (row < 0 || row >= Constants.MAZE_HEIGHT || col < 0 || col >= Constants.MAZE_WIDTH) {
            return false;
        }
        return maze[row][col] == Tile.DOOR;
    }

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= Constants.MAZE_HEIGHT || col < 0 || col >= Constants.MAZE_WIDTH) {
            return Tile.WALL;
        }
        return maze[row][col];
    }

    public void eatDot(int row, int col) {
        if (maze[row][col] == Tile.DOT) {
            maze[row][col] = Tile.EMPTY;
            dotsRemaining--;
        } else if (maze[row][col] == Tile.POWER_PELLET) {
            maze[row][col] = Tile.EMPTY;
            dotsRemaining--;
        }
    }

    public boolean areAllDotsEaten() {
        return dotsRemaining == 0;
    }

    public int getDotsRemaining() {
        return dotsRemaining;
    }

    public int getTotalDots() {
        return totalDots;
    }

    public int getMazeWidth() {
        return Constants.MAZE_WIDTH;
    }

    public int getMazeHeight() {
        return Constants.MAZE_HEIGHT;
    }
}
