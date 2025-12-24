package com.thantruongnhan.doanketthucmon.momo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

public class MomoSignature {
    public static String sign(String data, String secretKey) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            sha256.init(secretKeySpec);
            byte[] hash = sha256.doFinal(data.getBytes("UTF-8"));
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Cannot sign data", e);
        }
    }
}
