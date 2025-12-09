package core;

import static core.Config.MYSTERY_BOX;

public class MysteryBox extends LootBox {
    private String[] fadeMysteryPath = {
            getImagePath() + "gift_box_alpha_100.png",
            getImagePath() + "gift_box_alpha_70.png",
            getImagePath() + "gift_box_alpha_40.png",
            getImagePath() + "gift_box_alpha_15.png",
    };

    public MysteryBox(MainRoom belongsTo, int x, int y, int width, int height) {
        super(belongsTo, x, y, width, height);
        setImagePath("resources/loot box/mystery box/");
    }
}
