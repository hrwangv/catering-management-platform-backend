package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }


    //当设置唯一的用户名重复时，无法插入新数据，系统报异常，我们期望将其捕获
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) { //方法的重载
        String message = ex.getMessage(); //获取报错信息
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" "); //按照空格分离
            String username = split[2]; //索引为2的字符串
            String msg = username + MessageConstant.ALREADY_EXIST; //返回信息的拼接
            return Result.error(msg); //
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
