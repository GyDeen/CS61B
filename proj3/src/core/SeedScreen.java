package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

import static core.Config.WINDOW_HEIGHT;
import static core.Config.WINDOW_WIDTH;
import static core.UI.PROMPT_FONT_PATH;
import static core.UI.plainGameFont;

public class SeedScreen {
    private Font prompt = plainGameFont(Config.PROMPT_SIZE, PROMPT_FONT_PATH);

    public long draw() {
        StringBuilder sb = new StringBuilder();
        StdDraw.setCanvasSize(WINDOW_WIDTH * Config.TILE_SIZE, WINDOW_HEIGHT * Config.TILE_SIZE);
        StdDraw.setXscale(0, WINDOW_WIDTH);
        StdDraw.setYscale(0, WINDOW_HEIGHT);
        StdDraw.enableDoubleBuffering();

        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);

            StdDraw.setFont(prompt);
            StdDraw.text(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT * 0.70, "ENTER SEED");

            StdDraw.setFont(prompt);
            StdDraw.text(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT * 0.56,
                    "Type digits. Press S to start. ESC to cancel.");
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

