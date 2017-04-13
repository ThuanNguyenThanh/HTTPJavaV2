/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.api.handler;

import http.api.utils.GsonUtils;
import http.redis.util.RedisProccess;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author root
 */
public class ZAPIStatistical extends BaseApiHandler {

    private static final ZAPIStatistical instance = new ZAPIStatistical();

    private ZAPIStatistical() {
    }

    public static ZAPIStatistical getInstance() {
        return instance;
    }

    @Override
    public String doAction(HttpServletRequest req) {
        try {
            System.out.println("----------------New connection--------------- ");
            if (req.getMethod().compareToIgnoreCase("POST") != 0) {
                return "{result:0,code:405,msg:\"Wrong method. Must be POST\"}";
            }

            InputStream is = req.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte buf[] = new byte[2048];
            int letti = 0;

            while ((letti = is.read(buf)) > 0) {
                baos.write(buf, 0, letti);
            }

            String js = new String(baos.toByteArray(), Charset.forName("UTF-8"));
            JSMessageExample jsMsg = GsonUtils.fromJsonString(js, JSMessageExample.class);

            //do someting
            System.out.println("1. Total request for userID"
                    + "\n2. Total request for system"
                    + "\n3. Avg time proccess for userID");

            Scanner Snr = new Scanner(System.in);

            while (true) {
                System.out.print("Function: ");
                long choose = Snr.nextLong();

                if (choose == 1) {
                    if (RedisProccess.getInstance().countTotalReqPerUsr(jsMsg.statiscal) == false) {
                        return null;
                    }

                    System.out.println("----Request for user: " + jsMsg.statiscal + "----");
                    System.out.println("Success: " + ZMsgDefine.totalReqSucPerUsr);
                    System.out.println("Fail: " + ZMsgDefine.totalReqFailPerUsr);
                    System.out.println("Total request: " + ZMsgDefine.totalReqPerUsr);
                }

                if (choose == 2) {
                    if (RedisProccess.getInstance().countTotalRequest() == false) {
                        return null;
                    }

                    System.out.println("----Request for System----");
                    System.out.println("Success: " + ZMsgDefine.totalReqSuc);
                    System.out.println("Fail: " + ZMsgDefine.totalReqFail);
                    System.out.println("Total request: " + ZMsgDefine.totalReq);
                }

                if (choose == 3) {
                    if (RedisProccess.getInstance().getAvgTimeProccess(jsMsg.statiscal) == false) {
                        return null;
                    }

                    System.out.println("----Time Proccess for userID: " + jsMsg.statiscal + "----");
                    System.out.println("Min: " + ZMsgDefine.minTimeProPerUsr);
                    System.out.println("Max: " + ZMsgDefine.maxTimeProPerUsr);
                    System.out.println("Avg: " + ZMsgDefine.avgTimeProPerUsr);
                }

                if (choose == 0) {
                    break;
                }
            }
            //return json
            return "{result:0,code:404,msg:\"Success\"}";

        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }
}
