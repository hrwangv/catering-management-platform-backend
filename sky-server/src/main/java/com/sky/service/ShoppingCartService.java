package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
public interface ShoppingCartService {
    //添加购物车接口
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    //查看购物车
    List<ShoppingCart> showShoppingCart();

    //清空购物车
    void cleanShoppingCart();

    //减小购物车
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
