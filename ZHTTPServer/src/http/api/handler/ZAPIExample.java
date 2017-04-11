///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package http.api.handler;
//
//import com.google.protobuf.ByteString;
//import http.api.utils.GsonUtils;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//import javax.servlet.http.HttpServletRequest;
//
///**
// *
// * @author root
// */
//public class ZAPIExample extends BaseApiHandler {
//
//    private static final ZAPIExample instance = new ZAPIExample();
//
//    private ZAPIExample() {
//    }
//
//    public static ZAPIExample getInstance() {
//        return instance;
////    }
//    
//    @Override
//    public String doAction(HttpServletRequest req) {
//        try {
//            if (req.getMethod().compareToIgnoreCase("POST") != 0)
//                return "{result:0,code:405,msg:\"Wrong method. Must be POST\"}";
//            
//            InputStream is = req.getInputStream();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
////
//            byte buf[] = new byte[2048];
//            int letti = 0;
//
//            while ((letti = is.read(buf)) > 0) {
//                baos.write(buf, 0, letti);
//            }
//
//            String js = new String(baos.toByteArray(), Charset.forName("UTF-8"));
//            JSMessageExample jsMsg = GsonUtils.fromJsonString(js, JSMessageExample.class);
//            
//            //do someting
////            System.out.println(jsMsg. + " " + jsMsg.data 
////                    + " " + jsMsg.number + " " + jsMsg.signature);
//           
//            //return json
//            //int sum = 
//            return "{result:1,code:0,msg:\"abcdef\"}";
//
//        } catch (Exception ex) {
//            return "{result:0,code:404,msg:\"exception\"}";
//        }
//    }
//    
//}
