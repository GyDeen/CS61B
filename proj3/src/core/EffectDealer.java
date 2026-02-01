package core;

import org.jaxen.util.PrecedingAxisIterator;
import tileengine.TileType;

import java.util.Random;

import static core.Config.MODIFY_GAME_TIME_IN_SEC;

public class EffectDealer {
    public enum EffectType {
        COINS(false),
        SPEED_Up(false),
        GHOST_STun(false),
        ADD_Time(false),
        TIME_Froze(false),
        WEAPONIZE(false),
        KEY(false),
        SLOW(true),
        GHOST_SpeedUp(true),
        REDUCE_Time(true),
        INVERTED_Move(true),
        GHOST_Siren(true);


        private final boolean isDeBuff;
        EffectType(boolean isDeBuff) {
            this.isDeBuff = isDeBuff;
        }
    }

    public static EffectType getRandomEffect(Random rand) {
        return EffectType.values()[rand.nextInt(EffectType.values().length)];
    }

    public static EffectType getRandomBuff(Random rand) {
        return EffectType.values()[rand.nextInt(7)];
    }


    public static EffectType getRandomDebuff(Random rand) {
        return EffectType.values()[rand.nextInt(7,11)];
    }


    public void applyEffect(EffectType effectType, World world) {
        PacMan player = world.getPlayer();
        Random rand = world.getRandom();
        switch (effectType) {
            case COINS:
                int coinNum = rand.nextInt(-100, 100);
                world.addMoney(coinNum);
                break;
            case SPEED_Up:
                long currentMoveCoolDown = player.getMoveCoolDown();
                player.setMoveCoolDown((long) (currentMoveCoolDown * 0.5));
                break;
            case GHOST_STun:
                break;
            case ADD_Time:
                world.addGameTime((int) (MODIFY_GAME_TIME_IN_SEC / world.getDifficulty()));
                break;

        }
    }
}
