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

/**
 *
 * @author Kaz Voeten
 */
import center.CenterSessionManager;
import java.util.LinkedList;
import java.util.List;
import net.InPacket;
import client.avatar.AvatarData;
import crypto.BCrypt;

public class Account {

    public final int nAccountID;
    public final long nSessionID;
    public String sAccountName, sIP, sSPW, sHWID, sMAC;
    public final byte nState, nGender, nAdmin;
    public List<AvatarData> aAvatarData = new LinkedList<>();

    private Account(int nAccountID, long nSessionID, String sAccountName, String sIP, String sPIC,
            byte nState, byte nGender, byte nAdmin) {
        this.nAccountID = nAccountID;
        this.nSessionID = nSessionID;
        this.sAccountName = sAccountName;
        this.sIP = sIP;
        this.sSPW = sPIC;
        this.nState = nState;
        this.nGender = nGender;
        this.nAdmin = nAdmin;
    }

    public static Account Decode(InPacket iPacket) {
        Account pRet = new Account(
                iPacket.DecodeInt(),
                iPacket.DecodeLong(),
                iPacket.DecodeString(),
                iPacket.DecodeString(),
                iPacket.DecodeString(),
                iPacket.DecodeByte(),
                iPacket.DecodeByte(),
                iPacket.DecodeByte()
        );
        return pRet;
    }

    public void SetSPW(InPacket iPacket) {
        if (!this.sSPW.isEmpty()) {
            return;
        }
        this.sSPW = BCrypt.hashpw(iPacket.DecodeString(), BCrypt.gensalt());
    }

    public void VerifyWhitelisted(ClientSocket pSocket) {
        if (!sIP.isEmpty()) {
            if (ClientSessionManager.mMACBan.containsKey(sIP)) {
                Ban(pSocket);
            }
        }

        if (!sMAC.isEmpty()) {
            if (ClientSessionManager.mMACBan.containsKey(sMAC)) {
                Ban(pSocket);
            }
        }

        if (!sHWID.isEmpty()) {
            if (ClientSessionManager.mMACBan.containsKey(sHWID)) {
                Ban(pSocket);
            }
        }
    }

    public void Ban(ClientSocket pSocket) {
        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                //TODO: ban packet
            }
        });
    }

}
