package utils;

import java.util.Base64;

public class EncryptionUtil {

    private static final int CAESAR_SHIFT = 7;

    public static String encrypt(String text) {
        String shifted = caesarEncrypt(text);
        return Base64.getEncoder().encodeToString(shifted.getBytes());
    }

    public static String decrypt(String encrypted) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            String shifted = new String(decoded);
            return caesarDecrypt(shifted);
        } catch (Exception e) {
            return encrypted;
        }
    }

    private static String caesarEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                sb.append((char) ((c - base + CAESAR_SHIFT) % 26 + base));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String caesarDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                sb.append((char) ((c - base - CAESAR_SHIFT + 26) % 26 + base));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String hashPassword(String password) {
        // Simple hash for demo purposes
        int hash = 0;
        for (char c : password.toCharArray()) {
            hash = 31 * hash + c;
        }
        return "H" + Math.abs(hash) + "X";
    }

    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}
