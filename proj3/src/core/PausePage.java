package core;

public class PausePage extends NonGamingPage {
    public enum PauseChoice {
        RETURN_TO_MENU("Return to Menu"),
        SAVE_GAME("Save"),
        EXIT("Exit");

        private final String text;

        PauseChoice(String text) {this.text = text;}
    }

}
