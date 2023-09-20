package com.example.testPDF;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class PDFSplitter {

    private static final String PATH = "d:/resources/";

    public static void main(String[] args) throws FileNotFoundException {
        String inputFilePath = PATH + "kendo/kendo-jp.pdf"; // 输入PDF文件路径
        String outputFolderPath = PATH + "kendo/"; // 输出文件夹路径
        int maxPageSize = 3; // 限制每个分割文件的大小（以字节为单位）
        try {
            PDDocument document = PDDocument.load(new File(inputFilePath));
            Splitter splitter = new Splitter();
            splitter.setSplitAtPage(maxPageSize); // 设置分割大小
            List<PDDocument> splitDocuments = splitter.split(document);
            int pageNumber = 1;
            for (PDDocument splitDocument : splitDocuments) {
                String outputFilePath = outputFolderPath + "split_" + pageNumber + ".pdf";
                splitDocument.save(outputFilePath);
                splitDocument.close();
                System.out.println("Split PDF saved: " + outputFilePath);
                pageNumber++;
            }
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        String outputFilePath = PATH + "kendo/kendo-jp-merged.pdf"; // 合并后的PDF文件路径
//        PDFMergerUtility merger = new PDFMergerUtility();
//        // 添加要合并的PDF文件
//        merger.addSource(new File(PATH + "kendo/split_1.pdf"));
//        merger.addSource(new File(PATH + "kendo/split_2.pdf"));
//        merger.addSource(new File(PATH + "kendo/split_3.pdf"));
//        merger.addSource(new File(PATH + "kendo/split_4.pdf"));
//        merger.setDestinationFileName(outputFilePath);
//        try {
//            merger.mergeDocuments(null);
//            System.out.println("PDF files merged successfully!");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
