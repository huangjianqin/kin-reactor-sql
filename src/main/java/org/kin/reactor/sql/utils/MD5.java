package org.kin.reactor.sql.utils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * MD5工具类
 * 非线程安全
 *
 * @author huangjianqin
 * @date 2022/7/2
 */
public class MD5 {
    /** 默认MD5 */
    private static final MD5 COMMON = new MD5();
    /** thread local MD5 */
    private static final ThreadLocal<MD5> THREAD_LOCAL_MD5 = ThreadLocal.withInitial(MD5::new);

    /**
     * 返回默认MD5
     */
    public static MD5 common() {
        return COMMON;
    }

    /**
     * 返回thread local MD5
     */
    public static MD5 current() {
        return THREAD_LOCAL_MD5.get();
    }

    /** 十六进制编码字符 */
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
     * 计算字符串MD5编码, 返回byte数组
     */
    public byte[] digest(String s) {
        update(s.getBytes(UTF_8));
        return digest();
    }

    /**
     * 计算字符串MD5编码, 返回十六进制字符串
     */
    public String digestAsHex(String s) {
        return asHexString(digest(s));
    }

    /**
     * 计算字节流的MD5编码, 返回byte数组
     */
    public byte[] digest(InputStream is) {
        byte[] buffer = new byte[4096];
        int num;
        try {
            while ((num = is.read(buffer)) > 0) {
                mdInst.update(buffer, 0, num);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return digest();
    }

    /**
     * 计算字节流的MD5编码, 返回十六进制字符串
     */
    public String digestAsHex(InputStream is) {
        return asHexString(digest(is));
    }

    /**
     * 转换成十六进制字符串
     */
    private String asHexString(byte[] bytes) {
        int j = bytes.length;
        char[] chars = new char[j * 2];
        int k = 0;
        for (byte b : bytes) {
            chars[k++] = HEX_DIGITS[b >>> 4 & 0xf];
            chars[k++] = HEX_DIGITS[b & 0xf];
        }
        return new String(chars);
    }

    /**
     * 增量计算MD5
     * 配合{@link #digest()}和{@link #digestAsHex()}一起使用
     * 适合用于计算大字符串的MD5, 防止OOM
     */
    public void update(String s) {
        mdInst.update(s.getBytes(UTF_8));
    }

    /**
     * 增量计算MD5
     * 配合{@link #digest()}和{@link #digestAsHex()}一起使用
     * 适合用于计算大字符串的MD5, 防止OOM
     */
    public void update(byte[] bytes) {
        mdInst.update(bytes);
    }

    /**
     * 增量计算MD5
     * 配合{@link #digest()}和{@link #digestAsHex()}一起使用
     * 适合用于计算大字符串的MD5, 防止OOM
     */
    public void update(ByteBuffer byteBuffer) {
        mdInst.update(byteBuffer);
    }

    /**
     * 返回MD5字节数组
     */
    public byte[] digest() {
        return mdInst.digest();
    }

    /**
     * 返回MD5 十六进制字符串
     */
    public String digestAsHex() {
        return asHexString(digest());
    }
}
