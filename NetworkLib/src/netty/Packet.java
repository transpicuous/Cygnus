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
package netty;

import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class Packet {

    private byte[] aData;

    public Packet() {
    }

    public Packet(byte[] aData) {
        this.aData = aData;
    }

    public int GetLength() {
        if (aData != null) {
            return aData.length;
        }
        return -1;
    }

    public int GetHeader() {
        if (aData.length < 2) {
            return -1;
        }
        return (aData[0] + (aData[1] << 8));
    }

    public void SetData(byte[] aData) {
        this.aData = aData;
    }

    public byte[] GetData() {
        return aData;
    }

    @Override
    public String toString() {
        if (aData == null) {
            return "";
        }
        return HexUtils.ToHex(aData);
    }

    public Packet Clone() {
        byte[] aClone = new byte[aData.length];
        System.arraycopy(aData, 0, aClone, 0, aData.length);
        return new Packet(aClone);
    }
}
