package com.zijinph.base.auth.plugin;

import com.zijinph.base.auth.annotation.DataAuth;
import com.zijinph.base.auth.client.AuthClient;
import com.zijinph.base.auth.entity.DataRule;
import com.zijinph.base.auth.sql.SqlAppender;
import com.zijinph.base.auth.util.PermissionUtils;
import com.zijinph.base.auth.util.ReflectUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * mybatis数据权限拦截器 - prepare
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class DataAuthInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(DataAuthInterceptor.class);

    private Properties properties;

    private AuthClient authClient;

    public DataAuthInterceptor(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        ReflectUtil.setFieldValue(boundSql, "sql", permissionSql(ms, boundSql.getSql()));
        //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }


    /**
     * 权限sql包装
     */
    protected String permissionSql(MappedStatement mappedStatement, String sql) {
        DataAuth dataCrontrol = PermissionUtils.getPermissionByDelegate(mappedStatement);
        if (dataCrontrol == null || dataCrontrol.dataId() == null) {
            return sql;
        }
        log.info("原始SQL : {}", sql);
        //开启权限控制
        StringBuilder privSql = new StringBuilder();
        String userMethodPath = properties.getProperty("client-user-method");
        //获取当前登录人
        String userId = (String) ReflectUtil.reflectByPath(userMethodPath);
        //获取当前登录人数据权限配置
        String dataId = dataCrontrol.dataId();
        String dataField = "".equals(dataCrontrol.dataField()) ? "role_code" : dataCrontrol.dataField();
        List<DataRule> rules = authClient.getRuleList(userId, dataId);
        if (rules == null || rules.size() == 0) {//未找到权限配置
            log.warn("开启数据权限控制，但未找到具体配置信息！！");
            return sql;
        }
        for (int i = 0; i < rules.size(); i++) {
            DataRule dataRule = rules.get(i);
            String rightCode = dataRule.getRightCode();
            String rightVal = dataRule.getRightValue();
            String ifunion = privSql.length() == 0 ? " " : " or ";
            if ("2".equals(rightCode) || "4".equals(rightCode)) {//本职位和下属职位数据 或 指定职位和下级
                privSql.append(ifunion).append(dataField + " like \"" + rightVal + "%\" ");
            } else if ("3".equals(rightCode) || "5".equals(rightCode)) {//只本职位数据 或 只指定职位数据
                privSql.append(ifunion).append(dataField + " = \"" + rightVal + "\" ");
            }
        }

        String executeSql = SqlAppender.handlerSql(sql, "(" + privSql + ") and ");

        log.info("加数据权限后SQL: {}", executeSql);

        return executeSql;
    }

}
