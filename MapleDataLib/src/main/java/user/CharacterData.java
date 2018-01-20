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

import inventory.GW_ItemSlotBase;
import inventory.ItemSlotIndex;
import netty.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class CharacterData {

    public static final long dbCharFlag = 0xFFFFFFFFFFFFFFFFL;
    public final AvatarData pAvatar;

    public GW_ItemSlotBase[][] aaItemSlot = new GW_ItemSlotBase[5][60];//60 slots per inv for now
    public GW_ItemSlotBase[] aEquipped = new GW_ItemSlotBase[ItemSlotIndex.BP_COUNT.GetValue()];
    // public VirtualEquipInventory VEInventory = new VirtualEquipInventory();

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

    public CharacterData(AvatarData pAvatar) {
        this.pAvatar = pAvatar;
    }

    public boolean IncreaseInventorySize(int nType, int nSize) {
        if (aaItemSlot[nType].length > 128) {
            return false;
        }
        
        int nIncrementedSize = aaItemSlot[nType].length + nSize;
        if (nIncrementedSize > 128) {
            nIncrementedSize = 128;
        }
        
        GW_ItemSlotBase[] aItemSlot = new GW_ItemSlotBase[nIncrementedSize];
        System.arraycopy(aaItemSlot[nType], 0, aItemSlot, 0, aaItemSlot[nType].length);
        aaItemSlot[nType] = aItemSlot;
        return true;
    }

    public void Encode(OutPacket oPacket) {
        oPacket.EncodeLong(dbCharFlag);
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

        if ((dbCharFlag & 1) != 0) {
            pAvatar.pCharacterStat.Encode(oPacket);
            oPacket.Encode(0); //might be buddylist capacity

            oPacket.Encode(0); // if > 0 encode string
            oPacket.Encode(0); // if > 0 encode string
            oPacket.Encode(0); // if > 0 encode string
        }

        if ((dbCharFlag & 2) != 0) {
            oPacket.EncodeLong(nMoney);
        }

        if ((dbCharFlag & 8) != 0 || (dbCharFlag & 0x2000000) != 0) {
            oPacket.EncodeInteger(0);//if > 0 encode GW_ExpConsumeItem bs
        }

        if ((dbCharFlag & 0x8000) != 0) {
            oPacket.EncodeInteger(0);//if > 0 loop through array of GW_MonsterBattleMobInfo::Encode 's
        }

    }
}
