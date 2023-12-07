package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @Author : haoranwang
 * @Date :
 * @Description : 切面类
 * @Version :
 */

@Aspect  //表示是切面类
@Component //加入容器
@Slf4j
public class AutoFillAspect { //切面类：公共字段的自动填充
    //定义切入点和通知

    //切点表达式，要拦截加入的方法：在com.sky.mapper包下，而且同时要满足在方法上加入了注解Autofill
    @Pointcut("execution(* com.sky.mapper.*.*(..))  && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() { //切入点方法

    }

    //定义通知
    @Before("autoFillPointCut()") //在切点之前，前置通知，在修改/插入操作之前对公共字段的赋值
    //需要传入连接点JoinPoint，知道哪个方法被拦截到了，获取方法名方法参数等
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始进行公共字段的填充...");

        //1.获取被拦截方法上的数据库操作类型：创建或者修改，以便加创建或修改时间
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //方法操作对象，也叫方法的签名
        AutoFill autofill = signature.getMethod().getAnnotation(AutoFill.class);   //获取方法上的注解对象
        OperationType operationType = autofill.value(); //通过注解获取数据库的操作类型

        //2.获取被拦截方法的实体参数，即获取实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) { //方法没有参数
            return;
        }
        Object arg = args[0];//约定接受第一个实体对象，获取第一个实体即可

        //3.为实体对象公共属性统一赋值，获取赋值的数据，创建时间日期
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.通过对应属性反射赋值
        if (operationType == OperationType.INSERT) { //插入类型，为4个公共字段赋值
            //为4个公共字段赋值
            Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

            //通过反射为对象赋值
            setCreateTime.invoke(arg,now);
            setCreateUser.invoke(arg,currentId);
            setUpdateTime.invoke(arg,now);
            setUpdateUser.invoke(arg,currentId);

        } else if (operationType == OperationType.UPDATE) { //修改
            //为2个公共字段赋值
            Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

            setUpdateTime.invoke(arg,now);
            setUpdateUser.invoke(arg,currentId);
        }


    }

}
