package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart); //动态条件查询

    //根据id 修改购物车数量的语句
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    //插入购物车数据
    @Insert("insert into shopping_cart(name, user_id, dish_id, dish_flavor, number, amount, image,create_time )"
    + "values (#{name},#{userId}, #{dishId}, #{dishFlavor}, #{number}, #{amount}, #{image}, #{createTime})")
    void insert(ShoppingCart shoppingCart);


    @Delete("delete from shopping_cart where user_id = #{currentUserId}")
    void deleteByUserId(Long currentUserId);
}
