package com.zijinph.base.auth.util;

import com.zijinph.base.auth.annotation.DataAuth;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;

/**
 * 自定义权限相关工具类
 */
public class PermissionUtils {

    /**
     * 根据 StatementHandler 获取 注解对象
     */
    public static DataAuth getPermissionByDelegate(MappedStatement mappedStatement){
        DataAuth permissionAop = null;
        try {
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);
            final Class cls = Class.forName(className);
            final Method[] method = cls.getMethods();
            for (Method me : method) {
                if (me.getName().equals(methodName) && me.isAnnotationPresent(DataAuth.class)) {
                    permissionAop = me.getAnnotation(DataAuth.class);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return permissionAop;
    }
}
