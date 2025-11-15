package core;


import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

import static core.Config.WINDOW_HEIGHT;
import static core.Config.WINDOW_WIDTH;
import static core.UI.*;

public abstract class NonGamingPage {
    private static Font titleFont = plainGameFont(Config.TITLE_SIZE, TITLE_FONT_PATH);
    private Font promptFont = plainGameFont(Config.PROMPT_SIZE, PROMPT_FONT_PATH);


    public void draw() {
        StdDraw.setCanvasSize(WINDOW_WIDTH * Config.TILE_SIZE, WINDOW_HEIGHT * Config.TILE_SIZE);
        StdDraw.setXscale(0, WINDOW_WIDTH);
        StdDraw.setYscale(0, WINDOW_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
    }

    /** The default title font */
    public Font getDefaultTitleFont() {return titleFont;}


    /** The default prompt font */
    public Font getDefaultPromptFont() {return promptFont;}
}
