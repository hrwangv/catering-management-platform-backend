package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    //添加购物车的业务实现
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart); //属性拷贝
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId); //获得当前购物车操作的id

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //添加购物车时,若存在购物车只需要修改操作,修改数量加一
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //若购物车中不存在,执行插入操作
            //判断添加的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();//获取套餐的id
            if (dishId != null) { //本次添加的是菜品
                //设置购物车对象的属性,从菜品表中
                Dish dishbyId = dishMapper.getById(dishId);
                shoppingCart.setName(dishbyId.getName());
                shoppingCart.setImage(dishbyId.getImage());
                shoppingCart.setAmount(dishbyId.getPrice());
//                shoppingCart.setNumber(1);
//                shoppingCart.setCreateTime(LocalDateTime.now());
            } else { //本次添加的是套餐
                Setmeal setmealbyId = setmealMapper.getById(setmealId);//获取套餐对象SetMeal
                shoppingCart.setName(setmealbyId.getName());
                shoppingCart.setImage(setmealbyId.getImage());
                shoppingCart.setAmount(setmealbyId.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(shoppingCart); //将购物车对象添加到购物车表
        }
    }

    //查看购物车
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long currentUserId = BaseContext.getCurrentId(); //获取用户id
        ShoppingCart build = ShoppingCart.builder().userId(currentUserId).build();//根据id创建对象
        return shoppingCartMapper.list(build);
    }

    @Override
    public void cleanShoppingCart() {
        Long currentUserId = BaseContext.getCurrentId(); //获取用户id
        shoppingCartMapper.deleteByUserId(currentUserId);
    }
}
