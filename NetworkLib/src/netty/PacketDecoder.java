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

import crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.ByteOrder;
import java.util.List;

/**
 *
 * @author Kaz Voeten
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) throws Exception {
        Socket pSocket = chc.channel().attr(Socket.SESSION_KEY).get();

        int nState;
        InPacket iPacket = new InPacket();
        ByteBuf pBuff = in.readBytes(4).order(ByteOrder.LITTLE_ENDIAN);

        try {
            nState = iPacket.AppendBuffer(pBuff, pSocket);
            if (nState > 0 && pSocket.nLastState <= 0) {
                System.out.println(iPacket.DecodeSeqBase(pSocket.uSeqRcv));
            }
            if (iPacket.GetDataLen() > 0x50000) {
                System.out.println("Recv packet length overflow.");
            }
        } finally {
            pBuff.release();
        }

        pBuff = in.readBytes(iPacket.GetDataLen()).order(ByteOrder.LITTLE_ENDIAN);
        try {
            nState = iPacket.AppendBuffer(pBuff, pSocket);
            if (nState == 2) {
                if (pSocket.bEncryptData && !iPacket.DecryptData(pSocket.uSeqRcv)) {
                    System.out.println("Unable to decrypt data.");
                    pSocket.uSeqRcv = CIGCipher.InnoHash(pSocket.uSeqRcv, 4, 0);
                    return;
                }
                pSocket.uSeqRcv = CIGCipher.InnoHash(pSocket.uSeqRcv, 4, 0);
                out.add(iPacket);
            }
        } finally {
            pBuff.release();
        }
    }
}
