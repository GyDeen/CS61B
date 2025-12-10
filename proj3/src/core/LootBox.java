package core;

import tileengine.TETile;
import tileengine.TileType;

import javax.swing.*;


public abstract class LootBox extends GameObject {
    private MainRoom belongsTo;

    /** Loot Box take up 2 x 2 tiles. Therefore, it stores its top-left tile as the position */
    public LootBox(MainRoom room, int x, int y,int width, int height) {
        super(x, y, width, height);
        belongsTo = room;
    }


    /** Check current position is valid position for the loot box */
    public static boolean validPos(int x, int y, TETile[][] world) {
        if (x < 0 || y < 0 || x >= world.length || y >= world[0].length) return false;

        return TileType.toType(world[x][y]).isPassable() && TileType.toType(world[x + 1][y]).isPassable()
                && TileType.toType(world[x][y - 1]).isPassable() && TileType.toType(world[x + 1][y - 1]).isPassable();
    }


    public abstract int update(World w);
}

