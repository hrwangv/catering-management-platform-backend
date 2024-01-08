package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable { //购物车接收的数据

    private Long dishId;
    private Long setmealId;
    private String dishFlavor;

}
