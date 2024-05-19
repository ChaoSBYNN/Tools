package org.example.orm.totp;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Spike_Zhang
 * @description: TotpUtils TODO
 * @date 2024/5/19 9:05
 */
public class TOTPUtils {

    public static void main(String[] args) {
        try {

            String totp = generateMyTOTP();
            System.out.println(String.format("加密后: %s", totp));

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 共享密钥
     */
    private static final String SECRET_KEY = "364ADD65C42A77D4603A085485B6D404";

    /**
     * 时间步长 单位:毫秒 作为口令变化的时间周期
     */
    private static final long STEP = 3600;

    /**
     * 转码位数 [1-8]
     */
    private static final int CODE_DIGITS = 6;

    /**
     * 初始化时间
     */
//    private static final long INITIAL_TIME = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    private static final long INITIAL_TIME = 0;

    /**
     * 数子量级
     */
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};


    /**
     * 生成一次性密码
     *
     * @return String
     */
    public static String generateMyTOTP() {

        long now = LocalDateTime.now().withMinute(0).withSecond(0).toEpochSecond(ZoneOffset.of("+8"));
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        return generateTOTP(SECRET_KEY, time);
    }


    /**
     * 获取动态因子
     *
     * @param targetTime 指定时间
     * @return long
     */
    private static long timeFactor(long targetTime) {
        return (targetTime - INITIAL_TIME) / STEP;
    }

    /**
     * 哈希加密
     *
     * @param crypto   加密算法
     * @param keyBytes 密钥数组
     * @param text     加密内容
     * @return byte[]
     */
    private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "AES");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private static byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static String generateTOTP(String key, String time) {
        return generateTOTP(key, time, "HmacSHA1");
    }


    private static String generateTOTP(String key, String time, String crypto) {
        StringBuilder timeBuilder = new StringBuilder(time);
        while (timeBuilder.length() < 16)
            timeBuilder.insert(0, "0");
        time = timeBuilder.toString();

        byte[] msg = fromHexStringTo8bitUnsignedArray(time);
        byte[] k = fromHexStringTo8bitUnsignedArray(key);
        byte[] hash = hmac_sha(crypto, k, msg);
        return truncate(hash);
    }

    /**
     * 将十六进制字符串转换为8位无符号的字节数组。
     *
     * @param hexString 十六进制字符串
     * @return 长度符合8的倍数的无符号字节数组
     */
    public static byte[] fromHexStringTo8bitUnsignedArray(String hexString) {
        // 确保字符串长度为偶数
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString; // 添加前导零
        }

        // 计算结果数组的长度，确保为8的倍数
        int resultLength = (hexString.length() / 2);
        if (resultLength % 8 != 0) {
            resultLength += 8 - resultLength % 8;
        }

        byte[] data = new byte[resultLength];
        int dataIndex = 0;

        for (int i = 0; i < hexString.length(); i += 2) {
            // 解析两个十六进制字符为一个字节
            int byteValue = Integer.parseInt(hexString.substring(i, i + 2), 16);
            data[dataIndex++] = (byte) (byteValue & 0xFF); // 确保无符号
        }

        // 如果需要，可以截断数组以满足8位无符号数组的要求
        if (dataIndex < data.length) {
            byte[] truncatedData = new byte[dataIndex];
            System.arraycopy(data, 0, truncatedData, 0, dataIndex);
            return truncatedData;
        }

        return data;
    }


    /**
     * 截断函数
     *
     * @param target 20字节的字符串
     * @return String
     */
    private static String truncate(byte[] target) {
        StringBuilder result;
        int offset = target[target.length - 1] & 0xf;
        int binary = ((target[offset] & 0x7f) << 24)
                | ((target[offset + 1] & 0xff) << 16)
                | ((target[offset + 2] & 0xff) << 8) | (target[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[CODE_DIGITS];
        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < CODE_DIGITS) {
            result.insert(0, "0");
        }
        return result.toString();
    }
}
