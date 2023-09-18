package com.iot.sas.common.service.impl;

import com.iot.common.util.JsonUtil;
import com.iot.sas.common.service.FileTransferService;
import com.lds.coral.core.aop.ServiceTag;
import com.lds.coral.core.constants.ServiceTagEnum;
import com.lds.file.api.FileApiV2;
import com.lds.file.dto.FileDto;
import com.lds.file.enums.MimeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

/**
 * 文件传输 service 实现
 *
 * @author Spike_Zhang
 * @date 2023/08/28
 */
@Slf4j
@Service
public class FileTransferServiceImpl implements FileTransferService {

    @Resource
    @ServiceTag(ServiceTagEnum.LOCAL)
    private FileApiV2 localFileApi;

    @Resource
    @ServiceTag(ServiceTagEnum.SHADOW)
    private FileApiV2 cloudFileApi;



    @Override
    public FileDto transfer(String fileUrl, Long locationId, boolean isCloud) throws MalformedURLException {
        String fileType = getFileType(fileUrl);
        FileDto fileDto;
        if (isCloud) {
            fileDto = cloudFileApi.getPutUrl(locationId, MimeTypeEnum.findByExtension(fileType).getExtension(), "private");
        } else {
            fileDto = localFileApi.getPutUrl(locationId, MimeTypeEnum.findByExtension(fileType).getExtension(), "private");
        }
        log.info("zero copy. file : {}", JsonUtil.toJson(fileDto));

        if (ObjectUtils.isEmpty(fileDto)) {
            return fileDto;
        }

        String presetUrl = fileDto.getPresignedUrl();
        Map<String, String> header = fileDto.getHeader();
        HttpURLConnection connection = getConnection(presetUrl, header, fileType);
        URL originUrl = new URL(fileUrl);

        try (InputStream inputStream = originUrl.openStream();
             OutputStream outputStream = connection.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            int responseCode = connection.getResponseCode();
            log.info("zero copy. file sendfile done ！ code {}", responseCode);
        } catch (IOException e) {
            log.error("zero copy. failed {}", e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return fileDto;
    }

    private static String getFileType(String imageUrl){
        String fileType = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
        if(fileType.contains("?")){
            return fileType.substring(0, fileType.indexOf("?"));
        }
        return fileType;
    }

    public HttpURLConnection getConnection(String putUrl, Map<String, String> header, String fileType) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(putUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(300000);
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-type", getContentType(fileType));
            connection.setUseCaches(false);
            if (Optional.ofNullable(header).isPresent() && StringUtils.isNotBlank(header.get("x-oss-meta-author"))) {
                connection.setRequestProperty("x-oss-meta-author", "aliy");
            }
            return connection;
        } catch (Exception e) {
            log.error("zero copy. connection create failed ! : {}", e.getMessage(), e);
        }
        return null;
    }

    public static String getContentType(String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            return "application/octet-stream";
        }

        MimeTypeEnum mimeTypeEnum = MimeTypeEnum.findByExtension(fileType);
        if (mimeTypeEnum != null) {
            return mimeTypeEnum.getMimeType();
        } else {
            return "application/octet-stream";
        }
    }
}
