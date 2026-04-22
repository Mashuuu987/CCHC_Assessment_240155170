/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 *
 * @author amzte
 */
public class PasswordUtil {

    // create random salt（16 bytes），return hex
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    // byte[] -> hex
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // use SHA-256 calculate hash
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(input.getBytes());
            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    // for register and change password, password -> "salt:hash"
    public static String hashPassword(String plainPassword) {
        String salt = generateSalt();
        String hash = sha256(salt + plainPassword);
        return salt + ":" + hash;
    }

    // for login and check, use input password to check DB -> "salt:hash"
    public static boolean verifyPassword(String plainPassword, String storedValue) {
        if (storedValue == null || !storedValue.contains(":")) {
            return false;
        }
        String[] parts = storedValue.split(":", 2);
        String salt = parts[0];
        String hash = parts[1];

        String inputHash = sha256(salt + plainPassword);
        return inputHash.equals(hash);
    }
}
