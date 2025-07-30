package core;

import tileengine.TETile;

import java.awt.*;

public class Room extends TerrainInfo{
    TileType type;


    public Room(int height, int width, Point location,  TileType type) {
        super(height, width, location);

        if (type.passable) {
            this.type = type;
        }  else {
            throw new IllegalArgumentException("Invalid tile type");
        }
    }


    public Room(int height, int width, int x, int y, TileType type) {
        super(height, width, x, y);

        if (type.passable) {
            this.type = type;
        }  else {
            throw new IllegalArgumentException("Invalid tile type");
        }
    }


}
