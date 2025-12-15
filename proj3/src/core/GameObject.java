package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Random;

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


    /** Constructor for moving object such as PacMan and Ghost */
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


    public int getImageHeight() {
        return imageHeight;
    }


    public void drawImage() {
        StdDraw.picture(position.x, position.y, imagePath, imageWidth, imageHeight);
    }


    public void update() {

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


    public static Point findSpawnLocation(MainRoom room, Random rand, TETile[][] world) {
        int maxAttempt = Config.MAX_ATTEMPT_PIVOT;

        int minX = room.getLeft();
        int minY = room.getBottom();
        int maxX = room.getRight();
        int maxY = room.getTop();

        for (SubRoom s : room.getSubRooms()) {
            minX = Math.min(minX, s.getLeft());
            maxX = Math.max(maxX, s.getRight());
            minY = Math.min(minY, s.getBottom());
            maxY = Math.max(maxY, s.getTop());
        }

        for (int i = 0; i < maxAttempt; i++) {
            int x = rand.nextInt(minX, maxX);
            int y = rand.nextInt(minY, maxY);

            if (room.isInRoom(x, y) && TileType.toType(world[x][y]).isPassable()) {
                return new Point(x, y);
            }
        }

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (room.isInRoom(x, y) && TileType.toType(world[x][y]).isPassable()) {
                    return new Point(x, y);
                }
            }
        }

        return new Point(room.getLocation().x, room.getLocation().y);
    }
}
