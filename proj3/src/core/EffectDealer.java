package core;

import tileengine.TileType;

import java.util.Random;

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
        switch (effectType) {
            case COINS:

        }
    }
}
