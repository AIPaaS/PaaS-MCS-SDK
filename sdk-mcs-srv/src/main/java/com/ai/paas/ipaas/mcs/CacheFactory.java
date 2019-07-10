package com.ai.paas.ipaas.mcs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.ccs.inner.CCSComponentFactory;
import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.impl.CacheClusterClient;
import com.ai.paas.ipaas.mcs.impl.SentinelClient;
import com.ai.paas.ipaas.uac.service.UserClientFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.paas.ipaas.uac.vo.AuthResult;
import com.ai.paas.util.Assert;
import com.ai.paas.util.CiperUtil;
import com.google.gson.Gson;

public class CacheFactory {

    private static final String CACHE_CONFIG_PATH = "/MCS/";
    private static final String CACHE_COMMON_PATH = "/MCS/COMMON";
    private static final String REDIS_PASSWORD = "password";
    private static final String REDIS_HOST = "hosts";
    private static final String REDIS_SENTINEL = "sentinel";
    private static final String CACHE_KEY = "BaryTukyTukyBary";
    private static Map<String, ICacheClient> cacheClients = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(CacheFactory.class);

    private CacheFactory() {
        // 禁止私有化
    }

    @SuppressWarnings("rawtypes")
    public static ICacheClient getClient(AuthDescriptor ad) throws Exception {
        ICacheClient cacheClient = null;
        log.info("Check Formal Parameter AuthDescriptor ...");
        Assert.notNull(ad, "AuthDescriptor对象为空");
        Assert.notNull(ad.getServiceId(), "service_id为空");
        Assert.notNull(ad.getPid(), "pid is null");
        String srvId = ad.getServiceId();

        // 单例标签
        String instanceKey = ad.getPid() + "_" + ad.getServiceId();
        if (cacheClients.containsKey(instanceKey)) {
            cacheClient = cacheClients.get(instanceKey);
            return cacheClient;
        }
        // 服务号要检验
        // 传入用户描述对象，用户认证地址，服务申请号
        // 进行用户认证
        log.info("Check AuthResult ...");
        AuthResult authResult = UserClientFactory.getUserClient().auth(ad);
        // 认证通过后，判断是否存在已有实例，有，直接返回

        // 开始初始化
        Assert.notNull(authResult.getConfigAddr(), "ConfigAddr为空");
        Assert.notNull(authResult.getConfigUser(), "ConfigUser为空");
        Assert.notNull(authResult.getConfigPasswd(), "ConfigPasswd为空");
        Assert.notNull(authResult.getUserId(), "UserId为空");
        // 获取内部zk地址后取得该用户的cache配置信息，返回JSON String
        // 获取该用户申请的cache服务配置信息
        log.info("Get confBase&conf ...");

        String cacheConf = CCSComponentFactory
                .getConfigClient(authResult.getConfigAddr(), authResult.getConfigUser(), authResult.getConfigPasswd())
                .get(CACHE_COMMON_PATH);
        String personalConf = CCSComponentFactory
                .getConfigClient(authResult.getConfigAddr(), authResult.getConfigUser(), authResult.getConfigPasswd())
                .get(CACHE_CONFIG_PATH + srvId);
        // 封装成配置对象
        Gson gson = new Gson();
        GenericObjectPoolConfig config = gson.fromJson(cacheConf, GenericObjectPoolConfig.class);
        Map personalConfMap = gson.fromJson(personalConf, Map.class);
        log.info("Get pwd&host ...{}" , personalConfMap);
        String pwd = null;
        pwd = (String) personalConfMap.get(REDIS_PASSWORD);
        String host = (String) personalConfMap.get(REDIS_HOST);
        // 为了适应新添加的sentinel模式，对下列实例化方法进行更改，sentinel模式下的数据如：
        if (null != pwd)
            pwd = CiperUtil.decrypt(CACHE_KEY, pwd);
        if (host != null) {
            String[] hostArray = host.split(";");
            log.info("Get RedisClient ...");
            if (hostArray.length > 1) {
                cacheClient = new CacheClusterClient(config, hostArray, pwd);
            } else {
                cacheClient = new CacheClient(config, host, pwd);
            }
        } else {
            String sentinels = (String) personalConfMap.get(REDIS_SENTINEL);
            cacheClient = new SentinelClient(config, sentinels, pwd);
        }
        log.info("Get RedisClient ...");
        cacheClients.put(instanceKey, cacheClient);
        return cacheClient;
    }
}
