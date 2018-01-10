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
package server.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import netty.OutPacket;
import user.AvatarData;

/**
 *
 * @author Kaz Voeten
 */
public class Account {

    public final int nAccountID, nSessionID;
    public final String sAccountName, sIP, sPIC;
    public final byte nState, nGender, nAdmin;
    public final Date dBirthDay, dLastLoggedIn;
    private List<AvatarData> avatars = new LinkedList<>();

    public Account(int nAccountID, int nSessionID, String sAccountName, String sIP, String sPIC,
            byte nState, byte nGender, Date dBirthDay, Date dLastLoggedIn, byte nAdmin) {
        this.nAccountID = nAccountID;
        this.nSessionID = nSessionID;
        this.sAccountName = sAccountName;
        this.sIP = sIP;
        this.sPIC = sPIC;
        this.nState = nState;
        this.nGender = nGender;
        this.dBirthDay = dBirthDay;
        this.dLastLoggedIn = dLastLoggedIn;
        this.nAdmin = nAdmin;
    }
    
    public void Encode(OutPacket oPacket) {
        oPacket.EncodeInteger(nAccountID);
        oPacket.EncodeInteger(nSessionID);
        oPacket.EncodeString(sAccountName);
        oPacket.EncodeString(sIP);
        oPacket.EncodeString(sPIC);
        oPacket.Encode(nState);
        oPacket.Encode(nGender);
        oPacket.Encode(nAdmin);
    }

    public List<AvatarData> GetAvatars(int nAccountID, Connection con, boolean bReload) {
        if (!bReload) {
            return avatars;
        }
        LinkedList<AvatarData> ret = new LinkedList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT dwCharacterID FROM AvatarData WHERE accountID = ?");
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
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return avatars = ret;
    }
}
