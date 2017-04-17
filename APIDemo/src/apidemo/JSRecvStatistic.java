/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apidemo;

/**
 *
 * @author root
 */
// Parse Json is received from Server

public class JSRecvStatistic {

    private String success;
    private String fail;
    private String totalrequest;

    private String listSenderIDOfUserID;

    private String minTimeProcess;
    private String maxTimeProcess;
    private String avgTimeProcess;

    public String getSuccess() {
        return success;
    }

    public String getFail() {
        return fail;
    }

    public String getTotalRequest() {
        return totalrequest;
    }

    public String getListSenderIDOfUserID() {
        return listSenderIDOfUserID;
    }

    public String getMinTimeProcess() {
        return minTimeProcess;
    }

    public String getMaxTimeProcess() {
        return maxTimeProcess;
    }

    public String getAvgTimeProcess() {
        return avgTimeProcess;
    }

}
