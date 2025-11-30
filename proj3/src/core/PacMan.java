package core;

public class PacMan extends GameObject{
    private final String[] activeImage = {
            getImagePath() + "pac man/pac_man_1.png",
            getImagePath() + "pac man/pac_man_2.png"
    };

    private final String[] dyingImage = {
            getImagePath() + "pac man death/spr_pacdeath_0.png",
            getImagePath() + "pac man death/spr_pacdeath_1.png",
            getImagePath() + "pac man death/spr_pacdeath_2.png",
    };

    private int frameIndex;
    private long

    public PacMan(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImagePath("resources/pac man/pac man & life counter & death");
    }

    private void setImageBasedOnStatus() {
        if (isActive()) {

        }
    }
}
