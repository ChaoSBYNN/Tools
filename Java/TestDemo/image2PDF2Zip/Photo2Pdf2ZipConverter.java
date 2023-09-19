package com.example.testImage2PDF2Zip;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
@Slf4j
public class Photo2Pdf2ZipConverter {

    private static final String PATH = "d:/resources/";

    public static void main(String[] args) {
        String photoPath = PATH + "test.jpg"; // Replace with your photo path
        String outputPdfPath = PATH + "test.pdf"; // Replace with desired output PDF path
        String pdfPath = PATH + "test.pdf"; // Replace with your PDF file path
        String outputZipPath = PATH + "test.zip"; // Replace with desired output ZIP file path
        try {
            convertPhotoToPdf(photoPath, outputPdfPath);
            log.info("PDF conversion successful.");
            compressPdfFile(pdfPath, outputZipPath);
            log.info("PDF file compression successful.");
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        }
    }

    private static void convertPhotoToPdf(String photoPath, String outputPdfPath) throws Exception {
        Document document = null;
        PdfWriter writer = null;
        try {
            document = new Document();
            writer = PdfWriter.getInstance(document, new FileOutputStream(outputPdfPath));
            document.open();
            Image image = Image.getInstance(photoPath);
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            document.add(image);
        } finally {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }


    private static void compressPdfFile(String pdfPath, String outputZipPath) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(pdfPath)) {
            File file = new File(pdfPath);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[8192]; // Use a larger buffer size
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

}
