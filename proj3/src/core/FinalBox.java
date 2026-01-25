package core;

import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Random;

import static core.Config.WIN;

public class FinalBox extends LootBox {
    private String[] openFinalBox;
    private int openStep = 0;
    private boolean opening = false;

    /* Using the top left tile as the position with 2 x 2 size */
    private FinalBox(MainRoom room, int x, int y, int width, int height) {
        super(room, x, y, width, height);
        setImagePath("resources/loot box/Final Box");
        openFinalBox = new String[4];
        for (int i = 0; i < openFinalBox.length; i++) {
            if (i == 0) openFinalBox[i] = getImagePath() + "RPG Chests.jpg";
            else openFinalBox[i] = getImagePath() + "RPG Chests" + "-" + (i + 1) + ".jpg";
        }
    }


    /** Generator for final box */
    public static FinalBox generateFinalBox(MainRoom initialRoom, Random rand, TETile[][] world) {
        Point p = findSpawnLocation(initialRoom, rand, world);
        assert p != null;
        return new FinalBox(initialRoom, p.x, p.y, 1, 1);
    }

    public int update(World world) {
        Point p = getPosition();

    }


    public void startOpening() {opening = true;}
}
