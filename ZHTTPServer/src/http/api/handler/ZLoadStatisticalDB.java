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
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author root
 */
public class ZLoadStatisticalDB extends BaseApiHandler {

    private static volatile ZLoadStatisticalDB instance;

    private ZLoadStatisticalDB() {
    }

    public static ZLoadStatisticalDB getInstance() {
        if (instance == null) {
            instance = new ZLoadStatisticalDB();
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
            JSRecvStatistical jsMsg = GsonUtils.fromJsonString(js, JSRecvStatistical.class);

            //do someting
            if (jsMsg.optionStatistical == 1) {
                ResultAPI reqForUsr = RedisStatistical.getInstance().countTotalReqPerUsr(jsMsg.userStatistical);

                if (reqForUsr.result != 1) {
                    return "{\"result\":" + reqForUsr.result + ","
                            + "\"code\":" + reqForUsr.code + ", "
                            + "\"success\":" + reqForUsr.success + ", "
                            + "\"fail\":" + reqForUsr.fail + ", "
                            + "\"totalRequest\":" + reqForUsr.totalrequest + "}";
                }

                return "{\"result\":" + reqForUsr.result + ","
                        + "\"code\":" + reqForUsr.code + ", "
                        + "\"success\":" + reqForUsr.success + ", "
                        + "\"fail\":" + reqForUsr.fail + ", "
                        + "\"totalRequest\":" + reqForUsr.totalrequest + "}";
            }

            if (jsMsg.optionStatistical == 2) {
                ResultAPI reqForSystem = RedisStatistical.getInstance().countTotalRequest();

                if (reqForSystem.result != 1) {
                    return "{\"result\":" + reqForSystem.result + ","
                            + "\"code\":" + reqForSystem.code + ", "
                            + "\"success\":" + reqForSystem.success + ", "
                            + "\"fail\":" + reqForSystem.fail + ", "
                            + "\"totalRequest\":" + reqForSystem.totalrequest + "}";
                }

                return "{\"result\":" + reqForSystem.result + ","
                        + "\"code\":" + reqForSystem.code + ", "
                        + "\"success\":" + reqForSystem.success + ", "
                        + "\"fail\":" + reqForSystem.fail + ", "
                        + "\"totalRequest\":" + reqForSystem.totalrequest + "}";
            }

            if (jsMsg.optionStatistical == 3) {
                ResultAPI listSenderIDOfUserID = RedisStatistical.getInstance().getListSenderIDOfUserID(jsMsg.userStatistical);

                if (listSenderIDOfUserID.result != 1) {
                    return "{\"result\":" + listSenderIDOfUserID.result + ","
                            + "\"code\":" + listSenderIDOfUserID.code + ", "
                            + "\"listSenderID\":" + listSenderIDOfUserID.listSenderID + "}";
                }

                return "{\"result\":" + listSenderIDOfUserID.result + ","
                        + "\"code\":" + listSenderIDOfUserID.code + ", "
                        + "\"listSenderID\":" + listSenderIDOfUserID.listSenderID + "}";
            }

            if (jsMsg.optionStatistical == 4) {
                ResultAPI TimeProcess = RedisStatistical.getInstance().getAvgTimeProcess(jsMsg.userStatistical);

                if (TimeProcess.result != 1) {
                    return "{\"result\":" + TimeProcess.result + ","
                            + "\"code\":" + TimeProcess.code + ", "
                            + "\"avgTimeProccess\":" + TimeProcess.avgTimeProPerUsr + ", "
                            + "\"minTimeProccess\":" + TimeProcess.minTimeProPerUsr + ", "
                            + "\"maxTimeProccess\":" + TimeProcess.maxTimeProPerUsr + "}";
                }

                return "{\"result\":" + TimeProcess.result + ","
                        + "\"code\":" + TimeProcess.code + ", "
                        + "\"avgTimeProccess\":" + TimeProcess.avgTimeProPerUsr + ", "
                        + "\"minTimeProccess\":" + TimeProcess.minTimeProPerUsr + ", "
                        + "\"maxTimeProccess\":" + TimeProcess.maxTimeProPerUsr + "}";
            }

            //return json
            return "{result:0,code:404,msg:\"Fail\"}";
        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }
}
