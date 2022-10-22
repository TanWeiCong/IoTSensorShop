package com.example.iotsensorshop.activities;

import java.util.Random;

public class RandomString {
    private final String letters = "abcdefg";
    private final String integers = "1234567890";
    private final char[] alphaNumeric = (letters + letters.toUpperCase() + integers).toCharArray();

    public String generateAlphaNumeric(int length){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i ++) {
            result .append(alphaNumeric[new Random().nextInt(alphaNumeric.length)]);
        }
        return result.toString();
    }
}
