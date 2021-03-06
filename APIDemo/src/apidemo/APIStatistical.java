/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apidemo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author root
 */
public class APIStatistical {

    public static String sendPost(String url, byte[] data) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//        logger.info("url: " + url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new ByteArrayEntity(data));

        try (CloseableHttpResponse res = httpclient.execute(httpPost)) {
            HttpEntity entity = res.getEntity();

            InputStream inputStream = entity.getContent();
            String sResponse = IOUtils.toString(inputStream, "UTF-8");

            return sResponse;
        }
    }

    public static void main(String[] args) throws InterruptedException, Exception {
        try {
            JSStatisticalInfo js = new JSStatisticalInfo();

            Scanner Snr = new Scanner(System.in);

            System.out.println("Enter UserID:");
            long userStatistical = Snr.nextLong();
            js.setUserID(userStatistical);

            System.out.println("1. Total request for userID"
                    + "\n2. Total request for system"
                    + "\n3. List SenderID of UserID"
                    + "\n4. Avg time process for userID"
                    + "\n0. Exit"
                    + "\nEnter option statistical: ");

            //Long optionStatistical;
            long optionStatistical = Snr.nextLong();
            js.setOptionStatistical(optionStatistical);
            String strJSON = ObjectToString(js);

            //statiscal
            String res = sendPostJson("http://localhost:9494/api/group1/statistical", strJSON, 10000);

            JSRecvStatistic obj = StringToObject(res);

            if (optionStatistical == 1) {
                System.out.println("Request for userID: " + userStatistical);
                System.out.println("Result: " + obj.result + "\nCode: " + obj.code 
                        + "\nSuccess: " + obj.success + "\nFail: " + obj.fail + "\nTotal: " + obj.totalRequest);
            }

            if (optionStatistical == 2) {
                System.out.println("Request for system");
                System.out.println("Result: " + obj.result + "\nCode: " + obj.code 
                        + "\nSuccess: " + obj.success + "\nFail: " + obj.fail + "\nTotal: " + obj.totalRequest);
            }

            if (optionStatistical == 3) {
                System.out.println("Result: " + obj.result + "\nCode: " + obj.code 
                        + "\nList senderid: " + obj.listSenderID);
            }

            if (optionStatistical == 4) {
                System.out.println("Time process for userID: " + userStatistical);
                System.out.println("Result: " + obj.result + "\nCode: " + obj.code  
                        + "\nMin: " + obj.minTimeProcess + "\nMax: " + obj.maxTimeProcess + "\nAvg: " + obj.avgTimeProcess);
            }
            
        } catch (Exception ex) {
            System.out.println("Incorrect type data" + ex);
        }
    }

    public static String md5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String sendPostJson(String postUrl, String jsonContent, int timeout /*milisecond*/)
            throws Exception {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build()) {
            HttpPost httpPost = new HttpPost(postUrl);
            StringEntity input = new StringEntity(jsonContent, "UTF-8");
            input.setContentType("application/json");
            httpPost.setEntity(input);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("Failed : HTTP getStatusCode: "
                            + response.getStatusLine().getStatusCode()
                            + " HTTP getReasonPhrase: "
                            + response.getStatusLine().getReasonPhrase());
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())))) {
                    String output;
                    StringBuilder strBuilder = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        strBuilder.append(output);
                    }
                    return strBuilder.toString();
                }
            }
        }
    }

    public static String ObjectToString(Object obj) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Gson gson = gsonBuilder.disableHtmlEscaping().create();
        return gson.toJson(obj);
    }

    public static JSRecvStatistic StringToObject(String js) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Gson gson = gsonBuilder.disableHtmlEscaping().create();

        return gson.fromJson(js, JSRecvStatistic.class);
    }
}
