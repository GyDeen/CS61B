package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static core.Config.LARGE_COIN;
import static tileengine.Tileset.COIN;
import static tileengine.Tileset.FLOOR;

public class Gold extends GameObject{
    private int worth;
    private String[] coinImagePath;
    private boolean isLarge;

    private Gold(int worth, int x, int y, String imagePath, int imageWidth, int imageHeight, MainRoom belongsTo) {
        super(x, y, imagePath, imageWidth, imageHeight, belongsTo);
        this.worth = worth;
        this.isLarge = (imageWidth > 1);
        String base = "resources/Coin/";
        if (isLarge) {
            coinImagePath = new String[] {
                    base + "coin-1.jpg",
                    base + "coin-2.jpg",
                    base + "coin-3.jpg",
                    base + "coin-4.jpg"
            };
        } else {
            setImagePath(base + "golden-coin-with-clover-icon.png");
        }
    }



    public static Gold goldGenerator(int worthRange, ArrayList<MainRoom> mainRooms, Random random, TETile[][] world) {
        int worth = random.nextInt(worthRange);
        // If it worth more than 50, it will be 2 x 2
        int size = worth > 50 ? 2 : 1;
        int maxAttempt = 10;
        Point p;
        while (maxAttempt-- > 0) {
            MainRoom belongsTo = mainRooms.get(random.nextInt(mainRooms.size()));
            p = findSpawnLocation(belongsTo, size, random, world);
            if (p == null) continue;
            if (size == LARGE_COIN) {
                world[p.x][p.y] = COIN;
                world[p.x][p.y - 1] = COIN;
                world[p.x + 1][p.y] = COIN;
                world[p.x + 1][p.y - 1] = COIN;
            } else {
                world[p.x][p.y] = COIN;
            }

            return new Gold(worth, p.x, p.y, "resources/Coin", size, size, belongsTo);
        }
        return null;
    }


    @Override
    public void drawImage() {
        double x = getPosition().x;
        double y = getPosition().y;


        if (isLarge) {
            StdDraw.picture(x + 0.5, y + 0.5, coinImagePath[0], 1, 1);
            StdDraw.picture(x + 1.5, y + 0.5, coinImagePath[1], 1, 1);
            StdDraw.picture(x + 0.5, y - 0.5, coinImagePath[2], 1, 1);
            StdDraw.picture(x + 1.5, y - 0.5, coinImagePath[3], 1, 1);
        } else {
            // Standard 1x1 rendering centered on the tile
            StdDraw.picture(x + 0.5, y + 0.5, getImagePath(), 1, 1);
        }
    }

    public void update() {

    }
}
