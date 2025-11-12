package core;

import java.awt.*;

import static java.awt.Font.PLAIN;

public class UI {
    public static final int TOP_UI = 3;
    public static final int BOTTOM_UI = 1;


    public static final String TITLE_FONT_PATH = "src/resources/debrosee-font/Debrosee-ALPnL.ttf";
    public static final String PROMPT_FONT_PATH = "src/resources/freedom-font/Freedom-10eM.ttf";
    public static final String GAME_UI_FONT = "src/resources/cabal-font/Cabal-w5j3.ttf";

    /**
     * Returns the plain game font at the requested point size.
     * @param sizePt point size, e.g. 14f, 18f, 24f
     */
    public static Font plainGameFont(int sizePt, String fontName) {
        return new Font(fontName, PLAIN, sizePt);
    }


    /**
     * Returns the BOLD game font at the requested point size.
     * @param sizePt point size, e.g. 14f, 18f, 24f
     */
    public static Font boldGameFont(int sizePt, String fontName) {
        return new Font(fontName, Font.BOLD, sizePt);
    }


    /**
     * Returns the Italic game font at the requested point size.
     * @param sizePt point size, e.g. 14f, 18f, 24f
     */
    public static Font ItalicGameFont(int sizePt, String fontName) {
        return new Font(fontName, Font.ITALIC, sizePt);
    }


}
