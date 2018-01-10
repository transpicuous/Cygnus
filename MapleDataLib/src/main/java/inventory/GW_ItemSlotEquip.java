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
package inventory;

import netty.OutPacket;
import wz.item.EquipItem;

/**
 *
 * @author Kaz Voeten
 */
public class GW_ItemSlotEquip extends GW_ItemSlotBase {
    public GW_ItemSlotEquipBase eqBase = new GW_ItemSlotEquipBase();
    public int nPrevBonusExpRate = -1;
    public String sTitle = "";
    public boolean vSlot = false;
    public int nSlot = 0;
    
    //filetime
    public int ftEquippedLow = 0;
    public int ftEquippedHigh = 0;
    
    //uid for tracking
    public long liSN = 0;
    
    public int nGrade = 0; //potential grade
    public int nCHUC = 0; //enhancement grade
    public short nOption1 = 0; //pot line 1
    public short nOption2 = 0; //pot line 2
    public short nOption3 = 0; //pot line 3
    public short nOption4 = 0; //pot bonues line 1
    public short nOption5 = 0; //fusion anvil
    public short nOption6 = 0; //pot bonues line 2
    public short nOption7 = 0; //pot bonues line 3
    public short nSocketState = 0;
    public short nSocket1 = 0;
    public short nSocket2 = 0;
    public short nSocket3 = 0;
    public short nSoulOptionID = 0;
    public short nSoulSocketID = 0;
    public short nSoulOption = 0;

    public GW_ItemSlotEquip(int nItemID, EquipItem base) {
        super(nItemID, 1);
        applyStats(base.eqStats);
        
    }
    
    private void applyStats(GW_ItemSlotEquipBase pBase) {
        pBase.mStats.forEach((nFlag, nValue) -> this.eqBase.mStats.put(nFlag, nValue));
    }
    
    @Override
    public void RawEncode(OutPacket oPacket) {
        super.RawEncode(oPacket);
        eqBase.Encode(oPacket);
        
        oPacket.EncodeString(sTitle);
        
        oPacket.Encode(nGrade);
        oPacket.Encode(nCHUC);
        oPacket.EncodeShort(nOption1); 
        oPacket.EncodeShort(nOption2); 
        oPacket.EncodeShort(nOption3); 
        oPacket.EncodeShort(nOption4);
        oPacket.EncodeShort(nOption6);
        oPacket.EncodeShort(nOption7);
        oPacket.EncodeShort(nOption5);
        
        oPacket.EncodeShort(nSocketState);
        oPacket.EncodeShort(nSocket1);
        oPacket.EncodeShort(nSocket2);
        oPacket.EncodeShort(nSocket3);
        
        if (cashOpt != null) {
            oPacket.EncodeInteger(cashOpt.liCashItemSNLow);
            oPacket.EncodeInteger(cashOpt.liCashItemSNHigh);
        }
        
        oPacket.EncodeInteger(ftEquippedLow);
        oPacket.EncodeInteger(ftEquippedHigh);
        
        oPacket.EncodeInteger(nPrevBonusExpRate);
        
        cashOpt.Encode(oPacket);
        
        oPacket.EncodeShort(nSoulOptionID);
        oPacket.EncodeShort(nSoulSocketID);
        oPacket.EncodeShort(nSoulOption);
    }
}
