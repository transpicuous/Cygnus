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

import crypto.CAESCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.charset.Charset;

/**
 *
 * @author Kaz Voeten
 */
public class InPacket {

    private final ByteBuf pRecvBuff;
    private int uLength = 0, uRawSeq = 0, uDataLen = 0, nState = 0;
    private static Charset ASCII = Charset.forName("US-ASCII");

    public InPacket() {
        this.pRecvBuff = Unpooled.buffer();
    }

    public boolean DecryptData(int uSeqKey) {
        if (uDataLen > 0) {
            byte[] aData = new byte[uDataLen];
            pRecvBuff.readBytes(aData);
            CAESCipher.Crypt(aData, uSeqKey);
            pRecvBuff.writeBytes(aData);
            return true;
        }
        return false;
    }

    public int AppendBuffer(ByteBuf pBuff, Socket pSocket) {
        final int HEADER = Short.BYTES + Short.BYTES;// + uDataLen

        pSocket.nLastState = nState;
        int uSize = pBuff.readableBytes();
        if (nState == 0) {
            int uLen = Math.min(uSize, HEADER - uLength);
            RawAppendBuffer(pBuff, uLen);
            if (uSize >= HEADER) {
                nState = 1;
                uRawSeq = DecodeShort();
                uDataLen = DecodeShort();
                if (pSocket.bEncryptData) {
                    uDataLen ^= uRawSeq;
                }
            }
            uSize -= uLen;
            if (uSize == 0) {
                return nState;
            }
        }
        int uAppend = Math.min(uSize, uDataLen + HEADER - uLength);
        RawAppendBuffer(pBuff, uAppend);
        if (uLength >= uDataLen + HEADER) {
            nState = 2;
        }
        uSize -= uAppend;
        if (uSize > 0) {

        }
        return nState;
    }

    public void RawAppendBuffer(ByteBuf pBuff, int uSize) {
        if (uSize + uLength > pRecvBuff.readableBytes()) {
            pRecvBuff.writeBytes(pBuff);
        }
        uLength += uSize;
    }

    public short DecodeSeqBase(int uSeqKey) {
        return (short) ((uSeqKey >> 16) ^ uRawSeq);
    }

    public int Decode() {
        return pRecvBuff.readByte();
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

    public boolean DecodeBool() {
        return pRecvBuff.readBoolean();
    }

    public byte DecodeByte() {
        return (byte) Decode();
    }

    public short DecodeShort() {
        return pRecvBuff.readShort();
    }

    public char DecodeChar() {
        return pRecvBuff.readChar();
    }

    public int DecodeInteger() {
        return pRecvBuff.readInt();
    }

    public float DecodeFloat() {
        return Float.intBitsToFloat(DecodeInteger());
    }

    public long DecodeLong() {
        return pRecvBuff.readLong();
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
        int nOffset = pRecvBuff.readerIndex();
        int nLen = 0;
        while (Decode() != 0) {
            nLen++;
        }
        pRecvBuff.readerIndex(nOffset);
        return DecodeString(nLen);
    }

    public Point DecodePosition() {
        return new Point(DecodeShort(), DecodeShort());
    }

    public Rectangle DecodeRectanlge() {
        return new Rectangle(DecodeShort(), DecodeShort());
    }

    public InPacket Skip(int nLen) {
        pRecvBuff.readerIndex(pRecvBuff.readerIndex() + nLen);
        return this;
    }

    public int GetDataLen() {
        return pRecvBuff.readableBytes();
    }
    
    public byte[] GetRemainder() {
        byte[] aData = new byte[GetDataLen()];
        pRecvBuff.readBytes(aData);
        return aData;
    }
}
