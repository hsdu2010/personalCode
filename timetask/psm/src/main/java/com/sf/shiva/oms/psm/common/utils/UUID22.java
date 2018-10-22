/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.shiva.oms.psm.common.utils;

import java.util.UUID;

/**
 * 描述：UUID工具类
 * 
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID   DATE           PERSON          REASON
 *  1    2015年6月29日      568677         Create
 * ****************************************************************************
 * </pre>
 * 
 * @author zhangYao 568677
 * @since 1.0
 */
public class UUID22 {
    private UUID22() {
    }

    /**
     * 采用URL Base64字符，即把“+/”换成“-_”
     */
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=".toCharArray();

    /**
     * Base64 编码
     * 
     * @param data
     * @return
     */
    private static char[] encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        boolean quad, trip;
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            quad = trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = ALPHABET[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = ALPHABET[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = ALPHABET[val & 0x3F];
            val >>= 6;
            out[index + 0] = ALPHABET[val & 0x3F];
        }
        return out;
    }

    /**
     * 转成字节
     * 
     * @return
     */
    private static byte[] toBytes() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) ((msb >>> 8 * (7 - i)) & 0xFF);
            buffer[i + 8] = (byte) ((lsb >>> 8 * (7 - i)) & 0xFF);
        }
        return buffer;
    }

    public static String getUUID() {
        char[] res = encode(toBytes());
        return new String(res, 0, res.length - 2);
    }

    /**
     * 将随机UUID转换成22位字符串
     * 
     * @return
     */
    public static String getUUID22() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        char[] out = new char[24];
        int tmp, idx = 0;
        // 基础写法
        /*
         * tmp = (int) ((msb >>> 40) & 0xffffff); out[idx + 3] = alphabet[tmp &
         * 0x3f]; tmp >>= 6; out[idx + 2] = alphabet[tmp & 0x3f]; tmp >>= 6;
         * out[idx + 1] = alphabet[tmp & 0x3f]; tmp >>= 6; out[idx] =
         * alphabet[tmp & 0x3f]; idx += 4;
         * 
         * tmp = (int) ((msb >>> 16) & 0xffffff); out[idx + 3] = alphabet[tmp &
         * 0x3f]; tmp >>= 6; out[idx + 2] = alphabet[tmp & 0x3f]; tmp >>= 6;
         * out[idx + 1] = alphabet[tmp & 0x3f]; tmp >>= 6; out[idx] =
         * alphabet[tmp & 0x3f]; idx += 4;
         * 
         * tmp = (int) (((msb & 0xffff) << 8) | (lsb >>> 56 & 0xff)); out[idx +
         * 3] = alphabet[tmp & 0x3f]; tmp >>= 6; out[idx + 2] = alphabet[tmp &
         * 0x3f]; tmp >>= 6; out[idx + 1] = alphabet[tmp & 0x3f]; tmp >>= 6;
         * out[idx] = alphabet[tmp & 0x3f]; idx += 4;
         * 
         * tmp = (int) ((lsb >>> 32) & 0xffffff); out[idx + 3] = alphabet[tmp &
         * 0x3f]; tmp >>= 6; out[idx + 2] = alphabet[tmp & 0x3f]; tmp >>= 6;
         * out[idx + 1] = alphabet[tmp & 0x3f]; tmp >>= 6; out[idx] =
         * alphabet[tmp & 0x3f]; idx += 4;
         * 
         * tmp = (int) ((lsb >>> 8) & 0xffffff); out[idx + 3] = alphabet[tmp &
         * 0x3f]; tmp >>= 6; out[idx + 2] = alphabet[tmp & 0x3f]; tmp >>= 6;
         * out[idx + 1] = alphabet[tmp & 0x3f]; tmp >>= 6; out[idx] =
         * alphabet[tmp & 0x3f]; idx += 4;
         * 
         * tmp = (int) (lsb & 0xff); out[idx + 3] = alphabet[64]; out[idx + 2] =
         * alphabet[64]; tmp <<= 4; out[idx + 1] = alphabet[tmp & 0x3f]; tmp >>=
         * 6; out[idx] = alphabet[tmp & 0x3f]; idx += 4;
         */

        // 循环写法
        int bit = 0, bt1 = 8, bt2 = 8;
        int mask, offsetm, offsetl;

        for (; bit < 16; bit += 3, idx += 4) {
            offsetm = 64 - (bit + 3) * 8;
            tmp = 0;

            if (bt1 > 3) {
                mask = (1 << 8 * 3) - 1;
            } else if (bt1 >= 0) {
                mask = (1 << 8 * bt1) - 1;
                bt2 -= 3 - bt1;
            } else {
                mask = (1 << 8 * ((bt2 > 3) ? 3 : bt2)) - 1;
                bt2 -= 3;
            }
            if (bt1 > 0) {
                bt1 -= 3;
                tmp = (int) ((offsetm < 0) ? msb : (msb >>> offsetm) & mask);
                if (bt1 < 0) {
                    tmp <<= Math.abs(offsetm);
                    mask = (1 << 8 * Math.abs(bt1)) - 1;
                }
            }
            if (offsetm < 0) {
                offsetl = 64 + offsetm;
                tmp |= ((offsetl < 0) ? lsb : (lsb >>> offsetl)) & mask;
            }

            if (bit == 15) {
                out[idx + 3] = ALPHABET[64];
                out[idx + 2] = ALPHABET[64];
                tmp <<= 4;
            } else {
                out[idx + 3] = ALPHABET[tmp & 0x3f];
                tmp >>= 6;
                out[idx + 2] = ALPHABET[tmp & 0x3f];
                tmp >>= 6;
            }
            out[idx + 1] = ALPHABET[tmp & 0x3f];
            tmp >>= 6;
            out[idx] = ALPHABET[tmp & 0x3f];
        }

        return new String(out, 0, 22);
    }

    public static void main(String[] args) {
        // long a = System.currentTimeMillis();
        // for(int i = 0; i < 1000000; i++) {
        // UUID.randomUUID().toString();
        // getUUID22();
        // }
        // System.out.print(System.currentTimeMillis() -a);
    }
}
