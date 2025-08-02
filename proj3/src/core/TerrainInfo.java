package core;

import java.awt.*;


public abstract class TerrainInfo {
    private final int height;
    private final int width;
    private Point location;


    public TerrainInfo(int height, int width, int x, int y) {
        this.height = height;
        this.width = width;
        this.location = new Point(x, y);
    }


    public TerrainInfo(int height, int width, Point location) {
        this.height = height;
        this.width = width;
        this.location = location;
    }

    /** Return the height of the Terrain */
    public int getHeight() {
        return height;
    }


    /** Return the width of the Terrain */
    public int getWidth() {
        return width;
    }


    /** Return a copy of the location. The location represent the central point */
    public Point getLocation() {
        return new Point(location);
    }


    /** Check whether generated terrain will not exceed the window */
    public static boolean withinBounds(int x, int y, int width, int height) {
        return x - width / 2 > 0 && y - width / 2 > 0 && x + width / 2 < World.WINDOW_WIDTH && y + height / 2 < World.WORLD_HEIGHT;
    }


}
