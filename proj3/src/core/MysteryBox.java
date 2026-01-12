package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.Random;

import static core.Config.*;

public class MysteryBox extends LootBox {
    private String[][] fadeMysteryPath;

    private boolean fading = false;
    private int fadeStep = 0;
    private long nextFadeTimeMs = 0;


    /* Using the top left tile as the position with 2 x 2 size */
    private MysteryBox(MainRoom belongsTo, int x, int y, int width, int height) {
        super(belongsTo, x, y, width, height);
        setImagePath("resources/loot box/mystery box/");
        fadeMysteryPath = new String[4][4];
        String[] alphas = {"100", "70", "40", "15"};

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // j + 2 matches your files: -2.jpg, -3.jpg, -4.jpg, -5.jpg
                fadeMysteryPath[i][j] = getImagePath() + "gift_box_alpha_" + alphas[i] + "-" + (j + 2) + ".jpg";
            }
        }
    }


    public static MysteryBox generateMysteryBox(MainRoom belongsTo, TETile[][] world, Random rand) {
        Point p = findSpawnLocation(belongsTo, rand, world);
        world[p.x][p.y] = Tileset.CELL;
        world[p.x][p.y - 1] = Tileset.CELL;
        world[p.x + 1][p.y] = Tileset.CELL;
        world[p.x + 1][p.y - 1] = Tileset.CELL;
        return new MysteryBox(belongsTo, p.x, p.y, 2, 2);
    }


    /** Handle fading */
    public int update(World world) {
        if (!fading) {StdDraw.picture(getPosition().x + 0.5, getPosition().y + 0.5, fadeMysteryPath[fadeStep], 2, 2); return NOT_FADING;}
        if (world.getElapsedTimeMs() < nextFadeTimeMs) return FADING;

        if (fadeStep < fadeMysteryPath.length) {
            setImagePath(fadeMysteryPath[fadeStep++]);
            nextFadeTimeMs = world.getElapsedTimeMs() + FADE_INTERVAL_MS;
            return FADING;
        }

        return FINISHED;
    }
}
