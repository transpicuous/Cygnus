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
import java.util.List;

/**
 *
 * @author Kaz Voeten
 */
public enum EquipSlotType {
    Weapon(-11),
    Weapon_TakingBothSlot_Shield(-11),
    TamingMob(-18),
    TamingMob_MechanicTransistor(-1104),
    TamingMob_MechanicFrame(-1103),
    TamingMob_MechanicLeg(-1102),
    TamingMob_MechanicArm(-1101),
    TamingMob_MechanicEngine(-1100),
    TamingMob_DragonHat(-1000),
    TamingMob_DragonPendant(-1001),
    TamingMob_DragonWingAccessory(-1002),
    TamingMob_DragonTailAccessory(-1003),
    TamingMob_Android(-53),
    TamingMob_AndroidHeart(-54),
    TamingMob_Saddle(-24),
    Shoes(-7),
    Shoulder(-51),
    Si_Emblem(-61),
    Shield_OrDualBlade(-10),
    Ring(-12, -13, -15, -16),
    PetEquip,
    Pants(-6),
    Longcoat(-5),
    Coat(-5),
    Glove(-8),
    Cape(-9),
    Cap(-1),
    CashCap,
    Accessary_Pocket(-51),
    Accessary_Face(-2),
    Accessary_Eye(-3),
    Pendant(-59, -17), // -55 = pendant expansion.
    Medal(-49),
    Belt(-50),
    Earring(-4),
    Pet_ItemPounch(-120),
    Pet_MesoMagnet(-119),
    Bits,
    MonsterBook(-55),
    Badge(-56),
    Totem(-5000, -5001, -5002),
    UNKNOWN;

    private final List<Integer> nPossibleSlot;

    private EquipSlotType(int nSlot) {
        nPossibleSlot = new ArrayList<>();
        nPossibleSlot.add(nSlot);
    }

    private EquipSlotType(int... nSlots) {
        nPossibleSlot = new ArrayList<>();

        for (int i = 0; i < nSlots.length; i++) {
            nPossibleSlot.add(nSlots[i]);
        }
    }

    public List<Integer> GetPossibleSlots() {
        return nPossibleSlot;
    }

    public int GetSlot() {
        if (!nPossibleSlot.isEmpty()) {
            return nPossibleSlot.get(0);
        }
        throw new RuntimeException("[EquipSlotType] Item slot is unavailable for the EquipSlotType " + this.name());
    }

    public static EquipSlotType getSlotTypeFromString(final String wzName, final int nItemID) {
        switch (wzName) {
            case "Wp":
                return EquipSlotType.Weapon;
            case "WpSi":
                return EquipSlotType.Weapon_TakingBothSlot_Shield;
            case "Po":
                return EquipSlotType.Accessary_Pocket;
            case "Af":
                return EquipSlotType.Accessary_Face;
            case "Ay":
                return EquipSlotType.Accessary_Eye;
            case "Me":
                return EquipSlotType.Medal;
            case "Be":
                return EquipSlotType.Belt;
            case "Pe":
                return EquipSlotType.Pendant;
            case "Ae":
                return EquipSlotType.Earring;
            case "Sh":
                return EquipSlotType.Shoulder;
            case "Cp":
                return EquipSlotType.Cap;
            case "Sr":
                return EquipSlotType.Cape;
            case "Ma":
                return EquipSlotType.Coat;
            case "Gv":
                return EquipSlotType.Glove;
            case "MaPn":
                return EquipSlotType.Longcoat;
            case "Pn":
                return EquipSlotType.Pants;
            case "Ri":
                return EquipSlotType.Ring;
            case "Si":
                return EquipSlotType.Shield_OrDualBlade;
            case "So":
                return EquipSlotType.Shoes;
            case "Tm":
                final int div = nItemID / 1000;

                switch (div) {
                    case 1652: // 01652000.img
                        return EquipSlotType.TamingMob_MechanicTransistor;
                    case 1642:
                        return EquipSlotType.TamingMob_MechanicFrame;
                    case 1632:
                        return EquipSlotType.TamingMob_MechanicLeg;
                    case 1622:
                        return EquipSlotType.TamingMob_MechanicArm;
                    case 1612:
                        return EquipSlotType.TamingMob_MechanicEngine;
                    case 1662:
                        return EquipSlotType.TamingMob_Android;
                    case 1672:
                        return EquipSlotType.TamingMob_Android;
                    case 1942:
                        return EquipSlotType.TamingMob_DragonHat;
                    case 1952:
                        return EquipSlotType.TamingMob_DragonPendant;
                    case 1962:
                        return EquipSlotType.TamingMob_DragonWingAccessory;
                    case 1972:
                        return EquipSlotType.TamingMob_DragonTailAccessory;
                }
                return EquipSlotType.TamingMob;
            case "Sd":
                return EquipSlotType.TamingMob_Saddle;
            default:
                switch (nItemID) {
                    case 1812001: // item pounch
                        return EquipSlotType.Pet_ItemPounch;
                    case 1812000: // Meso Magnet
                        return EquipSlotType.Pet_MesoMagnet;
                }
                return EquipSlotType.UNKNOWN;
        }
    }

}
