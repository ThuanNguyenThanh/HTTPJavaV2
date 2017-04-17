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
                if (RedisStatistical.getInstance().countTotalReqPerUsr(jsMsg.userStatistical) == false) {
                    return null;
                }

                return "{\"success\":" + ZMsgDefine.totalReqSucPerUsr + ", "
                        + "\"fail\":" + ZMsgDefine.totalReqFailPerUsr + ", "
                        + "\"totalrequest\":" + ZMsgDefine.totalReqPerUsr + "}";
            }

            if (jsMsg.optionStatistical == 2) {
                if (RedisStatistical.getInstance().countTotalRequest() == false) {
                    return null;
                }

                return "{\"success\":" + ZMsgDefine.totalReqSuc + ", "
                        + "\"fail\":" + ZMsgDefine.totalReqFail + ", "
                        + "\"totalrequest\":" + ZMsgDefine.totalReq + "}";
            }

            if (jsMsg.optionStatistical == 3) {
                if (RedisStatistical.getInstance().getListSenderIDOfUserID(jsMsg.userStatistical) == false) {
                    return null;
                }
                
                return "{\"listSenderIDOfUserID\":" + "\"" + ZMsgDefine.listSenderID + "\"}";
            }

            if (jsMsg.optionStatistical == 4) {
                if (RedisStatistical.getInstance().getAvgTimeProcess(jsMsg.userStatistical) == false) {
                    return null;
                }

                return "{\"minTimeProcess\":" + ZMsgDefine.minTimeProPerUsr + ", "
                        + "\"maxTimeProcess\":" + ZMsgDefine.maxTimeProPerUsr + ", "
                        + "\"avgTimeProcess\":" + ZMsgDefine.avgTimeProPerUsr + "}";
            }

            //return json
            return "{result:0,code:404,msg:\"Fail\"}";
        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }
}
