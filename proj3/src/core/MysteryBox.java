package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
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


    public static MysteryBox generateMysteryBox(ArrayList<MainRoom> mainRooms, TETile[][] world, Random rand) {
        int maxAttempt = MAX_ATTEMPT_PIVOT;
        while (maxAttempt-- > 0) {
            MainRoom belongsTo = mainRooms.get(rand.nextInt(mainRooms.size()));
            Point p = findSpawnLocation(belongsTo, rand, world);
            if (p == null) continue;
            world[p.x][p.y] = Tileset.CELL;
            world[p.x][p.y - 1] = Tileset.CELL;
            world[p.x + 1][p.y] = Tileset.CELL;
            world[p.x + 1][p.y - 1] = Tileset.CELL;
            return new MysteryBox(belongsTo, p.x, p.y, 2, 2);
        }
        
        return null;
    }


    /** Handle fading */
    public int update(World world) {
        double posX = getPosition().x;
        double posY = getPosition().y;

        // Draw all 4 quarters for the current alpha step
        StdDraw.picture(posX + 0.5, posY + 0.5, fadeMysteryPath[fadeStep][0], 1, 1);
        StdDraw.picture(posX + 1.5, posY + 0.5, fadeMysteryPath[fadeStep][1], 1, 1);
        StdDraw.picture(posX + 0.5, posY - 0.5, fadeMysteryPath[fadeStep][2], 1, 1);
        StdDraw.picture(posX + 1.5, posY - 0.5, fadeMysteryPath[fadeStep][3], 1, 1);

        if (!fading) return NOT_FADING;

        // Handle time-based fading logic
        if (world.getElapsedTimeMs() >= nextFadeTimeMs) {
            if (fadeStep < 3) {
                fadeStep++;
                nextFadeTimeMs = world.getElapsedTimeMs() + FADE_INTERVAL_MS;
                return FADING;
            }
            return FINISHED;
        }
        return FADING;
    }


    public void startFading() {fading = true;}


    /** Check whether player is right close to the box*/
    public boolean canInteract(PacMan player) {
        Point pPos = player.getPosition();
        Point bPos = this.getPosition();

        // Check if player is within 1 tile of this 2x2 area
        boolean xInRange = pPos.x >= bPos.x - 1 && pPos.x <= bPos.x + 2;
        boolean yInRange = pPos.y >= bPos.y - 2 && pPos.y <= bPos.y + 1;

        return xInRange && yInRange;
    }
}
