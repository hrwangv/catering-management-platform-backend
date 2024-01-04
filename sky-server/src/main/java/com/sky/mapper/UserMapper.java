package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User getByOpenId (String openid);

    void insert(User user);
}
