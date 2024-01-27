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
        shoppingCart.setUserId(currentId); //获得当前购物车操作的用户id,通过JWT令牌拦截器
        // select * from shopping_cart where user_id = currentId and setmealId = ;
        // select * from shopping_cart where user_id = currentId and dishId = , dish_flavor =  ;
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //为什么要用List结构接受数据，虽然这里只可能有一个购物车，但是list方法是复用的
        //添加购物车时,若存在购物车只需要修改操作,修改数量加一
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0); //取出第一条数据也就是唯一的一条数据
            cart.setNumber(cart.getNumber() + 1); //购物车数量加一
            shoppingCartMapper.updateNumberById(cart);
        } else {
        //若购物车中不存在,执行插入操作
            //判断添加的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();//获取套餐的id
            if (dishId != null) { //菜品属性不为空，本次添加的是菜品
                //设置购物车对象的属性,从菜品表中
                Dish dishbyId = dishMapper.getById(dishId);
                shoppingCart.setName(dishbyId.getName());
                shoppingCart.setImage(dishbyId.getImage());
                shoppingCart.setAmount(dishbyId.getPrice());
                //shoppingCart.setNumber(1);
                //shoppingCart.setCreateTime(LocalDateTime.now());
            } else { //套餐属性不为空，本次添加的是套餐
                Setmeal setmealbyId = setmealMapper.getById(setmealId);//获取套餐对象SetMeal
                shoppingCart.setName(setmealbyId.getName());
                shoppingCart.setImage(setmealbyId.getImage());
                shoppingCart.setAmount(setmealbyId.getPrice());

            }
            //不管哪种情况都要设置的公共字段，提出
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

    //清空购物车
    @Override
    public void cleanShoppingCart() {
        Long currentUserId = BaseContext.getCurrentId(); //获取用户id
        shoppingCartMapper.deleteByUserId(currentUserId);
    }

    @Override
    //减少购物车
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart(); //创建购物车实体对象用于在mapper层操作
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart); //属性拷贝
        Long currendUserId = BaseContext.getCurrentId(); //获得当前ThreadLocal中的id
        shoppingCart.setUserId(currendUserId);//设置购物车实体中的操作用户id

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);//根据要查的购物车实体信息查出所有显示信息

        ShoppingCart cart = list.get(0); //因为我们取出的shoppingcart只有一组数据，获取这组数据
        //购物车数量减少时，如果购物车中菜品或者套餐数量大于2，则仅修改数量
        if(cart.getNumber() >= 2) {
            cart.setNumber(cart.getNumber() - 1); //购物车数量减1
            shoppingCartMapper.updateNumberById(cart); //更新购物车数量表

        } else { //如果菜品仅为1，减少后菜品从购物车消失(删除)
            //这里不能按用户id删除了，因为一个用户id可以有很多购物车
            shoppingCartMapper.deleteByCartId(cart.getId());
            //
        }

    }
}
