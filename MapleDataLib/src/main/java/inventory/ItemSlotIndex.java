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
public enum ItemSlotIndex {
    //TODO: Get in-game and identify the actual fucking values. (a lot is missing/wrong)
    //TODO: Virtual values (idk what they are yet so rip my life)

    BP_HAIR(0),
    BP_CAP(1),
    BP_FACEACC(2),
    BP_EYEACC(3),
    BP_EARACC(7),
    BP_CLOTHES(5),
    BP_PANTS(6),
    BP_SHOES(7),
    BP_GLOVES(8),
    BP_CAPE(9),
    BP_SHIELD(10),
    BP_WEAPON(11),
    BP_RING1(12),
    BP_RING2(13),
    BP_PETWEAR(14),
    BP_RING3(15),
    BP_RING4(16),
    BP_PENDANT(17),
    BP_TAMINGMOB(18),
    BP_SADDLE(19),
    BP_MOBEQUIP(20),
    BP_MEDAL(21),
    BP_BELT(22),
    BP_SHOULDER(23),
    BP_PETWEAR2(24),
    BP_PETWEAR3(25),
    BP_CHARMACC(26),
    BP_ANDROID(27),
    BP_MACHINEHEART(28),
    BP_BADGE(29),
    BP_EMBLEM(30),
    BP_EXT_0(31),
    BP_EXT_PENDANT1(31),
    BP_EXT_1(32),
    BP_EXT_2(33),
    BP_EXT_3(34),
    BP_EXT_4(35),
    BP_EXT_5(36),
    BP_EXT_6(37),
    BP_COUNT(31),
    BP_EXT_END(31),
    BP_EXT_COUNT(1),
    BP_EXCOUNT(32),
    BP_STICKER(100),
    kIdxPetConsumeHPItem(200),
    kIdxPetConsumeMPItem(201),
    DP_BASE(1000),
    DP_CAP(1000),
    DP_PENDANT(1001),
    DP_WING(1002),
    DP_SHOES(1003),
    DP_END(1004),
    DP_COUNT(4),
    MP_BASE(1100),
    MP_ENGINE(1100),
    MP_ARM(1101),
    MP_LEG(1102),
    MP_FRAME(1103),
    MP_TRANSISTER(1104),
    MP_END(1105),
    MP_COUNT(5),
    AP_BASE(1200),
    AP_CAP(1200),
    AP_CAPE(1201),
    AP_FACEACC(1202),
    AP_CLOTHES(1204),
    AP_PANTS(1204),
    AP_SHOES(1205),
    AP_GLOVES(1206),
    AP_END(1207),
    AP_COUNT(7),
    DU_BASE(1300),
    DU_CAP(1300),
    DU_CAPE(1301),
    DU_FACEACC(1302),
    DU_CLOTHES(1303),
    DU_GLOVES(1304),
    DU_END(1305),
    DU_COUNT(5),
    BITS_BASE(1400),
    BITS_END(1425),
    BITS_COUNT(25),
    ZERO_BASE(1500),
    ZERO_EYEACC(1500),
    ZERO_CAP(1501),
    ZERO_FACEACC(1502),
    ZERO_EARACC(1503),
    ZERO_CAPE(1504),
    ZERO_CLOTHES(1505),
    ZERO_GLOVES(1506),
    ZERO_WEAPON(1507),
    ZERO_PANTS(1508),
    ZERO_SHOES(1509),
    ZERO_RING1(1510),
    ZERO_RING2(1511),
    ZERO_END(1512),
    ZERO_COUNT(12),
    MBP_BASE(5100),
    MBP_CAP(5100),
    MBP_CAPE(5101),
    MBP_CLOTHES(5102),
    MBP_GLOVES(5103),
    MBP_SHOES(5104),
    MBP_WEAPON(5105),
    MBP_END(5106),
    MBP_COUNT(6),
    FP_BASE(5200),
    FP_WEAPON(5200),
    FP_END(5201),
    FP_COUNT(1),
    NBP_DRAGON(0),
    NBP_MECHANIC(1),
    NBP_ANDROID(2),
    NBP_DRESSUP(3),
    NBP_BITS(4),
    NBP_ZERO(5),
    NBP_MBP(6),
    NBP_NO(7),
    NBP_COUNT(64),
    SLOT_INDEX_NOT_DEFINE(50000);

