/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;

import http.api.handler.ZMsgDefine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author root
 */
public class RedisStatistical {

    private static volatile RedisStatistical instance;

    public static RedisStatistical getInstance() {
        if (instance == null) {
            instance = new RedisStatistical();
        }

        return instance;
    }

    public boolean countTotalReqPerUsr(Long userID) {
        if (userID <= 0) {
            return false;
        }

        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        if (numMsgID <= 0) {
            return false;
        }

        ZMsgDefine.totalReqFailPerUsr = 0;
        ZMsgDefine.totalReqSucPerUsr = 0;
        ZMsgDefine.totalReqPerUsr = 0;

        for (long msgID = 1; msgID <= numMsgID; msgID++) {
            long result = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT);
            if (result < 0 || result > 1) {
                return false;
            }

            if (result == 1) {
                ZMsgDefine.totalReqSucPerUsr++;
            } else {
                ZMsgDefine.totalReqFailPerUsr++;
            }
        }

        ZMsgDefine.totalReqPerUsr = ZMsgDefine.totalReqSucPerUsr + ZMsgDefine.totalReqFailPerUsr;
        return true;
    }

    public boolean countTotalRequest() {
        List listUsrID = RedisUtil.getZrange("ns:listuserid");

        if (listUsrID.isEmpty()) {
            return false;
        }

        ZMsgDefine.totalReqSuc = 0;
        ZMsgDefine.totalReqFail = 0;
        ZMsgDefine.totalReq = 0;

        Iterator<String> userID = listUsrID.iterator();

        while (userID.hasNext()) {
            countTotalReqPerUsr(Long.valueOf(userID.next()));

            ZMsgDefine.totalReqSuc += ZMsgDefine.totalReqSucPerUsr;
            ZMsgDefine.totalReqFail += ZMsgDefine.totalReqFailPerUsr;
            ZMsgDefine.totalReq += ZMsgDefine.totalReqPerUsr;
        }

        return true;
    }

    public boolean getListSenderIDOfUserID(Long userID) {
        if (userID <= 0) {
            return false;
        }

        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        if (numMsgID <= 0) {
            return false;
        }

        

        for (long msgID = 1; msgID <= numMsgID; msgID++) {
            long senderID = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_SENDERID);
            if (senderID <= 0) {
                return false;
            }

            if (!ZMsgDefine.listSenderID.contains(senderID)) {
                ZMsgDefine.listSenderID.add(senderID);
            }
        }

        System.out.println("List senderID of userID " + userID + ": " + ZMsgDefine.listSenderID);

        return true;
    }

    public boolean getAvgTimeProcess(long userID) {
        if (userID <= 0) {
            return false;
        }

        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        ZMsgDefine.minTimeProPerUsr = 0;
        ZMsgDefine.maxTimeProPerUsr = 0;
        ZMsgDefine.avgTimeProPerUsr = 0;

        for (long msgID = 1; msgID <= numMsgID; msgID++) {
            long timeProcess = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_PROCESS);

            if (timeProcess < 0) {
                return false;
            }

            if (msgID == 1) {
                ZMsgDefine.minTimeProPerUsr = timeProcess;
            }

            if (ZMsgDefine.minTimeProPerUsr > timeProcess) {
                ZMsgDefine.minTimeProPerUsr = timeProcess;
            }

            if (ZMsgDefine.maxTimeProPerUsr < timeProcess) {
                ZMsgDefine.maxTimeProPerUsr = timeProcess;
            }

            ZMsgDefine.avgTimeProPerUsr += timeProcess;
        }

        ZMsgDefine.avgTimeProPerUsr = ZMsgDefine.avgTimeProPerUsr / numMsgID;

        return true;
    }
}
