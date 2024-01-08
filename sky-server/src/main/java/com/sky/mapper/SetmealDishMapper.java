package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    //将套餐中的菜品插入到套餐菜品关联表
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);


    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

}
