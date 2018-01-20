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
package io;

import java.awt.Point;
import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Novak
 */
public class BinaryReader {

    private final ByteArrayByteStream pStream;
    byte[] aData, aIV;

    public BinaryReader(String filedata) {
        final File file = new File(filedata);
        if (!file.exists()) {
            throw new RuntimeException(String.format("File %s do not exist!", filedata));
        }

        try {
            byte[] aFileData = Files.readAllBytes(file.toPath());

            //Get IV
            this.aIV = new byte[16];
            System.arraycopy(aFileData, 0, aIV, 0, aIV.length);
            FileCrypto fcCrypto = new FileCrypto(aIV);

            //Trim IV off of data and decrypt data
            this.aData = new byte[aFileData.length - aIV.length];
            System.arraycopy(aFileData, aIV.length, aData, 0, aData.length);
            aData = fcCrypto.Decrypt(aData);

            this.pStream = new ByteArrayByteStream(aData);

        } catch (Exception ex) {
            Logger.getLogger(BinaryReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(String.format("File %s couldn't be read!", filedata));
        }
    }

    public final byte ReadByte() {
        return (byte) pStream.ReadByte();
    }

    public final boolean ReadBool() {
        return pStream.ReadByte() != 0;
    }

    public final int ReadInt() {
        final int byte1 = pStream.ReadByte();
        final int byte2 = pStream.ReadByte();
        final int byte3 = pStream.ReadByte();
        final int byte4 = pStream.ReadByte();
        return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    public final short ReadShort() {
        final int byte1 = pStream.ReadByte();
        final int byte2 = pStream.ReadByte();
        return (short) ((byte2 << 8) + byte1);
    }

    public final long ReadLong() {
        final int byte1 = pStream.ReadByte();
        final int byte2 = pStream.ReadByte();
        final int byte3 = pStream.ReadByte();
        final int byte4 = pStream.ReadByte();
        final long byte5 = pStream.ReadByte();
        final long byte6 = pStream.ReadByte();
        final long byte7 = pStream.ReadByte();
        final long byte8 = pStream.ReadByte();

        return (long) ((byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16)
                + (byte2 << 8) + byte1);
    }

    public final String ReadAsciiString(final int n) {
        final char ret[] = new char[n];
        for (int x = 0; x < n; x++) {
            ret[x] = (char) pStream.ReadByte();
        }
        return new String(ret);
    }

    public final String ReadString() {
        return ReadAsciiString(ReadShort());
    }

    public final Point ReadPos() {
        final int x = ReadShort();
        final int y = ReadShort();
        return new Point(x, y);
    }

    public final byte[] Read(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) pStream.ReadByte();
        }
        return data;
    }
}
