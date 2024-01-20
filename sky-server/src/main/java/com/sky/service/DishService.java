package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
public interface DishService {
    void  saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    void setStatus(Long id, Integer status);

    List<DishVO> listWithFlavor(Dish dish);//查询菜品及口味数据


    List<Dish> list(Long categoryId);
}
