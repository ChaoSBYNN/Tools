package org.example.orm.totp;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import org.apache.commons.codec.binary.Base32;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

public class CustomerCodeGenerator implements CodeGenerator {

    private final HashingAlgorithm algorithm;
    private final int digits;

    public CustomerCodeGenerator() {
        this(HashingAlgorithm.SHA1, 6);
    }

    public CustomerCodeGenerator(HashingAlgorithm algorithm) {
        this(algorithm, 6);
    }

    public CustomerCodeGenerator(HashingAlgorithm algorithm, int digits) {
        if (algorithm == null) {
            throw new InvalidParameterException("HashingAlgorithm must not be null.");
        }
        if (digits < 1) {
            throw new InvalidParameterException("Number of digits must be higher than 0.");
        }

        this.algorithm = algorithm;
        this.digits = digits;
    }

    @Override
    public String generate(String key, long counter) throws CodeGenerationException {
        try {
            byte[] hash = generateHash(key, counter);
            return getDigitsFromHash(hash);
        } catch (Exception e) {
            throw new CodeGenerationException("Failed to generate code. See nested exception.", e);
        }
    }

    /**
     * Generate a HMAC-SHA1 hash of the counter number.
     */
    private byte[] generateHash(String key, long counter) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] data = new byte[8];
        long value = counter;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        // Create a HMAC-SHA1 signing key from the shared key
        byte[] decodedKey = fromHexStringTo8bitUnsignedArray(key);
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, algorithm.getHmacAlgorithm());
        Mac mac = Mac.getInstance(algorithm.getHmacAlgorithm());
        mac.init(signKey);

        // Create a hash of the counter value
        return mac.doFinal(data);
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
     * Get the n-digit code for a given hash.
     */
    private String getDigitsFromHash(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xF;

        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= Math.pow(10, digits);

        // Left pad with 0s for a n-digit code
        return String.format("%0" + digits + "d", truncatedHash);
    }
}
