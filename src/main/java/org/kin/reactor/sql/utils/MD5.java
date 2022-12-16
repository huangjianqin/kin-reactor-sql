package org.kin.reactor.sql.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * MD5工具类
 * @author huangjianqin
 * @date 2022/7/2
 */
public class MD5 {
    private static final MD5 COMMON = new MD5();

    public static MD5 common(){
        return COMMON;
    }

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private MessageDigest mdInst;

    public MD5() {
        try {
            mdInst = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 计算MD5编码
     * @param input
     */
    public String encode(String input) {
        byte[] md5;
        // MessageDigest实例非线程安全
        synchronized (mdInst) {
            mdInst.update(input.getBytes(UTF_8));
            md5 = mdInst.digest();
        }

        int j = md5.length;
        char[] chars = new char[j * 2];
        int k = 0;
        for (byte byte0 : md5) {
            chars[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            chars[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        return new String(chars);
    }
}
