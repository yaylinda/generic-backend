package yay.linda.genericbackend.util;

import java.util.Random;

public class Utilities {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final Random random = new Random();

    /**
     *
     * @param length
     * @return
     */
    public static String randomStringGenerator(Integer length) {

        StringBuilder acc = new StringBuilder();

        for (int i = 0; i < length; i ++) {
            acc.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return acc.toString();
    }

}
