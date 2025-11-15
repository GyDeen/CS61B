package core;

import edu.princeton.cs.algs4.StdDraw;


/** Initial page interface and manage the branch go to different pages such as seed page and loading page. */
public class InitPage extends NonGamingPage{
    private enum MenuChoice {
        NEW_GAME("New Game"),
        LOAD_GAME("Load Game"),
        EXIT_GAME("Exit Game");

        private final String displayText;

        MenuChoice(String text) {
            this.displayText = text;
        }
    }

    @Override
    public void draw(){
        super.draw();
        StdDraw.setFont(super.getDefaultTitleFont());
        StdDraw.text((double) Config.WINDOW_WIDTH / 2, (double) (Config.WINDOW_HEIGHT * 4) /5, "GAME");

        StdDraw.setFont(super.getDefaultPromptFont());

        double centerX = Config.WINDOW_WIDTH / 2.0;

        double startY = Config.WINDOW_HEIGHT / 2.0;
        int i = 0;
        for (MenuChoice choice : MenuChoice.values()) {
            double y = startY - i * Config.BUFFER * 2;
            StdDraw.text(centerX, y, choice.displayText);
            i++;
        }
    }
}
