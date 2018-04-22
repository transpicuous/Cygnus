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
package center;

import center.packet.Center;
import center.packet.CenterPacket;
import client.Account;
import client.ClientSessionManager;
import client.packet.Login;
import io.netty.channel.Channel;
import java.util.ArrayList;
import net.InPacket;
import net.Socket;
import client.avatar.AvatarData;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class CenterSocket extends Socket {

    public int nWorldID;
    public String sWorldName, sMessage;
    public byte nState;
    public short nExp, nDrop;
    public boolean bCreateChar;
    public ArrayList<GameServer> aChannels = new ArrayList<>();

    public CenterSocket(Channel channel, int uSeqSend, int uSeqRcv) {
        super(channel, uSeqSend, uSeqRcv);
    }

    public void ProcessPacket(InPacket iPacket) {
        int nPacketID = iPacket.DecodeShort();
        long nSessionID;
        switch (nPacketID) {
            case CenterPacket.WorldInformation:
                this.nWorldID = iPacket.DecodeInt(); //Max 44 atm
                this.sWorldName = iPacket.DecodeString();
                this.sMessage = iPacket.DecodeString();
                this.nState = iPacket.DecodeByte();
                this.nExp = iPacket.DecodeShort();
                this.nDrop = iPacket.DecodeShort();
                this.bCreateChar = iPacket.DecodeBool();
                System.out.println("[Info] Registered world : " + sWorldName + ".");
                break;
            case CenterPacket.BlockList: {
                try {
                    Center.ParseBlockList(iPacket);
                } catch (ParseException ex) {
                    Logger.getLogger(CenterSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case CenterPacket.ChannelInformation:
                aChannels.clear();
                System.out.println("[Info] Cleared channel cache.");
                byte nSize = iPacket.DecodeByte();
                for (int i = 0; i < nSize; ++i) {
                    aChannels.add(GameServer.Decode(iPacket));
                    System.out.println("[Info] Registered GameServer with nChannelID "
                            + aChannels.get(i).nChannelID + " to world " + this.sWorldName + ".");
                }
                break;
            case CenterPacket.AccountInformation:
                nSessionID = iPacket.DecodeLong();
                ClientSessionManager.aSessions.forEach((pSocket) -> {
                    if (pSocket.nSessionID == nSessionID) {
                        pSocket.pAccount = Account.Decode(iPacket);
                        for (int i = iPacket.Decode(); i > 0; --i) {
                            int nCharListPosition = iPacket.DecodeInt();
                            AvatarData pAvatar = AvatarData.Decode(pSocket.pAccount.nAccountID, iPacket);
                            pAvatar.nCharlistPos = nCharListPosition;
                            pSocket.pAccount.aAvatarData.add(pAvatar);
                        }
                        pSocket.SendPacket(Login.AccountInfoResult(pSocket.pAccount));
                        pSocket.SendPacket(Login.SelectWorldResult(pSocket, false));
                    }
                });
                break;
            case CenterPacket.CheckDuplicatedIDResponse:
                nSessionID = iPacket.DecodeLong();
                ClientSessionManager.aSessions.forEach((pSocket) -> {
                    if (pSocket.nSessionID == nSessionID) {
                        pSocket.SendPacket(Login.DuplicateIDResponse(
                                iPacket.DecodeString(),
                                iPacket.DecodeBool()
                        ));
                    }
                });
                break;
            case CenterPacket.OnCreateCharacterResponse:
                nSessionID = iPacket.DecodeLong();
                ClientSessionManager.aSessions.forEach((pSocket) -> {
                    if (pSocket.nSessionID == nSessionID) {
                        Login.OnCreateNewCharacterResult(pSocket, iPacket);
                    }
                });
                break;
            default:
                System.out.println("[DEBUG] Received unhandled Center packet. nPacketID: "
                        + nPacketID + ". Data: "
                        + HexUtils.ToHex(iPacket.Decode(iPacket.GetRemainder())));
                break;
        }
    }
}
