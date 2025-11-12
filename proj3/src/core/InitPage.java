package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

import static core.Config.WINDOW_HEIGHT;
import static core.Config.WINDOW_WIDTH;
import static core.UI.*;


public class InitPage {
    private static Font titleFont = plainGameFont(Config.TITLE_SIZE, TITLE_FONT_PATH);
    private Font promptFont = plainGameFont(Config.PROMPT_SIZE, PROMPT_FONT_PATH);
    private String[] prompts = {"New Game", "Load Game", "Exit Game"};

    public void draw() {
        StdDraw.setCanvasSize(WINDOW_WIDTH * Config.TILE_SIZE, WINDOW_HEIGHT * Config.TILE_SIZE);
        StdDraw.setXscale(0, WINDOW_WIDTH);
        StdDraw.setYscale(0, WINDOW_HEIGHT);
        StdDraw.clear(Color.BLACK );
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.setFont(titleFont);
        StdDraw.setPenRadius(10);
        StdDraw.text(45, 30, "GAME");

        StdDraw.setFont(promptFont);
        for (int i = 0; i < prompts.length; i++) {
            StdDraw.text(45, 20 - Config.BUFFER*i, prompts[i]);
        }
    }
}
