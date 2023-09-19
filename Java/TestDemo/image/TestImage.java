package com.example.testImage;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/3/21 17:45
 */
@Slf4j
public class TestImage {

    public static void main(String[] args) {
//        String base64 = "data:image/jpg;base64,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";
        String base64 = "data:image/jpg;base64,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";
        String filePath = "C:\\Users\\zhangxianchen\\Desktop";
        String res = base64ToJpg(filePath, base64);
        log.info(res);
    }

    public static String base64ToJpg(String path,String base64){
        // 判断文件路径是否存在
        File filePath = new File(path);
        if (!filePath.exists()){
            filePath.mkdirs();
        }
        // 创建文件
        String jpgFile = path + "\\" + UUID.randomUUID() + ".jpg";
        File file = new File(jpgFile);
        boolean jpgFileExist = false;
        try {
            jpgFileExist = file.createNewFile();
            log.info("jpg文件创建成功");
        } catch (IOException e) {
            log.info("jpg文件创建失败");
            e.printStackTrace();
        }
        if (jpgFileExist){
            // 解密
            Base64.Decoder decoder = Base64.getDecoder();
            // 去掉base64前缀 data:image/jpeg;base64,
            base64 = base64.substring(base64.indexOf(",", 1) + 1, base64.length());
            byte[] b = decoder.decode(base64);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            // 保存图片
            try {
                FileOutputStream out = new FileOutputStream(jpgFile);
                out.write(b);
                out.flush();
                out.close();
                // 写入成功返回文件路径
                return jpgFile;
            } catch (FileNotFoundException e) {
                log.info("文件未找到");
                e.printStackTrace();
            } catch (IOException e) {
                log.info("写入失败");
                e.printStackTrace();
            }
        }
        return "文件不存在";
    }

}
