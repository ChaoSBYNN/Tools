package com.example.testQRCode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class QRCode {
    public static void main(String[] args) throws IOException, WriterException {
        encode("没事，黑白就能体现出我的高冷风了", "E:/QRCode.png", "E:/load.png");
//        encode("没事，黑白就能体现出我的高冷风了", "E:/QRCode.png");
        String s = decode("E:/QRCode.png");
        System.out.println(s);
    }

    private static void encode(String content, String path) throws WriterException, IOException {
        int x = 500;
        int y = 500;

        //0x为16位标识 FF 随后6位为色值
        int onColor = 0xFF000000;
        int offColor = 0xFFFFFFA;
        String format = "png";
        File out = new File(path);

        HashMap<EncodeHintType, Object> map = new HashMap<>();
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//Decode 出错，可以适当调节排错率
        map.put(EncodeHintType.MARGIN, 1);
        BitMatrix encode = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, x, y, map);

        MatrixToImageWriter.writeToPath(encode, format, out.toPath(), new MatrixToImageConfig(onColor, offColor));
    }

    private static void encode(String content, String path, String logoPath) throws WriterException, IOException {
        int x = 1000;
        int y = 1000;

        //0x为16位标识 FF 随后6位为色值
        int onColor = 0xFF000000;
        int offColor = 0xFFFFFFA;
        String format = "png";
        File out = new File(path);

        HashMap<EncodeHintType, Object> map = new HashMap<>();
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//Decode 出错，可以适当调节排错率
        map.put(EncodeHintType.MARGIN, 1);
        BitMatrix encode = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, x, y, map);

        BufferedImage qrCode = MatrixToImageWriter.toBufferedImage(encode, new MatrixToImageConfig(onColor, offColor));

        Graphics2D graphics = qrCode.createGraphics();
        BufferedImage logo = ImageIO.read(new File(logoPath));
        graphics.drawImage(logo, (qrCode.getWidth() - logo.getWidth()) / 2, (qrCode.getHeight() - logo.getHeight()) / 2, null);
        graphics.dispose();
        logo.flush();

        ImageIO.write(qrCode, format, out);
    }

    public static String decode(String path) throws IOException {
        File in = new File(path);
        BufferedImage qrCode = ImageIO.read(in);

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrCode)));

        HashMap<DecodeHintType, Object> map = new HashMap<>();
        map.put(DecodeHintType.CHARACTER_SET, "utf-8");

        try {
            Result decode = new MultiFormatReader().decode(binaryBitmap, map);
            return decode.toString();
        } catch (NotFoundException e) {
            return "";
        }
    }
}
