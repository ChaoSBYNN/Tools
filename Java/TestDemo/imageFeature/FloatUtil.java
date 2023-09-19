/**
 * 
 */
package com.example.testFeature;

/**
 * @author molin
 * @Date 2021-12-23 09:27:52
 */
public class FloatUtil {
    private FloatUtil() {
    }

    public static float[] bytesToFloats(byte[] bytes) {
        return null == bytes ? null : bytesToFloats(bytes, 0, bytes.length, bytes.length / 4);
    }

    public static byte[] floatsToBytes(float[] floats) {
        return null == floats ? null : floatsToBytes(floats, 0, floats.length);
    }

    static byte[] floatsToBytesEx(float[] floats) {
        return null == floats ? null : floatsToBytesEx(floats, 0, floats.length);
    }

    static byte[] floatToBytes(float f, byte[] b, int offset) {
        int fbit = Float.floatToIntBits(f);
        b[offset] = (byte)(fbit & 255);
        b[offset + 1] = (byte)(fbit >> 8 & 255);
        b[offset + 2] = (byte)(fbit >> 16 & 255);
        b[offset + 3] = (byte)(fbit >> 24 & 255);
        return b;
    }

    static byte[] floatToBytesEx(float f, byte[] b, int offset) {
        int fbit = Float.floatToIntBits(f);
        b[offset] = (byte)(fbit & 255);
        b[offset + 1] = (byte)(fbit >> 8 & 255);
        b[offset + 2] = (byte)(fbit >> 16 & 255);
        b[offset + 3] = (byte)(~(fbit >> 24 & 255));
        return b;
    }

    static float byteToFloat(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytes[offset] & 255 | (bytes[offset + 1] & 255) << 8 | (bytes[offset + 2] & 255) << 16 | bytes[offset + 3] << 24);
    }

    public static float byteToFloatEx(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytes[offset] & 255 | (bytes[offset + 1] & 255) << 8 | (bytes[offset + 2] & 255) << 16 | ~bytes[offset + 3] << 24);
    }

    static byte[] floatsToBytes(float[] floats, int offset, int length) {
        if (null == floats) {
            return new byte[0];
        } else if (offset >= 0 && length >= 0 && offset + length <= floats.length) {
            byte[] data = new byte[length * 4];

            for(int i = offset; i < offset + length; ++i) {
                floatToBytes(floats[i], data, i * 4);
            }

            return data;
        } else {
            return new byte[0];
        }
    }

    public static byte[] floatsToBytesEx(float[] floats, int offset, int length) {
        if (null == floats) {
            return new byte[0];
        } else if (offset >= 0 && length >= 0 && offset + length <= floats.length) {
            byte[] data = new byte[length * 4];

            for(int i = offset; i < offset + length; ++i) {
                floatToBytesEx(floats[i], data, i * 4);
            }

            return data;
        } else {
            return new byte[0];
        }
    }

    static float[] bytesToFloats(byte[] bytes, int offset, int bytesLength, int floatsLength) {
        if (null == bytes) {
            return new float[0];
        } else if (offset >= 0 && bytesLength >= 0 && offset + bytesLength <= bytes.length && bytesLength / 4 <= floatsLength) {
            float[] data = new float[floatsLength];

            for(int i = 0; i < data.length; ++i) {
                data[i] = byteToFloat(bytes, 4 * i + offset);
            }

            return data;
        } else {
            return new float[0];
        }
    }

    static void bytesToFloats(byte[] bytes, int offset, int bytesLength, float[] data) {
        if (null != bytes && null != data) {
            if (offset < 0 || bytesLength < 0 || offset + bytesLength > bytes.length || bytesLength / 4 > data.length) {
                return;
            }
            for(int i = 0; i < bytesLength / 4; ++i) {
                data[i] = byteToFloat(bytes, 4 * i + offset);
            }

        }
    }

    static void main() {
        float[] x = new float[]{1.0F, 2.0F, 4.0F, 0.1F, 100.0F};
        byte[] y = floatsToBytes(x);
        float[] z = bytesToFloats(y);

        for(int i = 0; i < z.length; ++i) {
//            System.out.println("i = " + i + ", x[i] = " + x[i] + ", z[i] = " + z[i]);
        }

    }
}
