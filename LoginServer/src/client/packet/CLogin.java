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
import java.util.Collections;
import java.util.List;
import net.InPacket;
import net.OutPacket;

import user.AvatarData;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class CLogin {

    public static OutPacket AliveReq() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckAliveAck.getValue());
        return oPacket;
    }

    public static OutPacket LastConnectedWorld(int world) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.LastConnectedWorld.getValue());
        oPacket.EncodeInt(world);
        return oPacket;
    }

    public static OutPacket RecommendWorldMessage(int world) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.RecommendWorldMessage.getValue());
        oPacket.Encode(1);
        oPacket.EncodeInt(world);
        oPacket.EncodeString("The greatest world for starting anew!");
        return oPacket;
    }

    public static OutPacket UserLimitResult(int status) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.UserLimitResult.getValue());
        oPacket.EncodeShort(status);
        return oPacket;
    }

    public static OutPacket GetLoginFailed(int reason) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckPasswordResult.getValue());
        oPacket.Encode(reason);
        oPacket.Encode(0);
        oPacket.EncodeInt(0);
        return oPacket;
    }

    public static OutPacket GetBanMessage(int reason, long time) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckPasswordResult.getValue());
        oPacket.Encode(2);
        oPacket.Encode(0);
        oPacket.EncodeInt(0x00);
        oPacket.Encode(reason);
        oPacket.EncodeLong(time);
        return oPacket;
    }

    public static OutPacket CharacterBurning(byte nType, int dwCharacterID) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.CharacterBurning.getValue());
        oPacket.Encode(nType);
        oPacket.EncodeInt(dwCharacterID);
        return oPacket;
    }

    public static OutPacket SelectWorldResult() {

        OutPacket oPacket = new OutPacket(LoopBackPacket.SelectWorldResult.getValue());
        oPacket.EncodeBool(true);
        return oPacket;
    }

    public static OutPacket DuplicateIDResponse(String name, boolean taken) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckDuplicatedIDResult.getValue());
        oPacket.EncodeString(name);
        oPacket.EncodeBool(taken);
        return oPacket;
    }

    public static OutPacket SecurityPacket() {

        OutPacket oPacket = new OutPacket(LoopBackPacket.SecurityPacket.getValue());
        oPacket.Encode(0x01); //0x04 to request response.
        return oPacket;
    }

    public static OutPacket AuthenMessage() {

        OutPacket oPacket = new OutPacket(LoopBackPacket.AuthenMessage.getValue());
        oPacket.EncodeInt(0);
        oPacket.Encode(0);
        return oPacket;
    }

    public static OutPacket ApplyHotFix() {

        OutPacket oPacket = new OutPacket(LoopBackPacket.ApplyHotFix.getValue());
        oPacket.EncodeBool(true);
        return oPacket;
    }

    public static OutPacket NCMOResult() {

        OutPacket oPacket = new OutPacket(LoopBackPacket.NMCOResult.getValue());
        oPacket.EncodeBool(true);
        return oPacket;
    }

    public static OutPacket PrivateServerPacket(int dwCurrentThreadID) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.PrivateServerPacket.getValue());

        int response = dwCurrentThreadID ^ LoopBackPacket.PrivateServerPacket.getValue();
        oPacket.EncodeInt(response);

        return oPacket;
    }

    /**
     * Send the available JobOrder, seems no longer needed though.
     *
     * @return Job Order packet.
     */
    public static OutPacket JobOrder() {

        OutPacket oPacket = new OutPacket(LoopBackPacket.JOB_ORDER.getValue());
        JobOrder.Encode(oPacket);
        return oPacket;
    }

    public static OutPacket CreateNewCharacterResult(AvatarData avatar, boolean success) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.CreateCharacterResult.getValue());

        oPacket.EncodeBool(!success);
        if (success) {
            avatar.EncodeForClient(oPacket, false);
        }

        return oPacket;
    }

    public static OutPacket DeleteCharacterResult(int cid, int state) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.DeleteCharacterResult.getValue());

        oPacket.EncodeInt(cid);
        oPacket.Encode(state);

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
    public static void OnWorldInformationRequest(CClientSocket pClient) {
        CenterSessionManager.aCenterSessions.forEach((pWorld) -> {

            OutPacket oPacket = new OutPacket(LoopBackPacket.WorldInformation.getValue());

            oPacket.Encode(pWorld.nWorldID);
            oPacket.EncodeString(pWorld.sWorldName);
            oPacket.Encode(pWorld.nState);
            oPacket.EncodeString(pWorld.sMessage);
            oPacket.EncodeBool(pWorld.bCreateChar);

            oPacket.Encode(pWorld.aChannels.size());
            pWorld.aChannels.forEach((pChannel) -> {
                oPacket.EncodeString(pWorld.sWorldName + "-" + pChannel.nChannelID);
                oPacket.EncodeInt(0);//pChannel.nGaugePx
                oPacket.Encode(pWorld.nWorldID);
                oPacket.Encode(pChannel.nChannelID - 1);
                oPacket.Encode(0);//bIsAdultChannel
            });

            oPacket.EncodeShort(0); //Balloons lel
            oPacket.EncodeInt(0);
            oPacket.Encode(0);

            pClient.SendPacket(oPacket);
        });

        OutPacket oPacket = new OutPacket(LoopBackPacket.WorldInformation.getValue());
        oPacket.Encode(0xFF).Encode(0).Encode(0).Encode(0);
        pClient.SendPacket(oPacket);
        pClient.SendPacket(LastConnectedWorld(CenterSessionManager.aCenterSessions.get(0).nWorldID));
        pClient.SendPacket((new OutPacket(LoopBackPacket.OnAliveReq.getValue())));
    }

    public static void OnSelectWorld(CClientSocket pSocket, InPacket iPacket) {
        if (!iPacket.DecodeBool()) {
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

    public static OutPacket AccountInfoResult(Account pAccount) {

        OutPacket oPacket = new OutPacket(LoopBackPacket.AccountInfoResult.getValue());
        oPacket.Encode(0x00);
        oPacket.EncodeInt(pAccount.nAccountID);
        oPacket.Encode(0);//?
        oPacket.Encode(pAccount.nAdmin); //nGradeCode
        oPacket.EncodeInt(0);//nGrade
        oPacket.EncodeInt(0);//nVIPGrade
        oPacket.Encode(0); //nAge (removed in v191)
        oPacket.EncodeString(pAccount.sAccountName);
        oPacket.Encode(0);//nPurchaseExp
        oPacket.Encode(0);//nChatBlockReason
        oPacket.EncodeLong(0);//dtChatUnblockDate
        oPacket.EncodeString(pAccount.sAccountName);
        oPacket.EncodeLong(0);
        oPacket.EncodeInt(3);
        oPacket.EncodeLong(0);
        oPacket.EncodeString("");//nAge?
        JobOrder.Encode(oPacket);
        oPacket.EncodeBool(false); //Make view world button shining?
        oPacket.EncodeInt(-1); //Has to do with that shining button, so worldID?
        return oPacket;
    }

    public static OutPacket SelectWorldResult(CClientSocket pSocket, boolean bIsEditedList) {
        List<AvatarData> liAvatarData = pSocket.pAccount.liAvatarData;

        OutPacket oPacket = new OutPacket(LoopBackPacket.SelectWorldResult.getValue());

        byte nDay = 0;
        oPacket.Encode(nDay);
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
        Collections.sort(liAvatarData, (AvatarData o1, AvatarData o2) -> o1.nCharlistPos - o2.nCharlistPos);
        if (bIsEditedList) {
            oPacket.EncodeInt(liAvatarData.size());
            for (AvatarData avatar : liAvatarData) {
                oPacket.EncodeInt(avatar.dwCharacterID);
            }
        } else {
            oPacket.EncodeInt(0);//0 chars edited
        }

        oPacket.Encode((byte) liAvatarData.size());
        for (AvatarData pAvatar : liAvatarData) {
            pAvatar.EncodeForClient(oPacket, false); //no ranking for now.
        }

        oPacket.Encode((byte) 0); //bHasPic
        oPacket.EncodeBool(false); //bQuerrySSNOnCreateNewCharacter LMAO LEZ STEAL THOSE NUMBERS
        oPacket.EncodeInt(pSocket.nCharacterSlots);
        oPacket.EncodeInt(0);//amount of chars bought with CS coupons? nBuyCharCount
        oPacket.EncodeInt(-1);//event new char job (maybe can be used for pinkbean)
        oPacket.EncodeInt(0);//highTimeStamp
        oPacket.EncodeInt(0);//lowTimeStamp
        oPacket.Encode((byte) 0); //enables the name change UI. value is count of names allowed to change
        oPacket.Encode((byte) 0); //idk what this is.
        oPacket.EncodeBool(pSocket.nWorldID == 45); //based on world ID so might be reboot related
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);

        return oPacket;
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
            pSocket.SendPacket(CreateNewCharacterResult(null, false));
            return;
        }

        CenterSessionManager.aCenterSessions.forEach((pCenterSocket) -> {
            if (pCenterSocket.nWorldID == pSocket.nWorldID) {
                pCenterSocket.SendPacket(
                        CCenter.CreateNewCharacter(
                                pSocket.nSessionID,
                                pSocket.pAccount.liAvatarData.size() + 1,
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

    public static void OnCreateNewCharacterResult(CClientSocket pSocket, InPacket iPacket) {
        boolean bSuccess = iPacket.DecodeBool();
        AvatarData pAvatar = null;
        if (bSuccess) {
            pAvatar = AvatarData.Decode(pSocket.pAccount.nAccountID, iPacket);
            pSocket.pAccount.liAvatarData.add(pAvatar);
        }
        pSocket.SendPacket(CreateNewCharacterResult(pAvatar, bSuccess));
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
