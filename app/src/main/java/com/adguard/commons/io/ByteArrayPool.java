package com.adguard.commons.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Pool for reusable byte arrays
 */
public class ByteArrayPool {

    /**
     * Max size of the byte arrays in the pool.
     * 512 Kb by default.
     */
    private final static int MAX_POOL_SIZE = 1024 * 1024;

    private final Object syncRoot = new Object();
    private final int arraySize;
    private final int maxAvailableArraysCount;
    private final LinkedList<ByteArray> availableByteArrays = new LinkedList<>();

    /**
     * Creates an instance of the ByteArrayPool
     *
     * @param arraySize Size of arrays
     */
    public ByteArrayPool(int arraySize) {
        this(arraySize, MAX_POOL_SIZE);
    }

    /**
     * Creates an instance of the ByteArrayPool
     *
     * @param arraySize   Byte array size
     * @param maxPoolSize Max size of the pool in bytes
     */
    public ByteArrayPool(int arraySize, int maxPoolSize) {
        this.arraySize = arraySize;
        this.maxAvailableArraysCount = maxPoolSize / arraySize;
    }

    /**
     * @return Size of arrays allocated by this pool
     */
    public int getArraySize() {
        return arraySize;
    }

    /**
     * Gets or creates ByteArray object.
     *
     * @return ByteArray object
     */
    public ByteArray getByteArray() {
        synchronized (syncRoot) {
            if (availableByteArrays.size() > 0) {
                return availableByteArrays.removeLast();
            } else {
                byte[] bytes = new byte[arraySize];
                return new ByteArray(bytes, this);
            }
        }
    }

    /**
     * Releases specified byte array
     *
     * @param byteArray Byte array
     */
    private void release(ByteArray byteArray) {
        synchronized (syncRoot) {
            if (availableByteArrays.size() >= maxAvailableArraysCount) {
                // Ignore released byte array
                return;
            }

            if (!availableByteArrays.contains(byteArray)) {
                availableByteArrays.add(byteArray);
            }
        }
    }

    /**
     * ByteArray wrapper.
     */
    public static class ByteArray {

        private final byte[] bytes;
        private final ByteArrayPool parentPool;
        private int contentLength;

        private ByteArray(byte[] bytes, ByteArrayPool parentPool) {
            this.bytes = bytes;
            this.parentPool = parentPool;
        }

        /**
         * @return Number of bytes left in this array
         */
        public int getBytesLeft() {
            return bytes.length - contentLength;
        }

        /**
         * @return Content length. Be aware
         */
        public int getContentLength() {
            return contentLength;
        }

        /**
         * @param contentLength Byte array content length
         */
        public void setContentLength(int contentLength) {
            this.contentLength = contentLength;
        }

        /**
         * @return Underlying byte array
         */
        public byte[] getBytes() {
            return bytes;
        }

        /**
         * Puts content from the specified byte array to this ByteArray
         *
         * @param buffer Buffer
         * @throws IllegalArgumentException in case of buffer.length is bigger than underlying byte array
         */
        public void put(byte[] buffer) {
            put(buffer, 0, buffer.length);
        }

        /**
         * Puts content from the specified byte array to this ByteArray
         *
         * @param buffer Buffer with data
         * @param offset Buffer offset
         * @param count  Count of bytes
         * @throws IllegalArgumentException in case of buffer.length is bigger than underlying byte array
         */
        public void put(byte[] buffer, int offset, int count) {
            if (count > (bytes.length - this.contentLength)) {
                throw new IllegalArgumentException("buffer is too large");
            }

            System.arraycopy(buffer, offset, bytes, this.contentLength, count);
            this.contentLength += count;
        }

        /**
         * Appends content from the specified byteBuffer to this ByteArray
         *
         * @param byteBuffer    Source byte buffer
         * @param contentLength Length of the content to put to
         * @throws IllegalArgumentException in case of contentLength is bigger than underlying byte array
         */
        public void put(ByteBuffer byteBuffer, int contentLength) {
            if (contentLength > (bytes.length - this.contentLength)) {
                throw new IllegalArgumentException("contentLength is too large");
            }

            byteBuffer.get(bytes, this.contentLength, contentLength);
            this.contentLength += contentLength;
        }

        /**
         * Copies content to the destination buffer
         *
         * @param srcPos Position to use a source offset
         * @param buffer Destination array
         * @param offset Destination array offset
         * @param count  Number of bytes to copy
         */
        public void copyTo(int srcPos, byte[] buffer, int offset, int count) {
            System.arraycopy(bytes, srcPos, buffer, offset, count);
        }

        /**
         * Copies content to the destination buffer
         *
         * @param buffer Destination buffer
         */
        public void copyTo(byte[] buffer) {
            copyTo(0, buffer, 0, buffer.length);
        }

        /**
         * Copies byte content to output stream
         *
         * @param outputStream Output stream
         */
        public void copyTo(OutputStream outputStream) throws IOException {
            outputStream.write(bytes, 0, contentLength);
        }

        /**
         * Releases this ByteArray.
         * After ByteArray is released it is available again at ByteArrayPool
         */
        public void release() {
            this.contentLength = 0;
            parentPool.release(this);
        }
    }
}
