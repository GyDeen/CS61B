package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;
import java.util.Random;


public class FinalBox extends LootBox {
    private String[] openFinalBox;

    /* Using the top left tile as the position with 2 x 2 size */
    private FinalBox(MainRoom room, int x, int y, int width, int height) {
        super(room, x, y, width, height);
        setImagePath("resources/loot box/FinalBox/");
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

        StdDraw.picture(p.x + 0.5, p.y + 0.5, openFinalBox[fadeStep], 1, 1);
        return fadingOrOpening(world.getElapsedTimeMs());
    }


    public void startOpening() {fading = true;}
}
