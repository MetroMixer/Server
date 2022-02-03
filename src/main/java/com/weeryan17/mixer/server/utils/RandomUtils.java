package com.weeryan17.mixer.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private char[] allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public String randomKey() {
        long time = System.currentTimeMillis();

        StringBuilder part1 = new StringBuilder();
        while (time > 0) {
            int num = (int) (time % 10);
            part1.append(allowedChars[num*2]);
            time /= 10L;
        }
        String part2 = randomString(30);
        return part1 + "." + part2;
    }

    public String randomString(int len) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            stringBuilder.append(allowedChars[random.nextInt(allowedChars.length - 1)]);
        }
        return stringBuilder.toString();
    }

    private static RandomUtils INS;
    public static RandomUtils getInstance() {
        if (INS == null) {
            INS = new RandomUtils();
        }
        return INS;
    }

}
