package core;

import tileengine.TETile;
import tileengine.TileType;

import javax.swing.*;

import static core.Config.FINAL_BOX;
import static core.Config.MYSTERY_BOX;

public class LootBox extends GameObject {
    private MainRoom belongsTo;
    private int boxType;

    /** Loot Box take up 2 x 2 tiles. Therefore, it stores its top-left tile as the position */
    public LootBox(MainRoom room, int x, int y,int width, int height, int boxType) {
        super(x, y, width, height);
        belongsTo = room;
        if (boxType == FINAL_BOX) {setImagePath("resources/loot box/Final Box"); boxType = FINAL_BOX;}
        if (boxType == MYSTERY_BOX) {setImagePath("resources/loot box/mystery box/vecteezy_pixel-art-illustration-gift-box-pixelated-party-gift_32858111.jpg"); boxType = MYSTERY_BOX;}
    }


    /** Check current position is valid position for the loot box */
    public static boolean validPos(int x, int y, TETile[][] world) {
        if (x < 0 || y < 0 || x >= world.length || y >= world[0].length) return false;

        return TileType.toType(world[x][y]).isPassable() && TileType.toType(world[x + 1][y]).isPassable()
                && TileType.toType(world[x][y - 1]).isPassable() && TileType.toType(world[x + 1][y - 1]).isPassable();
    }


    public void disappear(World w) {
        if (boxType == MYSTERY_BOX) {

        }

        if  (boxType == FINAL_BOX) {
            w
        }
    }
}

