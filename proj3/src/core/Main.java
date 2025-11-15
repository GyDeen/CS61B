package core;

public class Main {
    private enum GameState {INIT_MENU, ENTER_SEED, PLAYING, LOADING, QUIT}
    private GameState state = GameState.INIT_MENU;
    private static final InitPage init = new InitPage();
    private final SeedScreen seedScreen = new SeedScreen();

    public static void main(String[] args) {


//      World world = new World();
//      world.renderWorld();


    }
}
