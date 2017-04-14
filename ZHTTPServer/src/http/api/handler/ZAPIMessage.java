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
import http.redis.util.RedisMessage;
import http.redis.util.RedisUtil;
import java.sql.Timestamp;
import java.util.Random;

/**
 *
 * @author root
 */
public class ZAPIMessage extends BaseApiHandler {

    private static volatile ZAPIMessage instance;

    private ZAPIMessage() {
    }

    public static ZAPIMessage getInstance() {
        if (instance == null) {
            instance = new ZAPIMessage();
        }
        
        return instance;
    }

    private long RandomResult() {
        Random rand = new Random();
        return (long) rand.nextInt(2); //random value 0 1
    }

    @Override
    public String doAction(HttpServletRequest req) {
        try {
            Timestamp timeStart = new Timestamp(System.currentTimeMillis());
            System.out.println("----------------New connection--------------- "
                    + "\nTime start: " + timeStart.getTime());

            if (req.getMethod().compareToIgnoreCase("POST") != 0) {
                return "{result:0,code:405,msg:\"Wrong method. Must be POST\"}";
            }

            InputStream is = req.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            long resultProcess = RandomResult();

            byte buf[] = new byte[2048];
            int letti = 0;

            while ((letti = is.read(buf)) > 0) {
                baos.write(buf, 0, letti);
            }

            String js = new String(baos.toByteArray(), Charset.forName("UTF-8"));
            JSMessageExample jsMsg = GsonUtils.fromJsonString(js, JSMessageExample.class);

            //do someting
            System.out.println("SenderID: " + jsMsg.senderID + "\nUserID: " + jsMsg.userID + "\nData: " + jsMsg.data);

            if (RedisMessage.getInstance().countMsgForEachUserID(jsMsg.userID) == false) {
                return null;
            }

            long msgID = RedisUtil.getStringValue("ns:" + jsMsg.userID + ":msgcounter");

            if (msgID == 0) {
                return null;
            }

            System.out.println("MessageID: " + msgID);

            if (RedisMessage.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_SENDERID, jsMsg.senderID) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_DATA, jsMsg.data) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT, resultProcess) == false) {
                return null;
            }

            System.out.println("Result: " + resultProcess);

            Timestamp timeEnd = new Timestamp(System.currentTimeMillis());
            System.out.println("Time end: " + timeEnd.getTime());

            long timeProcess = timeEnd.getTime() - timeStart.getTime();
            System.out.println("Time process: " + timeProcess);

            if (RedisMessage.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_START, timeStart.getTime()) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setMsgInfo(msgID, jsMsg.userID, ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_PROCESS, timeProcess) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setListUserID(msgID, jsMsg.userID) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setListUserIDOfSenderID(jsMsg.senderID, jsMsg.userID) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setListMsgID(msgID, jsMsg.senderID, jsMsg.userID) == false) {
                return null;
            }

            if (RedisMessage.getInstance().setListSenderID(msgID, jsMsg.senderID) == false) {
                return null;
            }

            //return json
            return "{result:0,code:404,msg:\"Success\"}";

        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }
}
