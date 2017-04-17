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

    public String success;
    public String fail;
    public String totalrequest;

    public String listSenderIDOfUserID;

    public String minTimeProcess;
    public String maxTimeProcess;
    public String avgTimeProcess;

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
