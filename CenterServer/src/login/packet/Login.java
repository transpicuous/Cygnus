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

import database.Database;
import game.GameServerSessionManager;
import character.inventory.GW_ItemSlotEquip;
import character.inventory.ItemSlotIndex;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import login.LoginServerSocket;
import login.LoginSessionManager;
import net.InPacket;
import net.OutPacket;
import server.Server;
import account.Account;
import character.AvatarData;

/**
 *
 * @author Kaz Voeten
 */
public class Login {

    public static void GameServerInformation() {
        
        OutPacket oPacket = new OutPacket(LoopBackPacket.ChannelInformation);
        oPacket.EncodeByte(GameServerSessionManager.aSessions.size());
        GameServerSessionManager.aSessions.forEach((pGameServer) -> {
            oPacket.EncodeInt(pGameServer.nChannelID);
            oPacket.EncodeInt(pGameServer.nMaxUsers);
            oPacket.EncodeInt(pGameServer.nPort);
            oPacket.EncodeString(pGameServer.GetIP());
        });
        LoginSessionManager.pSession.SendPacket(oPacket);
    }

    public static void OnCheckDuplicateID(LoginServerSocket pSocket, InPacket iPacket) {
        long nSessionID = iPacket.DecodeLong();
        String sCharacterName = iPacket.DecodeString();
        boolean bDuplicatedID = false;

        if (sCharacterName.length() < 13
                || !Server.GetInstance().pDataFactory.pETCFactory.IsLegalName(sCharacterName)
                || (pSocket.mReservedCharacterNames.containsKey(sCharacterName) && (pSocket.mReservedCharacterNames.get(sCharacterName) != nSessionID))) {
            bDuplicatedID = true;
        }

        
        try (Connection con = Database.GetConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT dwCharacterID FROM gw_characterstat WHERE sCharacterName = ?");
            ps.setString(1, sCharacterName);

            ResultSet rs = ps.executeQuery();
            bDuplicatedID = rs.next();

            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!bDuplicatedID) {
            pSocket.mReservedCharacterNames.putIfAbsent(sCharacterName, nSessionID);
        }

        pSocket.SendPacket((new OutPacket(LoopBackPacket.CheckDuplicatedIDResponse))
                .EncodeLong(nSessionID)
                .EncodeString(sCharacterName)
                .EncodeBool(bDuplicatedID)
                
        );
    }

