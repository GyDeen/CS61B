package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.Random;

import static core.Config.*;

public class MysteryBox extends LootBox {
    private String[] fadeMysteryPath;

    private boolean fading = false;
    private int fadeStep = 0;
    private long nextFadeTimeMs = 0;


    /* Using the top left tile as the position with 2 x 2 size */
    private MysteryBox(MainRoom belongsTo, int x, int y, int width, int height) {
        super(belongsTo, x, y, width, height);
        setImagePath("resources/loot box/mystery box/");
        fadeMysteryPath = new String[]{
                getImagePath() + "gift_box_alpha_100.jpg",
                getImagePath() + "gift_box_alpha_70.jpg",
                getImagePath() + "gift_box_alpha_40.jpg",
                getImagePath() + "gift_box_alpha_15.jpg",
        };
    }


    public static MysteryBox generateMysteryBox(MainRoom belongsTo, TETile[][] world, Random rand) {
        Point p = findSpawnLocation(belongsTo, rand, world);
        world[p.x][p.y] = Tileset.CELL;
        return new MysteryBox(belongsTo, p.x, p.y, 1, 1);
    }


    /** Handle fading */
    public int update(World world) {
        if (!fading) {StdDraw.picture(getPosition().x + 0.5, getPosition().y + 0.5, fadeMysteryPath[fadeStep], 1, 1); return NOT_FADING;}
        if (world.getElapsedTimeMs() < nextFadeTimeMs) return FADING;

        if (fadeStep < fadeMysteryPath.length) {
            setImagePath(fadeMysteryPath[fadeStep++]);
            nextFadeTimeMs = world.getElapsedTimeMs() + FADE_INTERVAL_MS;
            return FADING;
        }

        return FINISHED;
    }
}
