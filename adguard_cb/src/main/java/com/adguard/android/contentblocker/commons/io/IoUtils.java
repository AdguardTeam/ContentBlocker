/**
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
 * <p>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.commons.io;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.adguard.android.contentblocker.commons.web.UrlUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Common methods for working with streams
 */
public class IoUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IoUtils.class);
    private static final int DOWNLOAD_LIMIT_SIZE = 5 * 1024 * 1024; // 5 MB

    /**
     * Closes a <code>Closeable</code> unconditionally.
     *
     * @param closeable the objects to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            LOG.debug("Suppressing an error while closing a Closeable\n", ioe);
        }
    }

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
     * Gets input stream from url
     *
     * @param context Application context
     * @param url     Path to file
     * @return Input stream from uri
     * @throws IOException Thrown if we can not open input stream
     */
    public static InputStream getInputStreamFromUrl(Context context, String url) throws IOException {
        InputStream inputStream;
        if (StringUtils.startsWith(url, "content://")) {
            ContentResolver contentResolver = context.getContentResolver();
            inputStream = contentResolver.openInputStream(Uri.parse(url));
        } else if (StringUtils.startsWith(url, "file://")) {
            String path = StringUtils.substringAfter(url, ":/");
            inputStream = getFileInputStream(path);
        } else {
            inputStream = getFileInputStream(url);
            if (inputStream == null) {
                String content = UrlUtils.downloadString(url, DOWNLOAD_LIMIT_SIZE);
                if (StringUtils.isNotBlank(content)) {
                    inputStream = new ByteArrayInputStream(content.getBytes());
                }
            }
            return inputStream;
        }

        checkInputStreamSize(inputStream);
        return inputStream != null ? new BufferedInputStream(inputStream) : null;
    }


    /**
     * Gets file input stream from url
     *
     * @param url File path
     * @return file         input stream
     * @throws IOException If file cannot be read
     */
    private static InputStream getFileInputStream(String url) throws IOException {
        File f = new File(url);
        if (f.exists() && f.isFile() && f.canRead()) {
            return new FileInputStream(f);
        }
        return null;
    }

    /**
     * Checks that the size of the input stream has exceeded the limit
     * If the input stream exceeds the limit, throw an exception
     *
     * @param inputStream input stream
     */
    private static void checkInputStreamSize(InputStream inputStream) throws IOException {
        if (inputStream != null && inputStream.available() > DOWNLOAD_LIMIT_SIZE) {
            throw new IOException("The input stream exceeded the limit of " + DOWNLOAD_LIMIT_SIZE + " bytes");
        }
    }
}
