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
package user;

import netty.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class CharacterData extends AvatarData {

    public static final long dbcharFlag = 0xFFFFFFFFFFFFFFFFL;
    public byte nCombatOrders = 0;
    public long nMoney = 0;
    public int nSlotHyper = 0;
    //GW_MonsterBattleRankInfo
    //BagData
    //MONSTERLIFE_INVITEINFO
    //GW_CoupleRecord
    //GW_FriendRecord
    //GW_MarriageRecord
    //GW_Core
    //GW_WildHunterInfo
    //GW_CharacterPotentialSkill
    //BuyLimitData
    //GW_ExpConsumeItem
            

    public CharacterData(int accountID) {
        super(accountID);
    }

    public void Encode(OutPacket oPacket) {
        oPacket.EncodeLong(dbcharFlag);
        oPacket.Encode(nCombatOrders);

        byte nKey = 3;
        do {
            oPacket.EncodeInteger(-20);//aPetActiveSkillCoolTime
            --nKey;
        } while (nKey > 0);

        oPacket.Encode(0); //if > 0 nPvPExp
        oPacket.Encode(0); //if > 0 weird loop
        oPacket.EncodeInteger(0); //if > 0 encode some filetime/willexp bs.
        oPacket.Encode(0); // (if byte > 0, decode a byte and an int. if int > 0, decodebuffer. after the loop, decode an int and if int > 0, decode buffer)
        
        if ((dbcharFlag & 1) != 0) {
            super.pCharacterStat.Encode(oPacket);
            oPacket.Encode(0); //might be buddylist capacity
            
            oPacket.Encode(0); // if > 0 encode string
            oPacket.Encode(0); // if > 0 encode string
            oPacket.Encode(0); // if > 0 encode string
        }
        
        if ((dbcharFlag & 2) != 0) {
            oPacket.EncodeLong(nMoney);
        }

        if ((dbcharFlag & 8) != 0 || (dbcharFlag & 0x2000000) != 0) {
            oPacket.EncodeInteger(0);//if > 0 encode GW_ExpConsumeItem bs
        }
        
        if ((dbcharFlag & 0x8000) != 0) {
            oPacket.EncodeInteger(0);//if > 0 loop through array of GW_MonsterBattleMobInfo::Encode 's
        }
        
    }
}
