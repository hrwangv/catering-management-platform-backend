package com.sky.config;

import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
@Slf4j
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Stateless 无状态模式)
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForStateless();
    }
}


