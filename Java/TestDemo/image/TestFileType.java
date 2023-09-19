package com.example.testImage;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/7/12 16:25
 */
public class TestFileType {

    public static void main(String[] args) {
        String fileUrl = "https://xxx-private-bucket.oss-cn-hangzhou.aliyuncs.com/999/jpg/ac07d4f72e734f10897c2ab1e17e9e78.jpg?Expires=1689184533&OSSAccessKeyId=LTAI5tAVEkWrY5YUba7wQQxt&Signature=0CfcShq1PYQhhimwC5VT3jGftjs%3D";
        String fileUrl2 = "https://xxx-private-bucket.oss-cn-hangzhou.aliyuncs.com/999/jpg/ac07d4f72e734f10897c2ab1e17e9e78.jpg";

        String fileType = fileUrl2.substring(fileUrl2.lastIndexOf(".") + 1);
        if (fileType.contains("?")) {
            fileType = fileType.substring(0, fileType.indexOf("?"));
        }

        System.out.println(fileType);
    }
}
