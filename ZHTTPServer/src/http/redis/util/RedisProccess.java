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
            long totalReqSucPerUsr = 0, totalReqFailPerUsr = 0;

            if (numMsgID <= 0) {
                return false;
            }

            for (msgID = 1; msgID <= numMsgID; msgID++) {
                long result = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT);
                if (result < 0 || result > 1) {
                    return false;
                }

                if (result == 1) {
                    totalReqSucPerUsr++;
                } else {
                    totalReqFailPerUsr++;
                }
            }

            totalReqPerUsr = totalReqSucPerUsr + totalReqFailPerUsr;

            System.out.println("----Request for userID: " + userID + "----");
            System.out.println("Success: " + totalReqSucPerUsr);
            System.out.println("Fail: " + totalReqFailPerUsr);
            System.out.println("Total request: " + totalReqPerUsr);

            totalReqSuc += totalReqSucPerUsr;
            totalReqFail += totalReqFailPerUsr;
            totalReq += totalReqPerUsr;
        }

        System.out.println("----Request for System----");
        System.out.println("Success: " + totalReqSuc);
        System.out.println("Fail: " + totalReqFail);
        System.out.println("Total request: " + totalReq);

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

    public boolean getAvgTimeProccess() {
        long minTimeProccess = 0, maxTimeProccess = 0, avgTimeProccess = 0;
        List listuserID = RedisUtil.getZrange("ns:listuserid");
        if (listuserID.isEmpty()) {
            return false;
        }

        for (Object userID : listuserID) {
            long minTimeProPerUsr = 0, maxTimeProPerUsr = 0, avgTimeProPerUsr = 0;
            long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");
            for (long msgID = 1; msgID <= numMsgID; msgID++) {
                long timeProccess = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_PROCCESS);

                if (timeProccess < 0) {
                    return false;
                }

                if (msgID == 1) {
                    minTimeProPerUsr = timeProccess;
                }
                
                if (minTimeProPerUsr > timeProccess) {
                    minTimeProPerUsr = timeProccess;
                }

                if (maxTimeProPerUsr < timeProccess) {
                    maxTimeProPerUsr = timeProccess;
                }

                avgTimeProPerUsr += timeProccess;
            }
            avgTimeProPerUsr = avgTimeProPerUsr / numMsgID;

            System.out.println("----Time Proccess for userID: " + userID + "----");
            System.out.println("Min: " + minTimeProPerUsr);
            System.out.println("Max: " + maxTimeProPerUsr);
            System.out.println("Avg: " + avgTimeProPerUsr);

            minTimeProccess += minTimeProPerUsr;
            maxTimeProccess += maxTimeProPerUsr;
            avgTimeProccess += avgTimeProPerUsr;
        }

        System.out.println("----Time Proccess for System----");
        System.out.println("Min: " + minTimeProccess/(listuserID.size()));
        System.out.println("Max: " + maxTimeProccess/(listuserID.size()));
        System.out.println("Avg: " + avgTimeProccess/(listuserID.size()));

        return true;
    }
}
