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
public class RedisProccess implements RedisInterface {

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
        if (msgID == 0 || userID == 0 || Field == null || Value == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", Field, Value) == false) {
            return false;
        }

        return true;
    }

    public boolean setMsgInfo(Long msgID, Long userID, String Field, Long Value) {
        if (msgID == 0 || userID == 0 || Field == null || Value == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", Field, Value.toString()) == false) {
            return false;
        }

        return true;
    }
    
    //public boolean countTotalRequest()
    //list userid
    public boolean setListUserID(Long msgID, Long userID) {
        if(msgID == null || userID == null)
            return false;
        
        if(RedisUtil.setZStringValue("ns:listuserid", (double)msgID, userID.toString()) == null)
            return false;
        System.out.println("setListUserID");
        return true;
    }
    
//    public String getMsgID(Long userID) {
//        if (userID == 0) {
//            return "";
//        }
//
//        Long msgCounter = RedisUtil.getStringValue("ns:" + userID + ":msgcounter");
//
//        if (msgCounter == null) {
//            return "";
//        }
//
//        return userID.toString() + msgCounter;
//    }

    @Override
    public boolean SetMsgIDInfo(String MsgID, String Field, String Value) {
        if ((MsgID == null) || (Field == null) || (Value == null)) {
            return false;
        }

        if (RedisUtil.setHashStringValue(MsgID.toString(), Field, Value) == false) {
            return false;
        }

        return true;
    }

    @Override
    public boolean SetMsgIDInfo(String MsgID, String Field, Long Value) {

        if ((MsgID == null) || (Field == null) || (Value == null)) {
            return false;
        }

        if (RedisUtil.setHashStringValue(MsgID.toString(), Field, Value.toString()) == false) {
            return false;
        }

        if (RedisUtil.getHashStringValue(MsgID.toString(), Field).compareTo(Value.toString()) == 0) {
            System.out.println("Pass");
        } else {
            System.err.println("Fail");
        }
        return true;
    }

    @Override
    public boolean SetUserIDAndSenderIDInfo(Long MsgID, Long SenderID, Long UserID) {
        if (MsgID == 0 || SenderID == 0 || UserID == 0) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listmsgofsenderid:" + SenderID + ":list", MsgID.toString()) == null) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listmsgofuserid:" + UserID + ":list", MsgID.toString()) == null) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listuseridofsenderid:" + SenderID + ":list", UserID.toString()) == null) {
            return false;
        }

        if (RedisUtil.setSStringValue("ns:listsenderidofuserid:" + UserID + ":list", SenderID.toString()) == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean IncreaseRequest(Long MsgID) {
        if (MsgID == null) {
            return false;
        }

        if (RedisUtil.Increase(ZMsgDefine.TOTAL_REQUEST) == null) {
            return false;
        }

        if (RedisUtil.getHashLongValue(MsgID.toString(), ZMsgDefine.RDS_MSG_INFO_FIELD_RESULT) == 1) {
            if (RedisUtil.Increase(ZMsgDefine.TOTAL_REQUEST_SUCCESS) == null) {
                return false;
            }
        } else if (RedisUtil.Increase(ZMsgDefine.TOTAL_REQUEST_FAIL) == null) {
            return false;
        }

        return true;
    }

    public boolean GetAverageTimeProccess(Long MsgID, Long TimeProccess) {
        if (MsgID == 0) {
            return false;
        }

        long Avg = 0;

        if (MsgID == 1) {
            Avg = TimeProccess;
            if (RedisUtil.setStringValue(ZMsgDefine.MAX_TIME_PROCCESS, TimeProccess.toString()) == null) {
                return false;
            }

            if (RedisUtil.setStringValue(ZMsgDefine.MIN_TIME_PROCCESS, TimeProccess.toString()) == null) {
                return false;
            }
        } else {
            long TempTimeAvg = RedisUtil.getStringValue(ZMsgDefine.AVG_TIME_PROCCESS);
            Avg = (TimeProccess + (MsgID - 1) * TempTimeAvg) / MsgID;
        }

        if (RedisUtil.setStringValue(ZMsgDefine.AVG_TIME_PROCCESS, TimeProccess.toString()) == null) {
            return false;
        }

        if (RedisUtil.getStringValue(ZMsgDefine.MAX_TIME_PROCCESS) < TimeProccess) {
            if (RedisUtil.setStringValue(ZMsgDefine.MAX_TIME_PROCCESS, TimeProccess.toString()) == null) {
                return false;
            }

            return true;
        }

        if (RedisUtil.getStringValue(ZMsgDefine.MIN_TIME_PROCCESS) > TimeProccess) {
            if (RedisUtil.setStringValue(ZMsgDefine.MIN_TIME_PROCCESS, TimeProccess.toString()) == null) {
                return false;
            }

            return true;
        }

        return true;
    }

    @Override
    public boolean CountTotalSenderID(Long SenderID) {
        if (SenderID == 0) {
            return false;
        }

        if (RedisUtil.setSStringValue(ZMsgDefine.SENDERID_EXIST, SenderID.toString()) == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean CountTotalUserID(Long UserID) {
        if (UserID == 0) {
            return false;
        }

        if (RedisUtil.setSStringValue(ZMsgDefine.USERID_EXIST, UserID.toString()) == null) {
            return false;
        }

        return true;
    }
}
