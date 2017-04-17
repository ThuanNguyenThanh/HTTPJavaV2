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
public class ResultAPI {
    public int result;
    public int code;
    public int success;
    public int fail;
    public int totalrequest;

    public List listSenderID = new ArrayList();

    public long avgTimeProPerUsr;
    public long minTimeProPerUsr;
    public long maxTimeProPerUsr;

    public ResultAPI() {
        this.result = 0;
        this.code = 0;
        this.success = 0;
        this.fail = 0;
        this.totalrequest = 0;
        
        this.listSenderID = null;
        
        this.avgTimeProPerUsr = 0;
        this.minTimeProPerUsr = 0;
        this.maxTimeProPerUsr = 0;
    }
}
