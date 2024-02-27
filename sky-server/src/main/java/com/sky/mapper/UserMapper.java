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

    //插入数据
    void insert(User user);

    //根据id查询数据
    @Select("select * from user where id = #{id}")
    User getById(Long userId);
}
