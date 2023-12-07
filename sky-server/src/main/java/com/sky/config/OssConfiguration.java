package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : haoranwang
 * @Date :
 * @Description : 配置类用于创建AliOssUtil对象
 * @Version :
 */
@Slf4j
@Configuration
public class OssConfiguration {
    @ConditionalOnMissingBean //当没有bean时才创建对象，配置类对象只用一次
    @Bean
    public AliOssUtil aliOssUtil (AliOssProperties aliOssProperties) {
       log.info("开始上传阿里云文件上传工具对象,{}",aliOssProperties);
        return new  AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
