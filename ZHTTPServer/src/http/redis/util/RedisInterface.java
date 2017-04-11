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
public interface RedisInterface {
    public boolean SetMsgIDInfo(String MsgID, String Field, String Value);
    public boolean SetMsgIDInfo(String MsgID, String Field, Long Value);
    public boolean SetUserIDAndSenderIDInfo(Long MsgID, Long SenderID, Long UserID);
    public boolean IncreaseRequest(Long MsgID);
    public boolean  CountTotalSenderID(Long SenderID);
    public boolean  CountTotalUserID(Long UserID);
}
