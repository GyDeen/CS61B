package core;

import tileengine.TETile;
import tileengine.TileType;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


public abstract class LootBox extends GameObject {

    /** Loot Box take up width x height tiles. Therefore, it stores its top-left tile as the position */
    public LootBox(MainRoom room, int x, int y,int width, int height) {
        super(x, y, width, height, room);
    }

    public abstract int update(World w);


    /** Finding a valid position for a LootBox*/
    public static Point findSpawnLocation(MainRoom room, Random rand, TETile[][] world) {
        int maxAttempts = 10;

        for (int i = 0; i < maxAttempts; i++) {
            // Get a random passable tile from the room
            Point pos = GameObject.findSpawnLocation(room,2, rand, world);
            if  (pos == null) continue;

            // Check if this point can support a 2x2 box without overlapping
            if (validPos(pos.x, pos.y,2, world)) {
                return pos;
            }
        }

        // Return null for other room allocation try
        return null;
    }
}

