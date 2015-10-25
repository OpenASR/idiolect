package com.jetbrains.idear;

/**
 * Created by breandan on 10/25/2015.
 */
public class WordToNumberConverter {
    public static int getNumber(String input) {
        int accumulator = 0;
        int total = 0;
        String[] words = input.trim().split("\\s+");

        for (String word : words) {
            switch (word) {
                case "zero":
                    accumulator += 0;
                    break;
                case "one":
                    accumulator += 1;
                    break;
                case "two":
                    accumulator += 2;
                    break;
                case "three":
                    accumulator += 3;
                    break;
                case "four":
                    accumulator += 4;
                    break;
                case "five":
                    accumulator += 5;
                    break;
                case "six":
                    accumulator += 6;
                    break;
                case "seven":
                    accumulator += 7;
                    break;
                case "eight":
                    accumulator += 8;
                    break;
                case "nine":
                    accumulator += 9;
                    break;
                case "ten":
                    accumulator += 10;
                    break;
                case "eleven":
                    accumulator += 11;
                    break;
                case "twelve":
                    accumulator += 12;
                    break;
                case "thirteen":
                    accumulator += 13;
                    break;
                case "fourteen":
                    accumulator += 14;
                    break;
                case "fifteen":
                    accumulator += 15;
                    break;
                case "sixteen":
                    accumulator += 16;
                    break;
                case "seventeen":
                    accumulator += 17;
                    break;
                case "eighteen":
                    accumulator += 18;
                    break;
                case "nineteen":
                    accumulator += 19;
                    break;
                case "twenty":
                    accumulator += 20;
                    break;
                case "thirty":
                    accumulator += 30;
                    break;
                case "forty":
                    accumulator += 40;
                    break;
                case "fifty":
                    accumulator += 50;
                    break;
                case "sixty":
                    accumulator += 60;
                    break;
                case "seventy":
                    accumulator += 70;
                    break;
                case "eighty":
                    accumulator += 80;
                    break;
                case "ninety":
                    accumulator += 90;
                    break;
                case "hundred":
                    accumulator *= 100;
                    break;
                case "thousand":
                    accumulator *= 1000;
                    total += accumulator;
                    accumulator = 0;
                    break;
            }
        }

        return total + accumulator;
    }
}