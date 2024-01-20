package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

//    菜品的分类查询：查菜品表所有数据，以及该菜品对应的分类名称（在id一致的地方左外连接）
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    Dish getById(Long id);

    //删除菜品数据
    @Delete("delete from dish where id =#{id}")
    void deleteById(Long id);


    //根据id动态修改菜品数据
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    //动态查询菜品数据
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    @Select("select * from dish where category_id = #{categoryID}")
    List<Dish> getDishByCategoryID(Long categoryId);
}
