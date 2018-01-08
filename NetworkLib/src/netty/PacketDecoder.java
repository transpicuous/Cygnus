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
import crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 *
 * @author Kaz Voeten
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf oBuffer, List<Object> iPacket) throws Exception {
        Socket pSocket = chc.channel().attr(Socket.SESSION_KEY).get();

        if (pSocket != null) {
            int dwKey = pSocket.uSeqRcv;
            if (pSocket.nSavedLen == -1) {
                if (oBuffer.readableBytes() >= 4) {
                    int nHeader = oBuffer.readInt();
                    if (!CAESCipher.ValidateHeader(nHeader, dwKey)) {
                        pSocket.Close();
                        return;
                    }
                    pSocket.nSavedLen = CAESCipher.GetLength(nHeader);
                } else {
                    return;
                }
            }
            if (oBuffer.readableBytes() >= pSocket.nSavedLen) {
                byte[] aData = new byte[pSocket.nSavedLen];
                oBuffer.readBytes(aData);
                pSocket.nSavedLen = -1;

                aData = CAESCipher.Crypt(aData, dwKey);
                if (pSocket.nCryptoMode == 2) {
                    //Decode opcode here
                }
                pSocket.uSeqRcv = CIGCipher.InnoHash(dwKey, 4, 0);

                iPacket.add(new Packet(aData));
            }
        }
    }
}
