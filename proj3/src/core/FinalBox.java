package core;

import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Random;

import static core.Config.WIN;

public class FinalBox extends LootBox {

    /* Using the top left tile as the position with 2 x 2 size */
    private FinalBox(MainRoom room, int x, int y, int width, int height) {
        super(room, x, y, width, height);
        setImagePath("resources/loot box/Final Box");
    }


    /** Generator for final box */
    public static FinalBox generateFinalBox(MainRoom initialRoom, Random rand, TETile[][] world) {
        Point p = findSpawnLocation(initialRoom, rand, world);
        return new FinalBox(initialRoom, p.x, p.y, 2, 2);
    }

    public int update(World world) {
        return WIN;
    }
}
