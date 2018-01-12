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

import netty.OutPacket;
import netty.Packet;

/**
 *
 * @author Kaz Voeten
 */
public class CCenter {

    public static Packet ProcessLogin(int nSessionID, String sToken) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.ProcessLogin.getValue());
        oPacket.EncodeInteger(nSessionID);
        oPacket.EncodeString(sToken);
        return oPacket.ToPacket();
    }

    public static Packet CheckDuplicatedID(int nSessionID, String sCharacterName) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CheckDuplicateID.getValue());
        oPacket.EncodeInteger(nSessionID);
        oPacket.EncodeString(sCharacterName);
        return oPacket.ToPacket();
    }

    public static Packet CreateNewCharacter(int nSessionID, int nCharlistPosition, byte[] aData) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LoopBackPacket.CreateNewCharacter.getValue());
        oPacket.Encode(nSessionID);
        oPacket.EncodeInteger(nCharlistPosition);
        oPacket.Encode(aData);
        return oPacket.ToPacket();
    }

}
