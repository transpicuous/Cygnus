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
import game.packet.LoopBackPacket;
import io.netty.channel.Channel;
import login.packet.LLogin;
import netty.InPacket;
import netty.Packet;
import netty.Socket;
import server.Configuration;

/**
 *
 * @author Kaz Voeten
 */
public class CGameServerSocket extends Socket {

    public byte nChannelID;
    public int nMaxUsers;
    public int nPort;
    //public HashMap<Integer, User> mUsers = new HashMap<>();

    public CGameServerSocket(Channel channel, int uSeqSend, int uSeqRcv) {
        super(channel, uSeqSend, uSeqRcv);
    }

    @Override
    public void SendPacket(Packet oPacket) {
        if (Configuration.SERVER_CHECK) {
            String sHead = "Unk";
            for (LoopBackPacket nPacketID : LoopBackPacket.values()) {
                if (nPacketID.getValue() == (int) oPacket.GetHeader()) {
                    sHead = nPacketID.name();
                    if (nPacketID.getValue() == 0xF0) {
                        sHead = "HandShake/" + sHead;
                    }
                }
            }
            System.out.printf("[Debug] Sent %s: %s%n", sHead, oPacket.toString());
        }
        super.SendPacket(oPacket);
    }

    public void ProcessPacket(GamePacket nPacketID, InPacket iPacket) {
        switch (nPacketID) {
            case GameServerInformation:
                this.nChannelID = iPacket.DecodeByte();
                this.nMaxUsers = iPacket.DecodeInteger();
                this.nPort = iPacket.DecodeInteger();
                LLogin.GameServerInformation();
                System.out.println("[Info] Registered GameServer with channel id: " + nChannelID);
                break;
        }
    }
}
