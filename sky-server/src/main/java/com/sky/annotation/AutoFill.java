package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :自定义注解，表示某个方法需要进行自定义公共字段的填充
 * @Version :
 */
@Target(ElementType.METHOD) //元注解target，表示注解加到方法上（这里就是insert和update）
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //指定属性（不是方法）数据库操作的类型：更新/修改。因为在查询或者修改时无需公共字段的填充
    OperationType value();//定义注解的参数，如果没有设默认值，则在实现时必须要在注解参数指定
    //如果注解只用一个值，value在设置时可以省略
}
