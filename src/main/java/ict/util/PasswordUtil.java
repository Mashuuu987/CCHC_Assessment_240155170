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

    // 產生隨機 salt（16 bytes），並回傳十六進位字串
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    // byte[] -> hex 字串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 使用 SHA-256 計算 hash
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(input.getBytes());
            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    // 註冊/改密碼用：明文密碼 -> "salt:hash"
    public static String hashPassword(String plainPassword) {
        String salt = generateSalt();
        String hash = sha256(salt + plainPassword);
        return salt + ":" + hash;
    }

    // 登入/驗證用：用輸入的密碼對比 DB 裡的 "salt:hash"
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
