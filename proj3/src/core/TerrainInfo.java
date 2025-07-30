package core;

import java.awt.*;

public abstract class TerrainInfo {
    private int height;
    private int width;
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


}
