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

    public long countMsgForEachUserID(long userID) {
        if (userID <= 0) {
            return 0;
        }

        Long numMsg = RedisUtil.Increase("ns:" + userID + ":msgcounter");

        if (numMsg == 0) {
            return 0;
        }

        return numMsg;
    }

    public boolean addSetRds(String strKey, String strMember) {
        if (strKey.isEmpty() || strMember.isEmpty()) {
            return false;
        }

        if (RedisUtil.setSStringValue(strKey, strMember) == null) {
            return false;
        }

        return true;
    }

    public boolean addMsgInfo(long msgID, long userID, String field, String value) {
        if (msgID <= 0 || userID <= 0 || field == null || value == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", field, value) == false) {
            return false;
        }

        return true;
    }

    public boolean addMsgInfo(long msgID, long userID, String field, long value) {
        if (msgID <= 0 || userID <= 0 || field == null) {
            return false;
        }

        if (RedisUtil.setHashStringValue("ns:" + userID + ":" + msgID + ":info", field, String.valueOf(value)) == false) {
            return false;
        }

        return true;
    }

    //list userid: zrange ns:listuserid 0 -1
    public boolean addListUserID(long msgID, long userID) {
        if (msgID <= 0 || userID <= 0) {
            return false;
        }

        if (RedisUtil.setZStringValue("ns:listuserid", (double) msgID, String.valueOf(userID)) == null) {
            return false;
        }

        return true;
    }

    //Them de tang toc do neu he thong du tai nguyen
    public boolean addListUserIDOfSenderID(long senderID, long userID) {
        if (senderID <= 0 || userID <= 0) {
            return false;
        }

        return addSetRds("ns:listuserid:" + senderID + ":list", String.valueOf(userID));
    }

    public boolean addListMsgID(long msgID, long senderID, long userID) {
        if (msgID <= 0 || senderID <= 0 || userID <= 0) {
            return false;
        }

        return addSetRds("ns:listmsgid:" + senderID + ":" + userID + ":list", String.valueOf(msgID));
    }

    public boolean addListSenderID(long msgID, long senderID) {
        if (msgID <= 0 || senderID <= 0) {
            return false;
        }

        if (RedisUtil.setZStringValue("ns:listsenderid", (double) msgID, String.valueOf(senderID)) == null) {
            return false;
        }

        return true;
    }
}