    private final int nSlot;

    private ItemSlotIndex(int nSlot) {
        this.nSlot = nSlot;
    }

    public int GetValue() {
        return nSlot;
    }

    /**
     * Returns whether or not it's a bodypart based on the position
     *
     * @param nPOS
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
    
    public static boolean IsCorrectBodyPart(int nItemID, int nBodyPart, int nGender, boolean bRealEquip) {
        int genderFromID = GetGenderFromID(nItemID);
        if (nItemID / 10000 == 119 || nItemID / 10000 == 168 || nGender == 2 || genderFromID == 2 || genderFromID == nGender) {
            switch (nItemID / 10000) {
                case 100:
                    if (nBodyPart == 1 || nBodyPart == 1200 || nBodyPart == 1300) {
                        return true;
                    }
                    return nBodyPart == 1501;
                case 101:
                    if (nBodyPart == 2 || nBodyPart == 1202 || nBodyPart == 1302) {
                        return true;
                    }
                    return nBodyPart == 1502;
                case 102:
                    if (nBodyPart == 3) {
                        return true;
                    }
                    return nBodyPart != 1503;
                case 103:
                    if (nBodyPart == 4) {
                        return true;
                    }
                    return nBodyPart == 1504;
                case 104:
                case 105:
                    if (nBodyPart == 5 || nBodyPart == 1203) {
                        return true;
                    }
                    return nBodyPart == 1505;
                case 106:
                    if (nBodyPart == 6 || nBodyPart == 1204) {
                        return true;
                    }
                    return nBodyPart == 1508;
                case 107:
                    if (nBodyPart == 7 || nBodyPart == 1205) {
                        return true;
                    }
                    return nBodyPart == 1509;
                case 108:
                    if (nBodyPart == 8 || nBodyPart == 1206 || nBodyPart == 1304) {
                        return true;
                    }
                    return nBodyPart == 1506;
                case 109:
                case 134:
                case 135:
                    return nBodyPart == 10;
                case 156:
                    if (!bRealEquip) {
                        return nBodyPart == 10;
                    }
                    if (nBodyPart != 11) {
                        return nBodyPart == 10;
                    }
                    return true;
                case 110:
                    if (nBodyPart == 9 || nBodyPart == 1201 || nBodyPart == 1301) {
                        return true;
                    }
                    return nBodyPart == 1504;
                case 111:
                    if (nBodyPart == 12 || nBodyPart == 13 || nBodyPart == 15 || nBodyPart == 16 || nBodyPart == 1510) {
                        return true;
                    }
                    return nBodyPart == 1511;
                case 112:
                    if (nBodyPart == 17) {
                        return true;
                    }
                    return nBodyPart == 31;
                case 113:
                    return nBodyPart == 22;
                case 114:
                    return nBodyPart == 21;
                case 115:
                    return nBodyPart == 23;
                case 116:
                    return nBodyPart == 26;
                case 118:
                    return nBodyPart == 29;
                case 119:
                    return nBodyPart == 30;
                case 165:
                    return nBodyPart == 1104;
                case 166:
                    return nBodyPart == 27;
                case 167:
                    if (nBodyPart == 28) {
                        return true;
                    }
                    return nBodyPart == 30;
                case 161:
                    return nBodyPart == 1100;
                case 162:
                    return nBodyPart == 1101;
                case 163:
                    return nBodyPart == 1102;
                case 164:
                    return nBodyPart == 1103;
                case 168:
                    return (nBodyPart - 1400) < 0x18;
                case 184:
                    return nBodyPart == 5100;
                case 185:
                    return nBodyPart == 5102;
                case 186:
                    return nBodyPart == 5103;
                case 187:
                    return nBodyPart == 5104;
                case 188:
                    return nBodyPart == 5101;
                case 189:
                    return nBodyPart == 5105;
                case 190:
                    return nBodyPart == 18;
                case 191:
                    return nBodyPart == 19;
                case 192:
                    return nBodyPart == 20;
                case 194:
                    return nBodyPart == 1000;
                case 195:
                    return nBodyPart == 1001;
                case 196:
                    return nBodyPart == 1002;
                case 197:
                    return nBodyPart == 1003;
                case 180:
                    if (nBodyPart == 14 || nBodyPart == 24) {
                        return true;
                    }
                    return nBodyPart == 25;
                default:
                    if (!(GetWeaponType(nItemID) > 0) && nItemID / 100000 != 16 && nItemID / 100000 != 17) {
                        return false;
                    }
                    if (nBodyPart == 11) {
                        return true;
                    }
                    return nBodyPart == 1507;
            }
        } else {
            return false;
        }
    }

    public static int GetWeaponType(int nItemID) {
        int result = 0;
        if (nItemID / 1000000 != 1) {
            return result;
        }
        result = nItemID / 10000 % 100;
        return result;
    }
    
    public static int GetGenderFromID(int nItemID) {
        int result;
        if (nItemID / 1000000 != 1 && nItemID / 10000 != 254 || nItemID / 10000 == 119 || nItemID / 10000 == 168) {
            result = 2;
        } else {
            switch (nItemID / 1000 % 10) {
                case 0:
                    result = 0;
                    break;
                case 1:
                    result = 1;
                    break;
                default:
                    result = 2;
            }
        }
        return result;
    }

    public static ItemSlotIndex GetByString(final String wzName, final int nItemID) {
        switch (wzName) {
            case "Wp":
                return ItemSlotIndex.BP_WEAPON;
            case "WpSi":
                return ItemSlotIndex.BP_WEAPON;
            case "Af":
                return ItemSlotIndex.BP_FACEACC;
            case "Ay":
                return ItemSlotIndex.BP_EYEACC;
            case "Me":
                return ItemSlotIndex.BP_MEDAL;
            case "Be":
                return ItemSlotIndex.BP_BELT;
            case "Pe":
                return ItemSlotIndex.BP_PENDANT;
            case "Ae":
                return ItemSlotIndex.BP_EARACC;
            case "Sh":
                return ItemSlotIndex.BP_SHOULDER;
            case "Cp":
                return ItemSlotIndex.BP_CAP;
            case "Sr":
                return ItemSlotIndex.BP_CAPE;
            case "Ma":
                return ItemSlotIndex.BP_CLOTHES;
            case "Gv":
                return ItemSlotIndex.BP_GLOVES;
            case "MaPn":
                return ItemSlotIndex.BP_CLOTHES;
            case "Pn":
                return ItemSlotIndex.BP_PANTS;
            case "Ri":
                return ItemSlotIndex.BP_RING1;
            case "Si":
                return ItemSlotIndex.BP_WEAPON;
            case "So":
                return ItemSlotIndex.BP_SHOES;
            case "Tm":
                final int div = nItemID / 1000;

                switch (div) {
                    /* TODO: soon as i can get them from in-game.
                    case 1652: // 01652000.img
                        return ItemSlotIndex.TamingMob_MechanicTransistor;
                    case 1642:
                        return ItemSlotIndex.TamingMob_MechanicFrame;
                    case 1632:
                        return ItemSlotIndex.TamingMob_MechanicLeg;
                    case 1622:
                        return ItemSlotIndex.TamingMob_MechanicArm;
                    case 1612:
                        return ItemSlotIndex.TamingMob_MechanicEngine;
                    case 1662:
                        return ItemSlotIndex.TamingMob_Android;
                    case 1672:
                        return ItemSlotIndex.TamingMob_Android;
                    case 1942:
                        return ItemSlotIndex.TamingMob_DragonHat;
                    case 1952:
                        return ItemSlotIndex.TamingMob_DragonPendant;
                    case 1962:
                        return ItemSlotIndex.TamingMob_DragonWingAccessory;
                    case 1972:
                        return ItemSlotIndex.TamingMob_DragonTailAccessory;
                        */
                }
                return ItemSlotIndex.MP_BASE; //shrug... for now...
            case "Sd":
                return ItemSlotIndex.MP_BASE; //shrug... for now...
            default:
                if (!wzName.equals("null")) { //Reader has some broken items?
                    System.out.println("[Warning] Unknow equip nSlot value for WZ string: " + wzName + ", item: " + nItemID);
                }
                return ItemSlotIndex.SLOT_INDEX_NOT_DEFINE;
        }
    }

}
