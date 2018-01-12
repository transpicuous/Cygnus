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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import netty.InPacket;
import netty.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class AvatarLook {

    public final int dwCharacterID;
    public byte nGender = 1; //for zero/ab
    public byte nSkin = 0;
    public int nFace = 0;
    public int nHair = 0;
    public int nJob = 0;
    public int nWeaponsStickerID = 0;
    public int nWeaponID = 0;
    public int nSubWeaponID = 0;
    public boolean bDrawElfEar = false;
    public int nXenonDefFaceAcc = 0;
    public int nDemonSlayerDefFaceAcc = 0;
    public int nBeastDefFaceAcc = 0;
    public int nBeastEars = 5010116;
    public int nBeastTail = 5010119;
    public byte nMixedHairColor = 0;
    public byte nMixHairPercent = 0;
    public int[] pets;
    public Map<Byte, Integer> anEquip = new HashMap<>();

    public AvatarLook(int dwCharacterID) {
        this.dwCharacterID = dwCharacterID;
    }

    public void SaveNew(Connection c) {
        try {
            PreparedStatement ps = c.prepareStatement("INSERT INTO AvatarLook (dwCharacterID, nGender, nSkin, nFace, nHair, "
                    + "nJob, nWeaponsStickerID, nWeaponID, nSubWeaponID, bDrawElfEar, nXenonDefFaceAcc, nDemonSlayerDefFaceAcc, nBeastDefFaceAcc, nBeastEars, nBeastTail, nMixedHairColor, nMixHairPercent) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            ps.setInt(1, dwCharacterID);
            ps.setByte(2, nGender);
            ps.setByte(3, nSkin);
            ps.setInt(4, nFace);
            ps.setInt(5, nHair);
            ps.setInt(6, nJob);
            ps.setInt(7, nWeaponsStickerID);
            ps.setInt(8, nWeaponID);
            ps.setInt(9, nSubWeaponID);
            ps.setBoolean(10, bDrawElfEar);
            ps.setInt(11, nXenonDefFaceAcc);
            ps.setInt(12, nDemonSlayerDefFaceAcc);
            ps.setInt(13, nBeastDefFaceAcc);
            ps.setInt(14, nBeastEars);
            ps.setInt(15, nBeastTail);
            ps.setInt(16, nMixedHairColor);
            ps.setInt(17, nMixHairPercent);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static AvatarLook LoadAvatarLook(Connection c, int dwCharacterID) {
        AvatarLook ret = new AvatarLook(dwCharacterID);
        try {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM AvatarLook WHERE dwCharacterID = ?");
            ps.setInt(1, dwCharacterID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ret.nGender = rs.getByte("nGender");
                ret.nSkin = rs.getByte("nSkin");
                ret.nFace = rs.getInt("nFace");
                ret.nHair = rs.getInt("nHair");
                ret.nJob = rs.getInt("nJob");
                ret.nWeaponsStickerID = rs.getInt("nWeaponsStickerID");
                ret.nWeaponID = rs.getInt("nWeaponID");
                ret.nSubWeaponID = rs.getInt("nSubWeaponID");
                ret.bDrawElfEar = rs.getBoolean("bDrawElfEar");
                ret.nXenonDefFaceAcc = rs.getInt("nXenonDefFaceAcc");
                ret.nDemonSlayerDefFaceAcc = rs.getInt("nDemonSlayerDefFaceAcc");
                ret.nBeastDefFaceAcc = rs.getInt("nBeastDefFaceAcc");
                ret.nBeastEars = rs.getInt("nBeastEars");
                ret.nBeastTail = rs.getInt("nBeastTail");
                ret.nMixedHairColor = rs.getByte("nMixedHairColor");
                ret.nMixHairPercent = rs.getByte("nMixHairPercent");
            }

            ps = c.prepareStatement("SELECT nItemID, nSlot FROM GW_ItemSlotEquip WHERE dwCharacterID = ?");
            ps.setInt(1, dwCharacterID);
            rs = ps.executeQuery();

            while (rs.next()) {
                ret.anEquip.put((byte) rs.getInt("nSlot"), rs.getInt("nItemID"));
            }
            ps.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public void Encode(OutPacket oPacket) {
        Encode(oPacket, null);
    }

    public void Encode(OutPacket oPacket, ZeroInfo zero) {
        oPacket.Encode(zero == null ? nGender : 1);
        oPacket.Encode(zero == null ? nSkin : zero.nSubSkin);
        oPacket.EncodeInteger(zero == null ? nFace : zero.nSubFace);
        oPacket.EncodeInteger(nJob);
        oPacket.Encode(false);
        oPacket.EncodeInteger(zero == null ? nHair : zero.nSubHair);

        HashMap<Byte, Integer> anHairEquip = new HashMap<>();
        HashMap<Byte, Integer> anUnseenEquip = new HashMap<>();
        HashMap<Byte, Integer> anTotemEquip = new HashMap<>();
        for (Map.Entry<Byte, Integer> item : anEquip.entrySet()) {
            byte pos = (byte) (item.getKey());
            if (pos > 127) {
                continue;
            }
            if (pos < 100 && !anHairEquip.containsKey(pos)) {
                anHairEquip.put(pos, item.getValue());
            } else if (pos > 100 && pos != 111) {
                pos -= 100;
                if (anHairEquip.containsKey(pos)) {
                    anUnseenEquip.put(pos, anHairEquip.get(pos));
                }
                anHairEquip.put(pos, item.getValue());
            } else if (anHairEquip.containsKey(pos)) {
                anUnseenEquip.put(pos, item.getValue());
            }
        }
        anHairEquip.forEach((k, v) -> oPacket.Encode(k).EncodeInteger(v));
        oPacket.Encode(0xFF);
        anUnseenEquip.forEach((k, v) -> oPacket.Encode(k).EncodeInteger(v));
        oPacket.Encode(0xFF);
        anTotemEquip.forEach((k, v) -> oPacket.Encode(k).EncodeInteger(v));
        oPacket.Encode(0xFF);

        oPacket.EncodeInteger(nWeaponsStickerID);
        oPacket.EncodeInteger(zero == null ? nWeaponID : zero.nLazuli);
        oPacket.EncodeInteger(nSubWeaponID > 0 ? nSubWeaponID : 0);

        oPacket.Encode(bDrawElfEar);
        oPacket.Encode(false);//new

        //pets todo
        for (int i = 0; i < 3; i++) {
            oPacket.EncodeInteger(0);
        }

        if (nJob / 100 != 31 && nJob != 3001) {
            if (nJob / 100 != 36 && nJob != 3002) {
                if (nJob != 10000 && nJob != 10100 && nJob != 10110 && nJob != 10111 && nJob != 10112) {
                    if (GW_CharacterStat.IsBeastJob(nJob)) {
                        oPacket.EncodeInteger(nBeastDefFaceAcc);
                        oPacket.Encode(true).EncodeInteger(nBeastEars);
                        oPacket.Encode(true).EncodeInteger(nBeastTail);
                    }
                } else {
                    oPacket.Encode(zero != null);
                }
            } else {
                oPacket.EncodeInteger(nXenonDefFaceAcc);
            }
        } else {
            oPacket.EncodeInteger(nDemonSlayerDefFaceAcc);
        }
        oPacket.Encode(nMixedHairColor);
        oPacket.Encode(nMixHairPercent);
    }

    public static AvatarLook Decode(int nCharacterID, InPacket iPacket) {
        AvatarLook ret = new AvatarLook(nCharacterID);
        ret.nGender = iPacket.DecodeByte();
        ret.nSkin = iPacket.DecodeByte();
        ret.nFace = iPacket.DecodeInteger();
        ret.nJob = iPacket.DecodeInteger();
        iPacket.DecodeByte();
        ret.nHair = iPacket.DecodeInteger();

        byte nPos = iPacket.DecodeByte();
        while (nPos != (byte) 0xFF) {
            ret.anEquip.put(nPos, iPacket.DecodeInteger());
            nPos = iPacket.DecodeByte();
        }
        nPos = iPacket.DecodeByte();
        while (nPos != (byte) 0xFF) {
            ret.anEquip.put(nPos, iPacket.DecodeInteger());
            nPos = iPacket.DecodeByte();
        }
        nPos = iPacket.DecodeByte();
        while (nPos != (byte) 0xFF) {
            ret.anEquip.put(nPos, iPacket.DecodeInteger());
            nPos = iPacket.DecodeByte();
        }

        ret.nWeaponsStickerID = iPacket.DecodeInteger();
        ret.nWeaponID = iPacket.DecodeInteger();
        ret.nSubWeaponID = iPacket.DecodeInteger();
        ret.bDrawElfEar = iPacket.DecodeBoolean();
        iPacket.DecodeBoolean(); //Welp, which job has a new thing?

        for (int i = 0; i < 3; i++) {
            iPacket.DecodeInteger();
        }

        if (ret.nJob / 100 != 31 && ret.nJob != 3001) {
            if (ret.nJob / 100 != 36 && ret.nJob != 3002) {
                if (ret.nJob != 10000 && ret.nJob != 10100 && ret.nJob != 10110 && ret.nJob != 10111 && ret.nJob != 10112) {
                    if (GW_CharacterStat.IsBeastJob(ret.nJob)) {
                        ret.nBeastDefFaceAcc = iPacket.DecodeInteger();
                        iPacket.DecodeByte();
                        ret.nBeastEars = iPacket.DecodeInteger();
                        iPacket.DecodeByte();
                        ret.nBeastTail = iPacket.DecodeInteger();
                    }
                } else {
                    iPacket.DecodeBoolean();//True if zero char.
                }
            } else {
                ret.nXenonDefFaceAcc = iPacket.DecodeInteger();
            }
        } else {
            ret.nDemonSlayerDefFaceAcc = iPacket.DecodeInteger();
        }
        ret.nMixedHairColor = iPacket.DecodeByte();
        ret.nMixHairPercent = iPacket.DecodeByte();
        return ret;
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
}
