package http.api.handler;

import java.util.List;
import org.apache.log4j.Logger;
import http.api.path.ZPathAPI;

public class MethodHandlerFactory {

    private static final Logger logger = Logger.getLogger(MethodHandlerFactory.class);

    public static IMethodHandler getMethodHandler(List<String> pathAPI) {
        IMethodHandler handler = null;
        if (pathAPI.size() < 1) {
            return handler;
        }

        String methodName = pathAPI.get(0).toLowerCase();

        switch (methodName) {
            case ZPathAPI.API_ROOT_PATH:
                if (pathAPI.size() >= 3) {
                    String group = "";
                    String typeMessage = "";

                    if (pathAPI.size() >= 2) {
                        group = pathAPI.get(1);
                    }

                    if (pathAPI.size() >= 3) {
                        typeMessage = pathAPI.get(2);
                        
                    }

                    if (group.compareToIgnoreCase(ZPathAPI.API_GROUP_1) == 0) {
                        //http://abc.com.vn/api/group1/database
                        if (typeMessage.compareToIgnoreCase(ZPathAPI.API_DATABASE) == 0) {
                            handler = ZSaveMessageDB.getInstance();
                        }

                        //http://abc.com.vn/api/group1/statiscal
                        if (typeMessage.compareToIgnoreCase(ZPathAPI.API_STATISTICAL) == 0) {
                            handler = ZLoadStatisticalDB.getInstance();
                        }

                        //http://abc.com.vn/api/group2/example2
                        //else if(typeMessage.compareToIgnoreCase(Define.API_PATH_SETCONFIG) == 0)
                        //{
                        //    //handler = ApiSetConfigHandler.getInstance();
                        //}
                        //else if(typeMessage.compareToIgnoreCase(Define.API_PATH_GETCONFIG) == 0)
                        //{
                        //    //handler = ApiGetConfigHandler.getInstance();
                        //}
                    }
                }
                break;
        }

        return handler;
    }
}
