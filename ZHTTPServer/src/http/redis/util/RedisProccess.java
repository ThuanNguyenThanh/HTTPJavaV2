/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;

import http.api.handler.ZMsgDefine;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author root
 */
public class RedisProccess {

    private static final RedisProccess instance = new RedisProccess();

    public static RedisProccess getInstance() {
        return RedisProccess.instance;
    }

    public boolean countMsgForEachUserID(long userID) {
        if (userID <= 0) {
            return false;
        }

        if (RedisUtil.Increase("ns:" + userID + ":msgcounter") == null) {
            return false;
        }

        return true;
    }

    public boolean setMsgInfo(long msgID, long userID, String field, String value) {
        if (msgID <= 0 || userID <= 0 || field == null || value == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", field, value) == false) {
            return false;
        }

        return true;
    }

    public boolean setMsgInfo(long msgID, long userID, String field, long value) {
        if (msgID <= 0 || userID <= 0 || field == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", field, String.valueOf(value)) == false) {
            return false;
        }

        return true;
    }

    //list userid: zrange ns:listuserid 0 -1
    public boolean setListUserID(long msgID, long userID) {
        if (msgID <= 0 || userID <= 0) {
            return false;
        }

        if (RedisUtil.setZStringValue("ns:listuserid", (double) msgID, String.valueOf(userID)) == null) {
            return false;
        }

        return true;
    }

    //Them de tang toc do neu he thong du tai nguyen
    public boolean setListUserIDOfSenderID(long senderID, long userID) {
        if (senderID <= 0 || userID <= 0) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listuserid:" + senderID + ":list", String.valueOf(userID)) == null) {
            return false;
        }

        return true;
    }

    public boolean setListMsgID(long msgID, long senderID, long userID) {
        if (msgID <= 0 || senderID <= 0 || userID <= 0) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listmsgid:" + senderID + ":" + userID + ":list", String.valueOf(msgID)) == null) {
            return false;
        }

        return true;
    }

    public boolean setListSenderID(long msgID, long senderID) {
        if (msgID <= 0 || senderID <= 0) {
            return false;
        }

        if (RedisUtil.setZStringValue("ns:listsenderid", (double) msgID, String.valueOf(senderID)) == null) {
            return false;
        }

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

    public boolean countTotalReqPerUsr(Long userID) {
        if (userID == null) {
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

    public boolean getAvgTimeProccess(long userID) {

        if (userID <= 0) {
            return false;
        }

        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        ZMsgDefine.minTimeProPerUsr = 0;
        ZMsgDefine.maxTimeProPerUsr = 0;
        ZMsgDefine.avgTimeProPerUsr = 0;
        
        for (long msgID = 1; msgID <= numMsgID; msgID++) {
            long timeProccess = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_PROCCESS);

            if (timeProccess < 0) {
                return false;
            }

            if (msgID == 1) {
                ZMsgDefine.minTimeProPerUsr = timeProccess;
            }

            if (ZMsgDefine.minTimeProPerUsr > timeProccess) {
                ZMsgDefine.minTimeProPerUsr = timeProccess;
            }

            if (ZMsgDefine.maxTimeProPerUsr < timeProccess) {
                ZMsgDefine.maxTimeProPerUsr = timeProccess;
            }

            ZMsgDefine.avgTimeProPerUsr += timeProccess;
        }
        
        ZMsgDefine.avgTimeProPerUsr = ZMsgDefine.avgTimeProPerUsr / numMsgID;

        return true;
    }
}
