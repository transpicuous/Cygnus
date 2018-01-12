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
import netty.InPacket;
import netty.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class ZeroInfo {

    public static final short Flag = -1; //unsigned _int16
    public int nSubHP = 6910;
    public int nSubMP = 10;
    public int nSubSkin = 0;
    public int nSubHair = 37623;
    public int nSubFace = 21290;
    public int nSubMHP = 6910;
    public int nSubMMP = 1000;
    public int dbcharZeroLinkCashPart = 0;
    public int nMixBaseHairColor = -1;
    public int nMixAddHairColor = 0;
    public int nMixHairBaseProb = 0;
    public boolean bIsBeta = false;
    public int nLapis = 0;
    public int nLazuli = 0;

    public ZeroInfo() {
    }

    public void Encode(OutPacket oPacket) {
        if ((Flag & 1) > 0) {
            oPacket.Encode(bIsBeta);
        }
        if ((Flag & 2) > 0) {
            oPacket.EncodeInteger(nSubHP);
        }
        if ((Flag & 4) > 0) {
            oPacket.EncodeInteger(nSubMP);
        }
        if ((Flag & 8) > 0) {
            oPacket.Encode(nSubSkin);
        }
        if ((Flag & 0x10) > 0) {
            oPacket.EncodeInteger(nSubHair);
        }
        if ((Flag & 0x20) > 0) {
            oPacket.EncodeInteger(nSubFace);
        }
        if ((Flag & 0x40) > 0) {
            oPacket.EncodeInteger(nSubMHP);
        }
        if ((Flag & 0x80) > 0) {
            oPacket.EncodeInteger(nSubMMP);
        }
        if ((Flag & 0x100) > 0) {
            oPacket.EncodeInteger(dbcharZeroLinkCashPart);
        }
        if ((Flag & 0x200) > 0) {
            oPacket.EncodeInteger(nMixBaseHairColor);
            oPacket.EncodeInteger(nMixAddHairColor);
            oPacket.EncodeInteger(nMixHairBaseProb);
        }
    }

    public static ZeroInfo Decode(InPacket iPacket) {
        ZeroInfo ret = new ZeroInfo();
        if ((Flag & 1) > 0) {
            ret.bIsBeta = iPacket.DecodeBoolean();
        }
        if ((Flag & 2) > 0) {
            ret.nSubHP = iPacket.DecodeInteger();
        }
        if ((Flag & 4) > 0) {
            ret.nSubMP = iPacket.DecodeInteger();
        }
        if ((Flag & 8) > 0) {
            ret.nSubSkin = iPacket.DecodeByte();
        }
        if ((Flag & 0x10) > 0) {
            ret.nSubHair = iPacket.DecodeInteger();
        }
        if ((Flag & 0x20) > 0) {
            ret.nSubFace = iPacket.DecodeInteger();
        }
        if ((Flag & 0x40) > 0) {
            ret.nSubMHP = iPacket.DecodeInteger();
        }
        if ((Flag & 0x80) > 0) {
            ret.nSubMMP = iPacket.DecodeInteger();
        }
        if ((Flag & 0x100) > 0) {
            ret.dbcharZeroLinkCashPart = iPacket.DecodeInteger();
        }
        if ((Flag & 0x200) > 0) {
            ret.nMixBaseHairColor = iPacket.DecodeInteger();
            ret.nMixAddHairColor = iPacket.DecodeInteger();
            ret.nMixHairBaseProb = iPacket.DecodeInteger();
        }
        return ret;
    }

    public void Update(Connection c, int dwCharacterID) {
        try {
            PreparedStatement ps = c.prepareStatement("UPDATE ZeroInfo SET nSubHP = ?, nSubMP = ?, nSubSkin = ?,"
                    + " nSubHair = ?, nSubFace = ?, nSubMHP = ?, nSubMMP = ?, dbcharZeroLinkCashPart = ?, "
                    + "nMixBaseHairColor = ?, nMixAddHairColor = ?, nMixHairBaseProb = ?, bIsBeta = ?, nLapis = ?, nLazuli = ? "
                    + "WHERE dwCharacterID = ?");
            ps.setInt(1, nSubHP);
            ps.setInt(2, nSubMP);
            ps.setInt(3, nSubSkin);
            ps.setInt(4, nSubHair);
            ps.setInt(5, nSubFace);
            ps.setInt(6, nSubMHP);
            ps.setInt(7, nSubMMP);
            ps.setInt(8, dbcharZeroLinkCashPart);
            ps.setInt(9, nMixBaseHairColor);
            ps.setInt(10, nMixAddHairColor);
            ps.setInt(11, nMixHairBaseProb);
            ps.setBoolean(12, bIsBeta);
            ps.setInt(13, nLapis);
            ps.setInt(14, nLazuli);
            ps.setInt(15, dwCharacterID);
            ps.executeUpdate();
            ps.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SaveNew(Connection c, int dwCharacterID) {
        try {
            PreparedStatement ps = c.prepareStatement("INSERT INTO ZeroInfo (dwCharacterID, nSubHP, nSubMP, nSubSkin, nSubHair, nSubFace, nSubMHP, nSubMMP, "
                    + "dbcharZeroLinkCashPart, nMixBaseHairColor, nMixAddHairColor, nMixHairBaseProb, bIsBeta, nLapis, nLazuli) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, dwCharacterID);
            ps.setInt(2, nSubHP);
            ps.setInt(3, nSubMP);
            ps.setInt(4, nSubSkin);
            ps.setInt(5, nSubHair);
            ps.setInt(6, nSubFace);
            ps.setInt(7, nSubMHP);
            ps.setInt(8, nSubMMP);
            ps.setInt(9, dbcharZeroLinkCashPart);
            ps.setInt(10, nMixBaseHairColor);
            ps.setInt(11, nMixAddHairColor);
            ps.setInt(12, nMixHairBaseProb);
            ps.setBoolean(13, bIsBeta);
            ps.setInt(14, nLapis);
            ps.setInt(15, nLazuli);
            ps.executeUpdate();
            ps.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Load(Connection c, int dwCharacterID) {
        try {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM ZeroInfo WHERE dwCharacterID = ?");
            ps.setInt(1, dwCharacterID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                this.nSubHP = rs.getInt("nSubHP");
                this.nSubMP = rs.getInt("nSubMP");
                this.nSubSkin = rs.getInt("nSubSkin");
                this.nSubHair = rs.getInt("nSubHair");
                this.nSubFace = rs.getInt("nSubFace");
                this.nSubMHP = rs.getInt("nSubMHP");
                this.nSubMMP = rs.getInt("nSubMMP");
                this.dbcharZeroLinkCashPart = rs.getInt("dbcharZeroLinkCashPart");
                this.nMixBaseHairColor = rs.getInt("nMixBaseHairColor");
                this.nMixAddHairColor = rs.getInt("nMixAddHairColor");
                this.nMixHairBaseProb = rs.getInt("nMixHairBaseProb");
                this.bIsBeta = rs.getBoolean("bIsBeta");
                this.nLapis = rs.getInt("nLapis");
                this.nLazuli = rs.getInt("nLazuli");
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
