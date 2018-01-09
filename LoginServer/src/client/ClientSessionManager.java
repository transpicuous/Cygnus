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

import client.packet.CLogin;
import client.packet.ClientPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import netty.InPacket;
import netty.OutPacket;
import netty.Packet;
import server.Configuration;

/**
 *
 * @author Kaz Voeten
 */
public class ClientSessionManager extends ChannelInboundHandlerAdapter {
    public static ArrayList<CClientSocket> aSessions = new ArrayList<>();
    private static final Random rand = new Random();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        int RecvSeq = rand.nextInt();
        int SendSeq = rand.nextInt();

        CClientSocket pClient = new CClientSocket(ch, SendSeq, RecvSeq);

        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(0x0F);
        oPacket.EncodeShort(Configuration.MAPLE_VERSION);
        oPacket.EncodeString(Configuration.BUILD_VERSION);
        oPacket.EncodeInteger(RecvSeq);
        oPacket.EncodeInteger(SendSeq);
        oPacket.Encode(Configuration.SERVER_TYPE);
        oPacket.Encode(0);
        pClient.SendPacket(oPacket.ToPacket());
        
        ch.attr(CClientSocket.SESSION_KEY).set(pClient);

        pClient.PingTask = ctx.channel().eventLoop().scheduleAtFixedRate(()
                -> pClient.SendPacket(CLogin.AliveReq()), 5, 5, TimeUnit.SECONDS);
        
        aSessions.add(pClient);

        System.out.printf("[Debug] Opened session with %s%n", pClient.GetIP());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        CClientSocket pClient = (CClientSocket) ch.attr(CClientSocket.SESSION_KEY).get();
        aSessions.remove(pClient);
        
        if (pClient.PingTask != null) {
            pClient.PingTask.cancel(true);
        }
        
        pClient.Close();
        System.out.printf("[Debug] Closed session with %s.%n", pClient.GetIP());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet pBuffer = (Packet) msg;
        Channel ch = ctx.channel();

        CClientSocket pClient = (CClientSocket) ch.attr(CClientSocket.SESSION_KEY).get();
        InPacket iPacket = pClient.Decoder.Next(pBuffer);

        short nPacketID = iPacket.DecodeShort();

        ClientPacket PacketID = ClientPacket.BeginSocket;
        for (ClientPacket cp : ClientPacket.values()) {
            if (cp.getValue() == nPacketID) {
                PacketID = cp;
            }
        }
        if (PacketID != ClientPacket.BeginSocket) {
            System.out.printf("[Debug] Received %s: %s%n", PacketID.name(), pBuffer.toString());
        }

        pClient.ProcessPacket(PacketID, iPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        CClientSocket client = (CClientSocket) ctx.channel().attr(CClientSocket.SESSION_KEY).get();
        if (client != null) {
            client.Close();
        }
    }
}
