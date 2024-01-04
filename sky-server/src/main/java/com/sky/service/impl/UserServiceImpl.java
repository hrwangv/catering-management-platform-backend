package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :微信登录逻辑实现
 * @Version :
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信官方接口服务
    public static final String WXLOGIN= "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    
    //调用微信接口服务，获取openid
    private String getOpenid(String code) {
        //hashmap用于存放请求参数
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("appid",weChatProperties.getAppid());
        stringStringHashMap.put("secret",weChatProperties.getSecret());
        stringStringHashMap.put("js_code",code);
        stringStringHashMap.put("grant_type","authorization_code");
        //调用微信方服务器接口服务，获得当前微信用户的openid
        String json = HttpClientUtil.doGet(WXLOGIN,stringStringHashMap);
        JSONObject jsonObject = JSON.parseObject(json);//从json字符串解析出openid

        return jsonObject.getString("openid");
    }
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
        
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，判断是否登录失败
        if(openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断是否新用户，即当前openid是否在表里
        User user = userMapper.getByOpenId(openid);
        //如果是新用户自动完成注册
        if (user == null) {
            //注册新用户
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回这个用户对象
        return user;
    }
}
