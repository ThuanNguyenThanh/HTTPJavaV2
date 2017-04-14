/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.api.handler;

import http.api.utils.GsonUtils;
import http.redis.util.RedisStatistical;
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

    private static volatile ZAPIStatistical instance;

    private ZAPIStatistical() {
    }

    public static ZAPIStatistical getInstance() {
        if (instance == null) {
            instance = new ZAPIStatistical();
        }
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
                    + "\n3. List SenderID of UserID"
                    + "\n4. Avg time process for userID"
                    + "\n0. Exit");

            Scanner Snr = new Scanner(System.in);

            while (true) {
                System.out.print("Function: ");
                int choose = Snr.nextInt();

                if ((choose < 1) || (choose > 4)) {
                    System.out.println("Exit");
                    break;
                }

                if (choose == 1) {
                    if (RedisStatistical.getInstance().countTotalReqPerUsr(jsMsg.userStatiscal) == false) {
                        return null;
                    }

                    System.out.println("----Request for user: " + jsMsg.userStatiscal + "----");
                    System.out.println("Success: " + ZMsgDefine.totalReqSucPerUsr);
                    System.out.println("Fail: " + ZMsgDefine.totalReqFailPerUsr);
                    System.out.println("Total request: " + ZMsgDefine.totalReqPerUsr);
                }

                if (choose == 2) {
                    if (RedisStatistical.getInstance().countTotalRequest() == false) {
                        return null;
                    }

                    System.out.println("----Request for System----");
                    System.out.println("Success: " + ZMsgDefine.totalReqSuc);
                    System.out.println("Fail: " + ZMsgDefine.totalReqFail);
                    System.out.println("Total request: " + ZMsgDefine.totalReq);
                }

                if (choose == 3) {
                    if (RedisStatistical.getInstance().getListSenderIDOfUserID(jsMsg.userStatiscal) == false) {
                        return null;
                    }
                }

                if (choose == 4) {
                    if (RedisStatistical.getInstance().getAvgTimeProcess(jsMsg.userStatiscal) == false) {
                        return null;
                    }

                    System.out.println("----Time Process for userID: " + jsMsg.userStatiscal + "----");
                    System.out.println("Min: " + ZMsgDefine.minTimeProPerUsr);
                    System.out.println("Max: " + ZMsgDefine.maxTimeProPerUsr);
                    System.out.println("Avg: " + ZMsgDefine.avgTimeProPerUsr);
                }
            }
            //return json
            return "{result:0,code:404,msg:\"Success\"}";

        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }
}
