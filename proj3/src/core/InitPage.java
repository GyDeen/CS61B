package core;

import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;
import java.awt.event.KeyEvent;


/** Initial page interface and manage the branch go to different pages such as seed page and loading page. */
public class InitPage extends NonGamingPage{
    public enum MenuChoice {
        NEW_GAME("New Game"),
        LOAD_GAME("Load Game"),
        EXIT_GAME("Exit Game");

        private final String displayText;

        MenuChoice(String text) {
            this.displayText = text;
        }
    }


    /** Set up the initial page */
    private void draw(int selectedIndex, int hoveredIndex){

        // Draw title
        StdDraw.setFont(super.getDefaultTitleFont());
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) Config.WINDOW_WIDTH / 2, (double) (Config.WINDOW_HEIGHT * 4) / 5, "GAME");

        // Draw menu options
        StdDraw.setFont(super.getDefaultPromptFont());

        double centerX = Config.WINDOW_WIDTH / 2.0;
        double startY = Config.WINDOW_HEIGHT / 2.0;
        double spacing = Config.BUFFER * 2;
        int i = 0;
        for (MenuChoice choice : MenuChoice.values()) {
            double y = startY - i * spacing;

            if (i == selectedIndex) {
                StdDraw.setPenColor(Color.YELLOW);
            } else if (i == hoveredIndex) {
                StdDraw.setPenColor(Color.LIGHT_GRAY);
            } else {
                StdDraw.setPenColor(Color.WHITE);
            }

            StdDraw.text(centerX, y, choice.displayText);
            i++;
        }

        StdDraw.show();
    }

    /** Waiting for player input */
    public MenuChoice initialRun() {
        MenuChoice[] options = MenuChoice.values();
        int count = options.length;

        int selectedIndex = 0;

        // Edge tracking so we only react once per key press
        boolean upPrev = false;
        boolean downPrev = false;
        boolean enterPrev = false;
        boolean escPrev = false;
        boolean mousePrev = false;

        while (true) {
            double centerX = Config.WINDOW_WIDTH / 2.0;
            double startY = Config.WINDOW_HEIGHT / 2.0;
            double spacing = Config.BUFFER * 2;

            // Checking mouse hovering
            int hoveredIndex = -1;
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();

            for (int i = 0; i < count; i++) {
                double y = startY - i * spacing;
                // Calculate approximate hitbox for the menu choice
                if (Math.abs(mouseY - y) <= spacing / 2.0 &&
                    Math.abs(mouseX - centerX) <= 10.0) {
                    hoveredIndex = i;
                    break;
                }
            }

            // Mouse has the highest priority: if hovering, that becomes the selected index
            if (hoveredIndex != -1) {
                selectedIndex = hoveredIndex;
            }

            // Draw current frame with highlight/hover
            draw(selectedIndex, hoveredIndex);

            boolean upNow = StdDraw.isKeyPressed(KeyEvent.VK_UP);
            boolean downNow = StdDraw.isKeyPressed(KeyEvent.VK_DOWN);
            boolean enterNow = StdDraw.isKeyPressed(KeyEvent.VK_ENTER);
            boolean escNow = StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE);

            if (upNow && !upPrev) {
                selectedIndex = (selectedIndex - 1 + count) % count;
            }
            if (downNow && !downPrev) {
                selectedIndex = (selectedIndex + 1) % count;
            }
            if (enterNow && !enterPrev) {
                return options[selectedIndex];
            }
            if (escNow && !escPrev) {
                return MenuChoice.EXIT_GAME;
            }

            upPrev = upNow;
            downPrev = downNow;
            enterPrev = enterNow;
            escPrev = escNow;

            boolean mouseNow = StdDraw.isMousePressed();
            if (mouseNow && !mousePrev && hoveredIndex != -1) {
                return options[hoveredIndex];
            }
            mousePrev = mouseNow;

            StdDraw.pause(8);
        }
    }

}
