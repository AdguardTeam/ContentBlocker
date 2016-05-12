/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2016 Performix LLC. All rights reserved.

 Adguard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 Adguard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 Adguard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.commons.io;

import com.adguard.commons.utils.CharsetUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Common methods for working with streams
 */
public class IoUtils {

    public static final int BUFFER_SIZE = 16 * 1024;
    public static final ByteArrayPool BUFFER_POOL = new ByteArrayPool(BUFFER_SIZE);
    public static final int CR = 13;
    public static final int LF = 10;
    public static final int SP = 32;
    public static final int EOF = -1;

    /**
     * Reads input stream to end
     *
     * @param inputStream Input stream
     * @return Bytes being read
     * @throws IOException
     */
    public static byte[] readToEnd(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copyLarge(inputStream, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Copies from input to output stream
     *
     * @param inputStream  Input stream
     * @param outputStream Output stream
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        copy(inputStream, outputStream, BUFFER_SIZE);
    }

    /**
     * Copies from input to output stream
     *
     * @param inputStream  Input stream
     * @param outputStream Output stream
     * @param bufferSize   Buffer size
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {
        byte[] buffer;
        ByteArrayPool.ByteArray array = null;
        if (bufferSize > BUFFER_SIZE) {
            buffer = new byte[bufferSize];
        } else {
            array = BUFFER_POOL.getByteArray();
            buffer = array.getBytes();
        }

        IOUtils.copyLarge(inputStream, outputStream, buffer);
        outputStream.flush();
        if (array != null) {
            array.release();
        }
    }

    /**
     * Checks if this byte is commonly used for BOM
     * https://en.wikipedia.org/wiki/Byte_order_mark
     *
     * @param byteToCheck Byte to check
     * @return True if this byte is often used for BOM
     */
    public static boolean isBomByte(byte byteToCheck) {

        // UTF-8 BOM
        return (byteToCheck == (byte) 239 || byteToCheck == (byte) 187 || byteToCheck == (byte) 191) ||
                // UTF-16 BOM
                (byteToCheck == (byte) 254 || byteToCheck == (byte) 255);
    }

    /**
     * Checks if symbol is empty or whitespace
     *
     * @param symbol Symbol to check
     * @return true if CR LF or SP
     */
    public static boolean isEmptyOrWhitespace(int symbol) {
        return symbol == CR || symbol == LF || symbol == SP;
    }

    /**
     * Reads line from the inputStream stream (until CRLF symbols)
     *
     * @param inputStream Input stream
     * @return line as byte array
     */
    public static byte[] readLineBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int byteRead = inputStream.read();
        while (byteRead != IoUtils.EOF) {

            switch (byteRead) {
                case 0:
                    // Ignore zero bytes
                    // That's very unlikely to get it (and it's not normal)
                    break;
                case LF:
                    return outputStream.toByteArray();
                default:
                    outputStream.write(byteRead);
                    break;
            }

            byteRead = inputStream.read();
        }

        byte[] array = outputStream.toByteArray();
        if (array.length == 0) {
            throw new IOException("Input stream is closed");
        }

        return array;
    }

    /**
     * Reads line from the input stream
     *
     * @param inputStream Input stream
     * @param encoding    Characters encoding
     * @return Line read or null if response is empty
     */
    public static String readLine(InputStream inputStream, Charset encoding) throws IOException {
        byte[] lineBytes = readLineBytes(inputStream);
        if (lineBytes == null || lineBytes.length == 0) {
            return null;
        }
        return StringUtils.trim(new String(lineBytes, encoding));
    }

    /**
     * Reads line from the input stream and decodes it to string using default http encoding.
     *
     * @param inputStream Input stream
     * @return Line read or null if response is empty
     */
    public static String readLine(InputStream inputStream) throws IOException {
        return readLine(inputStream, CharsetUtils.DEFAULT_HTTP_ENCODING);
    }
}
