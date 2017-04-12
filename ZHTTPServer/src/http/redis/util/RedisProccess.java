/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;

import http.api.handler.ZMsgDefine;
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
        System.out.println("setUserID: " + userID);
        return true;
    }

    //
    public boolean countTotalRequest() {
        List listUsrID = RedisUtil.getZrange("ns:listuserid");
        if (listUsrID.isEmpty()) {
            return false;
        }

        long msgID;
        long totalReqPerUsr = 0;
        long totalReqSuc = 0, totalReqFail = 0, totalReq = 0;

        for (Object userID : listUsrID) {
            long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

            if (numMsgID <= 0) {
                return false;
            }
            long totalReqSucPerUsr = 0, totalReqFailPerUsr = 0;

            for (msgID = 1; msgID <= numMsgID; msgID++) {

                if (RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT) == 1) {
                    totalReqSucPerUsr++;
                } else {
                    totalReqFailPerUsr++;
                }
            }
            totalReqPerUsr = totalReqSucPerUsr + totalReqFailPerUsr;
            System.out.println("Number of request success per user " + userID + ": " + totalReqSucPerUsr);

            System.out.println("Number of request fail per user " + userID + ": " + totalReqFailPerUsr);

            System.out.println("Number of request per user " + userID + ": " + totalReqPerUsr);
            totalReqSuc += totalReqSucPerUsr;
            totalReqFail += totalReqFailPerUsr;
            totalReq += totalReqPerUsr;
        }

        System.out.println("Number of request success: " + totalReqSuc);
        System.out.println("Number request fail: " + totalReqFail);
        System.out.println("Number request: " + totalReq);

        return true;
    }

    public boolean testZrange() {
        List l = RedisUtil.getZrange("ns:listuserid");

        if (l.isEmpty()) {
            return false;
        }

        //System.out.println("Noi dung list: " + l);
        for (Object number : l) {
            System.out.println("Noi dung list: " + number);
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

        System.out.println("setSenderID: " + senderID);
        return true;
    }

    public boolean getAverageTimeProccess(long msgID, long userID, long timeProccess) {
        //Long numMsg = RedisUtil.getStringValue(ZMsgDefine.TOTAL_REQUEST);

        if (msgID <= 0) {
            return false;
        }

        double avgTimeProccess;

        if (msgID == 1) {
            avgTimeProccess = timeProccess;

            if (RedisUtil.setZStringValue(ZMsgDefine.MAX_TIME_PROCCESS, timeProccess, String.valueOf(userID)) == null) {
                return false;
            }

            if (RedisUtil.setZStringValue(ZMsgDefine.MIN_TIME_PROCCESS, timeProccess, String.valueOf(userID)) == null) {
                return false;
            }
        } else {
            Double TempTimeAvg = RedisUtil.getZDoubleValue(ZMsgDefine.AVG_TIME_PROCCESS, String.valueOf(userID));
            avgTimeProccess = (timeProccess + (msgID - 1) * TempTimeAvg) / msgID;
        }

        if (RedisUtil.setZStringValue(ZMsgDefine.AVG_TIME_PROCCESS, avgTimeProccess, String.valueOf(userID)) == null) {
            return false;
        }

        if (RedisUtil.getZDoubleValue(ZMsgDefine.MAX_TIME_PROCCESS, String.valueOf(userID)) < timeProccess) {
            if (RedisUtil.setZStringValue(ZMsgDefine.MAX_TIME_PROCCESS, timeProccess, String.valueOf(userID)) == null) {
                return false;
            }

            return true;
        }

        if (RedisUtil.getZDoubleValue(ZMsgDefine.MIN_TIME_PROCCESS, String.valueOf(userID)) > timeProccess) {
            if (RedisUtil.setZStringValue(ZMsgDefine.MIN_TIME_PROCCESS, timeProccess, String.valueOf(userID)) == null) {
                return false;
            }

            return true;
        }

        return true;
    }
}