    public static void OnCreateNewCharacter(LoginServerSocket pSocket, InPacket iPacket) {
        long nSessionID = iPacket.DecodeLong();
        int nCharListPosition = iPacket.DecodeInt();
        String sCharacterName = iPacket.DecodeString();
        if (!pSocket.mReservedCharacterNames.containsKey(sCharacterName)
                || (pSocket.mReservedCharacterNames.get(sCharacterName) != nSessionID)) {
            
            OutPacket oPacket = new OutPacket(LoopBackPacket.OnCreateCharacterResponse);
            oPacket.EncodeLong(nSessionID);
            oPacket.EncodeBool(false);
            pSocket.SendPacket(oPacket);
            return;
        }

        pSocket.mReservedCharacterNames.remove(sCharacterName); //Only remove once claimed.

        Account pAccount = pSocket.mAccountStorage.get(nSessionID);
        AvatarData pAvatar = AvatarData.CreateAvatar(pAccount.nAccountID, nCharListPosition);
        pAvatar.pCharacterStat.sCharacterName = sCharacterName;

        int nFKMOption = iPacket.DecodeInt();
        iPacket.DecodeInt();
        int nCurSelectedRace = iPacket.DecodeInt();
        int nCurSelectedSubJob = iPacket.DecodeShort();
        int nGender = iPacket.DecodeByte();
        int nSkin = iPacket.DecodeByte();
        int nSize = iPacket.DecodeByte(); //num ints after this byte.
        int nFace = iPacket.DecodeInt();
        int nHair = iPacket.DecodeInt();

        pAvatar.SetFace(nFace);
        pAvatar.SetGender((byte) nGender);
        pAvatar.SetHair(nHair);
        pAvatar.SetSkin(nSkin);
        iPacket.DecodeInt();

        HashMap<Byte, Integer> mBody = new HashMap<>();
        for (int i = 0; i < (nSize - 3); i++) {
            int nItemID = iPacket.DecodeInt();
            int nItemGender = ItemSlotIndex.GetGenderFromID(nItemID);
            if (nItemGender != 2 && nItemGender != nGender) {
                continue;
            }
            GW_ItemSlotEquip pItem = GW_ItemSlotEquip.Create(
                    pAvatar.dwCharacterID,
                    nItemID
            );
            if (pItem != null) {
                pItem.nSlot = ItemSlotIndex.GetBodyPartFromItem(nItemID, nGender);
                pItem.Save(pAvatar.dwCharacterID);
                mBody.put((byte) pItem.nSlot, nItemID);
            }
        }
        pAvatar.pAvatarLook.anEquip = mBody;

        switch (nCurSelectedRace) {
            case -1: //UltimateAdventurer
                pAvatar.pCharacterStat.nJob = 0;
                pAvatar.pCharacterStat.dwPosMap = 100000000;
                break;
            case 0://Resistance
                pAvatar.pCharacterStat.nJob = 3000;
                pAvatar.pCharacterStat.dwPosMap = 931000000;
                break;
            case 1://Adventurer
                pAvatar.pCharacterStat.nJob = 0;
                pAvatar.pCharacterStat.dwPosMap = 10000;
                break;
            case 2://Cygnus
                pAvatar.pCharacterStat.nJob = 1000;
                pAvatar.pCharacterStat.dwPosMap = 130030000;
                break;
            case 3://Aran
                pAvatar.pCharacterStat.nJob = 2000;
                pAvatar.pCharacterStat.dwPosMap = 914000000;
                break;
            case 4://Evan
                pAvatar.pCharacterStat.nJob = 2001;
                pAvatar.pCharacterStat.dwPosMap = 900010000;
                break;
            case 5://Mercedes
                pAvatar.pCharacterStat.nJob = 2002;
                pAvatar.pCharacterStat.dwPosMap = 910150000;
                break;
            case 6://Demon
                pAvatar.pCharacterStat.nJob = 3001;
                pAvatar.pCharacterStat.dwPosMap = 927020070;
                break;
            case 7://Phantom
                pAvatar.pCharacterStat.nJob = 2003;
                pAvatar.pCharacterStat.dwPosMap = 915000000;
                break;
            case 8://DualBlade
                pAvatar.pCharacterStat.nJob = 0;
                pAvatar.pCharacterStat.dwPosMap = 103050900;
                break;
            case 9://Mihile
                pAvatar.pCharacterStat.nJob = 5000;
                pAvatar.pCharacterStat.dwPosMap = 913070000;
                break;
            case 10://Luminous
                pAvatar.pCharacterStat.nJob = 2004;
                pAvatar.pCharacterStat.dwPosMap = 931030000;
                pAvatar.pCharacterStat.nLevel = 10;
                pAvatar.pCharacterStat.nINT = 57;
                pAvatar.pCharacterStat.nMHP = 500;
                pAvatar.pCharacterStat.nHP = 500;
                pAvatar.pCharacterStat.nMMP = 1000;
                pAvatar.pCharacterStat.nMP = 1000;
                break;
            case 11://Kaiser
                pAvatar.pCharacterStat.nJob = 6000;
                pAvatar.pCharacterStat.dwPosMap = 940001000;
                mBody.put((byte) -10, 1352500);
                break;
            case 12://AngelicBuster
                pAvatar.pCharacterStat.nJob = 6001;
                pAvatar.pCharacterStat.dwPosMap = 940011000;
                pAvatar.pCharacterStat.nLevel = 10;
                pAvatar.pCharacterStat.nDEX = 68;
                pAvatar.pCharacterStat.nMHP = 1000;
                pAvatar.pCharacterStat.nHP = 1000;
                pAvatar.pCharacterStat.aSP[0] = 3;
                mBody.put((byte) -10, 1352601);
                //TODO: pCharacterData! AngelicBusterInfo stuff!
                break;
            case 13://Cannoneer
                pAvatar.pCharacterStat.nJob = 0;
                pAvatar.pCharacterStat.dwPosMap = 3000000;
                break;
            case 14://Xenon
                pAvatar.pCharacterStat.nJob = 3002;
                pAvatar.pCharacterStat.dwPosMap = 931050920;
                break;
            case 15://Zero
                pAvatar.pCharacterStat.nJob = 10112;
                pAvatar.pCharacterStat.dwPosMap = 321000000;
                pAvatar.pCharacterStat.nLevel = 100;
                pAvatar.pCharacterStat.nSTR = 518;
                pAvatar.pCharacterStat.nMHP = 6910;
                pAvatar.pCharacterStat.nHP = 6910;
                pAvatar.pCharacterStat.nMMP = 100;
                pAvatar.pCharacterStat.nMP = 100;
                pAvatar.pCharacterStat.aSP[0] = 3;
                pAvatar.pCharacterStat.aSP[1] = 3;
                pAvatar.pZeroInfo.nSubSkin = nSkin;
                pAvatar.pZeroInfo.nSubFace = 21290;
                pAvatar.pZeroInfo.nSubHair = 37623;
                pAvatar.pZeroInfo.nSubHP = 6910;
                pAvatar.pZeroInfo.nSubMHP = 6910;
                pAvatar.pZeroInfo.nSubMP = 100;
                pAvatar.pZeroInfo.nSubMMP = 100;
                break;
            case 16://Shade
                pAvatar.pCharacterStat.nJob = 2005;
                pAvatar.pCharacterStat.dwPosMap = 927030050;
                break;
            case 17://Jett
                pAvatar.pCharacterStat.nJob = 0;
                pAvatar.pCharacterStat.dwPosMap = 552000050;
                break;
            case 18://Hayato
                pAvatar.pCharacterStat.nJob = 4001;
                pAvatar.pCharacterStat.dwPosMap = 807000000;
                break;
            case 19://Kanna
                pAvatar.pCharacterStat.nJob = 4002;
                pAvatar.pCharacterStat.dwPosMap = 807040000;
                break;
            case 20://BeastTamer
                pAvatar.pCharacterStat.nJob = 11212;
                pAvatar.pCharacterStat.dwPosMap = 866000000;
                pAvatar.pCharacterStat.nLevel = 10;
                pAvatar.pCharacterStat.nMHP = 567;
                pAvatar.pCharacterStat.nHP = 551;
                pAvatar.pCharacterStat.nMMP = 270;
                pAvatar.pCharacterStat.nMP = 263;
                pAvatar.pCharacterStat.nAP = 45;
                pAvatar.pCharacterStat.aSP[0] = 3;
                break;
            case 21://PinkBean
                pAvatar.pCharacterStat.nJob = 13100;
                pAvatar.pCharacterStat.dwPosMap = 866000000;
                break;
            case 22://Kinesis
                pAvatar.pCharacterStat.nJob = 14000;
                pAvatar.pCharacterStat.dwPosMap = 331001110;
                pAvatar.pCharacterStat.nLevel = 10;
                pAvatar.pCharacterStat.nINT = 52;
                pAvatar.pCharacterStat.nMHP = 374;
                pAvatar.pCharacterStat.nHP = 374;
                pAvatar.pCharacterStat.nMMP = 5;
                pAvatar.pCharacterStat.nMP = 5;
                break;
            default:
                
                OutPacket oPacket = new OutPacket(LoopBackPacket.OnCreateCharacterResponse);
                oPacket.EncodeLong(nSessionID);
                oPacket.EncodeBool(false);
                pSocket.SendPacket(oPacket);
                return;
        }

        pAvatar.SaveNew();
        
        OutPacket oPacket = new OutPacket(LoopBackPacket.OnCreateCharacterResponse);
        oPacket.EncodeLong(nSessionID);
        oPacket.EncodeBool(true);
        pAvatar.Encode(oPacket, pAccount.nGradeCode <= 0);
        pSocket.SendPacket(oPacket);
    }
}
