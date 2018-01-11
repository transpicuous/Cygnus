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

import java.io.IOException;

public class ByteArrayByteStream {

    private int nPos = 0;
    private long nRead = 0;
    private final byte[] aData;

    /**
     * Class constructor.
     *
     * @param aData Array of bytes to wrap the stream around.
     */
    public ByteArrayByteStream(final byte[] aData) {
        this.aData = aData;
    }

    /**
     * Gets the current position of the stream.
     *
     * @return The current position of the stream.
     * @see SeekableInputStreamBytestream#getPosition()
     */
    public long GetPosition() {
        return nPos;
    }

    /**
     * Seeks the pointer the the specified position.
     *
     * @param offset The position you wish to seek to.
     * @throws java.io.IOException
     * @see SeekableInputStreamBytestream#seek(long)
     */
    public void Seek(final long offset) throws IOException {
        nPos = (int) offset;
    }

    /**
     * Returns the numbers of bytes read from the stream.
     *
     * @return The number of bytes read.
     * @see ByteInputStream#getBytesRead()
     */
    public long GetBytesRead() {
        return nRead;
    }

    /**
     * Reads a byte from the current position.
     *
     * @return The byte as an integer.
     * @see ByteInputStream#readByte()
     */
    public int ReadByte() {
        nRead++;
        return ((int) aData[nPos++]) & 0xFF;
    }

    /**
     * @action: Lowers position
     */
    public void UnreadByte() {
        nRead--;
    }

    /**
     * Reads a byte from the previous position.
     *
     * @return The byte as an integer.
     */
    public int ReadLastByte() {
        return ((int) aData[nPos]) & 0xFF;
    }

    /**
     * Reads a byte from the very previous position.
     *
     * @param bytes
     * @return The byte as an integer.
     */
    public int[] ReadLastBytes(int bytes) {
        while (nPos - bytes < 1) {
            bytes--; //Causing less errors
        }
        int[] a = null;
        int b = 0;
        while (bytes > 0) {
            a[b] += ((int) aData[nPos - bytes]);
            bytes--;
            b++;
        }
        return a;
    }

    /**
     * Returns the number of bytes available from the stream.
     *
     * @return Number of bytes available as a long integer.
     * @see ByteInputStream#available()
     */
    public long Available() {
        return aData.length - nPos;
    }
}
