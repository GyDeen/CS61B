package core;

import tileengine.TETile;
import tileengine.TileType;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static core.Config.*;
import static core.Config.FADING;
import static core.Config.FINISHED;


public abstract class LootBox extends GameObject {
    protected boolean fading = false;
    protected int fadeStep = 0;
    protected long nextFadeTime = 0;

    /** Loot Box take up width x height tiles. Therefore, it stores its top-left tile as the position */
    public LootBox(MainRoom room, int x, int y,int width, int height) {
        super(x, y, width, height, room);
    }

    public abstract int update(World w);


    /** Finding a valid position for a LootBox*/
    public static Point findSpawnLocation(MainRoom room, Random rand, TETile[][] world) {
        int maxAttempts = 100;

        for (int i = 0; i < maxAttempts; i++) {
            // Get a random passable tile from the room
            Point pos = GameObject.findSpawnLocation(room,2, rand, world);
            if  (pos == null) continue;

            // Check if this point can support a 2x2 box without overlapping
            if (validPos(pos.x, pos.y,2, 2, world)) {
                return pos;
            }
        }

        // Return null for other room allocation try
        return null;
    }


    public int fadingOrOpening(Long currentElapsedTimeMs) {
        if (!fading) return NOT_FADING;

        // Handle time-based fading logic
        if (currentElapsedTimeMs >= nextFadeTime) {
            if (fadeStep < 3) {
                fadeStep++;
                nextFadeTime = currentElapsedTimeMs + FADE_INTERVAL;
                return FADING;
            }
            return FINISHED;
        }
        return FADING;
    }


    public boolean isFading() {return fading;}
}

