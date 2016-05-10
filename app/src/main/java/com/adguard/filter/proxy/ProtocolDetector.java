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
package com.adguard.filter.proxy;

import com.adguard.commons.io.IoUtils;
import com.adguard.commons.utils.CharsetUtils;
import com.adguard.filter.html.Chars;
import com.adguard.filter.http.HttpMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to detect the protocol of incoming byte stream.
 */
public final class ProtocolDetector {

    private static final List<String> HTML_MARKERS = Arrays.asList("!doctype", "!--", "body", "html", "head", "div", "script", "meta");

    /**
     * Detects protocol of the input stream
     *
     * @param inputStream Stream to check
     * @return Protocol detected
     * @throws IOException                If something is wrong with the input stream
     * @throws IllegalArgumentException If stream does not support "mark"
     */
    public static Protocol detect(InputStream inputStream) throws IOException {
        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("Stream must support mark for protocol detection!");
        }

        byte[] packet = new byte[32];
        inputStream.mark(packet.length);
        int read = inputStream.read(packet);
        inputStream.reset();

        if (read < packet.length) {
            return Protocol.TOO_SMALL;
        } else if (ProtocolDetector.isHttpProtocol(packet)) {
            return Protocol.HTTP;
        } else if (ProtocolDetector.isTlsProtocol(packet)) {
            return Protocol.TLS;
        } else {
            return Protocol.OTHER;
        }
    }

    /**
     * Checks if incoming stream is HTML
     *
     * @param inputStream Input stream
     * @return true if this is a HTML protocol
     */
    public static boolean isHtml(InputStream inputStream) throws IOException {

        // At least 32 characters, should be enough to detect
        byte[] firstBytes = peekFirstNotEmptyBytes(inputStream, 32);

        if (firstBytes == null || firstBytes[0] != Chars.TAG_OPEN) {
            // If first not-empty symbol is not equal to Chars.TAG_OPEN - this is likely to be a non-HTML content
            return false;
        }

        for (String htmlMarker : HTML_MARKERS) {
            if (findStringIgnoreCase(firstBytes, 1, htmlMarker)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if first TCP packet is of TLS protocol
     *
     * @param packet First packet bytes
     * @return true for TLS protocol
     */
    private static boolean isTlsProtocol(byte[] packet) {
        // First byte: handshake type
        // Second and third bytes: TLS version (using 1.0)
        return (packet[0] & 0xff) == 0x16 &&
                (packet[1] & 0xff) == 0x03 &&
                (packet[2] & 0xff) == 0x01;
    }

    /**
     * Checks first TCP packet - if it is a HTTP protocol or not
     *
     * @param packet First packet bytes
     * @return true for HTTP
     */
    private static boolean isHttpProtocol(byte[] packet) {
        if (packet == null || packet.length == 0) {
            return false;
        }

        int firstLineIndex = ArrayUtils.indexOf(packet, (byte) IoUtils.LF);

        // Minimum line length: "GET / HTTP/1.1" is 14 symbols
        if (firstLineIndex >= 14) {
            // We have a line in first packet, so we should check HTTP version
            // That must have been at the end of line
            String line = new String(packet, firstLineIndex - 10, 10, CharsetUtils.DEFAULT_HTTP_ENCODING);
            return StringUtils.containsIgnoreCase(line, "HTTP/");
        }
        int firstSpaceIndex = ArrayUtils.indexOf(packet, (byte) IoUtils.SP);
        // Maybe packet was too small for whole packet to come in.
        // So we should check for valid HTTP method
        if (firstSpaceIndex <= 0) {
            return false;
        }

        String method = (new String(packet, 0, firstSpaceIndex)).trim().toUpperCase();
        return HttpMethod.isValidMethod(method);
    }

    /**
     * Peeks first N non-empty bytes from a peeking stream.
     *
     * @param inputStream Input stream
     * @param length      Length of the non-empty fragment we need
     * @return First non-empty bytes read or null if there is no fragment of the specified length
     */
    private static byte[] peekFirstNotEmptyBytes(InputStream inputStream, int length) throws IOException {

        final int markLength = 32 * 1024;
        int bytesRead = 0;
        int bytesFound = 0;
        byte[] firstBytes = new byte[length];
        byte[] buffer = new byte[512];

        inputStream.mark(markLength);
        try {
            while (bytesRead < markLength) {
                int bytesMarked = IOUtils.read(inputStream, buffer);

                // if stream is finished and we still didn't read the array of length we need
                if (bytesMarked <= 0) return null;
                bytesRead += bytesMarked;

                for (int i = 0; i < bytesMarked; i++) {
                    byte b = buffer[i];
                    boolean empty = IoUtils.isEmptyOrWhitespace(b) || IoUtils.isBomByte(b);
                    // bytesFound > 0 means that we have already found first non-empty character
                    // we also ignore zero byte to handle UTF-16
                    if ((!empty || bytesFound > 0) && b != 0) {
                        firstBytes[bytesFound] = b;
                        bytesFound++;
                    }

                    if (bytesFound == firstBytes.length) {
                        // Found enough bytes
                        return firstBytes;
                    }
                }

                // End of stream and we have not found enough bytes
                if (bytesMarked < buffer.length) {
                    return null;
                }
            }
        } finally {
            inputStream.reset();
        }

        return null;
    }

    /**
     * Searches for a substring in a byte array (ignoring characters case)
     *
     * @param bytes            Byte array to search in
     * @param searchStartIndex Index in the byte array (to start searching from that index)
     * @param searchString     Search string
     * @return true if substring was found
     */
    private static boolean findStringIgnoreCase(byte[] bytes, int searchStartIndex, String searchString) {
        if ((bytes.length - searchStartIndex) < searchString.length()) {
            return false;
        }

        for (int i = 0; i < searchString.length(); i++) {
            char c = searchString.charAt(i);
            byte b = bytes[i + searchStartIndex];
            if (b != c && b != Character.toUpperCase(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Protocols enumeration
     */
    public enum Protocol {

        /**
         * Plain HTTP protocol
         */
        HTTP,

        /**
         * TLS
         */
        TLS,

        /**
         * Other protocol
         */
        OTHER,

        /**
         * Cannot detect protocol because first packet is too small
         */
        TOO_SMALL
    }
}
