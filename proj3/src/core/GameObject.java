package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Random;

import static tileengine.TileType.BOX;
import static tileengine.TileType.COIN;

public abstract class GameObject {
    private Point position;
    private String imagePath;
    private int imageWidth;
    private int imageHeight;
    private boolean active = true;
    private MainRoom belongsTo;


    /** Constructor for object that doesn't move */
    public GameObject(int x, int y, int imageWidth, int imageHeight, MainRoom belongsTo) {
        position = new Point(x, y);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.belongsTo = belongsTo;
    }

    public GameObject(Point position, String imagePath, int imageWidth, int imageHeight, MainRoom belongsTo) {
        this.position = new Point(position);
        this.imagePath = imagePath;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.belongsTo = belongsTo;
    }


    /** Constructor for moving object such as PacMan */
    public GameObject(int x, int y, int width, int height) {
        position = new Point(x, y);
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public GameObject(int x, int y, String imagePath, int imageWidth, int imageHeight, MainRoom belongsTo) {
        position = new Point(x, y);
        this.imagePath = imagePath;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.belongsTo = belongsTo;
    }

    public Point getPosition() {
        return position;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setPosition(int x, int y) {
        this.position = new Point(x, y);
    }

    public int getImageWidth() {
        return imageWidth;
    }
    public void setImageWidth(int imageWidth) {this.imageWidth = imageWidth;}

    public int getImageHeight() {
        return imageHeight;
    }
    public void setImageHeight(int imageHeight) {this.imageHeight = imageHeight;}


    public void drawImage() {
        StdDraw.picture(position.x, position.y, imagePath, imageWidth, imageHeight);
    }

    public void setRoom(MainRoom belongsTo) {
        this.belongsTo = belongsTo;
    }

    public MainRoom getRoom() {
        return belongsTo;
    }

    public void destroy() {
        active = false;
    }

    public boolean isActive() { return active;}

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public static Point findSpawnLocation(MainRoom room, int objectSize ,Random rand, TETile[][] world) {
        int maxAttempt = Config.MAX_ATTEMPT_PIVOT;

        int minX = room.getLeft();
        int minY = room.getBottom();
        int maxX = room.getRight();
        int maxY = room.getTop();

        for (SubRoom s : room.getSubRooms()) {
            minX = Math.min(minX, s.getLeft() + 1);
            maxX = Math.max(maxX, s.getRight() - 1);
            minY = Math.min(minY, s.getBottom() + 1);
            maxY = Math.max(maxY, s.getTop() - 1);
        }

        for (int i = 0; i < maxAttempt; i++) {
            int x = rand.nextInt(minX, maxX);
            int y = rand.nextInt(minY, maxY);

            if (room.isInRoom(x, y) && validPos(x, y, objectSize,world)) {
                return new Point(x, y);
            }
        }

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (room.isInRoom(x, y) && validPos(x, y, objectSize, world)) {
                    return new Point(x, y);
                }
            }
        }

        return null;
    }


    public static boolean validPos(int x, int y, int size,TETile[][] world) {
        if (x < 0 || y < 0 || x >= world.length || y >= world[0].length) return false;


        // Checking no other objects around current objects
        for (int i = -2; i <= 3; i++) {
            for (int j = -3; j <= 2; j++) {
                if (x + i < 0 || y + j < 0 || x + i >= world.length || y + j >= world[0].length) return false;
                if (TileType.toType(world[x + i][y + j]) == BOX || TileType.toType(world[x + i][y + j]) == COIN) {
                    return false;
                }
            }
        }

        TETile[] area;
        if (size > 1) { area = new TETile[]{world[x][y], world[x + 1][y], world[x][y - 1], world[x + 1][y - 1]};}
        else {area = new TETile[]{world[x][y]};}


        for (TETile tile : area) {
            TileType type = TileType.toType(tile);
            // Only allow spawning if the tile is passable AND isn't already a BOX
            if (!type.isPassable()) {
                return false;
            }
        }

        return true;
    }
}
