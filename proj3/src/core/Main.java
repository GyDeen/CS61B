package core;

public class Main {
    private enum GameState {INIT_MENU, ENTER_SEED, PLAYING, LOADING, QUIT, PAUSE, SAVE}
    private static GameState state = GameState.INIT_MENU;
    private static World world;
    private static final InitPage init = new InitPage();
    private static final SeedScreen seedScreen = new SeedScreen();
    private static final LoadingPage loading = new LoadingPage();


    public static void main(String[] args) {
        while (state != GameState.QUIT) {
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
                    boolean paused = world.gameLoop();
                    if (paused) state = GameState.PAUSE;
                    break;
                }
                case EXIT_GAME : System.exit(0);
                case LOAD_GAME : {
                    loading.run();
                    break;
                }

            }

            if (state == GameState.PAUSE) {
                PausePage pause = new PausePage();
                PausePage.PauseChoice pauseChoice = pause.run();
                System.out.println("Current pause choice: " + pauseChoice.toString());
                switch (pauseChoice) {
                    case EXIT ->  state = GameState.QUIT;
                    case CONTINUE ->  state = GameState.PLAYING;
                    case RETURN_TO_MENU -> state = GameState.INIT_MENU;
                    case SAVE_GAME -> state = GameState.SAVE;
                }
            }

            if (state == GameState.SAVE) {}
            if (state == GameState.QUIT) {
                System.exit(0);
            }
            if (state == GameState.PLAYING) {
                boolean paused = world.gameLoop();
                if (paused) state = GameState.PAUSE;
            }
        }
    }
}
