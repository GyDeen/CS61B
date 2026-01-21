package core;

import edu.princeton.cs.algs4.StdDraw;

import static core.Config.LOSE;
import static core.Config.PAUSE;
import static core.GameEndPage.EXIT_GAME;

public class Main {
    private enum GameState {INIT_MENU, ENTER_SEED, PLAYING, LOADING, QUIT, PAUSE, SAVE, SUMMARY}
    private static GameState state = GameState.INIT_MENU;
    private static World world;
    private static InitPage init;
    private static final SeedScreen seedScreen = new SeedScreen();
    private static final LoadingPage loading = new LoadingPage();
    private static final int gameResult = LOSE;


    // Consume all the pending input
    private static void drainInputs() {
        while (StdDraw.hasNextKeyTyped()) {
            StdDraw.nextKeyTyped();
        }

        while (StdDraw.isMousePressed()) {
            StdDraw.show();
        }
    }


    public static void main(String[] args) {
        while (state != GameState.QUIT) {
            // Switch state based on Pause Page's result
            if (state == GameState.PAUSE) {
                PausePage pause = new PausePage();
                PausePage.PauseChoice pauseChoice = pause.run();
                System.out.println("Current pause choice: " + pauseChoice.toString());
                switch (pauseChoice) {
                    case EXIT ->  state = GameState.QUIT;
                    case CONTINUE ->  {
                        state = GameState.PLAYING;
                        world.continueGame();
                    }
                    case RETURN_TO_MENU -> state = GameState.INIT_MENU;
                    case SAVE_GAME -> state = GameState.SAVE;
                }
            }
            // Based on Init Menu result to switch state
            if (state == GameState.INIT_MENU) {
                drainInputs();
                init = new InitPage();
                init.setUpScreen();
                InitPage.MenuChoice nextPage = init.run();
                switch (nextPage) {
                    case NEW_GAME : {
                        long worldSeed = seedScreen.run();

                        if (worldSeed == -1L) {
                            continue;
                        }

                        world = new World(worldSeed);
                        world.renderWorld();
                        state = GameState.PLAYING;
                        break;
                    }
                    case EXIT_GAME : System.exit(0);
                    case LOAD_GAME : {
                        loading.run();
                        break;
                    }

                }
            }

            if (state == GameState.SAVE) {}
            // If either Init Menu or Pause Page set state to QUit, we quit immediately
            if (state == GameState.QUIT) {
                System.exit(0);
            }

            if (state == GameState.PLAYING) {
                int paused = world.gameLoop();
                if (paused == PAUSE) state = GameState.PAUSE;
            }

            if (state == GameState.SUMMARY) {
                GameEndPage gameEnd = new GameEndPage();
                if (gameEnd.run(gameResult, world.getMoney(), world.getElapsedTimeMs(), world.destroyedEnemies(), world.getDifficulty()) == EXIT_GAME) {
                    state = GameState.QUIT;
                } else {
                    state = GameState.INIT_MENU;
                }
            }
        }
    }
}
