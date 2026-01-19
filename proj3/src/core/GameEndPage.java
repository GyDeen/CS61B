package core;


import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

import static core.Config.*;

/** Game result based on World returned result */
public class GameEndPage extends NonGamingPage{
    private int score = 0;

    /* Display the text with a Terminal style text writer */
    private void typeWriterLine(String text, double x, double y, int delayMs) {
        for (int i = 0; i <= text.length(); i++) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(x, y, text.substring(0, i));
            StdDraw.show();
            StdDraw.pause(delayMs);
        }
    }


    /* Update score */
    private void updateScore(int count, double multiplier) {
        score += count * multiplier;
    }


    /* Update the final score */
    private void refreshScoreLine(double x, double y) {
        StdDraw.pause(300); // Brief pause to let player see the change
        // Use a black rectangle to clear only the score line
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(WINDOW_WIDTH / 2.0, y, WINDOW_WIDTH, 0.7);

        // Draw the new score immediately
        StdDraw.setPenColor(Color.GREEN); // Highlight evaluation in green
        StdDraw.textLeft(x, y, "FINAL EVALUATION: " + score);
        StdDraw.show();
    }


    private void run(int gameResult, int remainingMoney, long remainingTimeMs, int destroyedGhost) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 20)); // Terminal font

        double startX = WINDOW_WIDTH * 0.2;
        double startY = WINDOW_HEIGHT * 0.70;
        double lineSpacing = 1.5;
        double scoreLineY = startY - (3 * lineSpacing);

        typeWriterLine("INITIALIZING DATA RECOVERY...", startX, startY, 40);
        typeWriterLine("------------------------------", startX, startY - lineSpacing, 40);
        typeWriterLine("RESULT: " + (gameResult == WIN ? "SUCCESS" : "CRITICAL FAILURE"), startX, startY - (2 * lineSpacing), 40);

        typeWriterLine("FINAL EVALUATION: " + score, startX, scoreLineY, 40);
        typeWriterLine("GOLD REMAINED: $" + remainingMoney, startX, startY - (4 * lineSpacing), 40);
        updateScore(remainingMoney, 10.0);
        refreshScoreLine(startX, scoreLineY);

        int seconds = (int) (remainingTimeMs / 1000);
        typeWriterLine("TIME REMAINING: " + seconds + "s", startX, startY - (5 * lineSpacing), 40);
        updateScore(seconds, 5.0);
        refreshScoreLine(startX, scoreLineY);

        typeWriterLine("THREATS NEUTRALIZED: " + destroyedGhost, startX, startY - (6 * lineSpacing), 40);
        updateScore(destroyedGhost, 100.0);
        refreshScoreLine(startX, scoreLineY);

        typeWriterLine("------------------------------", startX, startY - (7 * lineSpacing), 40);
        typeWriterLine("PRESS ANY KEY TO EXIT_", startX, startY - (8 * lineSpacing), 40);
    }
}
