package core;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.awt.Font.PLAIN;

public class UI {
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


}
