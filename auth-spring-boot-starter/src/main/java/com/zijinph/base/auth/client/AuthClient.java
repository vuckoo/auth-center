package com.zijinph.base.auth.client;

import com.github.pagehelper.cache.Cache;
import com.github.pagehelper.cache.CacheFactory;
import com.zijinph.base.auth.entity.DataRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    private RestTemplate restTemplate;

    private Properties properties;

    private Cache<String, List<DataRule>> authCache;

    public AuthClient(RestTemplate restTemplate, Properties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;

        Properties cacheProperties = new Properties();
        cacheProperties.setProperty("auth.expireAfterWrite", "300000");
        authCache = CacheFactory.createCache(null, "auth", cacheProperties);
    }

    /**
     * 从权限系统获取用户数据权限
     *
     * @param userId
     * @param dataId
     * @return
     */
    public List<DataRule> getRuleList(String userId, String dataId) {
        try {
            String cacheKey = userId+"@@"+dataId;
            List<DataRule> rules = authCache.get(cacheKey);
            if(rules == null){
                String authUrl = properties.getProperty("auth-url");
                DataRule[] rulesarr = this.restTemplate.postForObject(authUrl + "/getDataAuth?userId={1}&dataId={2}",
                        null, DataRule[].class, userId, dataId);
                rules = Arrays.asList(rulesarr);
                authCache.put(cacheKey, rules);
            }

            return rules;
        } catch (Exception e) {
            log.error("从权限系统获取数据权限失败：{} {}", userId, dataId);
        }

        return null;
    }

}
