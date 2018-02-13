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
package center.packet;

import net.OutPacket;


/**
 *
 * @author Kaz Voeten
 */
public class Center {

    public static OutPacket ProcessLogin(long nSessionID, String sToken) {
        
        OutPacket oPacket = new OutPacket(LoopBackPacket.ProcessLogin);
        oPacket.EncodeLong(nSessionID);
        oPacket.EncodeString(sToken);
        return oPacket;
    }

    public static OutPacket CheckDuplicatedID(long nSessionID, String sCharacterName) {
        
        OutPacket oPacket = new OutPacket(LoopBackPacket.CheckDuplicateID);
        oPacket.EncodeLong(nSessionID);
        oPacket.EncodeString(sCharacterName);
        return oPacket;
    }

    public static OutPacket CreateNewCharacter(long nSessionID, int nCharlistPosition, byte[] aData) {
        
        OutPacket oPacket = new OutPacket(LoopBackPacket.CreateNewCharacter);
        oPacket.EncodeLong(nSessionID);
        oPacket.EncodeInt(nCharlistPosition);
        oPacket.Encode(aData);
        return oPacket;
    }

    public static OutPacket UpdatePIC(long nSessionID, String sSPW) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.UpdateSPW);
        oPacket.EncodeLong(nSessionID);
        oPacket.EncodeString(sSPW);
        return oPacket;
    }

}
