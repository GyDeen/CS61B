package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class PausePage extends NonGamingPage {
    public enum PauseChoice {
        CONTINUE("Continue"),
        RETURN_TO_MENU("Return to Menu"),
        SAVE_GAME("Save"),
        EXIT("Exit");

        private final String text;

        PauseChoice(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

    private boolean upPrev, downPrev, enterPrev, escPrev, mousePrev;
    private int selectedIndex;


    private void draw(int selectedIndex, int hoveredIndex) {
        StdDraw.clear(Color.BLACK);
        // Draw title
        StdDraw.setFont(super.getDefaultTitleFont());
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) Config.WINDOW_WIDTH / 2, (double) (Config.WINDOW_HEIGHT * 7) / 8, "Pause");

        StdDraw.setFont(super.getDefaultPromptFont());

        double centerX = Config.WINDOW_WIDTH / 2.0;
        double startY = Config.WINDOW_HEIGHT * 3 / 5.0;
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
        StdDraw.text(30, UI.BOTTOM_UI, "Press Q to exit the game.  Press R to return menu.  Press S to save current game. Press C to continue");

        StdDraw.show();
    }


    private void drainInputs() {
        while (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_ESCAPE)
                || StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_UP)
                || StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN)
                || StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_ENTER)) {
            StdDraw.pause(10);
        }
        upPrev = downPrev = enterPrev = escPrev = mousePrev = false;
    }

    private PauseChoice poll(PauseChoice[] options, int hoveredIndex) {
        final int count = options.length;

        boolean upNow = StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_UP);
        boolean downNow = StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN);
        boolean enterNow = StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_ENTER);
        boolean escNow = StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_ESCAPE);

        if (StdDraw.hasNextKeyTyped()) {
            char c = Character.toLowerCase(StdDraw.nextKeyTyped());
            if (c == 'c') return PauseChoice.CONTINUE;
            if (c == 'r') return PauseChoice.RETURN_TO_MENU;
            if (c == 's') return PauseChoice.SAVE_GAME;
            if (c == 'q') return PauseChoice.EXIT;
        }

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
            return PauseChoice.CONTINUE;
        }

        boolean mouseNow = StdDraw.isMousePressed();
        if (mouseNow && !mousePrev && hoveredIndex != -1) {
            return options[hoveredIndex];
        }

        // update latches for next frame
        upPrev = upNow;
        downPrev = downNow;
        enterPrev = enterNow;
        escPrev = escNow;
        mousePrev = mouseNow;

        return null;
    }


    public PausePage.PauseChoice run() {
        PausePage.PauseChoice[] options = PausePage.PauseChoice.values();
        int count = options.length;

        int selectedIndex = 0;
        drainInputs();

        while (true) {
            double centerX = Config.WINDOW_WIDTH / 2.0;
            double startY = Config.WINDOW_HEIGHT * 3 / 5.0;
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

            PauseChoice choice = poll(options, hoveredIndex);
            if (choice != null) return choice;

            StdDraw.pause(16);
        }

    }
}
