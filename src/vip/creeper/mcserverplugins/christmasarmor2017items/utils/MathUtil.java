package vip.creeper.mcserverplugins.christmasarmor2017items.utils;

/**
 * Created by July on 2017/12/30.
 */
public class MathUtil {
    public static int getRandomIntegerNum(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
}
