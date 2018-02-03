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
package net;

import crypto.CAESCipher;
import static crypto.CAESCipher.nVersion;
import crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author Kaz Voeten
 */
public class PacketEncoder extends MessageToByteEncoder<OutPacket> {

    @Override
    protected void encode(ChannelHandlerContext chc, OutPacket oPacket, ByteBuf out) throws Exception {
        Socket pSocket = chc.channel().attr(Socket.SESSION_KEY).get();
        byte[] pHeader = new byte[4];
        byte[] pBuffer = oPacket.GetData();

        if (pSocket != null) {
            pSocket.Lock();
            try {
                int uSeqSend = pSocket.uSeqSend;
                int uDataLen = (((pBuffer.length << 8) & 0xFF00) | (pBuffer.length >>> 8));
                int uSeqBase = (short) ((((0xFFFF - nVersion) >> 8) & 0xFF) | (((0xFFFF - nVersion) << 8) & 0xFF00));
                int uRawSeq = (short) ((((uSeqSend >> 24) & 0xFF) | (((uSeqSend >> 16) << 8) & 0xFF00)) ^ uSeqBase);

                if (pSocket.bEncryptData) {
                    uDataLen ^= uRawSeq;
                    if (pSocket.nCryptoMode == 1) {
                        CAESCipher.Crypt(pBuffer, uSeqSend);
                    } else if (pSocket.nCryptoMode == 2) {
                        CIGCipher.Encrypt(pBuffer, uSeqSend);
                    }
                }

                pHeader[0] = (byte) ((uRawSeq >>> 8) & 0xFF);
                pHeader[1] = (byte) (uRawSeq & 0xFF);
                pHeader[2] = (byte) ((uDataLen >>> 8) & 0xFF);
                pHeader[3] = (byte) (uDataLen & 0xFF);

                pSocket.uSeqSend = CIGCipher.InnoHash(uSeqSend, 4, 0);
                out.writeBytes(pHeader);
                out.writeBytes(pBuffer);
            } finally {
                pSocket.Unlock();
            }
        } else {
            out.writeBytes(pBuffer);
        }
    }
}
