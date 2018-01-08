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
package netty;

import java.awt.Point;
import java.awt.Rectangle;
import java.nio.charset.Charset;

/**
 *
 * @author Kaz Voeten
 */
public class InPacket {

    private int nOffset;
    private byte[] aData;
    private static Charset ASCII = Charset.forName("US-ASCII");

    public InPacket() {
        nOffset = -1;
        aData = null;
    }

    public InPacket Next(byte[] aData) {
        this.nOffset = -1;
        this.aData = aData;
        return this;
    }

    public InPacket Next(Packet packet) {
        return Next(packet.GetData());
    }

    public int Decode() {
        nOffset++;
        if (nOffset >= aData.length) {
            return -1;
        }
        return 0xFF & aData[nOffset];
    }

    public void Decode(byte[] aData) {
        Decode(aData, 0, aData.length);
    }

    public void Decode(byte[] aData, int nOffset, int nLen) {
        for (int i = nOffset; i < nLen; i++) {
            aData[i] = DecodeByte();
        }
    }

    public byte[] Decode(int nLen) {
        byte[] aRet = new byte[nLen];
        for (int i = 0; i < nLen; i++) {
            aRet[i] = DecodeByte();
        }
        return aRet;
    }

    public boolean DecodeBoolean() {
        return Decode() > 0;
    }

    public byte DecodeByte() {
        return (byte) Decode();
    }

    public short DecodeShort() {
        return (short) (Decode() + (Decode() << 8));
    }

    public char DecodeChar() {
        return (char) (Decode() + (Decode() << 8));
    }

    public int DecodeInteger() {
        return Decode() + (Decode() << 8) + (Decode() << 16)
                + (Decode() << 24);
    }

    public float DecodeFloat() {
        return Float.intBitsToFloat(DecodeInteger());
    }

    public long DecodeLong() {
        return Decode() + (Decode() << 8) + (Decode() << 16)
                + (Decode() << 24) + (Decode() << 32)
                + (Decode() << 40) + (Decode() << 48)
                + (Decode() << 56);
    }

    public double DecodeDouble() {
        return Double.longBitsToDouble(DecodeLong());
    }

    public String DecodeString(int len) {
        byte[] sd = new byte[len];
        for (int i = 0; i < len; i++) {
            sd[i] = DecodeByte();
        }
        return new String(sd, ASCII);
    }

    public String DecodeString() {
        return DecodeString(DecodeShort());
    }

    public String DecodeNullTerminatedString() {
        int c = 0;
        while (Decode() != 0) {
            c++;
        }
        nOffset -= (c + 1);
        return DecodeString(c);
    }

    public Point DecodePosition() {
        return new Point(DecodeShort(), DecodeShort());
    }

    public Rectangle DecodeRectanlge() {
        return new Rectangle(DecodeShort(), DecodeShort());
    }

    public InPacket Skip(int nLen) {
        nOffset += nLen;
        return this;
    }

    public int Available() {
        return aData.length - nOffset;
    }

    public int GetOffset() {
        return nOffset;
    }

    public byte[] GetData() {
        return aData;
    }

    public void Clear() {
        nOffset = -1;
        aData = null;
    }

    public byte[] GetRemainder() {
        byte[] remainder = new byte[Available()];
        System.arraycopy(aData, nOffset, remainder, 0, Available());
        return remainder;
    }

    public void Reverse(int nLength) {
        nOffset -= nLength;
    }
}
