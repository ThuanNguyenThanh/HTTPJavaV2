/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.api.handler;

import http.api.utils.GsonUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import http.redis.util.RedisProccess;
import http.redis.util.RedisUtil;
import java.sql.Timestamp;
import java.util.Random;

/**
 *
 * @author root
 */
public class ZAPIGetJson extends BaseApiHandler {

    private static final ZAPIGetJson instance = new ZAPIGetJson();
    //private static final RedisProccess RdsPro = new RedisProccess();

    private ZAPIGetJson() {
    }

    public static ZAPIGetJson getInstance() {
        return instance;
    }

    private long RandomResult() {
        Random rand = new Random();
        return (long) rand.nextInt(2);
    }

    @Override
    public String doAction(HttpServletRequest req) {
        try {
            Timestamp timeStart = new Timestamp(System.currentTimeMillis());
            System.out.println("Time start: " + timeStart.getTime());

            if (req.getMethod().compareToIgnoreCase("POST") != 0) {
                return "{result:0,code:405,msg:\"Wrong method. Must be POST\"}";
            }

            InputStream is = req.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            long result = RandomResult();

            byte buf[] = new byte[2048];
            int letti = 0;

            while ((letti = is.read(buf)) > 0) {
                baos.write(buf, 0, letti);
            }

            String js = new String(baos.toByteArray(), Charset.forName("UTF-8"));
            JSMessageExample jsMsg = GsonUtils.fromJsonString(js, JSMessageExample.class);

            //do someting
            System.out.println(jsMsg.senderID + " " + jsMsg.userID + " " + jsMsg.data);

            if (RedisProccess.getInstance().countMsgForEachUserID(jsMsg.userID) == false) {
                return null;
            }

            Long msgID = RedisUtil.getStringValue("ns:" + jsMsg.userID + ":msgcounter");
            if (msgID == null) {
                return null;
            }

            System.out.println("MessageID: " + msgID);

            if (RedisProccess.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_SENDERID, jsMsg.senderID) == false) {
                return null;
            }

            if (RedisProccess.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_DATA, jsMsg.data) == false) {
                return null;
            }

            if (RedisProccess.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT, result) == false) {
                return null;
            }

            Timestamp timeEnd = new Timestamp(System.currentTimeMillis());
            System.out.println("Time end: " + timeEnd.getTime());

            long timeProccess = timeEnd.getTime() - timeStart.getTime();
            System.out.println("Time proccess: " + timeProccess);

            if (RedisProccess.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_START, timeStart.getTime()) == false) {
                return null;
            }

            if (RedisProccess.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_PROCCESS, timeProccess) == false) {
                return null;
            }

            if (RedisProccess.getInstance().setListUserID(msgID, jsMsg.userID) == false) {
                return null;
            }
            //return json
            return "{result:1,code:0,msg:\"abcdef\"}";

        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }

}
