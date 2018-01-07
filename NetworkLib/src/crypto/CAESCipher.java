/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crypto;

/**
 * @author Kaz Voeten
 */
public final class CAESCipher {

    private static final AES cipher;
    private static final short sendVersion, recvVersion;

    private static final short nVersion = 188;
    private static final byte[] aKey = new byte[]{
        (byte) 0x29, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0xF6, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x18,
        (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x5E, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0xCA, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x5A,
        (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x40, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x61, (byte) 0x00,
        (byte) 0x00, (byte) 0x00
    };

    static {
        cipher = new AES();
        cipher.setKey(aKey);
        sendVersion = (short) ((((0xFFFF - nVersion) >> 8) & 0xFF) | (((0xFFFF - nVersion) << 8) & 0xFF00));
        recvVersion = (short) (((nVersion >> 8) & 0xFF) | ((nVersion << 8) & 0xFF00));
    }

    public static byte[] Crypt(byte[] delta, int pSrc) {
        byte[] pdwKey = new byte[]{
            (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };
        return Crypt(delta, pdwKey);
    }

    public static byte[] Crypt(byte[] delta, byte[] gamma) {
        int a = delta.length;
        int b = 0x5B0;
        int c = 0;
        while (a > 0) {
            byte[] d = multiplyBytes(gamma, 4, 4);
            if (a < b) {
                b = a;
            }
            for (int e = c; e < (c + b); e++) {
                if ((e - c) % d.length == 0) {
                    cipher.encrypt(d);
                }
                delta[e] ^= d[(e - c) % d.length];
            }
            c += b;
            a -= b;
            b = 0x5B4;
        }
        return delta;
    }

    public static byte[] multiplyBytes(byte[] iv, int i, int i0) {
        byte[] ret = new byte[i * i0];
        for (int x = 0; x < ret.length; x++) {
            ret[x] = iv[x % i];
        }
        return ret;
    }

    public static byte[] GetHeader(int nLen, int pSrc) {
        byte[] pdwKey = new byte[]{
            (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };

        int a = (pdwKey[3]) & 0xFF;
        a |= (pdwKey[2] << 8) & 0xFF00;
        a ^= sendVersion;
        int b = ((nLen << 8) & 0xFF00) | (nLen >>> 8);
        int c = a ^ b;
        byte[] nRet = new byte[4];
        nRet[0] = (byte) ((a >>> 8) & 0xFF);
        nRet[1] = (byte) (a & 0xFF);
        nRet[2] = (byte) ((c >>> 8) & 0xFF);
        nRet[3] = (byte) (c & 0xFF);

        return nRet;
    }

    public static int GetLength(int delta) {
        int a = ((delta >>> 16) ^ (delta & 0xFFFF));
        a = ((a << 8) & 0xFF00) | ((a >>> 8) & 0xFF);
        return a;
    }

    public static boolean ValidateHeader(int delta, int pSrc) {
        byte[] aDelta = new byte[2];
        aDelta[0] = (byte) ((delta >> 24) & 0xFF);
        aDelta[1] = (byte) ((delta >> 16) & 0xFF);

        byte[] pdwKey = new byte[]{
            (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };

        return ((((aDelta[0] ^ pdwKey[2]) & 0xFF) == ((recvVersion >> 8) & 0xFF))
                && (((aDelta[1] ^ pdwKey[3]) & 0xFF) == (recvVersion & 0xFF)));
    }
}
