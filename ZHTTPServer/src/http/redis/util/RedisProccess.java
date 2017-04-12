/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;

import http.api.handler.ZMsgDefine;

/**
 *
 * @author root
 */
public class RedisProccess{

    private static final RedisProccess instance = new RedisProccess();

    public static RedisProccess getInstance() {
        return RedisProccess.instance;
    }

    public boolean countMsgForEachUserID(Long userID) {
        if (userID == 0) {
            return false;
        }

        if (RedisUtil.Increase("ns:" + userID + ":msgcounter") == null) {
            return false;
        }

        return true;
    }

    public boolean setMsgInfo(Long msgID, Long userID, String Field, String Value) {
        if (msgID == null || userID == null || Field == null || Value == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", Field, Value) == false) {
            return false;
        }

        return true;
    }

    public boolean setMsgInfo(Long msgID, Long userID, String Field, Long Value) {
        if (msgID == null || userID == null || Field == null || Value == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", Field, Value.toString()) == false) {
            return false;
        }

        return true;
    }

    //list userid: zrange ns:listuserid 0 -1
    public boolean setListUserID(Long msgID, Long userID) {
        if (msgID == null || userID == null) {
            return false;
        }

        if (RedisUtil.setZStringValue("ns:listuserid", (double) msgID, userID.toString()) == null) {
            return false;
        }
        System.out.println("setUserID: " + userID);
        return true;
    }

    public boolean countTotalRequest(Long msgID, Long userID) {
        if (msgID == null) {
            return false;
        }

        if (RedisUtil.Increase(ZMsgDefine.TOTAL_REQUEST) == null) {
            return false;
        }

        if (RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT) == 1) {
            if (RedisUtil.Increase(ZMsgDefine.TOTAL_REQUEST_SUCCESS) == null) {
                return false;
            }
        } else if (RedisUtil.Increase(ZMsgDefine.TOTAL_REQUEST_FAIL) == null) {
            return false;
        }

        return true;
    }

    //Them de tang toc do neu he thong du tai nguyen
    public boolean setListUserIDOfSenderID(Long senderID, Long userID) {
        if (senderID == null || userID == null) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listuserid:" + senderID + ":list", userID.toString()) == null) {
            return false;
        }

        return true;
    }

    public boolean setListMsgID(Long msgID, Long senderID, Long userID) {
        if (msgID == null || senderID == null || userID == null) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listmsgid:" + senderID + ":" + userID + ":list", msgID.toString()) == null) {
            return false;
        }

        return true;
    }

    public boolean setListSenderID(Long msgID, Long senderID) {
        if (msgID == null || senderID == null) {
            return false;
        }

        if (RedisUtil.setZStringValue("ns:listsenderid", (double) msgID, senderID.toString()) == null) {
            return false;
        }

        System.out.println("setSenderID: " + senderID);
        return true;
    }

    public boolean getAverageTimeProccess(Long timeProccess) {
        Long numMsg = RedisUtil.getStringValue(ZMsgDefine.TOTAL_REQUEST);
        
        if (numMsg == null || numMsg == 0) {
            return false;
        }

        long Avg = 0;

        if (numMsg == 1) {
            Avg = timeProccess;
            if (RedisUtil.setStringValue(ZMsgDefine.MAX_TIME_PROCCESS, timeProccess.toString()) == null) {
                return false;
            }

            if (RedisUtil.setStringValue(ZMsgDefine.MIN_TIME_PROCCESS, timeProccess.toString()) == null) {
                return false;
            }
        } else {
            long TempTimeAvg = RedisUtil.getStringValue(ZMsgDefine.AVG_TIME_PROCCESS);
            Avg = (timeProccess + (numMsg - 1) * TempTimeAvg) / numMsg;
        }

        if (RedisUtil.setStringValue(ZMsgDefine.AVG_TIME_PROCCESS, timeProccess.toString()) == null) {
            return false;
        }

        if (RedisUtil.getStringValue(ZMsgDefine.MAX_TIME_PROCCESS) < timeProccess) {
            if (RedisUtil.setStringValue(ZMsgDefine.MAX_TIME_PROCCESS, timeProccess.toString()) == null) {
                return false;
            }

            return true;
        }

        if (RedisUtil.getStringValue(ZMsgDefine.MIN_TIME_PROCCESS) > timeProccess) {
            if (RedisUtil.setStringValue(ZMsgDefine.MIN_TIME_PROCCESS, timeProccess.toString()) == null) {
                return false;
            }

            return true;
        }

        return true;
    }
}
