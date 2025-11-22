package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

import static core.Config.WINDOW_HEIGHT;
import static core.Config.WINDOW_WIDTH;

public class SeedScreen extends NonGamingPage {

    public long run() {
        StdDraw.enableDoubleBuffering();

        StringBuilder sb = new StringBuilder();

        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);

            StdDraw.setFont(getDefaultPromptFont());
            StdDraw.text(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT * 0.70, "WORLD GENERATION");

            StdDraw.setFont(UI.loadFont(UI.PROMPT_FONT_PATH, 40));
            StdDraw.text(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT * 0.56,
                    "Type seed in digits. Press S to start. ESC to cancel.");
            StdDraw.text(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT * 0.46, sb.isEmpty() ? "_" : sb.toString());

            StdDraw.show();
            StdDraw.pause(16);

            if (!StdDraw.hasNextKeyTyped()) continue;
            char c = StdDraw.nextKeyTyped();

            if (c == 27) {
                return -1L;
            } else if (c == 'S' || c == 's') {
                if (sb.isEmpty()) continue; // ignore empty submit
                try {
                    return Long.parseLong(sb.toString());
                } catch (NumberFormatException nfe) {
                    // Too long for long: clamp, or re-prompt
                    // For simplicity, take hash:
                    return sb.toString().hashCode();
                }
            } else if (c == '\b') {
                if (!sb.isEmpty()) sb.deleteCharAt(sb.length() - 1);
            } else if (Character.isDigit(c)) {
                sb.append(c);
            }
            // else ignore other keys
        }
    }
}
