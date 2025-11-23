package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PausePage extends NonGamingPage {
    public enum PauseChoice {
        CONTINUE("Continue"),
        RETURN_TO_MENU("Return to Menu"),
        SAVE_GAME("Save"),
        EXIT("Exit");

        private final String text;

        PauseChoice(String text) {this.text = text;}
    }



    private void draw(int selectedIndex, int hoveredIndex) {
        // Draw title
        StdDraw.setFont(super.getDefaultTitleFont());
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) Config.WINDOW_WIDTH / 5, (double) (Config.WINDOW_HEIGHT * 9) / 10, "Pause");

        StdDraw.setFont(super.getDefaultPromptFont());

        double centerX = Config.WINDOW_WIDTH / 2.0;
        double startY = Config.WINDOW_HEIGHT / 2.0;
        double spacing = Config.BUFFER * 2;
        int i = 0;
        for (PausePage.PauseChoice choice : PausePage.PauseChoice.values()) {
            double y = startY - i * spacing;

            if (i == selectedIndex) {
                StdDraw.setPenColor(Color.YELLOW);
            } else if (i == hoveredIndex) {
                StdDraw.setPenColor(Color.LIGHT_GRAY);
            } else {
                StdDraw.setPenColor(Color.WHITE);
            }

            StdDraw.text(centerX, y, choice.text);
            i++;
        }

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(UI.loadFont(UI.PROMPT_FONT_PATH, 20));
        StdDraw.text(20, UI.BOTTOM_UI, "Press E to exit the game. Press S to save current game. Press C to continue");

        StdDraw.show();
    }



    public PausePage.PauseChoice run() {
        PausePage.PauseChoice[] options = PausePage.PauseChoice.values();
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
            boolean saveGame = StdDraw.isKeyPressed(KeyEvent.VK_S);
            boolean continueGame = StdDraw.isKeyPressed(KeyEvent.VK_C);
            boolean returnMenu = StdDraw.isKeyPressed(KeyEvent.VK_R);
            boolean exitGame = StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE);

            if (saveGame) return PauseChoice.SAVE_GAME;
            if (continueGame) return PauseChoice.CONTINUE;
            if (returnMenu) return PauseChoice.RETURN_TO_MENU;
            if (exitGame) return PauseChoice.EXIT;

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
                return PauseChoice.EXIT;
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
