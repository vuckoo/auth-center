package com.zijinph.base.auth.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataAuth {

    String dataId();

    String dataField() default "";
}
