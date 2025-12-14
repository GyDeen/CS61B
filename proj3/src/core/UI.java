package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static core.Config.*;
import static java.awt.Font.PLAIN;

public class UI {
    public static class Setting {
        private int settingX = SETTING_WIDTH / 2;
        private int settingY = WINDOW_HEIGHT - SETTING_HEIGHT / 2;
        private String settingImage = "src/resources/Icon/icons8-settings-50.png";

        public boolean onSetting(double x, double y) {
            return x >= 0 && x < SETTING_WIDTH
                    && y >= settingY - (double) SETTING_HEIGHT / 2 && y < settingY + SETTING_HEIGHT;
        }


        public void drawSetting() {
            java.io.File f = new java.io.File("src/resources/Icon/icons8-settings-50.png");
            // System.out.println("settings.png exists = " + f.getAbsolutePath() + " -> " + f.exists());
            StdDraw.picture(settingX, settingY, settingImage);
        }
    }

    public static final int TOP_UI = 4;
    public static final int BOTTOM_UI = 2;
    public static final int SETTING_HEIGHT = 4;
    public static final int SETTING_WIDTH = 4;


    public static final String TITLE_FONT_PATH = "src/resources/font/Rich Eatin'.otf";
    public static final String PROMPT_FONT_PATH = "src/resources/font/liera-sans/LieraSans-Regular.ttf";
    public static final String GAME_UI_FONT = "src/resources/cabal-font/Cabal-w5j3.ttf";


    /**
     * Loads a font from a .ttf file at a given size.
     */
    public static Font loadFont(String fontPath, float sizePt) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
            return font.deriveFont(sizePt);
        } catch (IOException | FontFormatException e) {
            System.err.println("Could not load font: " + fontPath);
            e.printStackTrace();
            // fallback to default
            return new Font("Monaco", Font.PLAIN, (int) sizePt);
        }
    }

    public static Font plainGameFont(int sizePt, String fontPath) {
        return loadFont(fontPath, sizePt);
    }

    public static Font boldGameFont(int sizePt, String fontPath) {
        return loadFont(fontPath, sizePt).deriveFont(Font.BOLD);
    }

    public static Font italicGameFont(int sizePt, String fontPath) {
        return loadFont(fontPath, sizePt).deriveFont(Font.ITALIC);
    }


    /** Make the UI position distinguishable from the world background */
    public static void drawUIBackground() {
        double centerX = WINDOW_WIDTH / 2.0;
        double centerY = WINDOW_HEIGHT - (double) TOP_UI / 2;

        StdDraw.setPenColor(40, 40, 40);
        StdDraw.filledRectangle(centerX, centerY, WINDOW_WIDTH / 2.0, TOP_UI / 2.0);
    }


}
