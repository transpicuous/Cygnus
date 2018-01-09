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
package login.packet;

import game.GameServerSessionManager;
import login.LoginSessionManager;
import netty.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class LLogin {
    public static void GameServerInformation() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.ChannelInformation.getValue());
        oPacket.Encode(GameServerSessionManager.aSessions.size());
        GameServerSessionManager.aSessions.forEach((pGameServer) -> {
            oPacket.Encode(pGameServer.nChannelID);
            oPacket.EncodeInteger(pGameServer.nMaxUsers);
            oPacket.EncodeInteger(pGameServer.nPort);
            oPacket.EncodeString(pGameServer.GetIP());
        });
        LoginSessionManager.pSession.SendPacket(oPacket.ToPacket());
    }
}
