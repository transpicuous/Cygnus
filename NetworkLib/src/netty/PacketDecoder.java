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
    protected void decode(ChannelHandlerContext chc, ByteBuf oBuffer, List<Object> oPacket) throws Exception {
        Socket Session = chc.channel().attr(Socket.SESSION_KEY).get();

        if (Session != null) {
            int dwKey = Session.uSeqRcv;
            if (Session.nSavedLen == -1) {
                if (oBuffer.readableBytes() >= 4) {
                    int nHeader = oBuffer.readInt();
                    if (!CAESCipher.ValidateHeader(nHeader, dwKey)) {
                        Session.close();
                        return;
                    }
                    Session.nSavedLen = CAESCipher.GetLength(nHeader);
                } else {
                    return;
                }
            }
            if (oBuffer.readableBytes() >= Session.nSavedLen) {
                byte[] aData = new byte[Session.nSavedLen];
                oBuffer.readBytes(aData);
                Session.nSavedLen = -1;
                
                aData = CAESCipher.Crypt(aData, dwKey);
                if (Session.nCryptoMode == 2) {
                    //Decode opcode here
                }
                Session.uSeqRcv = CIGCipher.InnoHash(dwKey, 4, 0);
                
                oPacket.add(new Packet(aData));
            }
        }
    }
}
