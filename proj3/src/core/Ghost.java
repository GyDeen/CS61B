package core;

public class Ghost extends GameObject{
    public Ghost(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImagePath("resources/pac man/ghost");
    }
}
