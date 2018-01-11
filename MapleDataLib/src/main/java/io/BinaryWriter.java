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
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Novak
 */
public class BinaryWriter {

    private static final Charset ASCII = Charset.forName("US-ASCII");
    private final ByteArrayOutputStream baos;
    private final FileOutputStream fos;
    private final Random rand;
    private final byte[] aIV;
    private byte[] aFileData;

    public BinaryWriter(String path) throws FileNotFoundException, IOException {
        this.rand = new Random();
        File newbin = new File(path);
        newbin.createNewFile();
        this.fos = new FileOutputStream(path, false);
        this.baos = new ByteArrayOutputStream(32);
        this.aIV = new byte[16];
        rand.nextBytes(aIV);
    }

    public void WriteFile() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileCrypto fcCrypto = new FileCrypto(aIV);
        aFileData = fcCrypto.Encrypt(baos.toByteArray());
        baos.reset();
        baos.write(aIV);
        baos.write(aFileData);
        fos.write(baos.toByteArray());
    }

    public final void Write(final byte[] b) {
        for (int x = 0; x < b.length; x++) {
            baos.write(b[x]);
        }
    }

    public final void Write(final byte b) {
        baos.write(b);
    }

    public final void WriteBool(final boolean b) {
        baos.write(b ? 1 : 0);
    }

    public final void WriteShort(final int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
    }

    public final void WriteShort(final short i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
    }

    public final void WriteInt(final int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 24) & 0xFF));
    }

    public final void WriteString(final String s) {
        WriteShort((short) s.length());
        BinaryWriter.this.Write(s.getBytes(ASCII));
    }

    public final void WritePos(final Point s) {
        BinaryWriter.this.WriteShort(s.x);
        BinaryWriter.this.WriteShort(s.y);
    }

    public final void WriteRect(final Rectangle s) {
        WriteInt(s.x);
        WriteInt(s.y);
        WriteInt(s.x + s.width);
        WriteInt(s.y + s.height);
    }

    public final void WriteLong(final long l) {
        baos.write((byte) (l & 0xFF));
        baos.write((byte) ((l >>> 8) & 0xFF));
        baos.write((byte) ((l >>> 16) & 0xFF));
        baos.write((byte) ((l >>> 24) & 0xFF));
        baos.write((byte) ((l >>> 32) & 0xFF));
        baos.write((byte) ((l >>> 40) & 0xFF));
        baos.write((byte) ((l >>> 48) & 0xFF));
        baos.write((byte) ((l >>> 56) & 0xFF));
    }

}
