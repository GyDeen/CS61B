package core;


import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

import static core.Config.*;

/** Game result based on World returned result */
public class GameEndPage extends NonGamingPage{
    public static final int EXIT_GAME = 0;
    public static final int BACK_INIT = 1;

    private int score = 0;

    /* Display the text with a Terminal style text writer */
    private void typeWriterLine(String text, double x, double y, int delayMs, boolean[] skip) {
        if (skip[0]) {
            StdDraw.setPenColor(Color.GREEN);
            StdDraw.textLeft(x, y, text);
            StdDraw.show();
            return;
        }

        for (int i = 0; i <= text.length(); i++) {
            if (StdDraw.hasNextKeyTyped()) {
                skip[0] = true;
                StdDraw.setPenColor(Color.GREEN);
                StdDraw.textLeft(x, y, text);
                StdDraw.show();
                return;
            }

            StdDraw.setPenColor(Color.GREEN);
            StdDraw.textLeft(x, y, text.substring(0, i));
            StdDraw.show();
            StdDraw.pause(delayMs);
        }
    }


    /* Accumulative update the score */
    private void accumulateScore(int targetScore, int finalScore,double x, double y, boolean[] skip) {
        if (skip[0]) {
            this.score = targetScore;
            drawFinalScoreLine(x, y, finalScore);
            return;
        }

        int startScore = this.score;
        // Speed up the tally if the gap is large
        int step = Math.max(1, (targetScore - startScore) / 20);

        for (int i = startScore; i <= targetScore; i += step) {
            if (StdDraw.hasNextKeyTyped()) {
                skip[0] = true;
                this.score = targetScore;
                drawFinalScoreLine(x, y, finalScore);
                return;
            }

            // Ensure we don't exceed the target due to step size
            int displayValue = Math.min(i, targetScore);

            // Only clear the score number
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.filledRectangle(x + 10, y, 10, 0.7);

            StdDraw.setPenColor(Color.GREEN);
            StdDraw.textLeft(x, y, "FINAL EVALUATION: " + displayValue);

            StdDraw.show();
            StdDraw.pause(10);
        }
        this.score = targetScore;
    }


    private void drawFinalScoreLine(double x, double y, int finalScore) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(x + 10, y, 10, 0.7);

        StdDraw.setPenColor(Color.GREEN);
        StdDraw.textLeft(x, y, "FINAL EVALUATION: " + finalScore);
        StdDraw.show();
    }


    public int run(int gameResult, int remainingMoney, long remainingTimeMs, int destroyedGhost, double difficulty) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 20));
        boolean[] skip = {false};

        double startX = WINDOW_WIDTH * 0.35;
        double startY = WINDOW_HEIGHT * 0.70;
        double lineSpacing = 3;
        double scoreLineY = startY - (3 * lineSpacing);
        int seconds = (int) (remainingTimeMs / 1000);
        int finalScore = (int) (score + remainingMoney * MONEY_SCORE_MULTIPLIER* difficulty +
                seconds * TIME_SCORE_MULTIPLIER * difficulty +
                destroyedGhost * DESTROY_GHOST_MULTIPLIER * difficulty);

        typeWriterLine("INITIALIZING DATA RECOVERY...", startX, startY, 40, skip);
        typeWriterLine("------------------------------", startX, startY - lineSpacing, 40, skip);
        typeWriterLine("RESULT: " + (gameResult == WIN ? "SUCCESS" : "CRITICAL FAILURE"), startX, startY - (2 * lineSpacing), 40, skip);

        typeWriterLine("FINAL EVALUATION: " + score, startX, scoreLineY, 40, skip);
        typeWriterLine("GOLD REMAINED: $" + remainingMoney, startX, startY - (4 * lineSpacing), 40, skip);
        accumulateScore((int) (this.score + remainingMoney * MONEY_SCORE_MULTIPLIER* difficulty), finalScore, startX, scoreLineY, skip);


        typeWriterLine("TIME REMAINING: " + seconds + "s", startX, startY - (5 * lineSpacing), 40, skip);
        accumulateScore((int) (this.score + seconds * TIME_SCORE_MULTIPLIER * difficulty), finalScore, startX, scoreLineY, skip);

        typeWriterLine("THREATS NEUTRALIZED: " + destroyedGhost, startX, startY - (6 * lineSpacing), 40, skip);
        accumulateScore((int) (this.score + destroyedGhost * DESTROY_GHOST_MULTIPLIER * difficulty), finalScore, startX, scoreLineY, skip);

        typeWriterLine("------------------------------", startX, startY - (7 * lineSpacing), 40, skip);
        typeWriterLine("PRESS ESC TO QUIT THE GAME. ENTER TO BACK TO INITIAL PAGE_", startX, startY - (8 * lineSpacing), 40, skip);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                // Press ESC
                if (key == 27) {
                    return EXIT_GAME;
                }
                // Press Enter
                if (key == '\n') {
                    return BACK_INIT;
                }
            }
            StdDraw.pause(20);
        }
    }
}
