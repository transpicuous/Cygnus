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

import center.CenterSessionManager;
import client.packet.CLogin;
import client.packet.ClientPacket;
import client.packet.LoopBackPacket;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import netty.InPacket;
import netty.Packet;
import netty.Socket;
import server.Configuration;

/**
 *
 * @author Kaz Voeten
 */
public class CClientSocket extends Socket {

    public int nSessionID = 0;
    public int nWorldID = -1;
    public int nChannelID = -1;
    public int nCharacterSlots = 15;//TODO
    public Account pAccount;

    public ScheduledFuture<?> PingTask;

    public CClientSocket(Channel channel, int uSeqSend, int uSeqRcv) {
        super(channel, uSeqSend, uSeqRcv);
    }

    @Override
    public void SendPacket(Packet oPacket) {
        if (Configuration.SERVER_CHECK) {
            String head = "Unk";
            for (LoopBackPacket packet : LoopBackPacket.values()) {
                if (packet.getValue() == (int) oPacket.GetHeader()) {
                    head = packet.name();
                    if (packet.getValue() == 0xF0) {
                        head = "HandShake/" + head;
                    }
                }
            }
            System.out.printf("[Debug] Sent %s: %s%n", head, oPacket.toString());
        }
        super.SendPacket(oPacket);
    }

    public void ProcessPacket(ClientPacket nPacketID, InPacket iPacket) {
        switch (nPacketID) {
            case NMCORequest:
                SendPacket(CLogin.NCMOResult());
                break;
            case PrivateServerPacket:
                SendPacket(CLogin.PrivateServerPacket(iPacket.DecodeInteger()));
                break;
            case CheckHotfix:
                SendPacket(CLogin.ApplyHotFix());
                SendPacket(CLogin.SecurityPacket());
                SendPacket(CLogin.AuthenMessage());
                break;
            case WorldInfoLogoutRequest:
            case WorldInfoForShiningRequest:
                CLogin.OnWorldInformationRequest(this);
                break;
            case ClientDumpLog:
                CLogin.OnClientDumpLog(iPacket);
                break;
            case UserLimitRequest:
                SendPacket(CLogin.UserLimitResult(0));
                break;
            case SelectWorld:
                CLogin.OnSelectWorld(this, iPacket);
                break;
            case CheckDuplicatedID:
                CLogin.OnCheckDuplicatedID(this, iPacket);
                break;
            case CreateNewCharacter:
                CLogin.OnCreateNewCharacter(this, iPacket);
                break;
            default:
                break;
        }
    }
}
