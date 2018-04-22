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
package login;

import account.APIFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Random;
import login.packet.LoopBackPacket;
import net.InPacket;
import net.OutPacket;

import server.Configuration;

/**
 *
 * @author Kaz Voeten
 */
public class LoginSessionManager extends ChannelInboundHandlerAdapter {

    public static LoginServerSocket pSession;
    private static final Random rand = new Random();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        LoginServerSocket pClient = new LoginServerSocket(ch, 0, 0);
        pClient.bEncryptData = false;
        ch.attr(LoginServerSocket.SESSION_KEY).set(pClient);
        pSession = pClient;

        System.out.printf("[Debug] Connected to Login Server at adress: %s%n", pClient.GetIP());
        
        OutPacket oPacket = new OutPacket(LoopBackPacket.WorldInformation);
        oPacket.EncodeInt(Configuration.WORLD_ID);
        oPacket.EncodeString(Configuration.WORLD_NAME);
        oPacket.EncodeString(Configuration.EVENT_MESSAGE);
        oPacket.EncodeByte(Configuration.EVENT_FLAG);
        oPacket.EncodeShort(Configuration.EVENT_EXP);
        oPacket.EncodeShort(Configuration.EVENT_DROP);
        oPacket.EncodeBool(Configuration.DISABLE_CHAR_CREATION);
        pClient.SendPacket(oPacket);
        
        APIFactory.GetInstance().GetBlockList(pClient);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        LoginServerSocket pClient = (LoginServerSocket) ch.attr(LoginServerSocket.SESSION_KEY).get();
        pSession = null;

        pClient.Close();
        System.out.println("[Debug] Disconnected from the Login Server");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object in) {
        Channel ch = ctx.channel();
        LoginServerSocket pClient = (LoginServerSocket) ch.attr(LoginServerSocket.SESSION_KEY).get();
        InPacket iPacket = (InPacket) in;
        pClient.ProcessPacket(iPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        LoginServerSocket client = (LoginServerSocket) ctx.channel().attr(LoginServerSocket.SESSION_KEY).get();
        if (client != null) {
            client.Close();
        }
    }
}
