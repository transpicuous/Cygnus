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
package client;

import client.packet.ClientPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.Random;
import client.packet.Login;
import java.util.concurrent.TimeUnit;
import net.InPacket;
import net.OutPacket;

import server.Configuration;

/**
 *
 * @author Kaz Voeten
 */
public class ClientSessionManager extends ChannelInboundHandlerAdapter {

    public static ArrayList<ClientSocket> aSessions = new ArrayList<>();
    private static final Random rand = new Random();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        int RecvSeq = rand.nextInt();
        int SendSeq = rand.nextInt();

        ClientSocket pClient = new ClientSocket(ch, SendSeq, RecvSeq);

        OutPacket oPacket = new OutPacket((short) 0x0F);
        oPacket.EncodeShort(Configuration.MAPLE_VERSION);
        oPacket.EncodeString(Configuration.BUILD_VERSION);
        oPacket.EncodeInt(RecvSeq);
        oPacket.EncodeInt(SendSeq);
        oPacket.EncodeByte(Configuration.SERVER_TYPE);
        oPacket.EncodeByte(0);
        pClient.SendPacket(oPacket);

        ch.attr(ClientSocket.SESSION_KEY).set(pClient);

        pClient.PingTask = ctx.channel().eventLoop().scheduleAtFixedRate(()
                -> pClient.SendPacket(Login.AliveReq()), 5, 5, TimeUnit.SECONDS);

        aSessions.add(pClient);

        System.out.printf("[Debug] GameServer connected! IP: %s%n", pClient.GetIP());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        ClientSocket pClient = (ClientSocket) ch.attr(ClientSocket.SESSION_KEY).get();
        aSessions.remove(pClient);

        //TODO: LCenter.DisconnectUser(CenterSessionManager.pSession);
        pClient.Close();
        System.out.printf("[Debug] GameServer disconnected! IP: %s.%n", pClient.GetIP());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object in) {
        Channel ch = ctx.channel();
        ClientSocket pClient = (ClientSocket) ch.attr(ClientSocket.SESSION_KEY).get();
        InPacket iPacket = (InPacket) in;
        pClient.ProcessPacket(iPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        ClientSocket client = (ClientSocket) ctx.channel().attr(ClientSocket.SESSION_KEY).get();
        if (client != null) {
            client.Close();
        }
    }
}
