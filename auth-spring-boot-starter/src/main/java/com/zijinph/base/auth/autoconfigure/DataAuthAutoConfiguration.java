package com.zijinph.base.auth.autoconfigure;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.zijinph.base.auth.client.AuthClient;
import com.zijinph.base.auth.plugin.DataAuthInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Configuration
@ConditionalOnBean({SqlSessionFactory.class})
@EnableConfigurationProperties({AuthConfigProperties.class})
@AutoConfigureAfter({PageHelperAutoConfiguration.class})
public class DataAuthAutoConfiguration {

    @Autowired
    private AuthConfigProperties properties;

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    public DataAuthAutoConfiguration() {
    }

    @Bean
    @ConfigurationProperties(
            prefix = AuthConfigProperties.PAGEHELPER_PREFIX
    )
    public Properties authConfigProperties() {
        return new Properties();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void addAuthInterceptor() {
        Properties properties = new Properties();
        properties.putAll(authConfigProperties());
        properties.putAll(this.properties.getProperties());

        AuthClient authClient = new AuthClient(restTemplate(), properties);
        DataAuthInterceptor interceptor = new DataAuthInterceptor(authClient);

        interceptor.setProperties(properties);
        Iterator var3 = this.sqlSessionFactoryList.iterator();
        while(var3.hasNext()) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)var3.next();
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }
}
