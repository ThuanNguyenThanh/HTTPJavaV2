/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.api.handler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class ZMsgDefine {
    public static final String RDS_MSG_INFO_FIELD_SENDERID        = "ns:senderid";
    public static final String RDS_MSG_INFO_FIELD_USERID          = "ns:userid";
    public static final String RDS_MSG_INFO_FIELD_DATA            = "ns:data";
    public static final String RDS_MSG_INFO_FIELD_TIME_START      = "ns:timestart";
    public static final String RDS_MSG_INFO_FIELD_TIME_PROCESS   = "ns:timeprocess";
    public static final String RDS_MSG_INFO_FIELD_RESULT          = "ns:result";

    public static final String MAX_TIME_PROCESS                  = "ns:maxtimeprocess";
    public static final String MIN_TIME_PROCESS                  = "ns:mintimeprocess";
    public static final String AVG_TIME_PROCESS                  = "ns:avgtimeprocess";
    public static final String SENDERID_EXIST                     = "ns:senderidexist";
    public static final String USERID_EXIST                       = "ns:useridexist";
    public static final String TOTAL_REQUEST                      = "ns:totalrequest";
    public static final String TOTAL_REQUEST_SUCCESS              = "ns:totalrequestsuccess";
    public static final String TOTAL_REQUEST_FAIL                 = "ns:totalrequestfail";

    public static long totalReqSucPerUsr;
    public static long totalReqFailPerUsr;
    public static long totalReqPerUsr;

    public static long totalReqSuc;
    public static long totalReqFail;
    public static long totalReq;
    
    public static long minTimeProPerUsr;
    public static long maxTimeProPerUsr;
    public static long avgTimeProPerUsr;
    
    public static List listSenderID = new ArrayList();
}
