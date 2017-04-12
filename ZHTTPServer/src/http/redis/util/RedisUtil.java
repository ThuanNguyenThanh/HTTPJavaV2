/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.redis.util;

import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.ClusterClientOptions;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.cluster.api.sync.RedisAdvancedClusterCommands;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import http.config.ZConfigReader;
import java.nio.ByteBuffer;

/**
 *
 * @author root
 */
public class RedisUtil {

    //private static RedisClient redisClient = null;
    private static RedisClusterClient redisClusterClient = null;
    private static RedisAdvancedClusterCommands<String, String> redisStringCommand = null;
    private static RedisAdvancedClusterCommands<String, byte[]> redisByteCommand = null;
    private static RedisAdvancedClusterCommands<String, Long> redisLongCommand = null;

    private static void createRdsIns() {
        /*
        RedisClusterClient clusterClient = new RedisClusterClient(RedisURI.create("redis://localhost:8000"));
        RedisAdvancedClusterConnection<String, String> cluster = clusterClient.connectCluster();
         */
 /*
        redisURIs.add(RedisURI.create("redis://127.0.0.1:8000"));
        redisURIs.add(RedisURI.create("redis://127.0.0.1:8001"));
        redisURIs.add(RedisURI.create("redis://127.0.0.1:8002"));
        redisURIs.add(RedisURI.create("redis://127.0.0.1:8003"));
        redisURIs.add(RedisURI.create("redis://127.0.0.1:8004"));
        redisURIs.add(RedisURI.create("redis://127.0.0.1:8005"));
         */

        List<RedisURI> redisURIs = new ArrayList<RedisURI>();
        List<String> lstRedis = ZConfigReader.getConfigReaderIns().getListRedis();
        for (int i = 0; i < lstRedis.size(); i++) {
            redisURIs.add(RedisURI.create(lstRedis.get(i)));
        }

        redisClusterClient = RedisClusterClient.create(redisURIs);
        redisClusterClient.setOptions(new ClusterClientOptions.Builder()
                .refreshClusterView(true)
                .refreshPeriod(1, TimeUnit.MINUTES)
                .build());

        //System.out.println(redisClient.getPartitions().toString());
        //StatefulRedisClusterConnection<String, String> connection = redisClusterClient.connect();
        //connection.sync()
        //connection.close();
        //redisClient.shutdown();
    }

    //Instances Redis
    public static RedisClusterClient getRdsIns() {
        if (redisClusterClient == null) {
            createRdsIns();
        }
        return redisClusterClient;
    }

    public static RedisAdvancedClusterCommands<String, byte[]> getRdsByteCmdIns() {
        if (redisByteCommand == null) {
            redisByteCommand = getRdsIns().connect(new ByteCodec()).sync();
        }
        return redisByteCommand;
    }

    public static RedisAdvancedClusterCommands<String, String> getRdsStringCmdIns() {
        if (redisStringCommand == null) {
            redisStringCommand = getRdsIns().connect().sync();
        }
        return redisStringCommand;
    }

    public static Long Increase(String key) {
        try {
            if ((key == null)) {
                return null;
            }

            return getRdsStringCmdIns().incr(key);

        } catch (Exception ex) {
            return null;
        }
    }

    public static String setStringValue(String key, String value) {
        try {
            if ((key == null) || (value == null)) {
                return null;
            }

            return getRdsStringCmdIns().set(key, value);

        } catch (Exception ex) {
            return null;
        }
    }

    public static String setByteValue(String key, byte[] value) {
        try {
            if ((key == null) || (value == null)) {
                return null;
            }

            return getRdsByteCmdIns().set(key, value);

        } catch (Exception ex) {
            return null;
        }
    }

    public static Long getStringValue(String key) {
        try {
            if (key.isEmpty()) {
                return null;
            }

            String s = getRdsStringCmdIns().get(key);
            return tryParseLong(s);

        } catch (Exception e) {
            return null;
        }
    }

    public static Long setZStringValue(String key, double score, String value) {
        try {
            if ((key == null) || (value == null)) {
                return null;
            }

            return getRdsStringCmdIns().zadd(key, score, value);

        } catch (Exception ex) {
            return null;
        }
    }

    public static Double getZDoubleValue(String key, String member) {
        try {
            if (key.isEmpty() || member.isEmpty()) {
                return null;
            }

            return getRdsStringCmdIns().zscore(key, member);

        } catch (Exception e) {
            return null;
        }
    }

    public static Long setSByteValue(String key, byte[] value) {
        try {
            if ((key == null) || (value == null)) {
                return null;
            }
            return getRdsByteCmdIns().sadd(key, value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Long setSStringValue(String key, String value) {
        try {
            if ((key == null) || (value == null)) {
                return null;
            }
            return getRdsStringCmdIns().sadd(key, value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean setHashStringValue(String key, String field, String value) {
        try {
            if ((key == null) || (field == null) || (value == null)) {
                return false;
            }

            if (getRdsStringCmdIns().hset(key, field, value) == false) {
                return false;
            }
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean setHashByteValue(String key, String field, byte[] value) {
        try {
            if ((key == null) || (field == null) || (value == null)) {
                return false;
            }

            if (getRdsByteCmdIns().hset(key, field, value) == false) {
                return false;
            }
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static String getHashStringValue(String hash, String key) {
        try {
            if (hash.isEmpty() || key.isEmpty()) {
                return null;
            }

            return getRdsStringCmdIns().hget(hash, key);

        } catch (Exception e) {
            return null;
        }
    }

    public static Long getHashLongValue(String hash, String key) {
        try {
            if (hash.isEmpty() || key.isEmpty()) {
                return null;
            }
            String s = getRdsStringCmdIns().hget(hash, key);
            return tryParseLong(s);

        } catch (Exception e) {
            return null;
        }
    }

    public static Long isExistKey(String... key) {
        return getRdsStringCmdIns().exists(key);
    }

    public static Integer tryParseInt(String text) {
        try {
            if (text == null) {
                return null;
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Long tryParseLong(String text) {
        try {
            if (text == null) {
                return null;
            }
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static byte[] ConvertLongtoByteArr(Long myLong) {
        return ByteBuffer.allocate(4).putLong(myLong).array();
    }

}
