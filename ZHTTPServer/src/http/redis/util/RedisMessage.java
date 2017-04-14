/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;
/**
 *
 * @author root
 */
public class RedisMessage {

    private static final RedisMessage instance = new RedisMessage();

    public static RedisMessage getInstance() {
        return RedisMessage.instance;
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
}
