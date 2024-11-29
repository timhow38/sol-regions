package me.thepond.solregions.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hex {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String fileToSha256Hex(File file) throws IOException {
        return bytesToSha256Hex(Files.readAllBytes(file.toPath()));
    }

    public static String bytesToSha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);

            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }
}
