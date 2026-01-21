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


    /* Accumulative update the score */
    private void accumulateScore(int targetScore, double x, double y) {
        int startScore = this.score;
        // Speed up the tally if the gap is large
        int step = Math.max(1, (targetScore - startScore) / 20);

        for (int i = startScore; i <= targetScore; i += step) {
            // Ensure we don't exceed the target due to step size
            int displayValue = Math.min(i, targetScore);

            // Only clear the score number
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.filledRectangle(x + 10, y, 5, 0.7);

            StdDraw.setPenColor(Color.GREEN);
            StdDraw.textLeft(x, y, "FINAL EVALUATION: " + displayValue);

            StdDraw.show();
            StdDraw.pause(10);
        }
        this.score = targetScore;
    }


    private void run(int gameResult, int remainingMoney, long remainingTimeMs, int destroyedGhost, int difficulty) {
        double difficultyMultiplier = switch (difficulty) {
            case MEDIUM -> MEDIUM_DIFFICULTY_MULTIPLIER;
            case HARD -> HARD_DIFFICULTY_MULTIPLIER;
            default -> DEFAULT_DIFFICULTY_MULTIPLIER;
        };
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
        accumulateScore((int) (this.score + remainingMoney * MONEY_SCORE_MULTIPLIER* difficultyMultiplier), startX, scoreLineY);

        int seconds = (int) (remainingTimeMs / 1000);
        typeWriterLine("TIME REMAINING: " + seconds + "s", startX, startY - (5 * lineSpacing), 40);
        accumulateScore((int) (this.score + seconds * TIME_SCORE_MULTIPLIER * difficultyMultiplier), startX, scoreLineY);

        typeWriterLine("THREATS NEUTRALIZED: " + destroyedGhost, startX, startY - (6 * lineSpacing), 40);
        accumulateScore((int) (this.score + destroyedGhost * DESTROY_GHOST_MULTIPLIER * difficultyMultiplier), startX, scoreLineY);

        typeWriterLine("------------------------------", startX, startY - (7 * lineSpacing), 40);
        typeWriterLine("PRESS ANY KEY TO EXIT_", startX, startY - (8 * lineSpacing), 40);
    }
}
