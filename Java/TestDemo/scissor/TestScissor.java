package com.example.testScissor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/12/19 12:51
 */
public class TestScissor {

    public static void main(String[] args) {

        String path = "d:/resources/";

        try {

            ScissorPos pos1 = ScissorPos.builder().top(0).buttom(500).left(0).right(20000).build();
//            ScissorPos pos2 = ScissorPos.builder().top(500).buttom(1000).left(500).right(1500).build();
//            ScissorPos pos3 = ScissorPos.builder().top(0).buttom(1000).left(300).right(500).build();
//            ScissorPos pos4 = ScissorPos.builder().top(500).buttom(1500).left(500).right(1000).build();

            ScissorImage image = ScissorImage.builder()
                    .filePath(path + "origin/1.jpg")
                    .outputPath(path + "target/")
                    .pos(Arrays.asList(pos1))
//                    .pos(Arrays.asList(pos1, pos2, pos3, pos4))
                    .build();


            scissor(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 根据传入的图片坐标进行图片截取
     * @throws IOException
     */
    public static void scissor(ScissorImage scissorImage) throws IOException {

        if (ObjectUtils.isEmpty(scissorImage)) {
            System.out.printf("截图参数为空");
            return;
        }

        if (StringUtils.isEmpty(scissorImage.getFilePath()) || StringUtils.isEmpty(scissorImage.getOutputPath())) {
            System.out.printf("截图参数异常:{}", scissorImage);
            return;
        }

        if (CollectionUtils.isEmpty(scissorImage.getPos())) {
            System.out.printf("截图坐标未配置:{}", scissorImage);
            return;
        }

        FileInputStream is = null;
        ImageInputStream iis = null;
        String fileExtension;
        String fileBaseName;
        String targetPath;

        try {
            // 图片文件名&后缀
            fileBaseName = FileNameUtils.getBaseName(scissorImage.getFilePath());
            fileExtension = FileNameUtils.getExtension(scissorImage.getFilePath()).toLowerCase();
            targetPath = (scissorImage.getOutputPath().endsWith("\\") ? scissorImage.getOutputPath() : scissorImage.getOutputPath() + "\\");


            // 读取图片文件
            is = new FileInputStream(scissorImage.getFilePath());

            /*
             * 返回包含所有当前已注册 ImageReader 的 Iterator，
             * 这些 ImageReader 声称能够解码指定格式。
             * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
             */
            Iterator<ImageReader> it = ImageIO
                    .getImageReadersByFormatName(fileExtension);
            ImageReader reader = it.next();

            // 获取图片流
            iis = ImageIO.createImageInputStream(is);

            /*
             * iis:读取源.true:只向前搜索，将它标记为 ‘只向前搜索’。
             * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许
             * reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
             */
            reader.setInput(iis, true);

            /*
             * 描述如何对流进行解码的类，用于指定如何在输入时从 Java Image I/O
             * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
             * 将从其 ImageReader 实现的
             * getDefaultReadParam方法中返回 ImageReadParam 的实例。
             */
            ImageReadParam param = reader.getDefaultReadParam();

            int index = 0;

            for (ScissorPos pos : scissorImage.getPos()) {
                /*
                 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
                 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
                 */
                Rectangle rect = new Rectangle(pos.x(), pos.y(), pos.width(), pos.height());

                // 提供一个 BufferedImage，将其用作解码像素数据的目标。
                param.setSourceRegion(rect);

                /*
                 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
                 * BufferedImage 返回。
                 */
                BufferedImage bi = reader.read(0, param);

                // 保存新图片
                ImageIO.write(bi, fileExtension, new File(targetPath + fileBaseName + "_" + index + "." + fileExtension));

                index++;
            }


        } finally {
            if (is != null)
                is.close();
            if (iis != null)
                iis.close();
        }
    }


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class ScissorPos {

        /**
         * 坐标 上
         */
        private int top = 0;

        /**
         * 坐标 下
         */
        private int buttom = 0;

        /**
         * 坐标 左
         */
        private int left = 0;

        /**
         * 坐标 右
         */
        private int right = 0;

        /**
         * 图片x轴坐标 左右中点
         * @return
         */
        public int x() {
            return left;
        }

        /**
         * 图片y轴坐标 上下中点
         * @return
         */
        public int y() {
            return top;
        }

        /**
         * 图片宽度左右之差
         * @return
         */
        public int width() {
            return right - left;
        }

        /**
         * 图片高度上下之差
         * @return
         */
        public int height() {
            return buttom - top;
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class ScissorImage {
        /**
         * 原图路径
         */
        private String filePath;

        /**
         * 截图输出路径
         */
        private String outputPath;

        /**
         * 图片坐标
         */
        private List<ScissorPos> pos;
    }
}
