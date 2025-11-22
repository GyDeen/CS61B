package core;

public class Main {
    private enum GameState {INIT_MENU, ENTER_SEED, PLAYING, LOADING, QUIT}
    private GameState state = GameState.INIT_MENU;
    private static final InitPage init = new InitPage();
    private static final SeedScreen seedScreen = new SeedScreen();
    private static final LoadingPage loading = new LoadingPage();

    public static void main(String[] args) {
        while (true) {
            init.setUpScreen();
            InitPage.MenuChoice nextPage = init.run();

            switch (nextPage) {
                case NEW_GAME : {
                    long worldSeed = seedScreen.run();

                    if (worldSeed == -1L) {
                        continue;
                    }

                    World world = new World(worldSeed);
                    world.renderWorld();
                    break;
                }
                case EXIT_GAME:System.exit(0);
                case LOAD_GAME : {
                    loading.run();
                    break;
                }
            }

            break;
        }
    }
}
