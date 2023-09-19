package com.example.testHttp;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TestHttps {

    private static Logger logger = LoggerFactory.getLogger(TestHttps.class);

    public static void main(String[] args) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            String url = "https://www.baidu.com";
            httpClient = createIgnoreSSLHttpClient();
            if (httpClient == null) {
                logger.error("HttpClient create fail.");
                return;
            }
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("NO_OK : " + null);
            } else {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("OK : " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static CloseableHttpClient createIgnoreSSLHttpClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
