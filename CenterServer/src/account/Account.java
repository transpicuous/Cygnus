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
package account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.OutPacket;
import character.AvatarData;
import character.CharacterData;
import database.Database;

/**
 *
 * @author Kaz Voeten
 */
public class Account {

    public final int nAccountID;
    public long nSessionID;
    public int nNexonCash, nMaplePoint, nMileage, nLastWorldID;
    public String sAccountName, sIP, sToken, sSecondPW;
    public byte nState, nGender, nGradeCode;
    public Date dBirthDay, dLastLoggedIn, dCreateDate;
    private List<AvatarData> avatars = new LinkedList<>(); //Charlist
    public CharacterData pCharacterData; //Where we add the logged in character for transitions.

    public Account(int nAccountID, long nSessionID, String sAccountName, String sIP, String sSecondPW,
            byte nState, byte nGender, Date dLastLoggedIn, Date dBirthDay, Date dCreateDate, byte nGradeCode, String sToken,
            short nLastWorldID, int nNexonCash, int nMaplePoint, int nMileage) {
        this.nAccountID = nAccountID;
        this.nSessionID = nSessionID;
        this.sAccountName = sAccountName;
        this.sIP = sIP;
        this.sSecondPW = sSecondPW;
        this.nState = nState;
        this.nGender = nGender;
        this.dLastLoggedIn = dLastLoggedIn;
        this.dBirthDay = dBirthDay;
        this.dCreateDate = dCreateDate;
        this.nGradeCode = nGradeCode;
        this.sToken = sToken;
        this.nLastWorldID = nLastWorldID;
        this.nNexonCash = nNexonCash;
        this.nMaplePoint = nMaplePoint;
        this.nMileage = nMileage;
    }

    public void Encode(OutPacket oPacket) {
        oPacket.EncodeInt(nAccountID);
        oPacket.EncodeLong(nSessionID);
        oPacket.EncodeString(sAccountName);
        oPacket.EncodeString(sIP);
        oPacket.EncodeString(sSecondPW);
        oPacket.EncodeByte(nState);
        oPacket.EncodeByte(nGender);
        oPacket.EncodeByte(nGradeCode);
    }

    public List<AvatarData> GetAvatars(int nAccountID, boolean bReload) {
        if (!bReload) {
            return avatars;
        }
        LinkedList<AvatarData> ret = new LinkedList<>();
        try (Connection con = Database.GetConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT dwCharacterID FROM avatardata WHERE nAccountID = ?");
            ps.setInt(1, nAccountID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(AvatarData.LoadAvatar(con, rs.getInt("dwCharacterID")));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return avatars;
        } 
        return avatars = ret;
    }
}
