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


    /** Check current position is valid position for the loot box */
    public static boolean validPos(int x, int y, TETile[][] world) {
        if (x < 0 || y < 0 || x >= world.length || y >= world[0].length) return false;

        TETile[] area = { world[x][y], world[x + 1][y], world[x][y - 1], world[x + 1][y - 1] };

        for (TETile tile : area) {
            TileType type = TileType.toType(tile);
            // Only allow spawning if the tile is passable AND isn't already a BOX
            if (!type.isPassable() || type == TileType.BOX) {
                return false;
            }
        }

        return true;
    }


    public abstract int update(World w);


    /** Finding a valid position for a LootBox*/
    public static Point findSpawnLocation(MainRoom room, Random rand, TETile[][] world) {
        int maxAttempts = 200;

        for (int i = 0; i < maxAttempts; i++) {
            // Get a random passable tile from the room
            Point pos = GameObject.findSpawnLocation(room, rand, world);

            // Check if this point can support a 2x2 box without overlapping
            if (validPos(pos.x, pos.y, world)) {
                return pos;
            }
        }

        // Fallback to room center if no valid spot found
        return room.getLocation();
    }
}

