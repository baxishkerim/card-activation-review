package com.example.cardactivation.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@RequiredArgsConstructor
public class PanUtils {

    public static String decryptAesCBC(String input,
                                       String key,
                                       String initialVector)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        String encodedKey = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
        return decrypt(
                "AES/CBC/PKCS5Padding",
                input,
                convertStringToSecretKey(encodedKey),
                new IvParameterSpec(initialVector.getBytes(StandardCharsets.UTF_8))
        );
    }


    public static String decrypt(String algorithm,
                                 String cipherText,
                                 SecretKey key,
                                 IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }
    public static SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

    }

    public static String maskCard(String pan) {
        if (Objects.isNull(pan)) return "000000000000000";
        StringBuilder masked = new StringBuilder(pan.substring(0, 6));
        String ending = pan.substring(pan.length() - 4);
        masked.append("*".repeat(Math.max(0, pan.length() - 10)));
        masked.append(ending);
        return masked.toString();
    }
}
