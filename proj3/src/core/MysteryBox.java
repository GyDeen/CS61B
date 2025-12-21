package core;

import tileengine.TETile;

import java.awt.*;
import java.util.Random;

import static core.Config.*;

public class MysteryBox extends LootBox {
    private String[] fadeMysteryPath = {
            getImagePath() + "gift_box_alpha_100.png",
            getImagePath() + "gift_box_alpha_70.png",
            getImagePath() + "gift_box_alpha_40.png",
            getImagePath() + "gift_box_alpha_15.png",
    };

    private boolean fading = false;
    private int fadeStep = 0;
    private long nextFadeTimeMs = 0;


    /* Using the top left tile as the position with 2 x 2 size */
    private MysteryBox(MainRoom belongsTo, int x, int y, int width, int height) {
        super(belongsTo, x, y, width, height);
        setImagePath("resources/loot box/mystery box/");
    }


    public static MysteryBox generateMysteryBox(MainRoom belongsTo, TETile[][] world, Random rand) {
        Point p = findSpawnLocation(belongsTo, rand, world);
        return new MysteryBox(belongsTo, p.x, p.y, 2, 2);
    }


    /** Handle fading */
    public int update(World world) {
        if (!fading) return NOT_FADING;
        if (world.getElapsedTimeMs() < nextFadeTimeMs) return FADING;

        if (fadeStep < fadeMysteryPath.length) {
            setImagePath(fadeMysteryPath[fadeStep++]);
            nextFadeTimeMs = world.getElapsedTimeMs() + FADE_INTERVAL_MS;
            return FADING;
        }

        return FINISHED;
    }


    public void startFading() {fading = true;}
}
