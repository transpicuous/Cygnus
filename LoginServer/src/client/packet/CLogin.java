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
package client.packet;

import center.CenterSessionManager;
import center.packet.CCenter;
import client.Account;
import client.CClientSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import netty.InPacket;
import netty.OutPacket;
import netty.Packet;
import user.AvatarData;

/**
 *
 * @author Kaz Voeten
 */
public class CLogin {

    public static Packet AliveReq() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CheckAliveAck.getValue());
        return oPacket.ToPacket();
    }

    public static Packet LastConnectedWorld(int world) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.LastConnectedWorld.getValue());
        oPacket.EncodeInteger(world);
        return oPacket.ToPacket();
    }

    public static Packet RecommendWorldMessage(int world) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.RecommendWorldMessage.getValue());
        oPacket.Encode(1);
        oPacket.EncodeInteger(world);
        oPacket.EncodeString("The greatest world for starting anew!");
        return oPacket.ToPacket();
    }

    public static Packet UserLimitResult(int status) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.UserLimitResult.getValue());
        oPacket.EncodeShort(status);
        return oPacket.ToPacket();
    }

    public static Packet GetLoginFailed(int reason) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CheckPasswordResult.getValue());
        oPacket.Encode(reason);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        return oPacket.ToPacket();
    }

    public static Packet GetBanMessage(int reason, long time) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CheckPasswordResult.getValue());
        oPacket.Encode(2);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0x00);
        oPacket.Encode(reason);
        oPacket.EncodeLong(time);
        return oPacket.ToPacket();
    }

    public static Packet CharacterBurning(byte nType, int dwCharacterID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CharacterBurning.getValue());
        oPacket.Encode(nType);
        oPacket.EncodeInteger(dwCharacterID);
        return oPacket.ToPacket();
    }

    public static Packet SelectWorldResult() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.SelectWorldResult.getValue());
        oPacket.Encode(true);
        return oPacket.ToPacket();
    }

    public static Packet DuplicateIDResponse(String name, boolean taken) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CheckDuplicatedIDResult.getValue());
        oPacket.EncodeString(name);
        oPacket.Encode(taken);
        return oPacket.ToPacket();
    }

    public static Packet SecurityPacket() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.SecurityPacket.getValue());
        oPacket.Encode(0x01); //0x04 to request response.
        return oPacket.ToPacket();
    }

    public static Packet AuthenMessage() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.AuthenMessage.getValue());
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        return oPacket.ToPacket();
    }

    public static Packet ApplyHotFix() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.ApplyHotFix.getValue());
        oPacket.Encode(true);
        return oPacket.ToPacket();
    }

    public static Packet NCMOResult() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.NMCOResult.getValue());
        oPacket.Encode(true);
        return oPacket.ToPacket();
    }

    public static Packet PrivateServerPacket(int dwCurrentThreadID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.PrivateServerPacket.getValue());

        int response = dwCurrentThreadID ^ LoopBackPacket.PrivateServerPacket.getValue();
        oPacket.EncodeInteger(response);

        return oPacket.ToPacket();
    }

    /**
     * Send the available JobOrder, seems no longer needed though.
     *
     * @return Job Order packet.
     */
    public static Packet JobOrder() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.JOB_ORDER.getValue());
        JobOrder.Encode(oPacket);
        return oPacket.ToPacket();
    }

    public static Packet CreateCharacterResult(AvatarData avatar, boolean success) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CreateCharacterResult.getValue());

        oPacket.Encode(!success);
        if (success) {
            avatar.Encode(oPacket, false);
        }

        return oPacket.ToPacket();
    }

    public static Packet DeleteCharacterResult(int cid, int state) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.DeleteCharacterResult.getValue());

        oPacket.EncodeInteger(cid);
        oPacket.Encode(state);

        return oPacket.ToPacket();
    }

    /*
    public static Packet SelectCharacterResult(GameChannel gc, int cid) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.SelectCharacterResult);

        oPacket.EncodeShort(0x00);
        oPacket.Encode(gc.IP);
        oPacket.EncodeShort(gc.PORT);
        oPacket.EncodeInteger(cid);
        oPacket.Fill(0x00, 5);

        return oPacket.ToPacket();
    } 
     */
    public static void OnWorldInformationRequest(CClientSocket pClient) {
        CenterSessionManager.aCenterSessions.forEach((pWorld) -> {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(LoopBackPacket.WorldInformation.getValue());

            oPacket.Encode(pWorld.nWorldID);
            oPacket.EncodeString(pWorld.sWorldName);
            oPacket.Encode(pWorld.nState);
            oPacket.EncodeString(pWorld.sMessage);
            oPacket.Encode(pWorld.bCreateChar);

            oPacket.Encode(pWorld.aChannels.size());
            pWorld.aChannels.forEach((pChannel) -> {
                oPacket.EncodeString(pWorld.sWorldName + "-" + pChannel.nChannelID);
                oPacket.EncodeInteger(0);//pChannel.nGaugePx
                oPacket.Encode(pWorld.nWorldID);
                oPacket.Encode(pChannel.nChannelID - 1);
                oPacket.Encode(0);//bIsAdultChannel
            });

            oPacket.EncodeShort(0); //Balloons lel
            oPacket.EncodeInteger(0);
            oPacket.Encode(0);

            pClient.SendPacket(oPacket.ToPacket());
        });

        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.WorldInformation.getValue());
        oPacket.Encode(0xFF).Encode(0).Encode(0).Encode(0);
        pClient.SendPacket(oPacket.ToPacket());
        pClient.SendPacket(LastConnectedWorld(CenterSessionManager.aCenterSessions.get(0).nWorldID));
        pClient.SendPacket((new OutPacket()).EncodeShort(LoopBackPacket.OnAliveReq.getValue()).ToPacket());
    }

    public static void OnSelectWorld(CClientSocket pSocket, InPacket iPacket) {
        if (!iPacket.DecodeBoolean()) {
            return;
        }
        String sToken = iPacket.DecodeString();
        iPacket.Skip(21);
        pSocket.nWorldID = iPacket.Decode();
        pSocket.nChannelID = iPacket.Decode() + 1;
        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(CCenter.ProcessLogin(pSocket.nSessionID, sToken));
            }
        });
    }

    public static Packet AccountInfoResult(Account pAccount) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.AccountInfoResult.getValue());
        oPacket.Encode(0x00);
        oPacket.EncodeInteger(pAccount.nAccountID);
        oPacket.Encode(0);//?
        oPacket.Encode(pAccount.nAdmin); //nGradeCode
        oPacket.EncodeInteger(0);//nGrade
        oPacket.EncodeInteger(0);//nVIPGrade
        oPacket.Encode(0); //nAge (removed in v191)
        oPacket.EncodeString(pAccount.sAccountName);

        oPacket.Encode(0);//nPurchaseExp
        oPacket.Encode(0);//nChatBlockReason
        oPacket.EncodeLong(0);//dtChatUnblockDate

        oPacket.EncodeString(pAccount.sAccountName);

        oPacket.EncodeLong(0);
        oPacket.EncodeInteger(3);
        oPacket.EncodeLong(0);

        oPacket.EncodeString("");//nAge?

        JobOrder.Encode(oPacket);

        oPacket.Encode(false); //Make view world button shining?
        oPacket.EncodeInteger(-1); //Has to do with that shining button, so worldID?

        return oPacket.ToPacket();
    }

    public static Packet SelectWorldResult(CClientSocket pSocket, boolean bIsEditedList) {
        List<AvatarData> liAvatarData = pSocket.pAccount.liAvatarData;
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.SelectWorldResult.getValue());

        byte nDay = 0;
        oPacket.Encode(nDay);
        if (nDay == 61) {
            boolean SendOTPForWebLaunching = false;
            oPacket.Encode(SendOTPForWebLaunching);
        }
        oPacket.EncodeString(pSocket.nWorldID == 45 ? "reboot" : "normal");//topkek
        oPacket.EncodeInteger(pSocket.nWorldID);// worldID?
        oPacket.Encode(false);//burning event blocked

        /**
         * if bigger than 0 write ReservedDeleteCharacter data, which writes which character is gonna be deleted at which time if I ever
         * wanna do scheduled character deletions for limited access to pink bean or someshit
         */
        oPacket.EncodeInteger(0);

        oPacket.EncodeInteger(0);//hightime
        oPacket.EncodeInteger(0);///lowtime

        oPacket.Encode(bIsEditedList); //bIsEditedList for after you reorganize the charlist
        Collections.sort(liAvatarData, (AvatarData o1, AvatarData o2) -> o1.nCharlistPos - o2.nCharlistPos);
        if (bIsEditedList) {
            oPacket.EncodeInteger(liAvatarData.size());
            for (AvatarData avatar : liAvatarData) {
                oPacket.EncodeInteger(avatar.dwCharacterID);
            }
        } else {
            oPacket.EncodeInteger(0);//0 chars edited
        }

        oPacket.Encode((byte) liAvatarData.size());
        for (AvatarData avatar : liAvatarData) {
            avatar.Encode(oPacket, false);
        }

        oPacket.Encode((byte) 0); //bHasPic
        oPacket.Encode(false); //bQuerrySSNOnCreateNewCharacter LMAO LEZ STEAL THOSE NUMBERS
        oPacket.EncodeInteger(pSocket.nCharacterSlots);
        oPacket.EncodeInteger(0);//amount of chars bought with CS coupons? nBuyCharCount
        oPacket.EncodeInteger(-1);//event new char job (maybe can be used for pinkbean)
        oPacket.EncodeInteger(0);//highTimeStamp
        oPacket.EncodeInteger(0);//lowTimeStamp
        oPacket.Encode((byte) 0); //enables the name change UI. value is count of names allowed to change
        oPacket.Encode((byte) 0); //idk what this is.
        oPacket.Encode(pSocket.nWorldID == 45); //based on world ID so might be reboot related
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static void OnCheckDuplicatedID(CClientSocket pSocket, InPacket iPacket) {
        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(CCenter.CheckDuplicatedID(pSocket.nSessionID, iPacket.DecodeString()));
            }
        });
    }

    public static void OnCreateNewCharacter(CClientSocket pSocket, InPacket iPacket) {
        if (pSocket.pAccount.liAvatarData.size() + 1 > pSocket.nCharacterSlots) {
            pSocket.SendPacket(CreateCharacterResult(null, false));
            return;
        }

        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(
                        CCenter.CreateNewCharacter(
                                pSocket.nSessionID,
                                pSocket.pAccount.liAvatarData.size() + 1,
                                iPacket.Decode(iPacket.Available()
                                )
                        )
                );
            }
        });
    }

    public static void OnClientDumpLog(InPacket iPacket) {
        String sType = "Unknow report type";
        if (iPacket.Available() < 8) {
            System.out.println(sType + iPacket.DecodeString(iPacket.Available()));
        } else {
            switch (iPacket.DecodeShort()) {
                case 1:
                    sType = "Invalid Decoding";
                    break;
                case 2:
                    sType = "Crash Report";
                    break;
                case 3:
                    sType = "Exception";
                    break;
            }

            int nError = iPacket.DecodeInteger();
            short nLen = iPacket.DecodeShort();
            int tTimeStamp = iPacket.DecodeInteger();
            short nPacketID = iPacket.DecodeShort();

            String sPacketName = "Unk";
            for (LoopBackPacket packet : LoopBackPacket.values()) {
                if (packet.getValue() == (int) nPacketID) {
                    sPacketName = packet.name();
                }
            }

            iPacket.Reverse(2);
            Packet pPacket = new Packet(iPacket.GetRemainder());

            System.out.println(String.format("[Debug] Report type: %s \r\n\t   Error Num: %d, Data Length: %d \r\n\t   Account: %s \r\n\t   Opcode: %s, %d | %s \r\n\t   Data: %s",
                    sType, nError, nLen, "//", sPacketName, nPacketID, "0x" + Integer.toHexString(nPacketID), pPacket.toString()
            ));

        }
    }

    public class Balloon {

        public int nX;
        public int nY;
        public String sMessage;

        public Balloon(String sMessage, int nX, int nY) {
            this.sMessage = sMessage;
            this.nX = nX;
            this.nY = nY;
        }
    }

}
