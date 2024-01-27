package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */

@RestController
@RequestMapping("/user/user")
@Api(tags="C端用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录，{}",userLoginDTO.getCode());
        //微信用户登录，返回的是要登陆的user
        User wxlogin = userService.wxlogin(userLoginDTO);
        //为微信用户生成jwt令牌
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();

        //将包含登录信息的User对象放到一个HashMap中
        stringObjectHashMap.put(JwtClaimsConstant.USER_ID,wxlogin.getId());

        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), stringObjectHashMap);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(wxlogin.getId())
                .openid(wxlogin.getOpenid())
                .token(jwt)
                .build();

        return Result.success(userLoginVO);
    }
}
