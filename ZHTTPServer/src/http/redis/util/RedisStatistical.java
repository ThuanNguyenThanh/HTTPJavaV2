/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;

import http.api.handler.ResultAPI;
import http.api.handler.ZMsgDefine;
import java.util.ArrayList;
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

    public ResultAPI countTotalReqPerUsr(Long userID) {
        ResultAPI result = new ResultAPI();

        if (userID <= 0) {
            result.result = 0;
            result.code = -100;
            return result;
        }

        int totalReqSucPerUsr = 0;
        int totalReqFailPerUsr = 0;
        int totalReqPerUsr = 0;

        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        if (numMsgID <= 0) {
            result.result = 0;
            result.code = -100;
            return result;
        }

        for (int msgID = 1; msgID <= numMsgID; msgID++) {
            long resultRequest = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT);
            if (resultRequest < 0 || resultRequest > 1) {
                result.result = 0;
                result.code = -100;
                return result;
            }

            if (resultRequest == 1) {
                totalReqSucPerUsr++;
            } else {
                totalReqFailPerUsr++;
            }
        }

        totalReqPerUsr = totalReqSucPerUsr + totalReqFailPerUsr;

        result.result = 1;
        result.code = 200;
        result.success = totalReqSucPerUsr;
        result.fail = totalReqFailPerUsr;
        result.totalrequest = totalReqPerUsr;
        return result;

    }

    public ResultAPI countTotalRequest() {

        ResultAPI result = new ResultAPI();

        List listUsrID = RedisUtil.getZrange("ns:listuserid");
        if (listUsrID.isEmpty()) {
            result.result = 0;
            result.code = -100;
            return result;
        }

        int totalReq = 0;
        int totalReqSuc = 0;
        int totalReqFail = 0;

        for (int index = 0; index < listUsrID.size(); index++) {
            long numMsgID = RedisUtil.getStringValue("ns:" + listUsrID.get(index) + ":msgcounter");

            if (numMsgID <= 0) {
                result.result = 0;
                result.code = -99;
                return result;
            }

            for (int msgID = 1; msgID <= numMsgID; msgID++) {
                long resultProcess = RedisUtil.getHashLongValue("ns:" + listUsrID.get(index) + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT);
                if (resultProcess < 0 || resultProcess > 1) {
                    result.result = 0;
                    result.code = -99;
                    return result;
                }

                if (resultProcess == 1) {
                    totalReqSuc++;
                } else {
                    totalReqFail++;
                }
            }
        }

        totalReq += totalReqSuc + totalReqFail;

        result.result = 1;
        result.code = 200;
        result.success = totalReqSuc;
        result.fail = totalReqFail;
        result.totalrequest = totalReq;
        return result;

    }

    public ResultAPI getListSenderIDOfUserID(Long userID) {
        ResultAPI result = new ResultAPI();
        if (userID <= 0) {
            result.result = 0;
            result.code = -100;
            return result;
        }

        List listSenderID = new ArrayList();
        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        if (numMsgID <= 0) {
            result.result = 0;
            result.code = -100;
            return result;
        }

        for (long msgID = 1; msgID <= numMsgID; msgID++) {
            long senderID = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_SENDERID);
            if (senderID <= 0) {
                result.result = 0;
                result.code = -100;
                return result;
            }

            if (!listSenderID.contains(senderID)) {
                listSenderID.add(senderID);
            }
        }
        result.result = 1;
        result.code = 200;
        result.listSenderID = listSenderID;
        return result;

    }

    public ResultAPI getAvgTimeProcess(long userID) {
        ResultAPI result = new ResultAPI();
        if (userID <= 0) {
            result.result = 0;
            result.code = -100;
            return result;
        }

        long numMsgID = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");

        for (long msgID = 1; msgID <= numMsgID; msgID++) {
            long timeProcess = RedisUtil.getHashLongValue("ns:" + userID + ":" + msgID + ":info", ZMsgDefine.RDS_MSG_INFO_FIELD_TIME_PROCESS);

            if (timeProcess < 0) {
                result.result = 0;
                result.code = -100;
                return result;
            }

            if (msgID == 1) {
                result.minTimeProPerUsr = timeProcess;
            }

            if (result.minTimeProPerUsr > timeProcess) {
                result.minTimeProPerUsr = timeProcess;
            }

            if (result.maxTimeProPerUsr < timeProcess) {
                result.maxTimeProPerUsr = timeProcess;
            }

            result.avgTimeProPerUsr += timeProcess;
        }

        result.avgTimeProPerUsr = result.avgTimeProPerUsr / numMsgID;
        result.result = 1;
        result.code = 200;
        return result;

    }
}
