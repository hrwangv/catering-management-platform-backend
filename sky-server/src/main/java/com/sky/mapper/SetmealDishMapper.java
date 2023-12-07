package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
@Mapper
public interface SetmealDishMapper {
    //根据菜品id查对应的套餐id
    //select setmeal_id from setmeal_dish where dish_id in
    List<Long> getSetmealIdByDishIds(List<Long> dishIds) ;

}
