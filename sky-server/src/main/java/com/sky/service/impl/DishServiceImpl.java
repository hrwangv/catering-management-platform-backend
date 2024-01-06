package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) { //多表操作，向菜品表和口味表中插入数据
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish); //属性住拷贝，前提是两种类型的属性值一致

        //向菜品表添加一条数据
        dishMapper.insert(dish);

        Long id = dish.getId();
        //向口味表添加多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) { // 该判断表示数据确实插入进来了
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //select * from dish limit 0,10
        //开始分页查询,使用pagehelper插件，实际上插件在后台做的也是字符串的拼接
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //插件要求返回类型必须为Page类型
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        //将page值拿出来封装到我们规定的格式PageResult
        long total = page.getTotal();
        List<DishVO> records = page.getResult();

        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能删除，在售的商品不能被删除
        //遍历数组取序号,根据id查
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //状态1，在售不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //被套餐关联的菜品不能被删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) { //要删的菜品有关联套餐
            //当前菜品被关联了
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联的口味表数据
            dishFlavorMapper.deleteByDishId(id);
        }

    }

    //根据id查询对应的菜品和口味数据
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //先查菜品表
        Dish dish = dishMapper.getById(id);
        //再查菜品关联的口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);//一个菜品可以有多个口味
        //两种数据的封装
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }


    //根据id修改菜品基本信息和关联的口味表
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish); //dish对象包括了所需的信息

        //修改菜品基本信息
        dishMapper.update(dish);
        //先将原来的口味数据删掉
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //再把后面的数据全部插入，而不用考虑是否修改等等复杂情况
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) { // 该判断表示数据确实插入进来了
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    //修改菜品的起售状态，同时也要修改套餐的起售状态
    @Override
    public void setStatus(Long id, Integer status) {
//        Dish dish = new Dish();
//        dish.setStatus(status);
//        dish.setId(id);
//        dishMapper.update(dish);//返回修改后的菜品数据
        Dish dish = dishMapper.getById(id); //得到要修改的菜品全部信息
        dish.setStatus(status);//修改菜品起售状态
        dishMapper.update(dish);//返回修改后的菜品数

        if (status == StatusConstant.ENABLE) { //如果状态设置是0即停售
            List<Long> ids = new ArrayList<>();
            ids.add(id); //将要修改的id加到要查询的方法中
            List<Long> mealIds = setmealDishMapper.getSetmealIdByDishIds(ids);
            if (mealIds != null && mealIds.size() > 0) { //要删的菜品有关联套餐
                for (Long mealid : mealIds) {
                    Setmeal setmeal = new Setmeal(); //创建新的对象
                    setmeal.setStatus(status);//这个对象设置状态和id
                    setmeal.setId(mealid);
                    setmealMapper.update(setmeal);//根据这个对象的id和状态更新数据库套餐表
                }
            }

        }
    }

    //条件查询菜品和口味
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        /**
         * 条件查询菜品和口味
         * @param dish
         * @return
         */
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    @Override
    public List<DishVO> list(Long categoryId) { //根据分类id查询菜品

        return null;
    }
}
