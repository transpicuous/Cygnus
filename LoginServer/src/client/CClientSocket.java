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
import client.packet.CP;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class CClientSocket extends ChannelInboundHandlerAdapter {

    private static final Random rand = new Random();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        int RecvSeq = rand.nextInt();
        int SendSeq = rand.nextInt();

        Client client = new Client(ch, SendSeq, RecvSeq);

        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(0x0F);
        oPacket.EncodeShort(Configuration.MAPLE_VERSION);
        oPacket.EncodeString(Configuration.BUILD_VERSION);
        oPacket.EncodeInteger(RecvSeq);
        oPacket.EncodeInteger(SendSeq);
        oPacket.Encode(Configuration.SERVER_TYPE);
        oPacket.Encode(0);
        client.write(oPacket.ToPacket());

        ch.attr(Client.SESSION_KEY).set(client);

        client.ping = ctx.channel().eventLoop().scheduleAtFixedRate(()
                -> client.write(CLogin.Ping()), 5, 5, TimeUnit.SECONDS);

        System.out.printf("[Debug] Opened session with %s%n", client.GetIP());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        Client client = (Client) ch.attr(Client.SESSION_KEY).get();
        client.close();
        if (client.ping != null) {
            client.ping.cancel(true);
        }

        System.out.printf("[Debug] Closed session with %s.%n", client.GetIP());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet pBuffer = (Packet) msg;
        Channel ch = ctx.channel();

        Client client = (Client) ch.attr(Client.SESSION_KEY).get();
        InPacket iPacket = client.Decoder.Next(pBuffer);

        int nPacketID = iPacket.DecodeShort();

        if (Configuration.SERVER_CHECK) {
            String sHead = "Unk";
            for (CP PacketID : CP.values()) {
                if (PacketID.getValue() == (int) nPacketID) {
                    sHead = PacketID.name();
                }
            }
            System.out.printf("[Debug] Received %s: %s%n", sHead, pBuffer.toString());
        }

        //TODO: Packet handling.
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        Client client = (Client) ctx.channel().attr(Client.SESSION_KEY).get();
        if (client != null) {
            client.close();
        }
    }
}
