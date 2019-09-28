package com.wangzhumo.app.base.delegate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2019-09-28  17:26
 *
 * 标注是否延期
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface IApp {

    //加载的模块名
    String name() default "APP";

    //优先级
    int priority() default 0;
}
