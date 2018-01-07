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
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class OutPacket {

    private int nOffset;
    private byte[] aData;
    private static final Charset ASCII = Charset.forName("US-ASCII");

    public OutPacket() {
        aData = new byte[0x50000];
        nOffset = 0;
    }
    
    public final OutPacket Encode(int nValue) {
        aData[nOffset++] = (byte) nValue;
        return this;
    }

    public final OutPacket Encode(boolean bData) {
        return Encode(bData ? 1 : 0);
    }

    public final OutPacket Encode(long nValue) {
        return Encode((int) nValue);
    }

    public final OutPacket Encode(byte[] aData) {
        return Encode(aData, 0, aData.length);
    }

    public final OutPacket Encode(byte[] aData, int nOffset, int nLength) {
        for (int i = nOffset; i < nLength; i++) {
            Encode(aData[i]);
        }
        return this;
    }

    public final OutPacket EncodeShort(int nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8);
    }

    public final OutPacket EncodeShort(short nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8);
    }

    public final OutPacket EncodeChar(char cValue) {
        return EncodeShort(cValue);
    }

    public final OutPacket EncodeInteger(int nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8).Encode(nValue >>> 16).Encode(nValue >>> 24);
    }

    public final OutPacket EncodeFloat(float nValue) {
        return EncodeInteger(Float.floatToIntBits(nValue));
    }

    public final OutPacket EncodeLong(long nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8).Encode(nValue >>> 16).
                Encode(nValue >>> 24).Encode(nValue >>> 32).Encode(nValue >>> 40).
                Encode(nValue >>> 48).Encode(nValue >>> 56);
    }

    public final OutPacket EncodeDouble(double nValue) {
        return EncodeLong(Double.doubleToLongBits(nValue));
    }

    public final OutPacket EncodeString(String sData, int nLen) {
        byte[] string = sData.getBytes(ASCII);
        byte[] fill = new byte[nLen - string.length];
        return Encode(string).Encode(fill);
    }

    public final OutPacket EncodeString(String sData) {
        return EncodeShort(sData.length()).Encode(sData.getBytes(ASCII));
    }
    
    public final OutPacket EncodeBuffer(String sData) {
        Encode(sData.getBytes(ASCII));
        return this;
    }

    public final OutPacket EncodeHex(String sData) {
        return Encode(HexUtils.ToBytes(sData));
    }
    
    public final OutPacket Fill(int nValue, int nLenth) {
        for (int i = 0; i < nLenth; i++) {
            Encode(nValue);
        }
        return this;
    }

    public final OutPacket EncodePosition(Point pData) {
        return EncodeShort(pData.x).EncodeShort(pData.y);
    }

    public final OutPacket EncodeRectangle(Rectangle rData) {
        return EncodeInteger(rData.x).EncodeInteger(rData.y)
                .EncodeInteger(rData.x + rData.width).EncodeInteger(rData.y + rData.height);
    }

    public final int GetOffset() {
        return nOffset;
    }

    public final void Clear() {
        nOffset = -1;
        aData = null;
    }

    @Override
    public final String toString() {
        return HexUtils.ToHex(aData);
    }

    private void Trim() {
        byte[] aExpanded = new byte[nOffset];
        System.arraycopy(aData, 0, aExpanded, 0, nOffset);
        aData = aExpanded;
    }

    public final byte[] GetData() {
        if (aData != null) {
            if (aData.length > nOffset) {
                Trim();
            }
            return aData;
        }
        return null;
    }

    public final Packet ToPacket() {
        if (aData != null) {
            return new Packet(GetData());
        }
        return null;
    }
}