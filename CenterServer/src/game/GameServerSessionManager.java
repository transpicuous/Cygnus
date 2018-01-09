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
package game;

import game.packet.GamePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.Random;
import login.LoginSessionManager;
import login.packet.LLogin;
import netty.InPacket;
import netty.Packet;

/**
 *
 * @author Kaz Voeten
 */
public class GameServerSessionManager extends ChannelInboundHandlerAdapter {
    public static ArrayList<CGameServerSocket> aSessions = new ArrayList<>();
    private static final Random rand = new Random();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        int RecvSeq = 0;
        int SendSeq = 0;

        CGameServerSocket pClient = new CGameServerSocket(ch, SendSeq, RecvSeq);
        ch.attr(CGameServerSocket.SESSION_KEY).set(pClient);
        aSessions.add(pClient);

        System.out.printf("[Debug] GameServer connected! IP: %s%n", pClient.GetIP());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        CGameServerSocket pClient = (CGameServerSocket) ch.attr(CGameServerSocket.SESSION_KEY).get();
        aSessions.remove(pClient);
        LLogin.GameServerInformation(LoginSessionManager.pSession);
        
        pClient.Close();
        System.out.printf("[Debug] GameServer disconnected! IP: %s.%n", pClient.GetIP());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet pBuffer = (Packet) msg;
        Channel ch = ctx.channel();

        CGameServerSocket pClient = (CGameServerSocket) ch.attr(CGameServerSocket.SESSION_KEY).get();
        InPacket iPacket = pClient.Decoder.Next(pBuffer);

        short nPacketID = iPacket.DecodeShort();

        
        GamePacket PacketID = GamePacket.BeginSocket;
        for (GamePacket cp : GamePacket.values()) {
            if (cp.getValue() == nPacketID) {
                PacketID = cp;
            }
        }
        if (PacketID != GamePacket.BeginSocket) {
            System.out.printf("[Debug] Received %s: %s%n", PacketID.name(), pBuffer.toString());
        }

        pClient.ProcessPacket(PacketID, iPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        CGameServerSocket client = (CGameServerSocket) ctx.channel().attr(CGameServerSocket.SESSION_KEY).get();
        if (client != null) {
            client.Close();
        }
    }
}
