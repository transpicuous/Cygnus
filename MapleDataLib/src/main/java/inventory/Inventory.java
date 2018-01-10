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

import java.util.ArrayList;

/**
 *
 * @author Kaz Voeten
 */
public class Inventory {
    public GW_ItemSlotBase[][] aaItemSlot = new GW_ItemSlotBase[5][60];//60 slots per inv for now
    public ArrayList<GW_ItemSlotBase> liEquipped = new ArrayList<>();
    public VirtualEquipInventory VEInventory= new VirtualEquipInventory();
    
    
    /**
     * Returns whether or not it's a bodypart based on the negative value of the poss
     * @param nPOS - pass negative poss
     * @return boolean is_nonbodypart
     */
    public static boolean Is_NonBodyPart(int nPOS) {
        return (nPOS - 1000) <= 3
                || (nPOS - 1100) <= 4
                || (nPOS - 1200) <= 6
                || (nPOS - 1300) <= 4
                || (nPOS - 1400) <= 0x18
                || (nPOS - 1500) <= 0xB
                || (nPOS - 5100) <= 5;
    }
    
    
    
    
}
