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
import center.packet.Center;
import client.Account;
import client.ClientSocket;
import java.util.Collections;
import java.util.List;
import net.InPacket;
import net.OutPacket;

import client.avatar.AvatarData;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class Login {

    public static OutPacket AliveReq() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckAliveAck);
        return oPacket;
    }

    public static OutPacket LastConnectedWorld(int nWorldID) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.LastConnectedWorld);
        oPacket.EncodeInt(nWorldID);
        return oPacket;
    }

    public static OutPacket RecommendWorldMessage(int nWorldID) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.RecommendWorldMessage);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(nWorldID);
        oPacket.EncodeString("The greatest world for starting anew!");
        return oPacket;
    }

    public static OutPacket UserLimitResult(int nStatus) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.UserLimitResult);
        oPacket.EncodeShort(nStatus);
        return oPacket;
    }

    public static OutPacket GetLoginFailed(int nReason) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckPasswordResult);
        oPacket.EncodeByte(nReason);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        return oPacket;
    }

    public static OutPacket GetBanMessage(int nReason, long lTime) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckPasswordResult);
        oPacket.EncodeByte(2);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0x00);
        oPacket.EncodeByte(nReason);
        oPacket.EncodeLong(lTime);
        return oPacket;
    }

    public static OutPacket CharacterBurning(byte nType, int dwCharacterID) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CharacterBurning);
        oPacket.EncodeByte(nType);
        oPacket.EncodeInt(dwCharacterID);
        return oPacket;
    }

    public static OutPacket SelectWorldResult() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.SelectWorldResult);
        oPacket.EncodeBool(true);
        return oPacket;
    }

    public static OutPacket DuplicateIDResponse(String sName, boolean bTaken) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckDuplicatedIDResult);
        oPacket.EncodeString(sName);
        oPacket.EncodeBool(bTaken);
        return oPacket;
    }

    public static OutPacket SecurityPacket() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.SecurityPacket);
        oPacket.EncodeByte(0x01); //0x04 to request response.
        return oPacket;
    }

    public static OutPacket AuthenMessage() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.AuthenMessage);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        return oPacket;
    }

    public static OutPacket ApplyHotFix() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.ApplyHotFix);
        oPacket.EncodeBool(true);
        return oPacket;
    }

    public static OutPacket NCMOResult() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.NMCOResult);
        oPacket.EncodeBool(true);
        return oPacket;
    }

    public static OutPacket PrivateServerPacket(int dwCurrentThreadID) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.PrivateServerPacket);
        oPacket.EncodeInt(dwCurrentThreadID ^ LoopBackPacket.PrivateServerPacket);
        return oPacket;
    }

    public static OutPacket JobOrder() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.JOB_ORDER);
        JobOrder.Encode(oPacket);
        return oPacket;
    }

    public static OutPacket CreateNewCharacterResult(AvatarData pAvatar, boolean bSuccess) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CreateCharacterResult);
        oPacket.EncodeBool(!bSuccess);
        if (bSuccess) {
            pAvatar.Encode(oPacket, false);
        }
        return oPacket;
    }

    public static OutPacket DeleteCharacterResult(int cid, int state) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.DeleteCharacterResult);
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(state);
        return oPacket;
    }

    /*
    public static OutPacket SelectCharacterResult(GameChannel gc, int cid) {
        
        oPacket.EncodeShort(LP.SelectCharacterResult);

        oPacket.EncodeShort(0x00);
        oPacket.Encode(gc.IP);
        oPacket.EncodeShort(gc.PORT);
        oPacket.EncodeInteger(cid);
        oPacket.Fill(0x00, 5);

        return oPacket;
    } 
     */
    public static void OnWorldInformationRequest(ClientSocket pClient) {
        CenterSessionManager.aCenterSessions.forEach((pWorld) -> {

            OutPacket oPacket = new OutPacket(LoopBackPacket.WorldInformation);

            oPacket.EncodeByte(pWorld.nWorldID);
            oPacket.EncodeString(pWorld.sWorldName);
            oPacket.EncodeByte(pWorld.nState);
            oPacket.EncodeString(pWorld.sMessage);
            oPacket.EncodeBool(pWorld.bCreateChar);

            oPacket.EncodeByte(pWorld.aChannels.size());
            pWorld.aChannels.forEach((pChannel) -> {
                oPacket.EncodeString(pWorld.sWorldName + "-" + pChannel.nChannelID);
                oPacket.EncodeInt(0);//pChannel.nGaugePx
                oPacket.EncodeByte(pWorld.nWorldID);
                oPacket.EncodeByte(pChannel.nChannelID - 1);
                oPacket.EncodeByte(0);//bIsAdultChannel
            });

            oPacket.EncodeShort(0); //Balloons lel
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);

            pClient.SendPacket(oPacket);
        });

        OutPacket oPacket = new OutPacket(LoopBackPacket.WorldInformation);
        oPacket.EncodeByte(0xFF).EncodeByte(0).EncodeByte(0).EncodeByte(0);
        pClient.SendPacket(oPacket);
        pClient.SendPacket(LastConnectedWorld(CenterSessionManager.aCenterSessions.get(0).nWorldID));
        pClient.SendPacket((new OutPacket(LoopBackPacket.OnAliveReq)));
    }

    public static void OnSelectWorld(ClientSocket pSocket, InPacket iPacket) {
        if (!iPacket.DecodeBool()) {
            return;
        }
        String sToken = iPacket.DecodeString();
        iPacket.Skip(21);
        pSocket.nWorldID = iPacket.Decode();
        pSocket.nChannelID = iPacket.Decode() + 1;
        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(Center.ProcessLogin(pSocket.nSessionID, sToken));
            }
        });
    }

    public static OutPacket AccountInfoResult(Account pAccount) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.AccountInfoResult);
        oPacket.EncodeByte(0x00);
        oPacket.EncodeInt(pAccount.nAccountID);
        oPacket.EncodeByte(0);//?
        oPacket.EncodeByte(pAccount.nAdmin); //nGradeCode
        oPacket.EncodeInt(0);//nGrade
        oPacket.EncodeInt(0);//nVIPGrade
        oPacket.EncodeByte(0); //nAge (removed in v191)
        oPacket.EncodeString(pAccount.sAccountName);
        oPacket.EncodeByte(0);//nPurchaseExp
        oPacket.EncodeByte(0);//nChatBlockReason
        oPacket.EncodeLong(0);//dtChatUnblockDate
        oPacket.EncodeString(pAccount.sAccountName);
        oPacket.EncodeLong(0);
        oPacket.EncodeInt(3);
        oPacket.EncodeLong(pAccount.nSessionID);
        oPacket.EncodeString("");//nAge?
        JobOrder.Encode(oPacket);
        oPacket.EncodeBool(false); //Make view world button shining?
        oPacket.EncodeInt(-1); //Has to do with that shining button, so worldID?
        return oPacket;
    }

    public static OutPacket SelectWorldResult(ClientSocket pSocket, boolean bIsEditedList) {
        List<AvatarData> aAvatarData = pSocket.pAccount.aAvatarData;

        OutPacket oPacket = new OutPacket(LoopBackPacket.SelectWorldResult);

        byte nDay = 0;
        oPacket.EncodeByte(nDay);
        if (nDay == 61) {
            boolean SendOTPForWebLaunching = false;
            oPacket.EncodeBool(SendOTPForWebLaunching);
        }
        oPacket.EncodeString(pSocket.nWorldID == 45 ? "reboot" : "normal");//topkek
        oPacket.EncodeInt(pSocket.nWorldID);// worldID?
        oPacket.EncodeBool(false);//burning event blocked

        /**
         * if bigger than 0 write ReservedDeleteCharacter data, which writes which character is gonna be deleted at which time if I ever
         * wanna do scheduled character deletions for limited access to pink bean or someshit
         */
        oPacket.EncodeInt(0);

        oPacket.EncodeInt(0);//hightime
        oPacket.EncodeInt(0);///lowtime

        //ReservedChar loops here after Long for time.
        oPacket.EncodeBool(bIsEditedList); //bIsEditedList for after you reorganize the charlist
        Collections.sort(aAvatarData, (AvatarData o1, AvatarData o2) -> o1.nCharlistPos - o2.nCharlistPos);
        if (bIsEditedList) {
            oPacket.EncodeInt(aAvatarData.size());
            for (AvatarData avatar : aAvatarData) {
                oPacket.EncodeInt(avatar.dwCharacterID);
            }
        } else {
            oPacket.EncodeInt(0);//0 chars edited
        }

        oPacket.EncodeByte((byte) aAvatarData.size());
        for (AvatarData pAvatar : aAvatarData) {
            pAvatar.Encode(oPacket, false); //no ranking for now.
        }

        oPacket.EncodeBool(!pSocket.pAccount.sSPW.isEmpty());
        oPacket.EncodeBool(false); //bQuerrySSNOnCreateNewCharacter LMAO LEZ STEAL THOSE NUMBERS
        oPacket.EncodeInt(pSocket.nCharacterSlots);
        oPacket.EncodeInt(0);//amount of chars bought with CS coupons? nBuyCharCount
        oPacket.EncodeInt(-1);//event new char job (maybe can be used for pinkbean)
        oPacket.EncodeInt(0);//highTimeStamp
        oPacket.EncodeInt(0);//lowTimeStamp
        oPacket.EncodeByte((byte) 0); //enables the name change UI. value is count of names allowed to change
        oPacket.EncodeByte((byte) 0); //idk what this is.
        oPacket.EncodeBool(pSocket.nWorldID == 45); //based on world ID so might be reboot related
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static void OnCheckDuplicatedID(ClientSocket pSocket, InPacket iPacket) {
        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(Center.CheckDuplicatedID(pSocket.nSessionID, iPacket.DecodeString()));
            }
        });
    }

    public static void OnCreateNewCharacter(ClientSocket pSocket, InPacket iPacket) {
        if (pSocket.pAccount.aAvatarData.size() + 1 > pSocket.nCharacterSlots) {
            pSocket.SendPacket(CreateNewCharacterResult(null, false));
            return;
        }

        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(Center.CreateNewCharacter(
                        pSocket.nSessionID,
                        pSocket.pAccount.aAvatarData.size() + 1,
                        iPacket.Decode(iPacket.GetRemainder())
                )
                );
            }
        });
    }

    public static void OnClientDumpLog(InPacket iPacket) {
        String sType = "Unknow report type";
        if (iPacket.uDataLen < 8) {
            System.out.println(sType + iPacket.DecodeString(iPacket.uDataLen));
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

            int nError = iPacket.DecodeInt();
            short nLen = iPacket.DecodeShort();
            int tTimeStamp = iPacket.DecodeInt();
            short nPacketID = iPacket.DecodeShort();

            String sData = HexUtils.ToHex(iPacket.Decode(iPacket.GetRemainder()));

            System.out.println(String.format("[Debug] Report type: %s \r\n\t   Error Num: %d, Data Length: %d \r\n\t   Account: %s \r\n\t   Opcode: %s, %d | %s \r\n\t   Data: %s",
                    sType, nError, nLen, "//", "", nPacketID, "0x" + Integer.toHexString(nPacketID), sData
            ));

        }
    }

    public static void OnCreateNewCharacterResult(ClientSocket pSocket, InPacket iPacket) {
        boolean bSuccess = iPacket.DecodeBool();
        AvatarData pAvatar = null;
        if (bSuccess) {
            pAvatar = AvatarData.Decode(pSocket.pAccount.nAccountID, iPacket);
            pSocket.pAccount.aAvatarData.add(pAvatar);
        }
        pSocket.SendPacket(CreateNewCharacterResult(pAvatar, bSuccess));
    }

    public static void OnSetSPW(ClientSocket pSocket, InPacket iPacket) {
        Account pAccount = pSocket.pAccount;
        
        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(Center.UpdatePIC(pSocket.nSessionID, iPacket.DecodeString()));
            }
        });
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
