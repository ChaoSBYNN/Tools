package com.example.testEncryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/12/19 16:14
 */
public class TestEncryption {

    public static void main(String[] args) {
        try {
            String data = encryptByDES("1607921943821410306", "VISITORS");
            System.out.println(data);
            System.out.println(decryptByDES("81b5ea8dde653194396aec1e837462cfebbbba7d93e31d1a", "VISITORS"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static String encryptByDES(String input,String key) throws Exception {
        // 算法
        String algorithm = "DES";
        String transformation = "DES";
        // Cipher：密码，获取加密对象
        // transformation:参数表示使用什么类型加密
        Cipher cipher = Cipher.getInstance(transformation);
        // 指定秘钥规则
        // 第一个参数表示：密钥，key的字节数组 长度必须是8位
        // 第二个参数表示：算法
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
        // 对加密进行初始化
        // 第一个参数：表示模式，有加密模式和解密模式
        // 第二个参数：表示秘钥规则
        cipher.init(Cipher.ENCRYPT_MODE,sks);
        // 进行加密
        byte[] bytes = cipher.doFinal(input.getBytes());
        return bytesToHexString(bytes);
    }

    public static String decryptByDES(String input,String key)throws Exception{
        // 算法
        String algorithm = "DES";
        String transformation = "DES";
        // Cipher：密码，获取加密对象
        // transformation:参数表示使用什么类型加密
        Cipher cipher = Cipher.getInstance(transformation);
        // 指定秘钥规则
        // 第一个参数表示：密钥，key的字节数组 长度必须是8位
        // 第二个参数表示：算法
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
        // 对加密进行初始化
        // 第一个参数：表示模式，有加密模式和解密模式
        // 第二个参数：表示秘钥规则
        cipher.init(Cipher.DECRYPT_MODE,sks);
        // 进行解密
        byte [] inputBytes = hexStringToBytes(input);
        byte[] bytes = cipher.doFinal(inputBytes);
        return new String(bytes);
    }

    public static String encryptByAES(String input,String key) throws Exception {
        // 算法
        String algorithm = "AES";
        String transformation = "AES";
        // Cipher：密码，获取加密对象
        // transformation:参数表示使用什么类型加密
        Cipher cipher = Cipher.getInstance(transformation);
        // 指定秘钥规则
        // 第一个参数表示：密钥，key的字节数组 长度必须是16位
        // 第二个参数表示：算法
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
        // 对加密进行初始化
        // 第一个参数：表示模式，有加密模式和解密模式
        // 第二个参数：表示秘钥规则
        cipher.init(Cipher.ENCRYPT_MODE,sks);
        // 进行加密
        byte[] bytes = cipher.doFinal(input.getBytes());
        return bytesToHexString(bytes);
    }

    public static String decryptByAES(String input,String key)throws Exception{
        // 算法
        String algorithm = "AES";
        String transformation = "AES";
        // Cipher：密码，获取加密对象
        // transformation:参数表示使用什么类型加密
        Cipher cipher = Cipher.getInstance(transformation);
        // 指定秘钥规则
        // 第一个参数表示：密钥，key的字节数组 长度必须是16位
        // 第二个参数表示：算法
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
        // 对加密进行初始化
        // 第一个参数：表示模式，有加密模式和解密模式
        // 第二个参数：表示秘钥规则
        cipher.init(Cipher.DECRYPT_MODE,sks);
        // 进行解密
        byte [] inputBytes = hexStringToBytes(input);
        byte[] bytes = cipher.doFinal(inputBytes);
        return new String(bytes);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString.length() % 2 != 0) throw new IllegalArgumentException("hexString length not valid");
        int length = hexString.length() / 2;
        byte[] resultBytes = new byte[length];
        for (int index = 0; index < length; index++) {
            String result = hexString.substring(index * 2, index * 2 + 2);
            resultBytes[index] = Integer.valueOf(Integer.parseInt(result, 16)).byteValue();
        }
        return resultBytes;
    }
    public static  String bytesToHexString(byte[] sources) {
        if (sources == null) return null;
        StringBuilder stringBuffer = new StringBuilder();
        for (byte source : sources) {
            String result = Integer.toHexString(source& 0xff);
            if (result.length() < 2) {
                result = "0" + result;
            }
            stringBuffer.append(result);
        }
        return stringBuffer.toString();
    }

}
