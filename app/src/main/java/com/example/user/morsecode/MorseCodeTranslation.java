package com.example.user.morsecode;

import java.util.HashMap;

class MorseCodeTranslation {
    private static String[] ALPHA = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "!", ",", "?",
            ".", "'" };
    private static String[] MORSE = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..",
            "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----", "-.-.--", "--..--",
            "..--..", ".-.-.-", ".----." };

    private static HashMap<String, String> ALPHA_TO_MORSE = new HashMap<>();
    private static HashMap<String, String> MORSE_TO_ALPHA = new HashMap<>();

    static {
        for (int i = 0; i < ALPHA.length  &&  i < MORSE.length; i++) {
            ALPHA_TO_MORSE.put(ALPHA[i], MORSE[i]);
            MORSE_TO_ALPHA.put(MORSE[i], ALPHA[i]);
        }
    }

    static String morseToletter(String morseCode) {
        return MORSE_TO_ALPHA.get(morseCode);
    }

    static String letterToMorse(String englishCode) {
        return ALPHA_TO_MORSE.get(englishCode);
    }
}
