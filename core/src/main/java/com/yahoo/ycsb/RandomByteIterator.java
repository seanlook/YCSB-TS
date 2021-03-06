/**
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb;

/**
 *  A ByteIterator that generates a random sequence of bytes.
 */
public class RandomByteIterator extends ByteIterator {
    private long len;
    private long off;
    private int bufOff;
    private byte[] buf;

    public RandomByteIterator(long len) {
        this.len = len;
        this.buf = new byte[6];
        this.bufOff = buf.length;
        fillBytes();
        this.off = 0;
    }

    @Override
    public boolean hasNext() {
        return (off + bufOff) < len;
    }

    private void fillBytesImpl(byte[] buffer, int base) {
        // we need A-Za-z0-9, which convertes to the range (Ascii/UTF-8):
        // 0-9 48-57
        // A-Z 65-90
        // a-z 97-122
        try {
            buffer[base + 0] = (byte) this.getRandAN();
            buffer[base + 1] = (byte) this.getRandAN();
            buffer[base + 2] = (byte) this.getRandAN();
            buffer[base + 3] = (byte) this.getRandAN();
            buffer[base + 4] = (byte) this.getRandAN();
            buffer[base + 5] = (byte) this.getRandAN();
        } catch (ArrayIndexOutOfBoundsException e) { /* ignore it */ }
    }

    private void fillBytes() {
        if (bufOff == buf.length) {
            fillBytesImpl(buf, 0);
            bufOff = 0;
            off += buf.length;
        }
    }

    public byte nextByte() {
        fillBytes();
        bufOff++;
        return buf[bufOff - 1];
    }

    @Override
    public int nextBuf(byte[] buffer, int bufferOffset) {
        int ret;
        if (len - off < buffer.length - bufferOffset) {
            ret = (int) (len - off);
        } else {
            ret = buffer.length - bufferOffset;
        }
        int i;
        for (i = 0; i < ret; i += 6) {
            fillBytesImpl(buffer, i + bufferOffset);
        }
        off += ret;
        return ret + bufferOffset;
    }

    @Override
    public long bytesLeft() {
        return len - off - bufOff;
    }
}
